package com.rueckert.customer.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.rueckert.customer.persistence.CustomerPersistence;
import com.rueckert.customer.persistence.CustomerPersistenceFirebaseImpl;

@Configuration
@Profile("firebase")
public class FirebaseConfig {
	private static final String PROJECT_ID = "customer-system-demo";

	@Bean
	public CustomerPersistence customerPersistence() throws IOException {
		return new CustomerPersistenceFirebaseImpl(firstoreDatabase());
	}
	
	@Bean
	public Firestore firstoreDatabase() throws IOException {
		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
		FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredentials(credentials)
		    .setProjectId(PROJECT_ID)
		    .build();
		FirebaseApp.initializeApp(options);

		return FirestoreClient.getFirestore();
	}
}
