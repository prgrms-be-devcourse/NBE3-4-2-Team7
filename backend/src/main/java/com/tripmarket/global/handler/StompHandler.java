package com.tripmarket.global.handler;

import java.util.Map;
import java.util.Objects;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import com.tripmarket.domain.chatting.service.RedisChattingService;
import com.tripmarket.domain.member.entity.Member;
import com.tripmarket.domain.member.repository.MemberRepository;
import com.tripmarket.global.exception.CustomException;
import com.tripmarket.global.exception.ErrorCode;
import com.tripmarket.global.jwt.JwtTokenProvider;
import com.tripmarket.global.oauth2.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

	private final RedisChattingService redisChattingService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;

	@Override
	public Message<?> preSend(@Nullable Message<?> message, @Nullable MessageChannel channel) {
		if (message == null) {
			return null;
		}

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		switch (Objects.requireNonNull(accessor.getCommand())) {
			case CONNECT -> handleConnect(accessor);
			case SUBSCRIBE -> handleSubscribe(accessor);
			case DISCONNECT -> handleDisconnect(accessor);
		}

		return message;
	}

	private void handleConnect(StompHeaderAccessor accessor) {
		try {
			String token = accessor.getFirstNativeHeader("Authorization");
			if (token == null || !token.trim().startsWith("Bearer ")) {
				throw new CustomException(ErrorCode.INVALID_TOKEN);
			}

			String accessToken = token.substring(7).trim();
			jwtTokenProvider.validateToken(accessToken);

			CustomOAuth2User user = (CustomOAuth2User)jwtTokenProvider.getAuthentication(accessToken).getPrincipal();
			Member member = getMember(user);
			String roomId = accessor.getFirstNativeHeader("roomId");
			String userEmail = member.getEmail();
			String userName = member.getName();
			String userImage = member.getImageUrl();

			setSessionAttribute(accessor, "userName", userName);
			setSessionAttribute(accessor, "userImage", userImage);

			log.info("세션 속성: {}", accessor.getSessionAttributes());

			if (roomId == null || userEmail == null) {
				throw new CustomException(ErrorCode.STOMP_INVALID_HEADER);
			}

			log.info("웹소켓 연결 성공: {} (roomId: {})", userEmail, roomId);
			redisChattingService.addUserChattingRoom(roomId, userEmail);
			redisChattingService.resetUnreadCount(roomId, userEmail);
		} catch (Exception e) {
			log.error("웹소켓 연결 중 예외 발생: ", e);
			throw e;
		}
	}

	private Member getMember(CustomOAuth2User user) {
		return memberRepository.findByEmail(user.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private void handleSubscribe(StompHeaderAccessor accessor) {
		String roomId = accessor.getDestination();
		String userName = (String)getSessionAttribute(accessor);
		log.debug("사용자 {}가 채팅방 {}에 구독했습니다.", userName, roomId);
	}

	private void handleDisconnect(StompHeaderAccessor accessor) {
		String token = accessor.getFirstNativeHeader("Authorization");
		if (token == null || !token.trim().startsWith("Bearer ")) {
			log.error("잘못된 Authorization 헤더: {}", token);
			return;
		}

		String accessToken = token.substring(7).trim();
		jwtTokenProvider.validateToken(accessToken);

		CustomOAuth2User user = (CustomOAuth2User)jwtTokenProvider.getAuthentication(accessToken).getPrincipal();
		String userEmail = user.getEmail();
		String roomId = accessor.getFirstNativeHeader("roomId");

		if (roomId != null && userEmail != null) {
			redisChattingService.deleteUserChattingRoom(roomId, userEmail);

		}
	}

	private void setSessionAttribute(StompHeaderAccessor accessor, String key, Object value) {
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		if (sessionAttributes == null) {
			throw new CustomException(ErrorCode.SESSION_ERROR);
		}
		sessionAttributes.put(key, value);
	}

	private Object getSessionAttribute(StompHeaderAccessor accessor) {
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		if (sessionAttributes == null) {
			throw new CustomException(ErrorCode.SESSION_ERROR);
		}
		return sessionAttributes.get("userName");
	}
}
