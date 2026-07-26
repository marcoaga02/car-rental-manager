package com.marcoaga02.carrentalmanager.transaction;

import com.marcoaga02.carrentalmanager.repository.CarRepository;
import com.marcoaga02.carrentalmanager.repository.CustomerRepository;

public interface TransactionContext {
	
	CarRepository carRepository();
	
	CustomerRepository customerRepository();
}
