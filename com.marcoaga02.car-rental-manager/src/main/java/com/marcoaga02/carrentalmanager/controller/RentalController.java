package com.marcoaga02.carrentalmanager.controller;

import com.marcoaga02.carrentalmanager.exception.CarAlreadyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.RentalNotFoundException;
import com.marcoaga02.carrentalmanager.service.RentalService;
import com.marcoaga02.carrentalmanager.view.RentalView;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;

public class RentalController {

	private final RentalService rentalService;

	private final RentalView rentalView;

	public RentalController(RentalService rentalService, RentalView rentalView) {
		this.rentalService = rentalService;
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

}
