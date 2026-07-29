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

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marcoaga02.carrentalmanager.exception.CarAlreadyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.CustomerNotFoundException;
import com.marcoaga02.carrentalmanager.exception.RentalNotFoundException;
import com.marcoaga02.carrentalmanager.mapper.RentalMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.repository.CarRepository;
import com.marcoaga02.carrentalmanager.repository.CustomerRepository;
import com.marcoaga02.carrentalmanager.repository.RentalRepository;
import com.marcoaga02.carrentalmanager.transaction.TransactionCode;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
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
	private CarRepository carRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private RentalMapper rentalMapper;

	private RentalServiceImpl rentalService;

	private static final Long A_CAR_ID = 1L;
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

	private static final Long A_CUSTOMER_ID = 3L;
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

	private final Clock fixedClock = Clock.fixed(A_START_DATE.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private Rental rental, anotherRental;
	private RentalViewModel rentalViewModel, anotherRentalViewModel;

	private Car car;

	private Customer customer;

	@BeforeEach
	void setup() {
		rentalService = new RentalServiceImpl(transactionManager, rentalMapper, fixedClock);

		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		rental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);
		rentalViewModel = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS, A_FULLNAME,
				A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);

		Car anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);
		Customer anotherCustomer = new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
		anotherRental = new Rental(anotherCar, anotherCustomer, ANOTHER_START_DATE, ANOTHER_NUMBER_OF_DAYS);
		anotherRentalViewModel = new RentalViewModel(ANOTHER_RENTAL_ID, ANOTHER_START_DATE, ANOTHER_END_DATE,
				ANOTHER_NUMBER_OF_DAYS, ANOTHER_FULLNAME, ANOTHER_CAR_DESCRIPTION, ANOTHER_TOTAL_AMOUNT);
	}

	// Required by the strict stubbing of MockitoExtension
	private RentalServiceImplTest stubTransaction() {
		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(transactionContext)));

		return this;
	}

	private RentalServiceImplTest withCarRepository() {
		when(transactionContext.carRepository()).thenReturn(carRepository);

		return this;
	}

	private RentalServiceImplTest withCustomerRepository() {
		when(transactionContext.customerRepository()).thenReturn(customerRepository);

		return this;
	}

	private RentalServiceImplTest withRentalRepository() {
		when(transactionContext.rentalRepository()).thenReturn(rentalRepository);

		return this;
	}

	private void fullStubTransaction() {
		stubTransaction().withCarRepository().withCustomerRepository().withRentalRepository();
	}

	@Nested
	class GetAllRentals {

		@Test
		void testGetAllRentalsWhenThereAreNoRentalsReturnAnEmptyList() {
			stubTransaction().withRentalRepository();

			when(rentalRepository.findAllActive()).thenReturn(Collections.emptyList());

			List<RentalViewModel> result = rentalService.getAllActiveRentals();

			assertThat(result).isEmpty();

			verify(rentalRepository).findAllActive();
			verifyNoMoreInteractions(rentalRepository);
			verifyNoInteractions(rentalMapper);
		}

		@Test
		void testGetAllRentalsWhenThereIsOnlyOneRentalReturnAListOfOneElement() {
			stubTransaction().withRentalRepository();

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
			stubTransaction().withRentalRepository();

			when(rentalRepository.findAllActive()).thenReturn(List.of(rental, anotherRental));
			when(rentalMapper.toViewModel(rental)).thenReturn(rentalViewModel);
			when(rentalMapper.toViewModel(anotherRental)).thenReturn(anotherRentalViewModel);

			List<RentalViewModel> result = rentalService
					.getAllActiveRentals()
					.stream()
					.sorted(Comparator.comparing(RentalViewModel::getId))
					.collect(Collectors.toList());

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(rentalViewModel, anotherRentalViewModel);

			verify(rentalRepository).findAllActive();
			verify(rentalMapper).toViewModel(rental);
			verify(rentalMapper).toViewModel(anotherRental);
			verifyNoMoreInteractions(rentalRepository, rentalMapper);
		}

	}

	@Nested
	class createRental {

		@Nested
		class InputValidation {

			@Test
			void testCreateRentalWhenTheInputIsNullThrowIllegalArgumentException() {
				assertThatThrownBy(() -> rentalService.createRental(null))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("request must not be null");
			}

			@Test
			void testCreateRentalWhenCarIdIsNullThrowIllegalArgumentException() {
				RentalCreationRequest illegalRequest = new RentalCreationRequest(null, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);

				assertThatThrownBy(() -> rentalService.createRental(illegalRequest))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("carId must not be null");
			}

			@Test
			void testCreateRentalWhenCustomerIdIsNullThrowIllegalArgumentException() {
				RentalCreationRequest illegalRequest = new RentalCreationRequest(A_CAR_ID, null, A_NUMBER_OF_DAYS);

				assertThatThrownBy(() -> rentalService.createRental(illegalRequest))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("customerId must not be null");
			}

			@Test
			void testCreateRentalWhenDaysIsNullThrowIllegalArgumentException() {
				RentalCreationRequest illegalRequest = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, null);

				assertThatThrownBy(() -> rentalService.createRental(illegalRequest))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("days must not be null");
			}

			@ParameterizedTest
			@ValueSource(ints = { 0, -1 })
			void testCreateRentalWhenDaysIsNotPositiveThrowIllegalArgumentException(Integer invalidDays) {
				RentalCreationRequest illegalRequest = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, invalidDays);

				assertThatThrownBy(() -> rentalService.createRental(illegalRequest))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("days must be a positive integer");
			}

		}

		@Test
		void testCreateRentalWhenInputIsValidAddTheNewRental() {
			fullStubTransaction();

			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);

			when(carRepository.findActiveById(A_CAR_ID)).thenReturn(Optional.of(car));
			when(customerRepository.findActiveById(A_CUSTOMER_ID)).thenReturn(Optional.of(customer));
			when(rentalRepository.findActiveByCarId(A_CAR_ID)).thenReturn(Optional.empty());

			Rental savedRental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);
			when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
			when(rentalMapper.toViewModel(savedRental)).thenReturn(rentalViewModel);

			RentalViewModel result = rentalService.createRental(request);

			assertThat(result).isEqualTo(rentalViewModel);

			ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);

			InOrder inOrder = inOrder(carRepository, customerRepository, rentalRepository, rentalMapper);
			inOrder.verify(carRepository).findActiveById(A_CAR_ID);
			inOrder.verify(customerRepository).findActiveById(A_CUSTOMER_ID);
			inOrder.verify(rentalRepository).findActiveByCarId(A_CAR_ID);
			inOrder.verify(rentalRepository).save(rentalCaptor.capture());
			inOrder.verify(rentalMapper).toViewModel(savedRental);
			inOrder.verifyNoMoreInteractions();

			Rental capturedRental = rentalCaptor.getValue();
			assertThat(capturedRental.getCar()).isEqualTo(car);
			assertThat(capturedRental.getCustomer()).isEqualTo(customer);
			assertThat(capturedRental.getDays()).isEqualTo(A_NUMBER_OF_DAYS);
			assertThat(capturedRental.getStartDate()).isEqualTo(A_START_DATE);
		}

		@Test
		void testCreateRentalWhenCarIdIsInvalidThrowsCarNotFoundException() {
			stubTransaction().withCarRepository();

			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);

			when(carRepository.findActiveById(A_CAR_ID)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> rentalService.createRental(request))
					.isInstanceOf(CarNotFoundException.class)
					.hasMessage("Car with id '" + A_CAR_ID + "' not found");

			verify(carRepository).findActiveById(A_CAR_ID);
			verifyNoMoreInteractions(carRepository);
			verifyNoInteractions(rentalMapper);
		}

		@Test
		void testCreateRentalWhenCustomerIdIsInvalidThrowsCustomerNotFoundException() {
			stubTransaction().withCarRepository().withCustomerRepository();

			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);

			when(carRepository.findActiveById(A_CAR_ID)).thenReturn(Optional.of(car));
			when(customerRepository.findActiveById(A_CUSTOMER_ID)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> rentalService.createRental(request))
					.isInstanceOf(CustomerNotFoundException.class)
					.hasMessage("Customer with id '" + A_CUSTOMER_ID + "' not found");

			verify(carRepository).findActiveById(A_CAR_ID);
			verify(customerRepository).findActiveById(A_CUSTOMER_ID);
			verifyNoMoreInteractions(carRepository, customerRepository);
			verifyNoInteractions(rentalMapper);
		}

		@Test
		void testCreateRentalWhenTheCarIsAlreadyRentedThrowCarAlreadyRentedException() {
			fullStubTransaction();

			RentalCreationRequest request = new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS);

			when(carRepository.findActiveById(A_CAR_ID)).thenReturn(Optional.of(car));
			when(customerRepository.findActiveById(A_CUSTOMER_ID)).thenReturn(Optional.of(customer));
			when(rentalRepository.findActiveByCarId(A_CAR_ID)).thenReturn(Optional.of(rental));

			assertThatThrownBy(() -> rentalService.createRental(request))
					.isInstanceOf(CarAlreadyRentedException.class)
					.hasMessage("Car with id '" + A_CAR_ID + "' is already rented");

			verify(carRepository).findActiveById(A_CAR_ID);
			verify(customerRepository).findActiveById(A_CUSTOMER_ID);
			verify(rentalRepository).findActiveByCarId(A_CAR_ID);
			verifyNoMoreInteractions(carRepository, customerRepository, rentalRepository);
			verifyNoInteractions(rentalMapper);
		}

	}

	@Nested
	class deleteRental {

		@Test
		void testDeleteRentalWhenInputIsNullThrowIllegalArgumentException() {
			assertThatThrownBy(() -> rentalService.deleteRental(null))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("rentalId must not be null");
		}

		@Test
		void testDeleteRentalWhenIdIsValidDeleteTheRental() {
			stubTransaction().withRentalRepository();

			when(rentalRepository.findActiveById(A_RENTAL_ID)).thenReturn(Optional.of(rental));

			rentalService.deleteRental(A_RENTAL_ID);

			InOrder inOrder = inOrder(rentalRepository);
			inOrder.verify(rentalRepository).findActiveById(A_RENTAL_ID);
			inOrder.verify(rentalRepository).deleteById(A_RENTAL_ID);
			inOrder.verifyNoMoreInteractions();

			verifyNoInteractions(rentalMapper);
		}

		@Test
		void testDeleteRentalWhenThereIsNoActiveRentalWithSameIdThrowsRentalNotFoundException() {
			stubTransaction().withRentalRepository();

			when(rentalRepository.findActiveById(ANOTHER_RENTAL_ID)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> rentalService.deleteRental(ANOTHER_RENTAL_ID))
					.isInstanceOf(RentalNotFoundException.class)
					.hasMessage("Rental with id '" + ANOTHER_RENTAL_ID + "' not found");

			verify(rentalRepository).findActiveById(ANOTHER_RENTAL_ID);
			verifyNoMoreInteractions(rentalRepository);
			verifyNoInteractions(rentalMapper);
		}

	}

}
