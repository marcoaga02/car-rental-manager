package com.marcoaga02.carrentalmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marcoaga02.carrentalmanager.exception.CarAlreadyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.RentalNotFoundException;
import com.marcoaga02.carrentalmanager.service.CarService;
import com.marcoaga02.carrentalmanager.service.CustomerService;
import com.marcoaga02.carrentalmanager.service.RentalService;
import com.marcoaga02.carrentalmanager.view.RentalView;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

	private static final Long A_RENTAL_ID = 10L;
	private static final LocalDate A_START_DATE = LocalDate.of(2026, Month.JUNE, 24);
	private static final LocalDate AN_END_DATE = LocalDate.of(2026, Month.JUNE, 30);
	private static final Integer A_NUMBER_OF_DAYS = 6;
	private static final String A_CUSTOMER_FULLNAME = "aFirstname aLastname";
	private static final String A_CAR_DESCRIPTION = "aBrand aModel [aCarPlate]";
	private static final BigDecimal A_TOTAL_AMOUNT = new BigDecimal("61.2");

	private static final Long ANOTHER_RENTAL_ID = 11L;
	private static final LocalDate ANOTHER_START_DATE = LocalDate.of(2026, Month.JULY, 10);
	private static final LocalDate ANOTHER_END_DATE = LocalDate.of(2026, Month.JULY, 25);
	private static final Integer ANOTHER_NUMBER_OF_DAYS = 15;
	private static final String ANOTHER_CUSTOMER_FULLNAME = "anotherFirstname anotherLastname";
	private static final String ANOTHER_CAR_DESCRIPTION = "anotherBrand anotherModel [anotherCarPlate]";
	private static final BigDecimal ANOTHER_TOTAL_AMOUNT = new BigDecimal("123.5");

	private static final Long A_CAR_ID = 12L;
	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final Long ANOTHER_CAR_ID = 13L;
	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final Long A_CUSTOMER_ID = 14L;
	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final Long ANOTHER_CUSTOMER_ID = 15L;
	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	@Mock
	private RentalService rentalService;

	@Mock
	private CarService carService;

	@Mock
	private CustomerService customerService;

	@Mock
	private RentalView rentalView;

	@InjectMocks
	private RentalController rentalController;

	@Nested
	class GetAllRentals {

		@Test
		void testGetAllRentalsWhenThereAreNoRentalsCallsShowAllRentalsWithEmptyList() {
			when(rentalService.getAllActiveRentals()).thenReturn(Collections.emptyList());

			rentalController.getAllActiveRentals();

			InOrder inOrder = inOrder(rentalService, rentalView);
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(Collections.emptyList());
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllRentalsWhenThereIsOnlyOneRentalCallsShowAllRentalsWithAListWithOneElement() {
			RentalViewModel rental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS,
					A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
			when(rentalService.getAllActiveRentals()).thenReturn(List.of(rental));

			rentalController.getAllActiveRentals();

			InOrder inOrder = inOrder(rentalService, rentalView);
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(List.of(rental));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllRentalsWhenThereAreSeveralRentalsCallsShowAllRentalsWithAListWithAllElements() {
			RentalViewModel firstRental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS,
					A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
			RentalViewModel secondRental = new RentalViewModel(ANOTHER_RENTAL_ID, ANOTHER_START_DATE, ANOTHER_END_DATE,
					ANOTHER_NUMBER_OF_DAYS, ANOTHER_CUSTOMER_FULLNAME, ANOTHER_CAR_DESCRIPTION, ANOTHER_TOTAL_AMOUNT);
			when(rentalService.getAllActiveRentals()).thenReturn(List.of(firstRental, secondRental));

			rentalController.getAllActiveRentals();

			InOrder inOrder = inOrder(rentalService, rentalView);
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(List.of(firstRental, secondRental));
			inOrder.verifyNoMoreInteractions();
		}

	}

	@Nested
	class CreateRental {

		@Test
		void testCreateRentalWhenSuccessfulRefreshesTheRentalList() {
			RentalViewModel rental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS,
					A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
			CarViewModel car = new CarViewModel(A_CAR_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);

			when(rentalService.getAllActiveRentals()).thenReturn(List.of(rental));
			when(carService.getAvailableCars()).thenReturn(List.of(car));

			rentalController.createRental(request);

			InOrder inOrder = inOrder(rentalService, carService, rentalView);
			inOrder.verify(rentalService).createRental(request);
			inOrder.verify(rentalView).clearFields();
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(List.of(rental));
			inOrder.verify(carService).getAvailableCars();
			inOrder.verify(rentalView).showAvailableCars(List.of(car));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testCreateRentalWhenCarNotFoundShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			CarNotFoundException exception = new CarNotFoundException(A_CAR_ID);
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
			verify(carService, never()).getAvailableCars();
			verify(rentalView, never()).showAvailableCars(any());
		}

		@Test
		void testCreateRentalWhenCustomerNotFoundShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			CustomerNotFoundException exception = new CustomerNotFoundException(A_CUSTOMER_ID);
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
			verify(carService, never()).getAvailableCars();
			verify(rentalView, never()).showAvailableCars(any());
		}

		@Test
		void testCreateRentalWhenAreIsAlreadyRentedShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			CarAlreadyRentedException exception = new CarAlreadyRentedException(A_CAR_ID);
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
			verify(carService, never()).getAvailableCars();
			verify(rentalView, never()).showAvailableCars(any());
		}

		@Test
		void testCreateRentalWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(null, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			IllegalArgumentException exception = new IllegalArgumentException("carId must not be blank");
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
			verify(carService, never()).getAvailableCars();
			verify(rentalView, never()).showAvailableCars(any());
		}

	}

	@Nested
	class DeleteRental {

		@Test
		void testDeleteRentalWhenSuccessfulRefreshesTheRentalList() {
			RentalViewModel rental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS,
					A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
			CarViewModel car = new CarViewModel(A_CAR_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

			when(rentalService.getAllActiveRentals()).thenReturn(List.of(rental));
			when(carService.getAvailableCars()).thenReturn(List.of(car));

			rentalController.deleteRental(A_RENTAL_ID);

			InOrder inOrder = inOrder(rentalService, carService, rentalView);
			inOrder.verify(rentalService).deleteRental(A_RENTAL_ID);
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(List.of(rental));
			inOrder.verify(carService).getAvailableCars();
			inOrder.verify(rentalView).showAvailableCars(List.of(car));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testDeleteRentalWhenRentalNotFoundShowsErrorAndDoesNotRefreshList() {
			RentalNotFoundException exception = new RentalNotFoundException(A_RENTAL_ID);
			doThrow(exception).when(rentalService).deleteRental(A_RENTAL_ID);

			rentalController.deleteRental(A_RENTAL_ID);

			verify(rentalService).deleteRental(A_RENTAL_ID);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
			verify(carService, never()).getAvailableCars();
			verify(rentalView, never()).showAvailableCars(any());
		}

		@Test
		void testDeleteRentalWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			IllegalArgumentException exception = new IllegalArgumentException("rentalId must not be null");
			doThrow(exception).when(rentalService).deleteRental(A_RENTAL_ID);

			rentalController.deleteRental(A_RENTAL_ID);

			verify(rentalService).deleteRental(A_RENTAL_ID);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
			verify(carService, never()).getAvailableCars();
			verify(rentalView, never()).showAvailableCars(any());
		}

	}

	@Nested
	class LoadAvailableCars {

		@Test
		void testLoadAvailableCarsWhenThereAreNoCarsCallsShowAvailableCarsWithEmptyList() {
			when(carService.getAvailableCars()).thenReturn(Collections.emptyList());

			rentalController.loadAvailableCars();

			InOrder inOrder = inOrder(carService, rentalView);
			inOrder.verify(carService).getAvailableCars();
			inOrder.verify(rentalView).showAvailableCars(Collections.emptyList());
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testLoadAvailableCarsWhenThereIsOnlyOneCarCallsShowAvailableCarsWithAListWithOneElement() {
			CarViewModel car = new CarViewModel(A_CAR_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			when(carService.getAvailableCars()).thenReturn(List.of(car));

			rentalController.loadAvailableCars();

			InOrder inOrder = inOrder(carService, rentalView);
			inOrder.verify(carService).getAvailableCars();
			inOrder.verify(rentalView).showAvailableCars(List.of(car));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testLoadAvailableCarsWhenThereAreSeveralCarsCallsShowAvailableCarsWithAListWithAllElements() {
			CarViewModel firstCar = new CarViewModel(A_CAR_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			CarViewModel secondCar = new CarViewModel(ANOTHER_CAR_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
					ANOTHER_DAILY_RATE);
			when(carService.getAvailableCars()).thenReturn(List.of(firstCar, secondCar));

			rentalController.loadAvailableCars();

			InOrder inOrder = inOrder(carService, rentalView);
			inOrder.verify(carService).getAvailableCars();
			inOrder.verify(rentalView).showAvailableCars(List.of(firstCar, secondCar));
			inOrder.verifyNoMoreInteractions();
		}

	}

	@Nested
	class LoadAvailableCustomers {

		@Test
		void testLoadAvailableCustomersWhenThereAreNoCustomersCallsShowAvailableCustomersWithEmptyList() {
			when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

			rentalController.loadAvailableCustomers();

			InOrder inOrder = inOrder(customerService, rentalView);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(rentalView).showAvailableCustomers(Collections.emptyList());
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testLoadAvailableCustomersWhenThereIsOnlyOneCustomerCallsShowAvailableCustomersWithAListWithOneElement() {
			CustomerViewModel customer = new CustomerViewModel(A_CUSTOMER_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
			when(customerService.getAllCustomers()).thenReturn(List.of(customer));

			rentalController.loadAvailableCustomers();

			InOrder inOrder = inOrder(customerService, rentalView);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(rentalView).showAvailableCustomers(List.of(customer));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testLoadAvailableCustomersWhenThereAreSeveralCustomersCallsShowAvailableCustomersWithAListWithAllElements() {
			CustomerViewModel firstCustomer = new CustomerViewModel(A_CUSTOMER_ID, A_TAX_ID_CODE, A_FIRSTNAME,
					A_LASTNAME);
			CustomerViewModel secondCustomer = new CustomerViewModel(ANOTHER_CUSTOMER_ID, ANOTHER_TAX_ID_CODE,
					ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
			when(customerService.getAllCustomers()).thenReturn(List.of(firstCustomer, secondCustomer));

			rentalController.loadAvailableCustomers();

			InOrder inOrder = inOrder(customerService, rentalView);
			inOrder.verify(customerService).getAllCustomers();
			inOrder.verify(rentalView).showAvailableCustomers(List.of(firstCustomer, secondCustomer));
			inOrder.verifyNoMoreInteractions();
		}

	}

}
