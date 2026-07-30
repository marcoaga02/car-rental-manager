package com.marcoaga02.carrentalmanager.testutils;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
public abstract class BasePostgresTest {

	@SuppressWarnings("resource")
	@Container
	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.4")
			.withDatabaseName("carrental_test")
			.withUsername("test")
			.withPassword("test");

	private EntityManagerFactory entityManagerFactory;

	protected EntityManager entityManager;

	@BeforeEach
	void baseSetUp() {
		Map<String, String> properties = new HashMap<>();
		properties.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
		properties.put("jakarta.persistence.jdbc.user", postgres.getUsername());
		properties.put("jakarta.persistence.jdbc.password", postgres.getPassword());

		entityManagerFactory = Persistence.createEntityManagerFactory("test-pu", properties);
		entityManager = entityManagerFactory.createEntityManager();
	}

	@AfterEach
	void baseTearDown() {
		if (entityManager != null) {
			entityManager.close();
		}
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

}
