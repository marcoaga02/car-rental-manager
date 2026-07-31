package com.marcoaga02.carrentalmanager.controller;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public interface CarView {
	
	void showAllCars(List<CarViewModel> cars);
	
	void showError(String errorMessage);

}
