package com.marcoaga02.carrentalmanager.exception;

public class CarAlreadyRentedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CarAlreadyRentedException(Long carId) {
		super("Car with id '" + carId + "' is already rented");
	}

}
