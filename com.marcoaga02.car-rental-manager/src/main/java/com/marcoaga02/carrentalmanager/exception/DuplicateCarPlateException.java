package com.marcoaga02.carrentalmanager.exception;

public class DuplicateCarPlateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateCarPlateException(String carPlate) {
		super("A car with carPlate '" + carPlate + "' already exists");
	}

}
