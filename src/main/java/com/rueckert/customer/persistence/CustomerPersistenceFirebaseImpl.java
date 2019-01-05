package com.rueckert.customer.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.rueckert.customer.domain.Customer;

public class CustomerPersistenceFirebaseImpl implements CustomerPersistence {
	private static final String CUSTOMER_COLLECTION_NAME = "customer";

	private Firestore firstoreDatabase;

	public CustomerPersistenceFirebaseImpl(Firestore firstoreDatabase) {
		this.firstoreDatabase = firstoreDatabase;
	}

	@Override
	public List<Customer> retrieveAllCustomers() {
		ApiFuture<QuerySnapshot> query = firstoreDatabase.collection(CUSTOMER_COLLECTION_NAME).get();

		QuerySnapshot querySnapshot = attemptToExecuteQuery(query);

		return convertMultipleCustomers(querySnapshot);
	}

	private QuerySnapshot attemptToExecuteQuery(ApiFuture<QuerySnapshot> query) {
		try {
			return query.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException("Could not retrieve customers.", e);
		}
	}

	private List<Customer> convertMultipleCustomers(QuerySnapshot querySnapshot) {
		List<Customer> customers = new ArrayList<>();

		List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		for (QueryDocumentSnapshot document : documents) {
			Customer customer = convertSingleCustomer(document);

			customers.add(customer);
		}

		return customers;
	}

	private Customer convertSingleCustomer(DocumentSnapshot document) {
		Customer customer = new Customer();

		customer.setId(document.getId());
		customer.setFirstName(document.getString("firstName"));
		customer.setLastName(document.getString("lastName"));
		customer.setAddressLine1(document.getString("addressLine1"));
		customer.setAddressLine2(document.getString("addressLine2"));
		customer.setEmail(document.getString("email"));

		return customer;
	}

	@Override
	public Customer retrieveCustomerByCustomerId(String id) {
		ApiFuture<DocumentSnapshot> query = firstoreDatabase.collection(CUSTOMER_COLLECTION_NAME).document(id).get();

		try {
			return convertSingleCustomer(query.get());
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException("Could not retrieve customer.", e);
		}
	}

	@Override
	public List<Customer> retrieveCustomersByLastName(String lastName) {
		Query query = firstoreDatabase.collection(CUSTOMER_COLLECTION_NAME).whereEqualTo("lastName", lastName);

		QuerySnapshot querySnapshot = attemptToExecuteQuery(query.get());

		return convertMultipleCustomers(querySnapshot);
	}

	@Override
	public Customer createCustomer(Customer customer) {
		return saveCustomer(customer);
	}

	private Customer saveCustomer(Customer customer) {
		DocumentReference documentReference = firstoreDatabase.collection(CUSTOMER_COLLECTION_NAME)
				.document(customer.id);

		Map<String, Object> customerMap = createCustomerMap(customer);

		documentReference.set(customerMap);

		return customer;
	}

	private Map<String, Object> createCustomerMap(Customer customer) {
		Map<String, Object> customerMap = new HashMap<>();

		customerMap.put("firstName", customer.firstName);
		customerMap.put("lastName", customer.lastName);
		customerMap.put("addressLine1", customer.addressLine1);
		customerMap.put("addressLine2", customer.addressLine2);
		customerMap.put("email", customer.email);

		return customerMap;
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return saveCustomer(customer);
	}

	@Override
	public void deleteCustomerByCustomerId(String id) {
		DocumentReference documentReference = firstoreDatabase.collection(CUSTOMER_COLLECTION_NAME).document(id);

		documentReference.delete();
	}
}
