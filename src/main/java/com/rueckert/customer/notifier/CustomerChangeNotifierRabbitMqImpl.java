package com.rueckert.customer.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.rueckert.customer.config.PcfRabbitMqConfig;

public class CustomerChangeNotifierRabbitMqImpl implements CustomerChangeNotifier {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RabbitTemplate rabbitTemplate;

	public CustomerChangeNotifierRabbitMqImpl(RabbitAdmin rabbitAdmin, Exchange exchange) {
		rabbitAdmin.declareExchange(exchange);
		this.rabbitTemplate = rabbitAdmin.getRabbitTemplate();

		rabbitTemplate.setExchange(exchange.getName());
	}

	@Override
	public void sendNotification(String id) {
		try {
			rabbitTemplate.convertAndSend(PcfRabbitMqConfig.CUSTOMER_TOPIC_NAME, null, id);
		} catch (AmqpException e) {
			logger.error("An exception occurred trying to publish a message.", e);
		}
	}
}
