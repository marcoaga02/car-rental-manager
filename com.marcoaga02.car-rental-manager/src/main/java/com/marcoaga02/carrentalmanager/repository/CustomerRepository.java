package com.marcoaga02.carrentalmanager.repository;

import java.util.List;

import com.marcoaga02.carrentalmanager.model.Customer;

public interface CustomerRepository {
	
	List<Customer> findAllActive();

}
