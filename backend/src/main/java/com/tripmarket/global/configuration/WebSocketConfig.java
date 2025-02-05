package com.tripmarket.global.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import reactor.netty.tcp.TcpClient;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.relay.port}")
	private int relayPort;

	@Value("${spring.rabbitmq.relay.client-login}")
	private String relayClientLogin;

	@Value("${spring.rabbitmq.relay.client-passcode}")
	private String relayClientPasscode;

	@Value("${spring.rabbitmq.relay.system-login}")
	private String relaySystemLogin;

	@Value("${spring.rabbitmq.relay.system-passcode}")
	private String relaySystemPasscode;

	// 애플리케이션 내부에서 사용할 path를 지정할 수 있음
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		ReactorNettyTcpClient<byte[]> tcpClient = createTcpClient();

		registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
			.setAutoStartup(true)
			.setTcpClient(tcpClient) // RabbitMQ와 연결할 클라이언트 설정
			.setRelayHost(host) // RabbitMQ 서버 주소
			.setRelayPort(relayPort) // RabbitMQ 포트(5672), STOMP(61613)
			.setSystemLogin(relaySystemLogin) // RabbitMQ 시스템 계정
			.setSystemPasscode(relaySystemPasscode) // RabbitMQ 시스템 비밀번호
			.setClientLogin(relayClientLogin) // RabbitMQ 클라이언트 계정
			.setClientPasscode(relayClientPasscode); // RabbitMQ 클라이언트 비밀번호
		registry.setPathMatcher(new AntPathMatcher("."));
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat")
			.setAllowedOriginPatterns("*")
			.withSockJS();
	}

	// TCP 설정
	private ReactorNettyTcpClient<byte[]> createTcpClient() {
		TcpClient tcpClient = TcpClient.create()
			.host(host)
			.port(relayPort);

		return new ReactorNettyTcpClient<>(tcpClient, new StompReactorNettyCodec());
	}
}