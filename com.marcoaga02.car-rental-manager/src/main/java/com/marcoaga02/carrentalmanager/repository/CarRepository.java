package com.marcoaga02.carrentalmanager.repository;

import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Car;

public interface CarRepository {

	List<Car> findAllActive();

	Optional<Car> findActiveWithSameCarPlate(String carPlate);

	Optional<Car> findActiveById(Long id);

	Car save(Car car);

}
