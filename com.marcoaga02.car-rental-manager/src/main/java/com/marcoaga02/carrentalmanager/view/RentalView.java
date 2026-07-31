package com.marcoaga02.carrentalmanager.view;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public interface RentalView extends BaseView {

	void showAllRentals(List<RentalViewModel> rentals);
	
}
