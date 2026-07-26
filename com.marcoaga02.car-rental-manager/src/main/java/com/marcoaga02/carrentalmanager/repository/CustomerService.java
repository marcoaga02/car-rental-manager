package com.marcoaga02.carrentalmanager.repository;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public interface CustomerService {
	
	List<CustomerViewModel> getAllCustomers();

}
