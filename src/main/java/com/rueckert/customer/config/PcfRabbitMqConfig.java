package com.rueckert.customer.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("Cloud")
public class PcfRabbitMqConfig extends AbstractCloudConfig {
	public static final String CUSTOMER_TOPIC_NAME = "customer-topic";
	public static final String CUSTOMER_QUEUE_NAME = "customer-email-queue";

	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(CUSTOMER_TOPIC_NAME, true, false);
	}

	@Bean
	public RabbitAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory().rabbitConnectionFactory());
	}
}
