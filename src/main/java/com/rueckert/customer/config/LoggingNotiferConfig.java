package com.rueckert.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.rueckert.customer.notifier.CustomerChangeNotifier;
import com.rueckert.customer.notifier.CustomerChangeNotifierLoggingImpl;

@Configuration
@Profile("logging-notifier")
public class LoggingNotiferConfig {
	@Bean
	public CustomerChangeNotifier customerChangeNotifier() {
		return new CustomerChangeNotifierLoggingImpl();
	}
}
