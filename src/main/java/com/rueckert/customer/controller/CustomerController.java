package com.rueckert.customer.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rueckert.customer.config.CloudConfig;
import com.rueckert.customer.domain.Customer;

@RestController
@ResponseStatus(value = HttpStatus.CREATED)
public class CustomerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private CrudRepository<Customer, String> repository;
	private RabbitTemplate rabbitTemplate;

	@Autowired
	public void setRepository(CrudRepository<Customer, String> repository) {
		this.repository = repository;
	}

	@Autowired
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Iterable<Customer> getCustomers() {
		return repository.findAll();
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Customer getCustomerById(@PathVariable String id) {
		return repository.findOne(id);
	}

	@RequestMapping(value = "/customer", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public Customer createCustomer(@RequestBody Customer customer) {
		String id = getCustomerId();
		customer.setId(id);

		Customer savedCustomer = repository.save(customer);
		
		publishMessage(id);

		return savedCustomer;
	}

	private static String getCustomerId() {
		String id = UUID.randomUUID().toString();
		return id;
	}
	
	private void publishMessage(String id) {
		try {
			rabbitTemplate.convertAndSend(CloudConfig.CUSTOMER_TOPIC_NAME, null, id);
		} catch (AmqpException e) {
			logger.error("An exception occurred trying to publish a message.", e);
		}
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.PUT)
	@ResponseStatus(code = HttpStatus.OK)
	public Customer updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
		customer.setId(id);

		Customer savedCustomer = repository.save(customer);
		
		publishMessage(id);

		return savedCustomer;
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(code = HttpStatus.OK)
	public void deleteCustomer(@PathVariable String id) {
		repository.delete(id);
	}
}
