package com.marcoaga02.carrentalmanager.service;

import java.util.List;

import com.marcoaga02.carrentalmanager.exception.CarCurrentlyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateCarPlateException;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public interface CarService {

	/**
	 * Retrieves all active cars.
	 *
	 * @return the list of active cars, or an empty list if none exist
	 */
	List<CarViewModel> getAllCars();

	/**
	 * Creates a new car.
	 *
	 * @param carViewModel the data of the car to create; must not be {@code null}
	 *                     and must contain a non-blank car plate, brand and model,
	 *                     and a positive daily rate
	 * @return the created car, including any values assigned by the system (e.g.
	 *         its id)
	 * @throws IllegalArgumentException   if {@code carViewModel} is {@code null} or
	 *                                    contains invalid data
	 * @throws DuplicateCarPlateException if an active car with the same car plate
	 *                                    already exists
	 */
	CarViewModel createCar(CarViewModel carViewModel);

	/**
	 * Deletes (deactivates) the car with the given id.
	 *
	 * @param carId the id of the car to delete; must not be {@code null}
	 * @throws IllegalArgumentException    if {@code carId} is {@code null}
	 * @throws CarNotFoundException        if no active car exists with the given id
	 * @throws CarCurrentlyRentedException if the car is currently involved in an
	 *                                     active rental
	 */
	void deleteCar(Long carId);

}
