package com.rueckert.customer.controller;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.rueckert.customer.domain.Customer;
import com.rueckert.customer.notifier.CustomerChangeNotifier;
import com.rueckert.customer.persistence.CustomerPersistence;

@RestController
public class CustomerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private CustomerPersistence customerPersistence;
	private CustomerChangeNotifier customerChangeNotifier;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public CustomerController(CustomerPersistence customerPersistence, CustomerChangeNotifier customerChangeNotifier) {
		this.customerPersistence = customerPersistence;
		this.customerChangeNotifier = customerChangeNotifier;
	}

	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Iterable<Customer> getCustomers() {
		String instanceIndex = retrieveInstanceIndex();
		logger.info(String.format("Current version {%s}", "10"));
		logger.info(String.format("Instance Index {%s}", instanceIndex));

		return customerPersistence.retrieveAllCustomers();
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

		return customerPersistence.retrieveCustomerByCustomerId(id);
	}

	@RequestMapping(value = "/customer", method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public Customer createCustomer(@RequestBody Customer customer) {
		String id = getCustomerId();
		customer.setId(id);

		Customer savedCustomer = customerPersistence.createCustomer(customer);

		publishMessage(id);

		return savedCustomer;
	}

	@RequestMapping(value = "/customer/lastname/{lastName}", method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Iterable<Customer> getCustomerByLastName(@PathVariable String lastName) {
		String instanceIndex = retrieveInstanceIndex();
		logger.info(String.format("Instance Index {%s}", instanceIndex));

		return customerPersistence.retrieveCustomersByLastName(lastName);
	}

	private static String getCustomerId() {
		String id = UUID.randomUUID().toString();
		return id;
	}

	private void publishMessage(String id) {
		customerChangeNotifier.sendNotification(id);
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.PUT)
	@ResponseStatus(code = HttpStatus.OK)
	public Customer updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
		customer.setId(id);

		Customer savedCustomer = customerPersistence.updateCustomer(customer);

		publishMessage(id);

		return savedCustomer;
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(code = HttpStatus.OK)
	public void deleteCustomer(@PathVariable String id) {
		customerPersistence.deleteCustomerByCustomerId(id);
	}
}
