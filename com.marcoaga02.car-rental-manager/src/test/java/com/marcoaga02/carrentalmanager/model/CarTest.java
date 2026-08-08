package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private Car car, anotherCar;

	@BeforeEach
	void setUp() {
		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		anotherCar = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTrue() {
		anotherCar.setUuid(car.getUuid());

		assertThat(car.equals(anotherCar)).isTrue();
	}

	@Test
	void testEqualsWhenUuidIsDifferentReturnFalse() {
		assertThat(car.equals(anotherCar)).isFalse();
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		anotherCar.setUuid(car.getUuid());

		assertThat(anotherCar).hasSameHashCodeAs(car);
	}

}
