package com.rueckert.customer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.PooledServiceConnectorConfig.PoolConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class CloudConfig extends AbstractCloudConfig {
	public static final String CUSTOMER_TOPIC_NAME = "customer-topic";
	public static final String CUSTOMER_QUEUE_NAME = "customer-email-queue";

	@Bean
	public Properties cloudProperties() {
		return properties();
	}

	@Bean
	public DataSource dataSource() {
		PoolConfig poolConfig = new PoolConfig(1, 2, 3000);
		DataSourceConfig dataSourceConfig = new DataSourceConfig(poolConfig, null);
		return connectionFactory().dataSource(dataSourceConfig);
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory().rabbitConnectionFactory());

		rabbitTemplate.setExchange(CUSTOMER_TOPIC_NAME);

		return rabbitTemplate;
	}
}
