package com.marcoaga02.carrentalmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.marcoaga02.carrentalmanager.model.Car;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
class CarRepositoryJpaTest {

	private static final Long AN_ID = 10L;

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";

	private static final String A_BRAND = "aBrand";
	private static final String ANOTHER_BRAND = "anotherBrand";

	private static final String A_MODEL = "aModel";
	private static final String ANOTHER_MODEL = "anotherModel";

	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	@SuppressWarnings("resource")
	@Container
	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.4")
			.withDatabaseName("carrental_test")
			.withUsername("test")
			.withPassword("test");

	private EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;

	private CarRepositoryJpa carRepository;

	@BeforeEach
	void baseSetUp() {
		Map<String, String> properties = new HashMap<>();
		properties.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
		properties.put("jakarta.persistence.jdbc.user", postgres.getUsername());
		properties.put("jakarta.persistence.jdbc.password", postgres.getPassword());

		entityManagerFactory = Persistence.createEntityManagerFactory("test-pu", properties);
		entityManager = entityManagerFactory.createEntityManager();

		carRepository = new CarRepositoryJpa(entityManager);
	}

	@AfterEach
	void baseTearDown() {
		if (entityManager != null) {
			entityManager.close();
		}
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

	@Nested
	class FindAllActive {

		@Test
		void testFindAllActiveWhenThereAreNoCarsAtAllReturnsAnEmptyList() {
			List<Car> result = carRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenThereIsADeletedCarReturnsAnEmptyList() {
			persistDeletedCar();

			List<Car> result = carRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenThereIsOnlyOneActiveCarReturnAListWithASingleElement() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			persistDeletedCar();

			List<Car> result = carRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(car);
		}

		@Test
		void testFindAllActiveWhenThereAreMultipleActiveCarsReturnAListWithAllActiveElements() {
			Car firstCar = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Car secondCar = persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
			persistDeletedCar();

			List<Car> result = carRepository.findAllActive();

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(firstCar, secondCar);
		}

	}

	@Nested
	class FindActiveByCarPlate {

		@Test
		void testFindActiveByCarPlateWhenThereAreNoCarsReturnsEmptyOptional() {
			Optional<Car> result = carRepository.findActiveByCarPlate(A_CAR_PLATE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarPlateWhenCarIsDeletedReturnsEmptyOptional() {
			Car deletedCar = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			deletedCar.setDeleted(true);
			persistCar(deletedCar);

			Optional<Car> result = carRepository.findActiveByCarPlate(A_CAR_PLATE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarPlateWhenCarIsActiveReturnsOptionalWithCar() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

			Optional<Car> result = carRepository.findActiveByCarPlate(A_CAR_PLATE);

			assertThat(result).contains(car);
		}

		@Test
		void testFindActiveByCarPlateWhenCarDoesNotExistReturnsEmpty() {
			persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

			Optional<Car> result = carRepository.findActiveByCarPlate(ANOTHER_CAR_PLATE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarPlateWhenThereIsADeletedAndAnActiveCarWithSamePlateReturnsOnlyTheActiveOne() {
			Car deletedCar = new Car(A_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);
			deletedCar.setDeleted(true);
			persistCar(deletedCar);

			Car activeCar = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

			Optional<Car> result = carRepository.findActiveByCarPlate(A_CAR_PLATE);

			assertThat(result).contains(activeCar);
		}

	}

	@Nested
	class FindActiveById {

		@Test
		void testFindActiveByIdWhenThereAreNoCarsReturnsEmptyOptional() {
			Optional<Car> result = carRepository.findActiveById(AN_ID);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCarDoesNotExistReturnsEmptyOptional() {
			persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

			Optional<Car> result = carRepository.findActiveById(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCarIsDeletedReturnsEmptyOptional() {
			Car deletedCar = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			deletedCar.setDeleted(true);
			persistCar(deletedCar);

			Optional<Car> result = carRepository.findActiveById(deletedCar.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCarIsActiveReturnsOptionalWithCar() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

			Optional<Car> result = carRepository.findActiveById(car.getId());

			assertThat(result).contains(car);
		}

	}

	@Nested
	class Save {

		@Test
		void testSaveWhenCarIsNewPersistsItAndReturnsItWithGeneratedId() {
			Car car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

			entityManager.getTransaction().begin();
			Car result = carRepository.save(car);
			entityManager.getTransaction().commit();

			assertThat(result.getId()).isNotNull();
			assertThat(result.getCarPlate()).isEqualTo(A_CAR_PLATE);
		}

		@Test
		void testSaveWhenCarIsNewCanBeRetrievedDirectlyFromDatabase() {
			entityManager.getTransaction().begin();
			Car car = carRepository.save(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			entityManager.getTransaction().commit();

			entityManager.clear();

			Car reloaded = entityManager.find(Car.class, car.getId());

			assertThat(reloaded).isEqualTo(car);
		}

		@Test
		void testSaveWhenCarAlreadyExistsUpdatesItsFieldsInDatabase() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			car.setDeleted(true);

			entityManager.getTransaction().begin();
			carRepository.save(car);
			entityManager.getTransaction().commit();

			entityManager.clear();

			Car reloaded = entityManager.find(Car.class, car.getId());

			assertThat(reloaded).isEqualTo(car);
			assertThat(reloaded.getDeleted()).isTrue();
		}

	}

	private void persistDeletedCar() {
		Car deletedCar = new Car("aDeletedPlate", "aDeletedBrand", "aDeletedModel", BigDecimal.valueOf(12.3));
		deletedCar.setDeleted(true);

		persistCar(deletedCar);
	}

	private Car persistCar(Car car) {
		entityManager.getTransaction().begin();
		entityManager.persist(car);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return car;
	}

}
