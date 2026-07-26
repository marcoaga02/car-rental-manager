package com.marcoaga02.carrentalmanager.mapper;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarMapper {

	public CarViewModel toViewModel(Car car) {
		if (car == null) {
			return null;
		}

		return new CarViewModel(car.getId(), car.getCarPlate(), car.getBrand(), car.getModel(),
				car.getDailyRate());
	}

	public Car toEntity(CarViewModel carViewModel) {
		if (carViewModel == null) {
			return null;
		}

		return new Car(carViewModel.getCarPlate(), carViewModel.getBrand(), carViewModel.getModel(),
				carViewModel.getDailyRate());
	}

}
