package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class CustomerTest {

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private Customer customer;

	@Before
	public void setUp() {
		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
	}

	@Test
	public void testEqualsWhenUuidIsTheSameReturnTrue() {
		Customer other = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		other.setUuid(customer.getUuid());

		assertThat(customer.equals(other)).isTrue();
	}

	@Test
	public void testEqualsWhenUuidIsDifferentReturnFalse() {
		Customer other = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);

		assertThat(customer.equals(other)).isFalse();
	}

	@Test
	public void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		Customer other = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		other.setUuid(customer.getUuid());

		assertThat(other).hasSameHashCodeAs(customer);
	}

}
