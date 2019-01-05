package com.rueckert.customer.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerChangeNotifierLoggingImpl implements CustomerChangeNotifier {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void sendNotification(String id) {
		logger.info(String.format("Notification that id %s was changed.", id));
	}
}
