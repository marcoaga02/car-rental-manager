package com.marcoaga02.carrentalmanager.service;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public interface CarService {
	
	List<CarViewModel> getAllCars();
	
	CarViewModel createCar(CarViewModel carViewModel);
	
}
