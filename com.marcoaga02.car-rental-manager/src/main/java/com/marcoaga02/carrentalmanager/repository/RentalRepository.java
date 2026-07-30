package com.marcoaga02.carrentalmanager.repository;

import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Rental;

public interface RentalRepository {

	List<Rental> findAllActive();

	boolean existsActiveByCarId(Long carId);

	boolean existsActiveByCustomerId(Long customerId);

	Optional<Rental> findActiveById(Long id);

	Rental save(Rental rental);

	void deleteById(Long id);

}
