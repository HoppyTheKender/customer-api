package com.rueckert.customer.persistence;

import java.util.List;
import java.util.Optional;

import com.rueckert.customer.domain.Customer;
import com.rueckert.customer.repositories.CustomerRepository;

public class CustomerPersistenceJpaImpl implements CustomerPersistence {
	private CustomerRepository repository;

	public CustomerPersistenceJpaImpl(CustomerRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Customer> retrieveAllCustomers() {
		return repository.findAll();
	}

	@Override
	public Customer retrieveCustomerByCustomerId(String id) {
		Optional<Customer> optionalCustomer = repository.findById(id);

		return optionalCustomer.get();
	}

	@Override
	public List<Customer> retrieveCustomersByLastName(String lastName) {
		return repository.findByLastName(lastName);
	}

	@Override
	public Customer createCustomer(Customer customer) {
		return repository.save(customer);
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return repository.save(customer);
	}

	@Override
	public void deleteCustomerByCustomerId(String id) {
		repository.deleteById(id);
	}
}
