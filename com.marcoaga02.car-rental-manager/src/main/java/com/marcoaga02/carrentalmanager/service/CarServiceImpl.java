package com.marcoaga02.carrentalmanager.service;

import java.util.List;
import java.util.stream.Collectors;

import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarServiceImpl implements CarService {

	private TransactionManager transactionManager;
	private CarMapper carMapper;

	public CarServiceImpl(TransactionManager transactionManager, CarMapper carMapper) {
		this.transactionManager = transactionManager;
		this.carMapper = carMapper;
	}

	@Override
	public List<CarViewModel> getAllCars() {
		List<Car> cars = transactionManager.doInTransaction(ctx -> ctx.carRepository().findAllActive());

		return cars.stream().map(carMapper::toViewModel).collect(Collectors.toList());
	}

}
