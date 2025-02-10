package com.tripmarket.global.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableRabbit
public class RabbitMQConfig {

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.chat.queue.name}")
	private String chatQueueName;

	@Value("${spring.rabbitmq.chat.exchange.name}")
	private String chatExchangeName;

	@Value("${spring.rabbitmq.chat.routing.key}")
	private String chatRoutingKey;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	@Value("${spring.rabbitmq.port}")
	private int port;

	@Value("${spring.rabbitmq.virtual-host}")
	private String virtualHost;

	// RabbitMQ와 연결 설정. CachingConnectionFactory 를 세팅
	@Bean
	public ConnectionFactory createConnectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(host);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setPort(port);
		factory.setVirtualHost(virtualHost);

		return factory;
	}

	// "chat.queue"라는 이름의 Queue 생성
	@Bean
	public Queue chatQueue() {
		return new Queue(chatQueueName, true);
	}

	// 4가지 Binding 전략이 있으며 4가지 전략마다 사용하는 대상이 다른데 채팅 메시지같은 경우 TopicExchange를 사용
	@Bean
	public TopicExchange chatExchange() {
		return new TopicExchange(chatExchangeName, true, false);
	}

	// Exchange와 Queue를 연결. "chat.queue"에 "chat.exchange" 규칙을 Binding
	@Bean
	public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
		Binding binding = BindingBuilder
			.bind(chatQueue)
			.to(chatExchange)
			.with(chatRoutingKey);
		return binding;
	}

	// RabbitMQ와 메시지를 주고받을수 있는 메서드
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter);
		return rabbitTemplate;
	}

	// @MessagingGateway, @SendTo 같은 어노테이션 사용가능하게 해주는 메서드
	@Bean
	public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
		return new RabbitMessagingTemplate(rabbitTemplate);
	}

	// 메시지를 JSON으로 직렬/역직렬화
	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
