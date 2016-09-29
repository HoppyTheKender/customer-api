package com.rueckert.customer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rueckert.customer.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
	// Intentionally left blank
}
