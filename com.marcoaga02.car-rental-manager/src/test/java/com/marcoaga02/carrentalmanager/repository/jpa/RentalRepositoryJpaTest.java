package com.marcoaga02.carrentalmanager.repository.jpa;

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
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;

class RentalRepositoryJpaTest extends BasePostgresTest {

	private static final Long A_CAR_ID = 1L;
	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final Long A_CUSTOMER_ID = 2L;
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

	private static final LocalDate BOUNDARY_EXPIRED_START_DATE = TODAY.minusDays(5);
	private static final Integer BOUNDARY_EXPIRED_NUMBER_OF_DAYS = 5;

	private static final LocalDate BOUNDARY_ACTIVE_START_DATE = TODAY;
	private static final Integer BOUNDARY_ACTIVE_NUMBER_OF_DAYS = 1;

	private RentalRepositoryJpa rentalRepository;
	private Car car, anotherCar;
	private Customer customer, anotherCustomer;
	private Rental activeRental, anotherActiveRental, boundaryActiveRental, boundaryExpiredRental, expiredRental;

	@BeforeEach
	void setUp() {
		rentalRepository = new RentalRepositoryJpa(entityManager, fixedClock);

		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);

		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);

		activeRental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);
		anotherActiveRental = new Rental(anotherCar, anotherCustomer, ANOTHER_START_DATE, ANOTHER_NUMBER_OF_DAYS);
		boundaryActiveRental = new Rental(car, customer, BOUNDARY_ACTIVE_START_DATE, BOUNDARY_ACTIVE_NUMBER_OF_DAYS);
		boundaryExpiredRental = new Rental(car, customer, BOUNDARY_EXPIRED_START_DATE, BOUNDARY_EXPIRED_NUMBER_OF_DAYS);
		expiredRental = new Rental(car, customer, EXPIRED_START_DATE, EXPIRED_NUMBER_OF_DAYS);
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
			persistCar(car);
			persistCustomer(customer);
			persistRental(expiredRental);

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenRentalEndsExactlyTodayIsConsideredExpired() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryExpiredRental);

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenRentalEndsTomorrowIsConsideredActive() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryActiveRental);

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(boundaryActiveRental);
		}

		@Test
		void testFindAllActiveWhenThereIsOnlyOneActiveRentalReturnsAListWithASingleElement() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(activeRental);
		}

		@Test
		void testFindAllActiveWhenThereAreMultipleActiveRentalsReturnAListWithAllActiveElements() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			persistCar(anotherCar);
			persistCustomer(anotherCustomer);
			persistRental(anotherActiveRental);

			persistRental(expiredRental);

			List<Rental> result = rentalRepository.findAllActive();

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(activeRental, anotherActiveRental);
		}

	}

	@Nested
	class FindActiveByCarId {

		@Test
		void testFindActiveByCarIdWhenThereAreNoRentalsReturnsFalse() {
			assertThat(rentalRepository.existsActiveByCarId(A_CAR_ID)).isFalse();
		}

		@Test
		void testFindActiveByCarIdWhenNoActiveRentalWithCarIdIsFoundReturnsFalse() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			assertThat(rentalRepository.existsActiveByCarId(Long.MAX_VALUE)).isFalse();
		}

		@Test
		void testFindActiveByCarIdWhenRentalIsExpiredBySeveralDaysReturnsFalse() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(expiredRental);

			assertThat(rentalRepository.existsActiveByCarId(car.getId())).isFalse();
		}

		@Test
		void testFindActiveByCarIdWhenRentalEndsExactlyTodayIsConsideredExpiredAndReturnsFalse() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryExpiredRental);

			assertThat(rentalRepository.existsActiveByCarId(car.getId())).isFalse();
		}

		@Test
		void testFindActiveByCarIdWhenRentalEndsTomorrowIsConsideredActiveAndReturnsTrue() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryActiveRental);

			assertThat(rentalRepository.existsActiveByCarId(car.getId())).isTrue();
		}

		@Test
		void testFindActiveByCarIdWhenRentalIsActiveReturnsTrue() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			assertThat(rentalRepository.existsActiveByCarId(car.getId())).isTrue();
		}

		@Test
		void testFindActiveByCarIdWhenThereIsAnExpiredAndAnActiveRentalForSameCarReturnsTrue() {
			persistCar(car);
			persistCustomer(customer);

			persistRental(expiredRental);
			persistRental(activeRental);

			assertThat(rentalRepository.existsActiveByCarId(car.getId())).isTrue();
		}

	}

	@Nested
	class FindActiveByCustomerId {

		@Test
		void testFindActiveByCustomerIdWhenThereAreNoRentalsReturnsFalse() {
			assertThat(rentalRepository.existsActiveByCustomerId(A_CUSTOMER_ID)).isFalse();
		}

		@Test
		void testFindActiveByCustomerIdWhenNoActiveRentalWithCustomerIdIsFoundReturnsFalse() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			assertThat(rentalRepository.existsActiveByCustomerId(Long.MAX_VALUE)).isFalse();
		}

		@Test
		void testFindActiveByCustomerIdWhenRentalIsExpiredBySeveralDaysReturnsFalse() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(expiredRental);

			assertThat(rentalRepository.existsActiveByCustomerId(customer.getId())).isFalse();
		}

		@Test
		void testFindActiveByCustomerIdWhenRentalEndsExactlyTodayIsConsideredExpiredAndReturnsFalse() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryExpiredRental);

			assertThat(rentalRepository.existsActiveByCustomerId(customer.getId())).isFalse();
		}

		@Test
		void testFindActiveByCustomerIdWhenRentalEndsTomorrowIsConsideredActiveAndReturnsTrue() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryActiveRental);

			assertThat(rentalRepository.existsActiveByCustomerId(customer.getId())).isTrue();
		}

		@Test
		void testFindActiveByCustomerIdWhenRentalIsActiveReturnsTrue() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			assertThat(rentalRepository.existsActiveByCustomerId(customer.getId())).isTrue();
		}

		@Test
		void testFindActiveByCustomerIdWhenThereIsAnExpiredAndAnActiveRentalForSameCustomerReturnsTrue() {
			persistCar(car);
			persistCustomer(customer);

			persistRental(expiredRental);
			persistRental(activeRental);

			assertThat(rentalRepository.existsActiveByCustomerId(customer.getId())).isTrue();
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
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			Optional<Rental> result = rentalRepository.findActiveById(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenRentalIsExpiredBySeveralDaysReturnsEmptyOptional() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(expiredRental);

			Optional<Rental> result = rentalRepository.findActiveById(expiredRental.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenRentalEndsExactlyTodayIsConsideredExpired() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryExpiredRental);

			Optional<Rental> result = rentalRepository.findActiveById(boundaryExpiredRental.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenRentalEndsTomorrowIsConsideredActive() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(boundaryActiveRental);

			Optional<Rental> result = rentalRepository.findActiveById(boundaryActiveRental.getId());

			assertThat(result).contains(boundaryActiveRental);
		}

		@Test
		void testFindActiveByIdWhenRentalIsActiveReturnsOptionalWithRental() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			Optional<Rental> result = rentalRepository.findActiveById(activeRental.getId());

			assertThat(result).contains(activeRental);
		}

	}

	@Nested
	class Save {

		@Test
		void testSaveWhenRentalIsNewPersistsItAndReturnsItWithGeneratedId() {
			persistCar(car);
			persistCustomer(customer);

			entityManager.getTransaction().begin();
			Rental result = rentalRepository.save(activeRental);
			entityManager.getTransaction().commit();

			assertThat(result).extracting(Rental::getId, Rental::getStartDate, Rental::getDays)
					.containsExactly(activeRental.getId(), A_START_DATE, A_NUMBER_OF_DAYS);

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
			persistCar(car);
			persistCustomer(customer);

			entityManager.getTransaction().begin();
			rentalRepository.save(activeRental);
			entityManager.getTransaction().commit();

			entityManager.clear();

			Rental reloaded = entityManager.find(Rental.class, activeRental.getId());

			assertThat(reloaded).isEqualTo(activeRental);
		}

		@Test
		void testSaveWhenRentalAlreadyExistsIsIdempotentAndReturnsSameData() {
			persistCar(car);
			persistCustomer(customer);
			persistRental(activeRental);

			entityManager.getTransaction().begin();
			Rental result = rentalRepository.save(activeRental);
			entityManager.getTransaction().commit();

			assertThat(result.getId()).isEqualTo(activeRental.getId());
			assertThat(result.getCar()).isEqualTo(car);
			assertThat(result.getCustomer()).isEqualTo(customer);
			assertThat(result.getStartDate()).isEqualTo(A_START_DATE);
			assertThat(result.getDays()).isEqualTo(A_NUMBER_OF_DAYS);
		}

	}

	@Test
	void testDeleteByIdWhenRentalExistsRemovesItFromDatabase() {
		persistCar(car);
		persistCustomer(customer);
		persistRental(activeRental);

		entityManager.getTransaction().begin();
		rentalRepository.deleteById(activeRental.getId());
		entityManager.getTransaction().commit();

		entityManager.clear();

		Rental result = entityManager.find(Rental.class, activeRental.getId());

		assertThat(result).isNull();
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
