package com.marcoaga02.carrentalmanager.controller.swing;

import com.marcoaga02.carrentalmanager.exception.CarCurrentlyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateCarPlateException;
import com.marcoaga02.carrentalmanager.service.CarService;
import com.marcoaga02.carrentalmanager.view.CarView;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarController {

	private final CarService carService;

	private final CarView carView;

	public CarController(CarService carService, CarView carView) {
		this.carService = carService;
		this.carView = carView;
	}

	public void getAllCars() {
		carView.showAllCars(carService.getAllCars());
	}

	public void createCar(CarViewModel request) {
		try {
			carService.createCar(request);
			this.getAllCars();
		} catch (IllegalArgumentException | DuplicateCarPlateException e) {
			carView.showError(e.getMessage());
		}
	}
	
	public void deleteCar(Long carId) {
		try {
			carService.deleteCar(carId);
			this.getAllCars();
		} catch (IllegalArgumentException | CarNotFoundException | CarCurrentlyRentedException e) {
			carView.showError(e.getMessage());
		}
	}

}
