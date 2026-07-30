package com.marcoaga02.carrentalmanager.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.repository.BaseRepositoryTest;

class CustomerRepositoryJpaTest extends BaseRepositoryTest {

	private static final Long AN_ID = 10L;

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";

	private static final String A_FIRSTNAME = "aFirstname";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";

	private static final String A_LASTNAME = "aLastname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private CustomerRepositoryJpa customerRepository;

	@BeforeEach
	void setUp() {
		customerRepository = new CustomerRepositoryJpa(entityManager);
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
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			persistDeletedCustomer();

			List<Customer> result = customerRepository.findAllActive();

			assertThat(result).hasSize(1).containsExactly(customer);
		}

		@Test
		void testFindAllActiveWhenThereAreMultipleActiveCustomersReturnAListWithAllActiveElements() {
			Customer firstCustomer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			Customer secondCustomer = persistCustomer(
					new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));
			persistDeletedCustomer();

			List<Customer> result = customerRepository.findAllActive();

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(firstCustomer, secondCustomer);
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
			Customer deletedCustomer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
			deletedCustomer.setDeleted(true);
			persistCustomer(deletedCustomer);

			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByTaxIdCodeWhenCustomerIsActiveReturnsOptionalWithCustomer() {
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

			Optional<Customer> result = customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE);

			assertThat(result).contains(customer);
		}

		@Test
		void testFindActiveByTaxIdCodeWhenCustomerDoesNotExistReturnsEmpty() {
			persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

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
			persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

			Optional<Customer> result = customerRepository.findActiveById(Long.MAX_VALUE);

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCustomerIsDeletedReturnsEmptyOptional() {
			Customer deletedCustomer = new Customer(A_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
			deletedCustomer.setDeleted(true);
			persistCustomer(deletedCustomer);

			Optional<Customer> result = customerRepository.findActiveById(deletedCustomer.getId());

			assertThat(result).isEmpty();
		}

		@Test
		void testFindActiveByIdWhenCustomerIsActiveReturnsOptionalWithCustomer() {
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

			Optional<Customer> result = customerRepository.findActiveById(customer.getId());

			assertThat(result).contains(customer);
		}

	}

	@Nested
	class Save {

		@Test
		void testSaveWhenCustomerIsNewPersistsItAndReturnsItWithGeneratedId() {
			Customer customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);

			entityManager.getTransaction().begin();
			Customer result = customerRepository.save(customer);
			entityManager.getTransaction().commit();

			assertThat(result.getId()).isNotNull();
			assertThat(result.getTaxIdCode()).isEqualTo(A_TAX_ID_CODE);
		}

		@Test
		void testSaveWhenCustomerIsNewCanBeRetrievedDirectlyFromDatabase() {
			entityManager.getTransaction().begin();
			Customer customer = customerRepository.save(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
			entityManager.getTransaction().commit();

			entityManager.clear();

			Customer reloaded = entityManager.find(Customer.class, customer.getId());

			assertThat(reloaded).isEqualTo(customer);
		}

		@Test
		void testSaveWhenCustomerAlreadyExistsUpdatesItsFieldsInDatabase() {
			Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
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

	private void persistDeletedCustomer() {
		Customer deletedCustomer = new Customer("aDeletedTaxId", "aDeletedFirstname", "aDeletedLastname");
		deletedCustomer.setDeleted(true);

		persistCustomer(deletedCustomer);
	}

	private Customer persistCustomer(Customer customer) {
		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return customer;
	}

}
