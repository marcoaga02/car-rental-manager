package com.marcoaga02.carrentalmanager.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.marcoaga02.carrentalmanager.exception.CarAlreadyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.RentalNotFoundException;
import com.marcoaga02.carrentalmanager.mapper.RentalMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public class RentalServiceImpl implements RentalService {

	private final TransactionManager transactionManager;

	private final RentalMapper rentalMapper;

	private final Clock clock;

	public RentalServiceImpl(TransactionManager transactionManager, RentalMapper rentalMapper, Clock clock) {
		this.transactionManager = transactionManager;
		this.rentalMapper = rentalMapper;
		this.clock = clock;
	}

	@Override
	public List<RentalViewModel> getAllActiveRentals() {
		return transactionManager
				.doInTransaction(ctx -> ctx.rentalRepository().findAllActive())
				.stream()
				.map(rentalMapper::toViewModel)
				.collect(Collectors.toList());
	}

	@Override
	public RentalViewModel createRental(RentalCreationRequest request) {
		validateCreationInput(request);

		return transactionManager.doInTransaction(ctx -> {
			final Long carId = request.getCarId();
			Car car = ctx.carRepository().findActiveById(carId).orElseThrow(() -> new CarNotFoundException(carId));

			final Long customerId = request.getCustomerId();
			Customer customer = ctx
					.customerRepository()
					.findActiveById(customerId)
					.orElseThrow(() -> new CustomerNotFoundException(customerId));

			if (ctx.rentalRepository().existsActiveByCarId(carId)) {
				throw new CarAlreadyRentedException(carId);
			}

			Rental rental = new Rental(car, customer, LocalDate.now(clock), request.getDays());
			return rentalMapper.toViewModel(ctx.rentalRepository().save(rental));
		});
	}

	private void validateCreationInput(RentalCreationRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request must not be null");
		}
		if (request.getCarId() == null) {
			throw new IllegalArgumentException("carId must not be null");
		}
		if (request.getCustomerId() == null) {
			throw new IllegalArgumentException("customerId must not be null");
		}
		if (request.getDays() == null) {
			throw new IllegalArgumentException("days must not be null");
		}
		if (request.getDays() <= 0) {
			throw new IllegalArgumentException("days must be a positive integer");
		}
	}

	@Override
	public void deleteRental(Long rentalId) {
		if (rentalId == null) {
			throw new IllegalArgumentException("rentalId must not be null");
		}

		transactionManager.doInTransaction(ctx -> {
			ctx.rentalRepository().findActiveById(rentalId).orElseThrow(() -> new RentalNotFoundException(rentalId));

			ctx.rentalRepository().deleteById(rentalId);
			return null;
		});
	}

}
