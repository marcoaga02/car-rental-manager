package com.marcoaga02.carrentalmanager.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.testutils.BasePostgresTest;

class CustomerRepositoryJpaTest extends BasePostgresTest {

	private static final Long AN_ID = 10L;
	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final String A_DELETED_TAX_ID_CODE = "aDeletedTaxIdCode";
	private static final String A_DELETED_FIRSTNAME = "aDeletedFirstname";
	private static final String A_DELETED_LASTNAME = "aDeletedLastname";

	private CustomerRepositoryJpa customerRepository;
	private Customer customer, anotherCustomer;

	@BeforeEach
	void setUp() {
		customerRepository = new CustomerRepositoryJpa(entityManager);

		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
	}

	@Nested
	class FindAllActive {

		@Test
		void testFindAllActiveWhenThereAreNoCustomerReturnsAnEmptyList() {
			List<Customer> result = customerRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenThereIsADeletedCustomerReturnsAnEmptyList() {
			persistDeletedCustomer();

			List<Customer> result = customerRepository.findAllActive();

			assertThat(result).isEmpty();
		}

		@Test
		void testFindAllActiveWhenThereIsOnlyOneActiveCustomerReturnAListWithASingleElement() {
			persistCustomer(customer);
			persistDeletedCustomer();

			List<Customer> result = customerRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(customer);
		}

		@Test
		void testFindAllActiveWhenThereAreMultipleActiveCustomersReturnAListWithAllActiveElements() {
			persistCustomer(customer);
			persistCustomer(anotherCustomer);
			persistDeletedCustomer();

			List<Customer> result = customerRepository.findAllActive();

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(customer, anotherCustomer);
		}

	}

	@Nested
	class FindActiveByTaxIdCode {

		@Test
		void testFindActiveByTaxIdCodeWhenThereAreNoCustomersReturnsEmptyOptional() {
			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByTaxIdCodeWhenCustomerIsDeletedReturnsEmptyOptional() {
			persistDeletedCustomer();

			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(A_DELETED_TAX_ID_CODE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByTaxIdCodeWhenCustomerIsActiveReturnsOptionalWithCustomer() {
			persistCustomer(customer);

			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE);

			assertThat(result).contains(customer);
		}

		@Test
		void testFindActiveByTaxIdCodeWhenCustomerDoesNotExistReturnsEmpty() {
			persistCustomer(customer);

			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(ANOTHER_TAX_ID_CODE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByTaxIdCodeWhenThereIsADeletedAndAnActiveCustomerWithSameTaxIdCodeReturnsOnlyTheActiveOne() {
			Customer deletedCustomer = new Customer(A_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
			deletedCustomer.setDeleted(true);
			persistCustomer(deletedCustomer);

			Customer activeCustomer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE);

			assertThat(result).contains(activeCustomer);
		}

	}

	@Nested
	class FindActiveById {

		@Test
		void testFindActiveByIdWhenThereAreNoCustomersReturnsEmptyOptional() {
			Optional<Customer> result = customerRepository.findActiveById(AN_ID);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCustomerDoesNotExistReturnsEmptyOptional() {
			persistCustomer(customer);

			Optional<Customer> result = customerRepository.findActiveById(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCustomerIsDeletedReturnsEmptyOptional() {
			Customer deletedCustomer = persistDeletedCustomer();

			Optional<Customer> result = customerRepository.findActiveById(deletedCustomer.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCustomerIsActiveReturnsOptionalWithCustomer() {
			persistCustomer(customer);

			Optional<Customer> result = customerRepository.findActiveById(customer.getId());

			assertThat(result).contains(customer);
		}

	}

	@Nested
	class Save {

		@Test
		void testSaveWhenCustomerIsNewPersistsItAndReturnsItWithGeneratedId() {
			entityManager.getTransaction().begin();
			Customer result = customerRepository.save(customer);
			entityManager.getTransaction().commit();

			assertThat(result.getId()).isNotNull();
			assertThat(result.getTaxIdCode()).isEqualTo(A_TAX_ID_CODE);
			assertThat(result.getFirstname()).isEqualTo(A_FIRSTNAME);
			assertThat(result.getLastname()).isEqualTo(A_LASTNAME);
		}

		@Test
		void testSaveWhenCustomerIsNewCanBeRetrievedDirectlyFromDatabase() {
			entityManager.getTransaction().begin();
			Customer result = customerRepository.save(customer);
			entityManager.getTransaction().commit();

			entityManager.clear();

			Customer reloaded = entityManager.find(Customer.class, result.getId());

			assertThat(reloaded).isEqualTo(result);
		}

		@Test
		void testSaveWhenCustomerAlreadyExistsUpdatesItsFieldsInDatabase() {
			persistCustomer(customer);
			customer.setDeleted(true);

			entityManager.getTransaction().begin();
			customerRepository.save(customer);
			entityManager.getTransaction().commit();

			entityManager.clear();

			Customer reloaded = entityManager.find(Customer.class, customer.getId());

			assertThat(reloaded).isEqualTo(customer);
			assertThat(reloaded.getDeleted()).isTrue();
		}

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

}
