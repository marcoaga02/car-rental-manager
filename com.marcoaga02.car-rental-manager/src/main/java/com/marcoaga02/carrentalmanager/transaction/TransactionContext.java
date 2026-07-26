package com.marcoaga02.carrentalmanager.transaction;

import com.marcoaga02.carrentalmanager.repository.CarRepository;

public interface TransactionContext {
	CarRepository carRepository();
}
