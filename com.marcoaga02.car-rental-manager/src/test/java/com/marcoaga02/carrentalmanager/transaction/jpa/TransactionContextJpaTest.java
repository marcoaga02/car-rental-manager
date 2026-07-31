package com.marcoaga02.carrentalmanager.transaction.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.repository.jpa.CarRepositoryJpa;
import com.marcoaga02.carrentalmanager.repository.jpa.CustomerRepositoryJpa;
import com.marcoaga02.carrentalmanager.repository.jpa.RentalRepositoryJpa;
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;

class TransactionContextJpaTest extends BasePostgresTest {

	private Clock clock;

	private TransactionContextJpa context;

	@BeforeEach
	void setUp() {
		clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
		context = new TransactionContextJpa(entityManager, clock);
	}

	@Test
	void testCarRepositoryIsNotNull() {
		assertThat(context.carRepository()).isNotNull();
	}

	@Test
	void testCarRepositoryIsOfTheCorrectType() {
		assertThat(context.carRepository()).isInstanceOf(CarRepositoryJpa.class);
	}

	@Test
	void testCarRepositoryReturnsAlwaysTheSameInstance() {
		assertThat(context.carRepository()).isSameAs(context.carRepository());
	}

	@Test
	void testCustomerRepositoryIsNotNull() {
		assertThat(context.customerRepository()).isNotNull();
	}

	@Test
	void testCustomerRepositoryIsOfTheCorrectType() {
		assertThat(context.customerRepository()).isInstanceOf(CustomerRepositoryJpa.class);
	}

	@Test
	void testCustomerRepositoryReturnsAlwaysTheSameInstance() {
		assertThat(context.customerRepository()).isSameAs(context.customerRepository());
	}

	@Test
	void testRentalRepositoryIsNotNull() {
		assertThat(context.rentalRepository()).isNotNull();
	}

	@Test
	void testRentalRepositoryIsOfTheCorrectType() {
		assertThat(context.rentalRepository()).isInstanceOf(RentalRepositoryJpa.class);
	}

	@Test
	void testRentalRepositoryReturnsAlwaysTheSameInstance() {
		assertThat(context.rentalRepository()).isSameAs(context.rentalRepository());
	}

}
