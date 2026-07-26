package com.marcoaga02.carrentalmanager.repository;

import java.util.List;
import java.util.Optional;

import com.marcoaga02.carrentalmanager.model.Customer;

public interface CustomerRepository {

	List<Customer> findAllActive();

	Optional<Customer> findActiveByTaxIdCode(String taxIdCode);

	Optional<Customer> findActiveById(Long id);

	Customer save(Customer customer);

}
