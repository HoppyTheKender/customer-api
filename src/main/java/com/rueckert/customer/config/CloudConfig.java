package com.rueckert.customer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;

public class CloudConfig extends AbstractCloudConfig {
	public static final String CUSTOMER_TOPIC_NAME = "customer-topic";
	public static final String CUSTOMER_QUEUE_NAME = "customer-email-queue";

	@Bean
	public Properties cloudProperties() {
		return properties();
	}

	@Bean
	public DataSource dataSource() {
		return connectionFactory().dataSource();
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory().rabbitConnectionFactory());

		rabbitTemplate.setExchange(CUSTOMER_TOPIC_NAME);

		return rabbitTemplate;
	}
}
