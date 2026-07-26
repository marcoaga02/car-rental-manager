package com.marcoaga02.carrentalmanager.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public class RentalMapper {

	public RentalViewModel toViewModel(Rental rental) {
		if (rental == null) {
			return null;
		}

		final Car car = rental.getCar();
		final Customer customer = rental.getCustomer();

		final LocalDate endDate = rental.getStartDate().plusDays(rental.getDays());
		final String customerFullname = String
				.format("%s %s", customer.getFirstname(), customer.getLastname());
		final String carDescription = String
				.format("%s %s [%s]", car.getBrand(), car.getModel(), car.getCarPlate());

		final BigDecimal totalAmount = car
				.getDailyRate()
				.multiply(BigDecimal.valueOf(rental.getDays()));

		return new RentalViewModel(rental.getId(), rental.getStartDate(), endDate, rental.getDays(),
				customerFullname, carDescription, totalAmount);
	}

}
