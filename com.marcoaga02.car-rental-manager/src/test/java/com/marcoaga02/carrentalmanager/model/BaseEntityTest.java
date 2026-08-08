package com.marcoaga02.carrentalmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BaseEntityTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String A_STRING = "aString";

	private Car entity, anotherEntity;

	@BeforeEach
	void setUp() {
		entity = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		anotherEntity = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
	}

	@Test
	void testGetUuidIsNotNullByDefault() {
		assertThat(entity.getUuid()).isNotNull();
	}

	@Test
	void testGetIdIsNullWhenNotPersisted() {
		assertThat(entity.getId()).isNull();
	}

	@Test
	void testEqualsWhenSameReferenceReturnTrue() {
		assertThat(entity.equals(entity)).isTrue();
	}

	@Test
	void testEqualsWhenObjectIsNullReturnFalse() {
		assertThat(entity.equals(null)).isFalse();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testEqualsWhenObjectIsNotABaseEntityReturnFalse() {
		assertThat(entity.equals(A_STRING)).isFalse();
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTrue() {
		anotherEntity.setUuid(entity.getUuid());

		assertThat(entity.equals(anotherEntity)).isTrue();
	}

	@Test
	void testEqualsWhenUuidIsDifferentReturnFalse() {
		assertThat(entity.equals(anotherEntity)).isFalse();
	}

	@Test
	void testEqualsWhenUuidIsTheSameReturnTheSameHashCode() {
		anotherEntity.setUuid(entity.getUuid());

		assertThat(anotherEntity).hasSameHashCodeAs(entity);
	}
}
