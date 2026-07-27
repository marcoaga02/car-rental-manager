package com.marcoaga02.carrentalmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marcoaga02.carrentalmanager.mapper.RentalMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.repository.RentalRepository;
import com.marcoaga02.carrentalmanager.transaction.TransactionCode;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private TransactionContext transactionContext;

	@Mock
	private RentalRepository rentalRepository;

	@Mock
	private RentalMapper rentalMapper;

	@InjectMocks
	private RentalServiceImpl rentalService;

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);
	private static final String A_CAR_DESCRIPTION = "aCarDescription";

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);
	private static final String ANOTHER_CAR_DESCRIPTION = "anotherCarDescription";

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";
	private static final String A_FULLNAME = "aFullname";

	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";
	private static final String ANOTHER_FULLNAME = "anotherFullname";

	private static final Long A_RENTAL_ID = 5L;
	private static final Long ANOTHER_RENTAL_ID = 6L;

	private static final Integer A_NUMBER_OF_DAYS = 6;
	private static final Integer ANOTHER_NUMBER_OF_DAYS = 3;

	private static final LocalDate A_START_DATE = LocalDate.parse("2026-05-10");
	private static final LocalDate ANOTHER_START_DATE = LocalDate.parse("2026-06-20");

	private static final LocalDate AN_END_DATE = LocalDate.parse("2026-05-16");
	private static final LocalDate ANOTHER_END_DATE = LocalDate.parse("2026-06-23");

	private static final BigDecimal A_TOTAL_AMOUNT = BigDecimal.valueOf(61.2);
	private static final BigDecimal ANOTHER_TOTAL_AMOUNT = BigDecimal.valueOf(12.9);

	private Rental rental, anotherRental;
	private RentalViewModel rentalViewModel, anotherRentalViewModel;

	@BeforeEach
	void setup() {
		Car car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		Customer customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		rental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);
		rentalViewModel = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE,
				A_NUMBER_OF_DAYS, A_FULLNAME, A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);

		Car anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);
		Customer anotherCustomer = new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME,
				ANOTHER_LASTNAME);
		anotherRental = new Rental(anotherCar, anotherCustomer, ANOTHER_START_DATE,
				ANOTHER_NUMBER_OF_DAYS);
		anotherRentalViewModel = new RentalViewModel(ANOTHER_RENTAL_ID, ANOTHER_START_DATE,
				ANOTHER_END_DATE, ANOTHER_NUMBER_OF_DAYS, ANOTHER_FULLNAME, ANOTHER_CAR_DESCRIPTION,
				ANOTHER_TOTAL_AMOUNT);
	}

	// Required by the strict stubbing of MockitoExtension
	private void stubTransaction() {
		when(transactionContext.rentalRepository()).thenReturn(rentalRepository);
		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(transactionContext)));
	}

	@Nested
	class GetAllRentals {

		@Test
		void testGetAllRentalsWhenThereAreNoRentalsReturnAnEmptyList() {
			stubTransaction();

			when(rentalRepository.findAllActive()).thenReturn(Collections.emptyList());

			List<RentalViewModel> result = rentalService.getAllActiveRentals();

			assertThat(result).isEmpty();

			verify(rentalRepository).findAllActive();
			verifyNoMoreInteractions(rentalRepository);
			verifyNoInteractions(rentalMapper);
		}

		@Test
		void testGetAllRentalsWhenThereIsOnlyOneRentalReturnAListOfOneElement() {
			stubTransaction();

			when(rentalRepository.findAllActive()).thenReturn(List.of(rental));
			when(rentalMapper.toViewModel(rental)).thenReturn(rentalViewModel);

			List<RentalViewModel> result = rentalService.getAllActiveRentals();

			assertThat(result).hasSize(1).first().isEqualTo(rentalViewModel);

			InOrder inOrder = inOrder(rentalRepository, rentalMapper);
			inOrder.verify(rentalRepository).findAllActive();
			inOrder.verify(rentalMapper).toViewModel(rental);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllRentalsWhenThereAreMultipleRentalsReturnAListWithAllElements() {
			stubTransaction();

			when(rentalRepository.findAllActive()).thenReturn(List.of(rental, anotherRental));
			when(rentalMapper.toViewModel(rental)).thenReturn(rentalViewModel);
			when(rentalMapper.toViewModel(anotherRental)).thenReturn(anotherRentalViewModel);

			List<RentalViewModel> result = rentalService
					.getAllActiveRentals()
					.stream()
					.sorted(Comparator.comparing(RentalViewModel::getId))
					.collect(Collectors.toList());

			assertThat(result)
					.hasSize(2)
					.containsExactlyInAnyOrder(rentalViewModel, anotherRentalViewModel);

			verify(rentalRepository).findAllActive();
			verify(rentalMapper).toViewModel(rental);
			verify(rentalMapper).toViewModel(anotherRental);
			verifyNoMoreInteractions(rentalRepository, rentalMapper);
		}

	}

}
