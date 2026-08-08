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

import com.marcoaga02.carrentalmanager.exception.CustomerHasActiveRentalException;
import com.marcoaga02.carrentalmanager.exception.DuplicateTaxIdCodeException;
import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

class CustomerServiceImplIT extends BasePostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final String A_DELETED_TAX_ID_CODE = "aDeletedTaxIdCode";
	private static final String A_DELETED_FIRSTNAME = "aDeletedFirstname";
	private static final String A_DELETED_LASTNAME = "aDeletedLastname";

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");

	private static final Integer A_NUMBER_OF_DAYS = 6;

	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private Customer customer, anotherCustomer;
	private Car car;

	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);
		customerService = new CustomerServiceImpl(transactionManager, new CustomerMapper());

		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);

		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
	}

	@Test
	void testGetAllCustomersIncludesAllActiveCustomers() {
		persistCar(car);
		persistDeletedCustomer();
		persistCustomer(customer);
		persistCustomer(anotherCustomer);
		persistRental(new Rental(car, anotherCustomer, TODAY, A_NUMBER_OF_DAYS));

		List<CustomerViewModel> result = customerService.getAllCustomers();

		assertThat(result).hasSize(2).extracting(CustomerViewModel::getId).containsExactlyInAnyOrder(customer.getId(),
				anotherCustomer.getId());
	}

	@Test
	void testCreateCustomerPersistsTheCustomerAndReturnsItsGeneratedId() {
		CustomerViewModel result = customerService
				.createCustomer(new CustomerViewModel(null, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

		assertThat(result.getId()).isNotNull();

		Customer persisted = entityManager.find(Customer.class, result.getId());
		assertThat(persisted).isNotNull();
		assertThat(persisted.getTaxIdCode()).isEqualTo(A_TAX_ID_CODE);
		assertThat(persisted.getFirstname()).isEqualTo(A_FIRSTNAME);
		assertThat(persisted.getLastname()).isEqualTo(A_LASTNAME);
		assertThat(persisted.getDeleted()).isFalse();
	}

	@Test
	void testCreateCustomerThrowsDuplicateTaxIdCodeExceptionWhenAnActiveCustomerWithTheSameTaxIdCodeAlreadyExists() {
		persistCustomer(customer);
		CustomerViewModel request = new CustomerViewModel(null, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);

		assertThatThrownBy(() -> customerService.createCustomer(request))
				.isInstanceOf(DuplicateTaxIdCodeException.class);
	}

	@Test
	void testCreateCustomerDoesNotConsiderDeletedCustomersAsDuplicatesAndCreatesANewElement() {
		Customer deletedCustomer = persistDeletedCustomer();

		CustomerViewModel result = customerService
				.createCustomer(new CustomerViewModel(null, A_DELETED_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

		assertThat(result.getId()).isNotNull();
		assertThat(deletedCustomer.getId()).isNotNull().isNotEqualTo(result.getId());
	}

	@Test
	void testDeleteCustomerSoftDeletesTheCustomerWhenItIsNotRented() {
		persistCustomer(customer);
		Long customerId = customer.getId();

		customerService.deleteCustomer(customerId);

		Customer persisted = entityManager.find(Customer.class, customerId);
		assertThat(persisted.getDeleted()).isTrue();
	}

	@Test
	void testDeleteCustomerThrowsCustomerHasActiveRentalExceptionWhenTheCustomerHasAnActiveRental() {
		persistCar(car);
		persistCustomer(customer);
		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		Long customerId = customer.getId();

		assertThatThrownBy(() -> customerService.deleteCustomer(customerId))
				.isInstanceOf(CustomerHasActiveRentalException.class);

		Customer persisted = entityManager.find(Customer.class, customerId);
		assertThat(persisted.getDeleted()).isFalse();
	}

	private Customer persistDeletedCustomer() {
		Customer deletedCustomer = new Customer(A_DELETED_TAX_ID_CODE, A_DELETED_FIRSTNAME, A_DELETED_LASTNAME);
		deletedCustomer.setDeleted(true);
		return persistCustomer(deletedCustomer);
	}

	private Customer persistCustomer(Customer customer) {
		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return customer;
	}

	private Car persistCar(Car car) {
		entityManager.getTransaction().begin();
		entityManager.persist(car);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return car;
	}

	private Rental persistRental(Rental rental) {
		entityManager.getTransaction().begin();
		entityManager.persist(rental);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return rental;
	}
}
