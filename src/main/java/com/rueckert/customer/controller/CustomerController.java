package com.rueckert.customer.controller;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rueckert.customer.config.CloudConfig;
import com.rueckert.customer.domain.Customer;
import com.rueckert.customer.repositories.CustomerRepository;

@RestController
@ResponseStatus(value = HttpStatus.CREATED)
public class CustomerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private CustomerRepository repository;
	private RabbitTemplate rabbitTemplate;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public CustomerController(RabbitAdmin rabbitAdmin, Exchange exchange) {
		rabbitAdmin.declareExchange(exchange);
		this.rabbitTemplate = rabbitAdmin.getRabbitTemplate();

		rabbitTemplate.setExchange(exchange.getName());
	}

	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Iterable<Customer> getCustomers() {
		String instanceIndex = retrieveInstanceIndex();
		logger.info(String.format("Instance Index {%s}", instanceIndex));

		return repository.findAll();
	}

	private String retrieveInstanceIndex() {
		String vcapApplication = System.getenv("VCAP_APPLICATION");
		try {
			JsonNode jsonNode = objectMapper.readValue(vcapApplication, JsonNode.class);
			JsonNode instanceIndex = jsonNode.get("instance_index");
			return instanceIndex.asText();
		} catch (IOException e) {
			return "Unknown";
		}
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Customer getCustomerById(@PathVariable String id) {
		String instanceIndex = retrieveInstanceIndex();
		logger.info(String.format("Instance Index {%s}", instanceIndex));

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

	@RequestMapping(value = "/customer/lastname/{lastName}", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Iterable<Customer> getCustomerByLastName(@PathVariable String lastName) {
		String instanceIndex = retrieveInstanceIndex();
		logger.info(String.format("Instance Index {%s}", instanceIndex));

		return repository.findByLastName(lastName);
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
