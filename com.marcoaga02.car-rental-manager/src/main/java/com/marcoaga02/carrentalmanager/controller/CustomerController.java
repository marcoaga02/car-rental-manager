package com.marcoaga02.carrentalmanager.controller;

import com.marcoaga02.carrentalmanager.exception.CustomerHasActiveRentalException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateTaxIdCodeException;
import com.marcoaga02.carrentalmanager.service.CustomerService;
import com.marcoaga02.carrentalmanager.view.CustomerView;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public class CustomerController {

	private final CustomerService customerService;

	private final CustomerView customerView;

	public CustomerController(CustomerService customerService, CustomerView customerView) {
		this.customerService = customerService;
		this.customerView = customerView;
	}

	public void getAllCustomers() {
		customerView.showAllCustomers(customerService.getAllCustomers());
	}

	public void createCustomer(CustomerViewModel request) {
		try {
			customerService.createCustomer(request);
			this.getAllCustomers();
		} catch (IllegalArgumentException | DuplicateTaxIdCodeException e) {
			customerView.showError(e.getMessage());
		}
	}

	public void deleteCustomer(Long customerId) {
		try {
			customerService.deleteCustomer(customerId);
			this.getAllCustomers();
		} catch (IllegalArgumentException | CustomerNotFoundException | CustomerHasActiveRentalException e) {
			customerView.showError(e.getMessage());
		}
	}

}
