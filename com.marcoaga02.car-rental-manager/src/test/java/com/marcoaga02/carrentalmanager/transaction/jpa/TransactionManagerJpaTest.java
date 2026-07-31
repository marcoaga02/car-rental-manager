package com.marcoaga02.carrentalmanager.transaction.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;

class TransactionManagerJpaTest extends BasePostgresTest {

	private TransactionManagerJpa transactionManager;
	private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

	@BeforeEach
	void setUp() {
		transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);
	}

	@Test
	void testConstructorWhenEntityManagerFactoryIsNullThrowsNullPointerException() {
		assertThatThrownBy(() -> new TransactionManagerJpa(null, fixedClock)).isInstanceOf(NullPointerException.class);
	}

	@Test
	void testConstructorWhenClockIsNullThrowsNullPointerException() {
		assertThatThrownBy(() -> new TransactionManagerJpa(entityManagerFactory, null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void testDoInTransactionWhenCodeSucceedsReturnsResult() {
		final String success = "success";
		String result = transactionManager.doInTransaction(context -> success);

		assertThat(result).isEqualTo(success);
	}

	@Test
	void testDoInTransactionWhenCodeSucceedsReturnsNull() {
		Object result = transactionManager.doInTransaction(context -> null);

		assertThat(result).isNull();
	}

	@Test
	void testDoInTransactionWhenCalledProvidesNonNullContext() {
		transactionManager.doInTransaction(context -> {
			assertThat(context).isNotNull();
			return null;
		});
	}

	@Test
	void testDoInTransactionWhenCalledProvidesAllRepositories() {
		transactionManager.doInTransaction(context -> {
			assertThat(context.carRepository()).isNotNull();
			assertThat(context.customerRepository()).isNotNull();
			assertThat(context.rentalRepository()).isNotNull();
			return null;
		});
	}

	@Test
	void testDoInTransactionWhenCodeThrowsRuntimeExceptionPropagatesException() {
		final String errorMessage = "simulated failure";
		assertThatThrownBy(() -> transactionManager.doInTransaction(context -> {
			throw new RuntimeException(errorMessage);
		})).isInstanceOf(RuntimeException.class).hasMessage(errorMessage);
	}

	@Test
	void testDoInTransactionWhenCodeThrowsExceptionEntityManagerIsClosed() {
		final String errorMessage = "simulated failure";
		assertThatThrownBy(() -> transactionManager.doInTransaction(context -> {
			throw new RuntimeException(errorMessage);
		})).isInstanceOf(RuntimeException.class).hasMessage(errorMessage);

		final String recovered = "recovered";
		String result = transactionManager.doInTransaction(context -> recovered);
		assertThat(result).isEqualTo(recovered);
	}

	@Test
	void testDoInTransactionWhenCalledMultipleTimesUsesIndependentContexts() {
		TransactionContext firstContext = transactionManager.doInTransaction(context -> context);
		TransactionContext secondContext = transactionManager.doInTransaction(context -> context);

		assertThat(firstContext).isNotSameAs(secondContext);
	}

}
