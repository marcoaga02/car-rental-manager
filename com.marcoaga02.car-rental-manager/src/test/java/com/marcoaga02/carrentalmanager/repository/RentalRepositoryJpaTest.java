package com.marcoaga02.carrentalmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;

class RentalRepositoryJpaTest extends BaseRepositoryTest {

	private static final Long A_CAR_ID = 1L;
	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final Long A_RENTAL_ID = 5L;

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");
	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private static final LocalDate A_START_DATE = TODAY;
	private static final Integer A_NUMBER_OF_DAYS = 6;

	private static final LocalDate ANOTHER_START_DATE = TODAY.minusDays(3);
	private static final Integer ANOTHER_NUMBER_OF_DAYS = 10;

	private static final LocalDate EXPIRED_START_DATE = TODAY.minusDays(30);
	private static final Integer EXPIRED_NUMBER_OF_DAYS = 5;

	private static final LocalDate BOUNDARY_START_DATE = TODAY.minusDays(5);
	private static final Integer BOUNDARY_NUMBER_OF_DAYS = 5;

	private static final LocalDate ALMOST_EXPIRED_START_DATE = TODAY;
	private static final Integer ALMOST_EXPIRED_NUMBER_OF_DAYS = 1;

	private RentalRepositoryJpa rentalRepository;

	@BeforeEach
	void setUp() {
		rentalRepository = new RentalRepositoryJpa(entityManager, fixedClock);
	}

	@Nested
	class FindAllActive {

		@Test
		void testFindAllActiveWhenThereAreNoRentalsReturnsAnEmptyList() {
			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenThereIsAnExpiredRentalReturnsAnEmptyList() {
			persistExpiredRental();

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenRentalEndsExactlyTodayIsConsideredExpired() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			persistRental(new Rental(car, customer, BOUNDARY_START_DATE, BOUNDARY_NUMBER_OF_DAYS));

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenRentalEndsTomorrowIsConsideredActive() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(
					new Rental(car, customer, ALMOST_EXPIRED_START_DATE, ALMOST_EXPIRED_NUMBER_OF_DAYS));

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(rental);
		}

		@Test
		void testFindAllActiveWhenThereIsOnlyOneActiveRentalReturnsAListWithASingleElement() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(rental);
		}

		@Test
		void testFindAllActiveWhenThereAreMultipleActiveRentalsReturnAListWithAllActiveElements() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			Car anotherCar = persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
			Customer anotherCustomer = persistCustomer(
					new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));
			Rental anotherRental = persistRental(
					new Rental(anotherCar, anotherCustomer, ANOTHER_START_DATE, ANOTHER_NUMBER_OF_DAYS));

