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

import com.marcoaga02.carrentalmanager.exception.CarAlreadyRentedException;
import com.marcoaga02.carrentalmanager.mapper.RentalMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

class RentalServiceImplIT extends BasePostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(50.2);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final Integer A_NUMBER_OF_DAYS = 6;

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");

	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private Car car;

	private Customer customer;

	private RentalService rentalService;

	@BeforeEach
	void setUp() {
		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);
		rentalService = new RentalServiceImpl(transactionManager, new RentalMapper(), fixedClock);

		car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
	}

	@Test
	void testGetAllActiveRentalsExcludesExpiredRentals() {
		Rental activeRental = persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		Car anotherCar = persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
		LocalDate expiredStartDate = TODAY.minusDays(A_NUMBER_OF_DAYS + 1);
		persistRental(new Rental(anotherCar, customer, expiredStartDate, A_NUMBER_OF_DAYS));

		List<RentalViewModel> result = rentalService.getAllActiveRentals();

		assertThat(result).extracting(RentalViewModel::getId).containsExactly(activeRental.getId());
	}

	@Test
	void testCreateRentalPersistsTheRentalAndReturnsItsGeneratedId() {
		RentalViewModel result = rentalService
				.createRental(new RentalCreationRequest(car.getId(), customer.getId(), A_NUMBER_OF_DAYS));

		assertThat(result.getId()).isNotNull();

		Rental persisted = entityManager.find(Rental.class, result.getId());
		assertThat(persisted).isNotNull();
		assertThat(persisted.getCar().getId()).isEqualTo(car.getId());
		assertThat(persisted.getCustomer().getId()).isEqualTo(customer.getId());
		assertThat(persisted.getStartDate()).isEqualTo(TODAY);
		assertThat(persisted.getDays()).isEqualTo(A_NUMBER_OF_DAYS);
	}

	@Test
	void testCreateRentalThrowsCarAlreadyRentedExceptionWhenTheCarHasAnActiveRental() {
		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		RentalCreationRequest request = new RentalCreationRequest(car.getId(), customer.getId(), A_NUMBER_OF_DAYS);

		assertThatThrownBy(() -> rentalService.createRental(request)).isInstanceOf(CarAlreadyRentedException.class);
	}

	@Test
	void testCreateRentalSucceedsWhenThePreviousRentalOnTheSameCarHasAlreadyExpired() {
		LocalDate expiredStartDate = TODAY.minusDays(A_NUMBER_OF_DAYS + 1);
		Rental expiredRental = persistRental(new Rental(car, customer, expiredStartDate, A_NUMBER_OF_DAYS));

		RentalCreationRequest request = new RentalCreationRequest(car.getId(), customer.getId(), A_NUMBER_OF_DAYS);

		RentalViewModel result = rentalService.createRental(request);

		assertThat(result.getId()).isNotNull().isNotEqualTo(expiredRental.getId());
	}

	@Test
	void testDeleteRentalRemovesTheRentalFromTheDatabase() {
		Rental rental = persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));
		Long rentalId = rental.getId();

		rentalService.deleteRental(rentalId);

		Rental persisted = entityManager.find(Rental.class, rentalId);
		assertThat(persisted).isNull();
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
