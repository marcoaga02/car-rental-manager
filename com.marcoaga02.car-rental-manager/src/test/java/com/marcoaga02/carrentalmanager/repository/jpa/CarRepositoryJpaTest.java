package com.marcoaga02.carrentalmanager.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;

class CarRepositoryJpaTest extends BasePostgresTest {

	private static final Long AN_ID = 10L;
	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final String A_DELETED_CAR_PLATE = "aDeletedPlate";
	private static final String A_DELETED_BRAND = "aDeletedBrand";
	private static final String A_DELETED_MODEL = "aDeletedModel";
	private static final BigDecimal A_DELETED_DAILY_RATE = BigDecimal.valueOf(12.3);

	private CarRepositoryJpa carRepository;
	private Car car, anotherCar;

	@BeforeEach
	void setUp() {
		carRepository = new CarRepositoryJpa(entityManager);

		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);
	}

	@Nested
	class FindAllActive {

		@Test
		void testFindAllActiveWhenThereAreNoCarsReturnsAnEmptyList() {
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
			persistCar(car);
			persistDeletedCar();

			List<Car> result = carRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(car);
		}

		@Test
		void testFindAllActiveWhenThereAreMultipleActiveCarsReturnAListWithAllActiveElements() {
			persistCar(car);
			persistCar(anotherCar);
			persistDeletedCar();

			List<Car> result = carRepository.findAllActive();

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(car, anotherCar);
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
			persistDeletedCar();

			Optional<Car> result = carRepository.findActiveByCarPlate(A_DELETED_CAR_PLATE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarPlateWhenCarIsActiveReturnsOptionalWithCar() {
			persistCar(car);

			Optional<Car> result = carRepository.findActiveByCarPlate(A_CAR_PLATE);

			assertThat(result).contains(car);
		}

		@Test
		void testFindActiveByCarPlateWhenCarDoesNotExistReturnsEmpty() {
			persistCar(car);

			Optional<Car> result = carRepository.findActiveByCarPlate(ANOTHER_CAR_PLATE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarPlateWhenThereIsADeletedAndAnActiveCarWithSamePlateReturnsOnlyTheActiveOne() {
			Car deletedCar = new Car(A_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);
			deletedCar.setDeleted(true);
			persistCar(deletedCar);

			Car activeCar = persistCar(car);

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
			persistCar(car);

			Optional<Car> result = carRepository.findActiveById(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCarIsDeletedReturnsEmptyOptional() {
			Car deletedCar = persistDeletedCar();

			Optional<Car> result = carRepository.findActiveById(deletedCar.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCarIsActiveReturnsOptionalWithCar() {
			persistCar(car);

			Optional<Car> result = carRepository.findActiveById(car.getId());

			assertThat(result).contains(car);
		}

	}

	@Nested
	class Save {

		@Test
		void testSaveWhenCarIsNewPersistsItAndReturnsItWithGeneratedId() {
			entityManager.getTransaction().begin();
			Car result = carRepository.save(car);
			entityManager.getTransaction().commit();

			assertThat(result.getId()).isNotNull();
			assertThat(result.getCarPlate()).isEqualTo(A_CAR_PLATE);
			assertThat(result.getBrand()).isEqualTo(A_BRAND);
			assertThat(result.getModel()).isEqualTo(A_MODEL);
			assertThat(result.getDailyRate()).isEqualTo(A_DAILY_RATE);
		}

		@Test
		void testSaveWhenCarIsNewCanBeRetrievedDirectlyFromDatabase() {
			entityManager.getTransaction().begin();
			Car result = carRepository.save(car);
			entityManager.getTransaction().commit();

			entityManager.clear();

			Car reloaded = entityManager.find(Car.class, result.getId());

			assertThat(reloaded).isEqualTo(result);
		}

		@Test
		void testSaveWhenCarAlreadyExistsUpdatesItsFieldsInDatabase() {
			persistCar(car);
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

	private Car persistDeletedCar() {
		Car deletedCar = new Car(A_DELETED_CAR_PLATE, A_DELETED_BRAND, A_DELETED_MODEL, A_DELETED_DAILY_RATE);
		deletedCar.setDeleted(true);

		return persistCar(deletedCar);
	}

	private Car persistCar(Car car) {
		entityManager.getTransaction().begin();
		entityManager.persist(car);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return car;
	}

}
