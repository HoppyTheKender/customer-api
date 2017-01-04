package com.rueckert.customer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.PooledServiceConnectorConfig.PoolConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(CUSTOMER_TOPIC_NAME, true, false);
	}

	@Bean
	public RabbitAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory().rabbitConnectionFactory());
	}
}
