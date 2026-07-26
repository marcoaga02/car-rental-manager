package com.marcoaga02.carrentalmanager.service;

import java.util.List;
import java.util.stream.Collectors;

import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.repository.CustomerService;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public class CustomerServiceImpl implements CustomerService {

	private TransactionManager transactionManager;
	private CustomerMapper customerMapper;

	public CustomerServiceImpl(TransactionManager transactionManager,
			CustomerMapper customerMapper) {
		this.transactionManager = transactionManager;
		this.customerMapper = customerMapper;
	}

	@Override
	public List<CustomerViewModel> getAllCustomers() {
		return transactionManager
				.doInTransaction(ctx -> ctx.customerRepository().findAllActive())
				.stream()
				.map(customerMapper::toViewModel)
				.collect(Collectors.toList());
	}

}
