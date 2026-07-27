package com.marcoaga02.carrentalmanager.exception;

public class RentalNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RentalNotFoundException(Long id) {
		super("Rental with id '" + id + "' not found");
	}

}
