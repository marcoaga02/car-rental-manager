package com.marcoaga02.carrentalmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateTaxIdCodeException;
import com.marcoaga02.carrentalmanager.service.CustomerService;
import com.marcoaga02.carrentalmanager.view.CustomerView;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

	private static final Long AN_ID = 10L;
	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final Long ANOTHER_ID = 15L;
	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	@Mock
	private CustomerService customerService;

	@Mock
	private CustomerView customerView;

	@InjectMocks
	private CustomerController customerController;

	private CustomerViewModel customer, anotherCustomer;

	@BeforeEach
	void setUp() {
		customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
	}

	@Nested
	class GetAllCustomers {

		@Test
		void testGetAllCustomersWhenThereAreNoCustomerCallsShowAllCustomersWithEmptyList() {
			when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

			customerController.getAllCustomers();

			InOrder inOrder = inOrder(customerService, customerView);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(customerView).showAllCustomers(Collections.emptyList());
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllCustomersWhenThereIsOnlyOneCustomerCallsShowAllCustomersWithAListWithOneElement() {
			when(customerService.getAllCustomers()).thenReturn(List.of(customer));

			customerController.getAllCustomers();

			InOrder inOrder = inOrder(customerService, customerView);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(customerView).showAllCustomers(List.of(customer));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllCustomersWhenThereAreSeveralCustomersCallsShowAllCustomersWithAListWithAllElements() {
			when(customerService.getAllCustomers()).thenReturn(List.of(customer, anotherCustomer));

			customerController.getAllCustomers();

			InOrder inOrder = inOrder(customerService, customerView);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(customerView).showAllCustomers(List.of(customer, anotherCustomer));
			inOrder.verifyNoMoreInteractions();
		}

	}

	@Nested
	class CreateCustomer {

		@Test
		void testCreateCustomerWhenSuccessfulRefreshesTheCustomerList() {
			when(customerService.getAllCustomers()).thenReturn(List.of(customer));

			customerController.createCustomer(customer);

			InOrder inOrder = inOrder(customerService, customerView);
			inOrder.verify(customerService).createCustomer(customer);
			inOrder.verify(customerView).clearFields();
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(customerView).showAllCustomers(List.of(customer));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testCreateCustomerWhenDuplicateTaxIdCodeShowsErrorAndDoesNotRefreshList() {
			DuplicateTaxIdCodeException exception = new DuplicateTaxIdCodeException(A_TAX_ID_CODE);
			doThrow(exception).when(customerService).createCustomer(customer);

			customerController.createCustomer(customer);

			verify(customerService).createCustomer(customer);
			verify(customerView).showError(exception.getMessage());
			verify(customerView, never()).clearFields();
			verify(customerService, never()).getAllCustomers();
			verify(customerView, never()).showAllCustomers(any());
		}

		@Test
		void testCreateCustomerWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			CustomerViewModel request = new CustomerViewModel(AN_ID, null, A_FIRSTNAME, A_LASTNAME);
			IllegalArgumentException exception = new IllegalArgumentException("taxIdCode must not be blank");
			doThrow(exception).when(customerService).createCustomer(request);

			customerController.createCustomer(request);

			verify(customerService).createCustomer(request);
			verify(customerView).showError(exception.getMessage());
			verify(customerView, never()).clearFields();
			verify(customerService, never()).getAllCustomers();
			verify(customerView, never()).showAllCustomers(any());
		}

	}

	@Nested
	class DeleteCustomer {

		@Test
		void testDeleteCustomerWhenSuccessfulRefreshesTheCustomerList() {
			when(customerService.getAllCustomers()).thenReturn(List.of(customer));

			customerController.deleteCustomer(ANOTHER_ID);

			InOrder inOrder = inOrder(customerService, customerView);
			inOrder.verify(customerService).deleteCustomer(ANOTHER_ID);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(customerView).showAllCustomers(List.of(customer));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testDeleteCustomerWhenCustomerNotFoundShowsErrorAndDoesNotRefreshList() {
			CustomerNotFoundException exception = new CustomerNotFoundException(AN_ID);
			doThrow(exception).when(customerService).deleteCustomer(AN_ID);

			customerController.deleteCustomer(AN_ID);

			verify(customerService).deleteCustomer(AN_ID);
			verify(customerView).showError(exception.getMessage());
			verify(customerService, never()).getAllCustomers();
			verify(customerView, never()).showAllCustomers(any());
		}

		@Test
		void testDeleteCustomerWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			IllegalArgumentException exception = new IllegalArgumentException("customerId must not be null");
			doThrow(exception).when(customerService).deleteCustomer(null);

			customerController.deleteCustomer(null);

			verify(customerService).deleteCustomer(null);
			verify(customerView).showError(exception.getMessage());
			verify(customerService, never()).getAllCustomers();
			verify(customerView, never()).showAllCustomers(any());
		}

	}
}
