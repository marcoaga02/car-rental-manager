package com.marcoaga02.carrentalmanager.repository.jpa;

import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.repository.CustomerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class CustomerRepositoryJpa implements CustomerRepository {

	private final EntityManager entityManager;

	public CustomerRepositoryJpa(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Customer> findAllActive() {
		TypedQuery<Customer> query = entityManager
				.createQuery("SELECT c FROM Customer c WHERE c.deleted = false", Customer.class);

		return query.getResultList();
	}

	@Override
	public Optional<Customer> findActiveByTaxIdCode(String taxIdCode) {
		TypedQuery<Customer> query = entityManager
				.createQuery("SELECT c FROM Customer c WHERE c.taxIdCode = :taxIdCode AND c.deleted = false",
						Customer.class);
		query.setParameter("taxIdCode", taxIdCode);

		return query.getResultStream().findFirst();
	}

	@Override
	public Optional<Customer> findActiveById(Long id) {
		TypedQuery<Customer> query = entityManager
				.createQuery("SELECT c FROM Customer c WHERE c.id = :id AND c.deleted = false", Customer.class);
		query.setParameter("id", id);

		return query.getResultStream().findFirst();
	}

	@Override
	public Customer save(Customer customer) {
		if (customer.getId() == null) {
			entityManager.persist(customer);
			return customer;
		}

		return entityManager.merge(customer);
	}

}
