package com.marcoaga02.carrentalmanager.view;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public interface RentalView extends BaseView {

	void showAllRentals(List<RentalViewModel> rentals);
	
	void showAvailableCars(List<CarViewModel> cars);
	
	void showAvailableCustomers(List<CustomerViewModel> customers);

}
