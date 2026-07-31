package com.marcoaga02.carrentalmanager.transaction.jpa;

import java.time.Clock;

import com.marcoaga02.carrentalmanager.repository.CarRepository;
import com.marcoaga02.carrentalmanager.repository.CustomerRepository;
import com.marcoaga02.carrentalmanager.repository.RentalRepository;
import com.marcoaga02.carrentalmanager.repository.jpa.CarRepositoryJpa;
import com.marcoaga02.carrentalmanager.repository.jpa.CustomerRepositoryJpa;
import com.marcoaga02.carrentalmanager.repository.jpa.RentalRepositoryJpa;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;

import jakarta.persistence.EntityManager;

public class TransactionContextJpa implements TransactionContext {

	private final CarRepository carRepository;
	private final CustomerRepository customerRepository;
	private final RentalRepository rentalRepository;

	public TransactionContextJpa(EntityManager entityManager, Clock clock) {
		this.carRepository = new CarRepositoryJpa(entityManager);
		this.customerRepository = new CustomerRepositoryJpa(entityManager);
		this.rentalRepository = new RentalRepositoryJpa(entityManager, clock);
	}

	@Override
	public CarRepository carRepository() {
		return carRepository;
	}

	@Override
	public CustomerRepository customerRepository() {
		return customerRepository;
	}

	@Override
	public RentalRepository rentalRepository() {
		return rentalRepository;
	}

}
