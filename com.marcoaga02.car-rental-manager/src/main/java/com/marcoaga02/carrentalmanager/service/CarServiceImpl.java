package com.marcoaga02.carrentalmanager.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateCarPlateException;
import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarServiceImpl implements CarService {

	private final TransactionManager transactionManager;

	private final CarMapper carMapper;

	public CarServiceImpl(TransactionManager transactionManager, CarMapper carMapper) {
		this.transactionManager = transactionManager;
		this.carMapper = carMapper;
	}

	@Override
	public List<CarViewModel> getAllCars() {
		return transactionManager
				.doInTransaction(ctx -> ctx
						.carRepository()
						.findAllActive()
						.stream()
						.map(carMapper::toViewModel)
						.collect(Collectors.toList()));
	}

	@Override
	public CarViewModel createCar(CarViewModel carViewModel) {
		validateCreationInput(carViewModel);

		return transactionManager.doInTransaction(ctx -> {
			final String carPlate = carViewModel.getCarPlate();
			ctx.carRepository().findActiveByCarPlate(carPlate).ifPresent(existingCar -> {
				throw new DuplicateCarPlateException(carPlate);
			});

			Car toSave = carMapper.toEntity(carViewModel);
			return carMapper.toViewModel(ctx.carRepository().save(toSave));
		});
	}

	private void validateCreationInput(CarViewModel carViewModel) {
		if (carViewModel == null) {
			throw new IllegalArgumentException("carViewModel must not be null");
		}
		if (StringUtils.isBlank(carViewModel.getCarPlate())) {
			throw new IllegalArgumentException("carPlate must not be blank");
		}
		if (StringUtils.isBlank(carViewModel.getBrand())) {
			throw new IllegalArgumentException("brand must not be blank");
		}
		if (StringUtils.isBlank(carViewModel.getModel())) {
			throw new IllegalArgumentException("model must not be blank");
		}
		if (carViewModel.getDailyRate() == null) {
			throw new IllegalArgumentException("dailyRate must not be null");
		}
		if (carViewModel.getDailyRate().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("dailyRate must be positive");
		}
	}

	@Override
	public void deleteCar(Long carId) {
		if (carId == null) {
			throw new IllegalArgumentException("carId must not be null");
		}

		transactionManager.doInTransaction(ctx -> {
			Car car = ctx.carRepository().findActiveById(carId).orElseThrow(() -> new CarNotFoundException(carId));

			car.setDeleted(true);
			ctx.carRepository().save(car);

			return null;
		});
	}

}
