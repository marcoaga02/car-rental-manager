package com.marcoaga02.carrentalmanager.service;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public interface CustomerService {

	List<CustomerViewModel> getAllCustomers();

	CustomerViewModel createCustomer(CustomerViewModel customerViewModel);

	void deleteCustomer(Long customerId);

}
