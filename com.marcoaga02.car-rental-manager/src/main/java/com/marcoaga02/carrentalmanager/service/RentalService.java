package com.marcoaga02.carrentalmanager.service;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public interface RentalService {

	/**
	 * Retrieves all active rentals.
	 *
	 * @return the list of active rentals, or an empty list if none exist
	 */
	List<RentalViewModel> getAllActiveRentals();

	/**
	 * Creates a new rental for the given car and customer, starting on the current
	 * date.
	 *
	 * @param request the rental creation data; must not be {@code null} and must
	 *                contain a non-null car id, a non-null customer id, and a
	 *                positive number of days
	 * @return the created rental, including any values assigned by the system (e.g.
	 *         its id)
	 * @throws IllegalArgumentException  if {@code request} is {@code null} or
	 *                                   contains invalid data
	 * @throws CarNotFoundException      if no active car exists with the given car
	 *                                   id
	 * @throws CustomerNotFoundException if no active customer exists with the given
	 *                                   customer id
	 * @throws CarAlreadyRentedException if the car already has an active rental
	 */
	RentalViewModel createRental(RentalCreationRequest request);

	/**
	 * Deletes the rental with the given id.
	 *
	 * @param rentalId the id of the rental to delete; must not be {@code null}
	 * @throws IllegalArgumentException if {@code rentalId} is {@code null}
	 * @throws RentalNotFoundException if no active rental exists with the given id
	 */
	void deleteRental(Long rentalId);

}
