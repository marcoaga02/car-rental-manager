package com.marcoaga02.carrentalmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.exception.CarCurrentlyRentedException;
import com.marcoaga02.carrentalmanager.exception.DuplicateCarPlateException;
import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

class CarServiceImplIT extends BasePostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(50.2);

	private static final String A_DELETED_CAR_PLATE = "aDeletedCarPlate";
	private static final String A_DELETED_BRAND = "aDeletedBrand";
	private static final String A_DELETED_MODEL = "aDeletedModel";
	private static final BigDecimal A_DELETED_DAILY_RATE = BigDecimal.valueOf(15.3);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");

	private static final Integer A_NUMBER_OF_DAYS = 6;

	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private Car car, anotherCar;
	private Customer customer;

	private CarService carService;

	@BeforeEach
	void setUp() {
		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);
		carService = new CarServiceImpl(transactionManager, new CarMapper());

		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);

		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
	}

	@Test
	void testGetAllCarsIncludesAllActiveCars() {
		persistDeletedCar();
		persistCar(car);
		persistCar(anotherCar);
		persistCustomer(customer);
		persistRental(new Rental(anotherCar, customer, TODAY, A_NUMBER_OF_DAYS));

		List<CarViewModel> result = carService.getAllCars();

		assertThat(result).hasSize(2).extracting(CarViewModel::getId).containsExactlyInAnyOrder(car.getId(),
				anotherCar.getId());
	}

	@Test
	void testCreateCarPersistsTheCarAndReturnsItsGeneratedId() {
		CarViewModel request = new CarViewModel(null, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		CarViewModel result = carService.createCar(request);

		assertThat(result.getId()).isNotNull();

		Car persisted = entityManager.find(Car.class, result.getId());
		assertThat(persisted).isNotNull();
		assertThat(persisted.getCarPlate()).isEqualTo(A_CAR_PLATE);
		assertThat(persisted.getBrand()).isEqualTo(A_BRAND);
		assertThat(persisted.getModel()).isEqualTo(A_MODEL);
		assertThat(persisted.getDailyRate()).isEqualByComparingTo(A_DAILY_RATE);
		assertThat(persisted.getDeleted()).isFalse();
	}

	@Test
	void testCreateCarThrowsDuplicateCarPlateExceptionWhenAnActiveCarWithTheSamePlateAlreadyExists() {
		persistCar(car);

		CarViewModel request = new CarViewModel(null, A_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);

		assertThatThrownBy(() -> carService.createCar(request)).isInstanceOf(DuplicateCarPlateException.class);
	}

	@Test
	void testCreateCarDoesNotConsiderDeletedCarsAsDuplicatesAndCreatesANewElement() {
		Car deletedCar = persistDeletedCar();

		CarViewModel result = carService
				.createCar(new CarViewModel(null, A_DELETED_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

		assertThat(result.getId()).isNotNull();
		assertThat(deletedCar.getId()).isNotNull().isNotEqualTo(result.getId());
	}

	@Test
	void testDeleteCarSoftDeletesTheCarWhenItIsNotRented() {
		persistCar(car);
		Long carId = car.getId();

		carService.deleteCar(carId);

		Car persisted = entityManager.find(Car.class, carId);
		assertThat(persisted.getDeleted()).isTrue();
	}

	@Test
	void testDeleteCarThrowsCarCurrentlyRentedExceptionWhenTheCarHasAnActiveRental() {
		persistCar(car);
		persistCustomer(customer);
		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		Long carId = car.getId();

		assertThatThrownBy(() -> carService.deleteCar(carId)).isInstanceOf(CarCurrentlyRentedException.class);

		Car persisted = entityManager.find(Car.class, carId);
		assertThat(persisted.getDeleted()).isFalse();
	}

	@Test
	void testGetAvailableCarsExcludesCarsWithAnActiveRental() {
		Car availableCar = persistCar(car);
		Car rentedCar = persistCar(anotherCar);
		persistCustomer(customer);
		persistRental(new Rental(rentedCar, customer, TODAY, A_NUMBER_OF_DAYS));

		List<CarViewModel> result = carService.getAvailableCars();

		assertThat(result).extracting(CarViewModel::getId).containsExactly(availableCar.getId());
	}

	@Test
	void testGetAvailableCarsExcludesDeletedCars() {
		persistCar(car);
		persistDeletedCar();

		List<CarViewModel> result = carService.getAvailableCars();

		assertThat(result).extracting(CarViewModel::getId).containsExactly(car.getId());
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

	private Customer persistCustomer(Customer customer) {
		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return customer;
	}

	private Rental persistRental(Rental rental) {
		entityManager.getTransaction().begin();
		entityManager.persist(rental);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return rental;
	}

}
