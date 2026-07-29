package com.marcoaga02.carrentalmanager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

class CustomerMapperTest {

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";
	private static final Long AN_ID = 10L;

	private CustomerMapper customerMapper;

	@BeforeEach
	void setUp() {
		customerMapper = new CustomerMapper();
	}

	@Test
	void testToViewModelWhenInputIsNullReturnNull() {
		assertThat(customerMapper.toViewModel(null)).isNull();
	}

	@Test
	void testToViewModelWhenInputIsValidReturnTheCorrectMapping() {
		Customer customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);

		CustomerViewModel result = customerMapper.toViewModel(customer);

		assertThat(result.getId()).isNull();
		assertThat(result.getTaxIdCode()).isEqualTo(A_TAX_ID_CODE);
		assertThat(result.getFirstname()).isEqualTo(A_FIRSTNAME);
		assertThat(result.getLastname()).isEqualTo(A_LASTNAME);
	}

	@Test
	void testToEntityWhenInputIsNullReturnNull() {
		assertThat(customerMapper.toEntity(null)).isNull();
	}

	@Test
	void testToEntityWhenInputIsValidReturnTheCorrectMapping() {
		CustomerViewModel customerViewModel = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);

		Customer result = customerMapper.toEntity(customerViewModel);

		assertThat(result.getId()).isNull();
		assertThat(result.getTaxIdCode()).isEqualTo(A_TAX_ID_CODE);
		assertThat(result.getFirstname()).isEqualTo(A_FIRSTNAME);
		assertThat(result.getLastname()).isEqualTo(A_LASTNAME);
		assertThat(result.getDeleted()).isFalse();
	}

}
