package com.marcoaga02.carrentalmanager.transaction.jpa;

import java.time.Clock;
import java.util.Objects;

import com.marcoaga02.carrentalmanager.transaction.TransactionCode;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class TransactionManagerJpa implements TransactionManager {

	private final EntityManagerFactory entityManagerFactory;
	private final Clock clock;

	public TransactionManagerJpa(EntityManagerFactory entityManagerFactory, Clock clock) {
		this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory);
		this.clock = Objects.requireNonNull(clock);
	}

	@Override
	public <T> T doInTransaction(TransactionCode<T> code) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();

			TransactionContext context = new TransactionContextJpa(entityManager, clock);
			T result = code.apply(context);

			transaction.commit();

			return result;
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		} finally {
			entityManager.close();
		}
	}

}
