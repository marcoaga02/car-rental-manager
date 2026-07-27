package com.marcoaga02.carrentalmanager.service;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public interface RentalService {

	List<RentalViewModel> getAllActiveRentals();

	RentalViewModel createRental(RentalCreationRequest request);

	void deleteRental(Long rentalId);

}
