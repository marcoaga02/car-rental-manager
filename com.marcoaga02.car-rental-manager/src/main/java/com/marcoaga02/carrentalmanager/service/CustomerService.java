package com.marcoaga02.carrentalmanager.service;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public interface CustomerService {

	/**
	 * Retrieves all active customers.
	 *
	 * @return the list of active customers, or an empty list if none exist
	 */
	List<CustomerViewModel> getAllCustomers();

	/**
	 * Creates a new customer.
	 *
	 * @param customerViewModel the data of the customer to create; must not be
	 *                          {@code null} and must contain a non-blank tax id
	 *                          code, first name and last name
	 * @return the created customer, including any values assigned by the system
	 *         (e.g. its id)
	 * @throws IllegalArgumentException    if {@code customerViewModel} is
	 *                                     {@code null} or contains invalid data
	 * @throws DuplicateTaxIdCodeException if an active customer with the same tax
	 *                                     id code already exists
	 */
	CustomerViewModel createCustomer(CustomerViewModel customerViewModel);

	/**
	 * Deletes (deactivates) the customer with the given id.
	 *
	 * @param customerId the id of the customer to delete; must not be {@code null}
	 * @throws IllegalArgumentException         if {@code customerId} is
	 *                                          {@code null}
	 * @throws CustomerNotFoundException        if no active customer exists with
	 *                                          the given id
	 * @throws CustomerHasActiveRentalException if the customer has an active rental
	 *                                          in progress
	 */
	void deleteCustomer(Long customerId);

}
