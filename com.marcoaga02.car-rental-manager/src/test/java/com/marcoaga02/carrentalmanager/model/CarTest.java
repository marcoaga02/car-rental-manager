package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class CarTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private Car car;

	@Before
	public void setUp() {
		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
	}

	@Test
	public void testEqualsWhenUuidIsTheSameReturnTrue() {
		Car other = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		other.setUuid(car.getUuid());

		assertThat(car.equals(other)).isTrue();
	}

	@Test
	public void testEqualsWhenUuidIsDifferentReturnFalse() {
		Car other = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		assertThat(car.equals(other)).isFalse();
	}

	@Test
	public void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		Car other = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		other.setUuid(car.getUuid());

		assertThat(other).hasSameHashCodeAs(car);
	}

}
