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

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");

	private static final Integer A_NUMBER_OF_DAYS = 6;

	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private CarService carService;

	@BeforeEach
	void setUp() {
		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);
		carService = new CarServiceImpl(transactionManager, new CarMapper());
	}

	@Test
	void testGetAllCarsIncludesAllActiveCars() {
		persistDeletedCar(new Car(A_DELETED_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Car availableCar = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Car rentedCar = persistActiveCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistRental(new Rental(rentedCar, customer, TODAY, A_NUMBER_OF_DAYS));

		List<CarViewModel> result = carService.getAllCars();

		assertThat(result).hasSize(2).extracting(CarViewModel::getId).containsExactlyInAnyOrder(availableCar.getId(),
				rentedCar.getId());
	}
	

	@Test
	void testCreateCarPersistsTheCarAndReturnsItsGeneratedId() {
		CarViewModel result = carService.createCar(new CarViewModel(null, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

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
		persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		CarViewModel car = new CarViewModel(null, A_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);

		assertThatThrownBy(() -> carService.createCar(car)).isInstanceOf(DuplicateCarPlateException.class);
	}

	@Test
	void testCreateCarDoesNotConsiderDeletedCarsAsDuplicatesAndCreatesANewElement() {
		Car deletedCar = persistDeletedCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

		CarViewModel result = carService.createCar(new CarViewModel(null, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

		assertThat(result.getId()).isNotNull();
		assertThat(deletedCar.getId()).isNotNull().isNotEqualTo(result.getId());
	}

	@Test
	void testDeleteCarSoftDeletesTheCarWhenItIsNotRented() {
		Car car = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Long carId = car.getId();

		carService.deleteCar(carId);

		Car persisted = entityManager.find(Car.class, carId);
		assertThat(persisted.getDeleted()).isTrue();
	}

	@Test
	void testDeleteCarThrowsCarCurrentlyRentedExceptionWhenTheCarHasAnActiveRental() {
		Car car = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		Long carId = car.getId();

		assertThatThrownBy(() -> carService.deleteCar(carId)).isInstanceOf(CarCurrentlyRentedException.class);

		Car persisted = entityManager.find(Car.class, carId);
		assertThat(persisted.getDeleted()).isFalse();
	}

	@Test
	void testGetAvailableCarsExcludesCarsWithAnActiveRental() {
		Car availableCar = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Car rentedCar = persistActiveCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistRental(new Rental(rentedCar, customer, TODAY, A_NUMBER_OF_DAYS));

		List<CarViewModel> result = carService.getAvailableCars();

		assertThat(result).extracting(CarViewModel::getId).containsExactly(availableCar.getId());
	}

	@Test
	void testGetAvailableCarsExcludesDeletedCars() {
		Car availableCar = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistDeletedCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));

		List<CarViewModel> result = carService.getAvailableCars();

		assertThat(result).extracting(CarViewModel::getId).containsExactly(availableCar.getId());
	}

	private Car persistActiveCar(Car car) {
		return persistCar(car, false);
	}

	private Car persistDeletedCar(Car car) {
		return persistCar(car, true);
	}

	private Car persistCar(Car car, boolean deleted) {
		car.setDeleted(deleted);
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
