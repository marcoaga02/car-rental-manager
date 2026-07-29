package com.marcoaga02.carrentalmanager.repository;

import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Car;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class CarRepositoryJpa implements CarRepository {

	private final EntityManager entityManager;

	public CarRepositoryJpa(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Car> findAllActive() {
		TypedQuery<Car> query = entityManager
				.createQuery("SELECT c FROM Car c WHERE c.deleted = false", Car.class);

		return query.getResultList();
	}

	@Override
	public Optional<Car> findActiveByCarPlate(String carPlate) {
		TypedQuery<Car> query = entityManager
				.createQuery(
						"SELECT c FROM Car c WHERE c.carPlate = :carPlate AND c.deleted = false",
						Car.class);
		query.setParameter("carPlate", carPlate);
		
		return query.getResultStream().findFirst();
	}

	@Override
	public Optional<Car> findActiveById(Long id) {
		TypedQuery<Car> query = entityManager
				.createQuery(
						"SELECT c FROM Car c WHERE c.id = :id AND c.deleted = false",
						Car.class);
		query.setParameter("id", id);
		
		return query.getResultStream().findFirst();
	}

	@Override
	public Car save(Car car) {
	    if (car.getId() == null) {
	        entityManager.persist(car);
	        return car;
	    }
	    
	    return entityManager.merge(car);
	}
}
