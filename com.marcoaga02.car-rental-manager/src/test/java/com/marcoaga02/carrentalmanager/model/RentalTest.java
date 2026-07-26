package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RentalTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final LocalDate A_START_DATE = LocalDate.of(2026, Month.JULY, 25);
	private static final Integer A_NUMBER_OF_DAYS = 6;

	private Rental rental;

	@BeforeEach
	void setUp() {
		Car car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		Customer customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		rental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTrue() {
		Rental other = new Rental(rental.getCar(), rental.getCustomer(), A_START_DATE,
				A_NUMBER_OF_DAYS);
		other.setUuid(rental.getUuid());

		assertThat(rental.equals(other)).isTrue();
	}

	@Test
	void testEqualsWhenUuidIsDifferentReturnFalse() {
		Rental other = new Rental(rental.getCar(), rental.getCustomer(), A_START_DATE,
				A_NUMBER_OF_DAYS);

		assertThat(rental.equals(other)).isFalse();
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		Rental other = new Rental(rental.getCar(), rental.getCustomer(), A_START_DATE,
				A_NUMBER_OF_DAYS);
		other.setUuid(rental.getUuid());

		assertThat(other).hasSameHashCodeAs(rental);
	}

}
