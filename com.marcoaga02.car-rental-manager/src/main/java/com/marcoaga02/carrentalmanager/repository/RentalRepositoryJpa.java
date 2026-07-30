package com.marcoaga02.carrentalmanager.repository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Rental;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class RentalRepositoryJpa implements RentalRepository {

	private static final String TODAY_PARAM = "today";

	private final EntityManager entityManager;
	private final Clock clock;

	public RentalRepositoryJpa(EntityManager entityManager, Clock clock) {
		this.entityManager = entityManager;
		this.clock = clock;
	}

	@Override
	public List<Rental> findAllActive() {
		TypedQuery<Rental> query = entityManager
				.createQuery("SELECT r FROM Rental r WHERE (r.startDate + r.days day) > :" + TODAY_PARAM, Rental.class);
		query.setParameter(TODAY_PARAM, LocalDate.now(clock));

		return query.getResultList();
	}

	@Override
	public boolean existsActiveByCarId(Long carId) {
		TypedQuery<Long> query = entityManager
				.createQuery("SELECT COUNT(r) FROM Rental r WHERE r.car.id = :carId AND (r.startDate + r.days day) > :"
						+ TODAY_PARAM, Long.class);
		query.setParameter("carId", carId);
		query.setParameter(TODAY_PARAM, LocalDate.now(clock));

		return query.getSingleResult() > 0;
	}

	@Override
	public Optional<Rental> findActiveById(Long id) {
		TypedQuery<Rental> query = entityManager
				.createQuery("SELECT r FROM Rental r WHERE r.id = :id AND (r.startDate + r.days day) > :" + TODAY_PARAM,
						Rental.class);
		query.setParameter("id", id);
		query.setParameter(TODAY_PARAM, LocalDate.now(clock));

		return query.getResultStream().findFirst();
	}

	@Override
	public Rental save(Rental rental) {
		if (rental.getId() == null) {
			entityManager.persist(rental);
			return rental;
		}

		return entityManager.merge(rental);
	}

	@Override
	public void deleteById(Long id) {
		Rental rental = entityManager.find(Rental.class, id);

		entityManager.remove(rental);
	}

}
