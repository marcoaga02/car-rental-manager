package com.marcoaga02.carrentalmanager.repository;

import java.util.List;

import com.marcoaga02.carrentalmanager.model.Rental;

public interface RentalRepository {
	
	List<Rental> findAllActive();

}
