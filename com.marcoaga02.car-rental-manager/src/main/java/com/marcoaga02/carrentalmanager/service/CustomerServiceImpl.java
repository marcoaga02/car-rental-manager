package com.marcoaga02.carrentalmanager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.marcoaga02.carrentalmanager.exception.CustomerHasActiveRentalException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateTaxIdCodeException;
import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public class CustomerServiceImpl implements CustomerService {

	private final TransactionManager transactionManager;

	private final CustomerMapper customerMapper;

	public CustomerServiceImpl(TransactionManager transactionManager, CustomerMapper customerMapper) {
		this.transactionManager = transactionManager;
		this.customerMapper = customerMapper;
	}

	@Override
	public List<CustomerViewModel> getAllCustomers() {
		return transactionManager.doInTransaction(ctx -> ctx.customerRepository().findAllActive()).stream()
				.map(customerMapper::toViewModel).collect(Collectors.toList());
	}

	@Override
	public CustomerViewModel createCustomer(CustomerViewModel customerViewModel) {
		validateCreationInput(customerViewModel);

		return transactionManager.doInTransaction(ctx -> {
			final String taxIdCode = customerViewModel.getTaxIdCode();
			ctx.customerRepository().findActiveByTaxIdCode(taxIdCode).ifPresent(existingCustomer -> {
				throw new DuplicateTaxIdCodeException(taxIdCode);
			});

			Customer toSave = customerMapper.toEntity(customerViewModel);
			return customerMapper.toViewModel(ctx.customerRepository().save(toSave));
		});
	}

	private void validateCreationInput(CustomerViewModel customerViewModel) {
		if (customerViewModel == null) {
			throw new IllegalArgumentException("customerViewModel must not be null");
		}
		if (StringUtils.isBlank(customerViewModel.getTaxIdCode())) {
			throw new IllegalArgumentException("taxIdCode must not be blank");
		}
		if (StringUtils.isBlank(customerViewModel.getFirstname())) {
			throw new IllegalArgumentException("firstname must not be blank");
		}
		if (StringUtils.isBlank(customerViewModel.getLastname())) {
			throw new IllegalArgumentException("lastname must not be blank");
		}
	}

	@Override
	public void deleteCustomer(Long customerId) {
		if (customerId == null) {
			throw new IllegalArgumentException("customerId must not be null");
		}

		transactionManager.doInTransaction(ctx -> {
			Customer customer = ctx.customerRepository().findActiveById(customerId)
					.orElseThrow(() -> new CustomerNotFoundException(customerId));

			if (ctx.rentalRepository().existsActiveByCustomerId(customerId)) {
				throw new CustomerHasActiveRentalException(customerId);
			}

			customer.setDeleted(true);
			ctx.customerRepository().save(customer);

			return null;
		});
	}

}
