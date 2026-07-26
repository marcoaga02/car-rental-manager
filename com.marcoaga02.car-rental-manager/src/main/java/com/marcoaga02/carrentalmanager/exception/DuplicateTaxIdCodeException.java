package com.marcoaga02.carrentalmanager.exception;

public class DuplicateTaxIdCodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateTaxIdCodeException(String taxIdCode) {
		super("A customer with taxIdCode '" + taxIdCode + "' already exists");
	}

}
