package com.marcoaga02.carrentalmanager.view;

import java.util.List;

import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public interface CustomerView extends BaseView {

	void showAllCustomers(List<CustomerViewModel> customers);

}
