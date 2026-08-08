package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerTest {

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private Customer customer, anotherCustomer;

	@BeforeEach
	void setUp() {
		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTrue() {
		anotherCustomer.setUuid(customer.getUuid());

		assertThat(customer.equals(anotherCustomer)).isTrue();
	}

	@Test
	void testEqualsWhenUuidIsDifferentReturnFalse() {
		assertThat(customer.equals(anotherCustomer)).isFalse();
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		anotherCustomer.setUuid(customer.getUuid());

		assertThat(anotherCustomer).hasSameHashCodeAs(customer);
	}

}
