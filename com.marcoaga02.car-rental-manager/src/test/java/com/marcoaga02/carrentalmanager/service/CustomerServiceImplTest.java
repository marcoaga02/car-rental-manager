package com.marcoaga02.carrentalmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateTaxIdCodeException;
import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.repository.CustomerRepository;
import com.marcoaga02.carrentalmanager.transaction.TransactionCode;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private TransactionContext transactionContext;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerMapper customerMapper;

	@InjectMocks
	private CustomerServiceImpl customerService;

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";

	private static final String A_FIRSTNAME = "aFirstname";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";

	private static final String A_LASTNAME = "aLastname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final Long AN_ID = 10L;
	private static final Long ANOTHER_ID = 15L;

	private Customer customer, anotherCustomer;
	private CustomerViewModel customerViewModel, anotherCustomerViewModel;

	@BeforeEach
	void setUp() {
		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerViewModel = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);

		anotherCustomer = new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
		anotherCustomerViewModel = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE,
				ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
	}

	// Required by the strict stubbing of MockitoExtension
	private void stubTransaction() {
		when(transactionContext.customerRepository()).thenReturn(customerRepository);
		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(transactionContext)));
	}

	@Nested
	class GetAllCustomers {

		@Test
		void testGetAllCustomersWhenThereAreNoCustomersReturnAnEmptyList() {
			stubTransaction();

			when(customerRepository.findAllActive()).thenReturn(Collections.emptyList());

			List<CustomerViewModel> result = customerService.getAllCustomers();

			assertThat(result).isEmpty();

			verify(customerRepository).findAllActive();
			verifyNoMoreInteractions(customerRepository);
			verifyNoInteractions(customerMapper);
		}

		@Test
		void testGetAllCustomersWhenThereIsOnlyOneCustomerReturnAListOfOneElement() {
			stubTransaction();

			when(customerRepository.findAllActive()).thenReturn(List.of(customer));
			when(customerMapper.toViewModel(customer)).thenReturn(customerViewModel);

			List<CustomerViewModel> result = customerService.getAllCustomers();

			assertThat(result).hasSize(1).first().isEqualTo(customerViewModel);

			InOrder inOrder = inOrder(customerRepository, customerMapper);
			inOrder.verify(customerRepository).findAllActive();
			inOrder.verify(customerMapper).toViewModel(customer);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllCustomersWhenThereAreMultipleCustomersReturnAListWithAllElements() {
			stubTransaction();

			when(customerRepository.findAllActive()).thenReturn(List.of(customer, anotherCustomer));
			when(customerMapper.toViewModel(customer)).thenReturn(customerViewModel);
			when(customerMapper.toViewModel(anotherCustomer)).thenReturn(anotherCustomerViewModel);

			List<CustomerViewModel> result = customerService
					.getAllCustomers()
					.stream()
					.sorted(Comparator.comparing(CustomerViewModel::getId))
					.collect(Collectors.toList());

			assertThat(result)
					.hasSize(2)
					.containsExactlyInAnyOrder(customerViewModel, anotherCustomerViewModel);

			verify(customerRepository).findAllActive();
			verify(customerMapper).toViewModel(customer);
			verify(customerMapper).toViewModel(anotherCustomer);
			verifyNoMoreInteractions(customerRepository, customerMapper);
		}

	}

	@Nested
	class CreateCustomer {

		@Nested
		class InputValidation {

			@Test
			void testCreateCustomerWhenTheInputIsNullThrowIllegalArgumentException() {
				assertThatThrownBy(() -> customerService.createCustomer(null))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("customerViewModel must not be null");
			}

			@ParameterizedTest
			@NullSource
			@ValueSource(strings = { "", " ", " \t" })
			void testCreateCustomerWhenTaxIdCodeIsNullOrBlankThrowIllegalArgumentException(
					String invalidTaxIdCode) {
				CustomerViewModel invalidCustomer = new CustomerViewModel(AN_ID, invalidTaxIdCode,
						A_FIRSTNAME, A_LASTNAME);

				assertThatThrownBy(() -> customerService.createCustomer(invalidCustomer))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("taxIdCode must not be blank");
			}

			@ParameterizedTest
			@NullSource
			@ValueSource(strings = { "", " ", " \t" })
			void testCreateCustomerWhenFirstnameIsNullOrBlankThrowIllegalArgumentException(
					String invalidFirstname) {
				CustomerViewModel invalidCustomer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE,
						invalidFirstname, A_LASTNAME);

				assertThatThrownBy(() -> customerService.createCustomer(invalidCustomer))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("firstname must not be blank");
			}

			@ParameterizedTest
			@NullSource
			@ValueSource(strings = { "", " ", " \t" })
			void testCreateCustomerWhenLastnameIsNullOrBlankThrowIllegalArgumentException(
					String invalidLastname) {
				CustomerViewModel invalidCustomer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE,
						A_FIRSTNAME, invalidLastname);

				assertThatThrownBy(() -> customerService.createCustomer(invalidCustomer))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("lastname must not be blank");
			}

		}

		@Test
		void testCreateCustomerWhenInputIsValidAddTheNewCustomer() {
			stubTransaction();

			CustomerViewModel inputViewModel = new CustomerViewModel(null, A_TAX_ID_CODE,
					A_FIRSTNAME, A_LASTNAME);

			when(customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE))
					.thenReturn(Optional.empty());
			when(customerMapper.toEntity(inputViewModel)).thenReturn(customer);
			when(customerRepository.save(customer)).thenReturn(customer);
			when(customerMapper.toViewModel(customer)).thenReturn(customerViewModel);

			CustomerViewModel result = customerService.createCustomer(inputViewModel);

			assertThat(result).isEqualTo(customerViewModel);

			InOrder inOrder = inOrder(customerRepository, customerMapper);
			inOrder.verify(customerRepository).findActiveByTaxIdCode(A_TAX_ID_CODE);
			inOrder.verify(customerMapper).toEntity(inputViewModel);
			inOrder.verify(customerRepository).save(customer);
			inOrder.verify(customerMapper).toViewModel(customer);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testCreateCustomerWhenExistAnActiveCustomerWithSameTaxIdCodeThrowsDuplicateTaxIdCodeException() {
			stubTransaction();

			CustomerViewModel inputViewModel = new CustomerViewModel(null, A_TAX_ID_CODE,
					A_FIRSTNAME, A_LASTNAME);

			when(customerRepository.findActiveByTaxIdCode(A_TAX_ID_CODE))
					.thenReturn(Optional.of(customer));

			assertThatThrownBy(() -> customerService.createCustomer(inputViewModel))
					.isInstanceOf(DuplicateTaxIdCodeException.class)
					.hasMessage("A customer with taxIdCode '" + A_TAX_ID_CODE + "' already exists");

			verify(customerRepository).findActiveByTaxIdCode(A_TAX_ID_CODE);
			verifyNoMoreInteractions(customerRepository);
			verifyNoInteractions(customerMapper);
		}

	}

	@Nested
	class DeleteCustomer {

		@Test
		void testDeleteCustomerWhenInputIsNullThrowIllegalArgumentException() {
			assertThatThrownBy(() -> customerService.deleteCustomer(null))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("customerId must not be null");
		}

		@Test
		void testDeleteCustomerWhenIdIsValidDeleteTheCustomer() {
			stubTransaction();

			when(customerRepository.findActiveById(AN_ID)).thenReturn(Optional.of(customer));
			when(customerRepository.save(customer)).thenReturn(customer);

			customerService.deleteCustomer(AN_ID);

			ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

			InOrder inOrder = inOrder(customerRepository);
			inOrder.verify(customerRepository).findActiveById(AN_ID);
			inOrder.verify(customerRepository).save(customerCaptor.capture());
			inOrder.verifyNoMoreInteractions();

			Customer savedCustomer = customerCaptor.getValue();
			assertThat(savedCustomer.getDeleted()).isTrue();
			assertThat(savedCustomer).isSameAs(customer);

			verifyNoInteractions(customerMapper);
		}

		@Test
		void testDeleteCustomerWhenThereIsNoActiveCustomerWithSameIdThrowsCustomerNotFoundException() {
			stubTransaction();

			when(customerRepository.findActiveById(ANOTHER_ID)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> customerService.deleteCustomer(ANOTHER_ID))
					.isInstanceOf(CustomerNotFoundException.class)
					.hasMessage("Customer with id '" + ANOTHER_ID + "' not found");

			verify(customerRepository).findActiveById(ANOTHER_ID);
			verifyNoMoreInteractions(customerRepository);
			verifyNoInteractions(customerMapper);
		}

	}

}
