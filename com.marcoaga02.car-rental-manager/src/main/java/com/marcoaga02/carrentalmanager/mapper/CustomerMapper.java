package com.marcoaga02.carrentalmanager.mapper;

import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public class CustomerMapper {

	public CustomerViewModel toViewModel(Customer customer) {
		if (customer == null) {
			return null;
		}

		return new CustomerViewModel(customer.getId(), customer.getTaxIdCode(),
				customer.getFirstname(), customer.getLastname());
	}

	public Customer toEntity(CustomerViewModel customerViewModel) {
		if (customerViewModel == null) {
			return null;
		}

		return new Customer(customerViewModel.getTaxIdCode(), customerViewModel.getFirstname(),
				customerViewModel.getLastname());
	}

}
