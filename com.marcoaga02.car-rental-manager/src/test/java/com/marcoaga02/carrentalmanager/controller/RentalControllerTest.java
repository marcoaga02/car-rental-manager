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
import com.marcoaga02.carrentalmanager.service.RentalService;
import com.marcoaga02.carrentalmanager.view.RentalView;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

@ExtendWith(MockitoExtension.class)
class RentalControllerTest {

	private static final Long A_CAR_ID = 10L;
	private static final Long A_CUSTOMER_ID = 11L;
	private static final Long A_RENTAL_ID = 12L;

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

	@Mock
	private RentalService rentalService;

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

			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			when(rentalService.getAllActiveRentals()).thenReturn(List.of(rental));

			rentalController.createRental(request);

			InOrder inOrder = inOrder(rentalService, rentalView);
			inOrder.verify(rentalService).createRental(request);
			inOrder.verify(rentalView).clearFields();
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(List.of(rental));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testCreateCarWhenCarNotFoundShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			CarNotFoundException exception = new CarNotFoundException(A_CAR_ID);
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
		}

		@Test
		void testCreateCarWhenCustomerNotFoundShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			CustomerNotFoundException exception = new CustomerNotFoundException(A_CUSTOMER_ID);
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
		}

		@Test
		void testCreateCarWhenAreIsAlreadyRentedShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			CarAlreadyRentedException exception = new CarAlreadyRentedException(A_CAR_ID);
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
		}

		@Test
		void testCreateCarWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			RentalCreationRequest request = new RentalCreationRequest(null, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);
			IllegalArgumentException exception = new IllegalArgumentException("carId must not be blank");
			doThrow(exception).when(rentalService).createRental(request);

			rentalController.createRental(request);

			verify(rentalService).createRental(request);
			verify(rentalView).showError(exception.getMessage());
			verify(rentalView, never()).clearFields();
			verify(rentalService, never()).getAllActiveRentals();
			verify(rentalView, never()).showAllRentals(any());
		}

	}

	@Nested
	class DeleteRental {

		@Test
		void testDeleteRentalWhenSuccessfulRefreshesTheRentalList() {
			RentalViewModel rental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS,
					A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
			when(rentalService.getAllActiveRentals()).thenReturn(List.of(rental));

			rentalController.deleteRental(A_RENTAL_ID);

			InOrder inOrder = inOrder(rentalService, rentalView);
			inOrder.verify(rentalService).deleteRental(A_RENTAL_ID);
			inOrder.verify(rentalService).getAllActiveRentals();
			inOrder.verify(rentalView).showAllRentals(List.of(rental));
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
		}

	}

}
