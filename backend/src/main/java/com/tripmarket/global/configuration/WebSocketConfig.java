package com.tripmarket.global.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.tripmarket.global.handler.StompHandler;

import lombok.RequiredArgsConstructor;
import reactor.netty.tcp.TcpClient;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 간단한 메모리 기반 브로커 활성화
		registry.enableSimpleBroker("/topic"); // 구독 경로
		registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 메시지 전송 경로
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트가 연결할 WebSocket 엔드포인트
		registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
	}

	@Override
	public void configureWebSocketTransport(
		org.springframework.web.socket.config.annotation.WebSocketTransportRegistration registration) {
		registration
			.setSendTimeLimit(20000)
			.setSendBufferSizeLimit(512 * 1024)
			.setMessageSizeLimit(128 * 1024);
	}
}
