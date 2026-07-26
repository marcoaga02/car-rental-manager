package com.marcoaga02.carrentalmanager.exception;

public class CarNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CarNotFoundException(Long id) {
		super("Car with id '" + id + "' not found");
	}

}
