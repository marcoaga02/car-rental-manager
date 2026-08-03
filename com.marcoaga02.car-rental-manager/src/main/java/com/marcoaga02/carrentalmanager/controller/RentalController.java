package com.marcoaga02.carrentalmanager.controller;

import com.marcoaga02.carrentalmanager.exception.CarAlreadyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.RentalNotFoundException;
import com.marcoaga02.carrentalmanager.service.CarService;
import com.marcoaga02.carrentalmanager.service.CustomerService;
import com.marcoaga02.carrentalmanager.service.RentalService;
import com.marcoaga02.carrentalmanager.view.RentalView;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;

public class RentalController {

	private final RentalService rentalService;
	private final CarService carService;
	private final CustomerService customerService;

	private final RentalView rentalView;

	public RentalController(RentalService rentalService, CarService carService, CustomerService customerService,
			RentalView rentalView) {
		this.rentalService = rentalService;
		this.carService = carService;
		this.customerService = customerService;
		this.rentalView = rentalView;
	}

	public void getAllActiveRentals() {
		rentalView.showAllRentals(rentalService.getAllActiveRentals());
	}

	public void createRental(RentalCreationRequest request) {
		try {
			rentalService.createRental(request);
			rentalView.clearFields();
			this.getAllActiveRentals();
		} catch (IllegalArgumentException | CarNotFoundException | CustomerNotFoundException
				| CarAlreadyRentedException e) {
			rentalView.showError(e.getMessage());
		}
	}

	public void deleteRental(Long rentalId) {
		try {
			rentalService.deleteRental(rentalId);
			this.getAllActiveRentals();
		} catch (IllegalArgumentException | RentalNotFoundException e) {
			rentalView.showError(e.getMessage());
		}
	}
	
	public void loadAvailableCars() {
		rentalView.showAvailableCars(carService.getAvailableCars());
	}

	public void loadAvailableCustomers() {
		rentalView.showAvailableCustomers(customerService.getAllCustomers());
	}

}
