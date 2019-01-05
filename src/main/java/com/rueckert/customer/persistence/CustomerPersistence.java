package com.rueckert.customer.persistence;

import java.util.List;

import com.rueckert.customer.domain.Customer;

public interface CustomerPersistence {
	List<Customer> retrieveAllCustomers();

	Customer retrieveCustomerByCustomerId(String id);

	List<Customer> retrieveCustomersByLastName(String lastName);

	Customer createCustomer(Customer customer);

	Customer updateCustomer(Customer customer);

	void deleteCustomerByCustomerId(String id);
}
