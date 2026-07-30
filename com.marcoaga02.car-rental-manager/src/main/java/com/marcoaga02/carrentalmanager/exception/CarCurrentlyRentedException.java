package com.marcoaga02.carrentalmanager.exception;

public class CarCurrentlyRentedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CarCurrentlyRentedException(Long carId) {
		super("Car with id '" + carId + "' is currently rented and cannot be deleted");
	}

}
