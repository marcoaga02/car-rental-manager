package com.marcoaga02.carrentalmanager.repository;

import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Rental;

public interface RentalRepository {

	List<Rental> findAllActive();

	Optional<Rental> findActiveByCarId(Long carId);

	Rental save(Rental rental);

}
