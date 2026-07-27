package com.marcoaga02.carrentalmanager.transaction;

import com.marcoaga02.carrentalmanager.repository.CarRepository;
import com.marcoaga02.carrentalmanager.repository.CustomerRepository;
import com.marcoaga02.carrentalmanager.repository.RentalRepository;

public interface TransactionContext {

	CarRepository carRepository();

	CustomerRepository customerRepository();
	
	RentalRepository rentalRepository();

}
