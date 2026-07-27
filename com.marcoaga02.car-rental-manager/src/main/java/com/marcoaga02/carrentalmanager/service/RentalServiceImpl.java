package com.marcoaga02.carrentalmanager.service;

import java.util.List;
import java.util.stream.Collectors;

import com.marcoaga02.carrentalmanager.mapper.RentalMapper;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public class RentalServiceImpl implements RentalService {

	private TransactionManager transactionManager;

	private RentalMapper rentalMapper;

	public RentalServiceImpl(TransactionManager transactionManager, RentalMapper rentalMapper) {
		this.transactionManager = transactionManager;
		this.rentalMapper = rentalMapper;
	}

	@Override
	public List<RentalViewModel> getAllActiveRentals() {
		return transactionManager
				.doInTransaction(ctx -> ctx.rentalRepository().findAllActive())
				.stream()
				.map(rentalMapper::toViewModel)
				.collect(Collectors.toList());
	}

}
