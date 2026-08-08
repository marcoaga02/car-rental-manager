package com.marcoaga02.carrentalmanager.testutils;

import java.util.HashMap;
import java.util.Map;

import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public abstract class BaseSwingPostgresTest extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("resource")
	@ClassRule
	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.4")
			.withDatabaseName("carrental_test").withUsername("test").withPassword("test");

	protected EntityManagerFactory entityManagerFactory;
	protected EntityManager entityManager;

	@Override
	protected void onSetUp() {
		Map<String, String> properties = new HashMap<>();
		properties.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
		properties.put("jakarta.persistence.jdbc.user", postgres.getUsername());
		properties.put("jakarta.persistence.jdbc.password", postgres.getPassword());

		entityManagerFactory = Persistence.createEntityManagerFactory("test-pu", properties);
		entityManager = entityManagerFactory.createEntityManager();
	}

	@Override
	protected void onTearDown() {
		if (entityManager != null) {
			entityManager.close();
		}
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}
}
