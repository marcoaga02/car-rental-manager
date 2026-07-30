package com.marcoaga02.carrentalmanager.exception;

public class CustomerHasActiveRentalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CustomerHasActiveRentalException(Long customerId) {
		super("Customer with id '" + customerId + "' has an active rental and cannot be deleted");
	}

}
