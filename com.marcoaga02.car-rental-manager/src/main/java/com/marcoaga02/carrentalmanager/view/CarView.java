package com.marcoaga02.carrentalmanager.view;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public interface CarView extends BaseView {
	
	void showAllCars(List<CarViewModel> cars);

}