			persistExpiredRental();

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(rental, anotherRental);
		}

	}

	@Nested
	class FindActiveByCarId {

		@Test
		void testFindActiveByCarIdWhenThereAreNoRentalsReturnsEmptyOptional() {
			Optional<Rental> result = rentalRepository.findActiveByCarId(A_CAR_ID);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarIdWhenNoActiveRentalWithCarIdIsFoundReturnsEmpty() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveByCarId(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarIdWhenRentalIsExpiredBySeveralDaysReturnsEmptyOptional() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			persistRental(new Rental(car, customer, EXPIRED_START_DATE, EXPIRED_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveByCarId(car.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarIdWhenRentalEndsExactlyTodayIsConsideredExpired() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			persistRental(new Rental(car, customer, BOUNDARY_START_DATE, BOUNDARY_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveByCarId(car.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByCarIdWhenRentalEndsTomorrowIsConsideredActive() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(
					new Rental(car, customer, ALMOST_EXPIRED_START_DATE, ALMOST_EXPIRED_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveByCarId(car.getId());

			assertThat(result).contains(rental);
		}

		@Test
		void testFindActiveByCarIdWhenRentalIsActiveReturnsOptionalWithRental() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveByCarId(car.getId());

			assertThat(result).contains(rental);
		}

		@Test
		void testFindActiveByCarIdWhenThereIsAnExpiredAndAnActiveRentalForSameCarReturnsOnlyTheActiveOne() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Customer anotherCustomer = persistCustomer(
					new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

			persistRental(new Rental(car, customer, EXPIRED_START_DATE, EXPIRED_NUMBER_OF_DAYS));
			Rental activeRental = persistRental(new Rental(car, anotherCustomer, A_START_DATE, A_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveByCarId(car.getId());

			assertThat(result).contains(activeRental);
		}

	}

	@Nested
	class FindActiveById {

		@Test
		void testFindActiveByIdWhenThereAreNoRentalsReturnsEmptyOptional() {
			Optional<Rental> result = rentalRepository.findActiveById(A_RENTAL_ID);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenNoActiveRentalWithIdIsFoundReturnsEmpty() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveById(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenRentalIsExpiredBySeveralDaysReturnsEmptyOptional() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, EXPIRED_START_DATE, EXPIRED_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveById(rental.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenRentalEndsExactlyTodayIsConsideredExpired() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, BOUNDARY_START_DATE, BOUNDARY_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveById(rental.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenRentalEndsTomorrowIsConsideredActive() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(
					new Rental(car, customer, ALMOST_EXPIRED_START_DATE, ALMOST_EXPIRED_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveById(rental.getId());

			assertThat(result).contains(rental);
		}

		@Test
		void testFindActiveByIdWhenRentalIsActiveReturnsOptionalWithRental() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			Optional<Rental> result = rentalRepository.findActiveById(rental.getId());

			assertThat(result).contains(rental);
		}

	}

	@Nested
	class Save {

		@Test
		void testSaveWhenRentalIsNewPersistsItAndReturnsItWithGeneratedId() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);

			entityManager.getTransaction().begin();
			Rental result = rentalRepository.save(rental);
			entityManager.getTransaction().commit();

			assertThat(result)
					.extracting(Rental::getId, Rental::getStartDate, Rental::getDays)
					.containsExactly(rental.getId(), A_START_DATE, A_NUMBER_OF_DAYS);

			assertThat(result.getCar())
					.extracting(Car::getId, Car::getCarPlate, Car::getBrand, Car::getModel, Car::getDailyRate)
					.usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
					.containsExactly(car.getId(), A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

			assertThat(result.getCustomer())
					.extracting(Customer::getId, Customer::getTaxIdCode, Customer::getFirstname, Customer::getLastname)
					.containsExactly(customer.getId(), A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		}

		@Test
		void testSaveWhenRentalIsNewCanBeRetrievedDirectlyFromDatabase() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);

			entityManager.getTransaction().begin();
			rentalRepository.save(rental);
			entityManager.getTransaction().commit();

			entityManager.clear();

			Rental reloaded = entityManager.find(Rental.class, rental.getId());

			assertThat(reloaded).isEqualTo(rental);
		}

		@Test
		void testSaveWhenRentalAlreadyExistsIsIdempotentAndReturnsSameData() {
			Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

			entityManager.getTransaction().begin();
			Rental result = rentalRepository.save(rental);
			entityManager.getTransaction().commit();

			assertThat(result.getId()).isEqualTo(rental.getId());
			assertThat(result.getStartDate()).isEqualTo(A_START_DATE);
			assertThat(result.getDays()).isEqualTo(A_NUMBER_OF_DAYS);
		}

	}

	@Test
	void testDeleteByIdWhenRentalExistsRemovesItFromDatabase() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));

		entityManager.getTransaction().begin();
		rentalRepository.deleteById(rental.getId());
		entityManager.getTransaction().commit();

		entityManager.clear();

		Rental result = entityManager.find(Rental.class, rental.getId());

		assertThat(result).isNull();
	}

	private void persistExpiredRental() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		Rental expiredRental = new Rental(car, customer, EXPIRED_START_DATE, EXPIRED_NUMBER_OF_DAYS);

		persistRental(expiredRental);
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
