package com.marcoaga02.carrentalmanager.repository;

import java.util.List;

import com.marcoaga02.carrentalmanager.model.Car;

public interface CarRepository {
	
	List<Car> findAllActive();

}
