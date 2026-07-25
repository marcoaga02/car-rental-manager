package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class BaseEntityTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String A_STRING = "aString";

	private Car entity;

	@Before
	public void setUp() {
		entity = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
	}

	@Test
	public void testGetUuidIsNotNullByDefault() {
		assertThat(entity.getUuid()).isNotNull();
	}

	@Test
	public void testGetIdIsNullWhenNotPersisted() {
		assertThat(entity.getId()).isNull();
	}

	@Test
	public void testEqualsWhenSameReferenceReturnTrue() {
		assertThat(entity.equals(entity)).isTrue();
	}

	@Test
	public void testEqualsWhenObjectIsNullReturnFalse() {
		assertThat(entity.equals(null)).isFalse();
	}

	@Test
	public void testEqualsWhenObjectIsNotABaseEntityReturnFalse() {
		assertThat(entity.equals(A_STRING)).isFalse();
	}

	@Test
	public void testEqualsWhenUuidIsTheSameReturnTrue() {
		Car other = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		other.setUuid(entity.getUuid());

		assertThat(entity.equals(other)).isTrue();
	}

	@Test
	public void testEqualsWhenUuidIsDifferentReturnFalse() {
		Car other = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		assertThat(entity.equals(other)).isFalse();
	}

	@Test
	public void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		Car other = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		other.setUuid(entity.getUuid());

		assertThat(other).hasSameHashCodeAs(entity);
	}
}
