package com.rueckert.customer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.PooledServiceConnectorConfig.PoolConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.rueckert.customer.persistence.CustomerPersistence;
import com.rueckert.customer.persistence.CustomerPersistenceJpaImpl;
import com.rueckert.customer.repositories.CustomerRepository;

@Configuration
@Profile("Cloud")
public class PcfJpaDatasourceConfig extends AbstractCloudConfig {
	@Autowired
	private CustomerRepository repository;

	@Bean
	public CustomerPersistence customerPersistence() {
		return new CustomerPersistenceJpaImpl(repository);
	}

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
}
