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
import java.time.LocalDate;
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

import com.marcoaga02.carrentalmanager.exception.CarCurrentlyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateCarPlateException;
import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.repository.CarRepository;
import com.marcoaga02.carrentalmanager.repository.RentalRepository;
import com.marcoaga02.carrentalmanager.transaction.TransactionCode;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private TransactionContext transactionContext;

	@Mock
	private CarRepository carRepository;

	@Mock
	private RentalRepository rentalRepository;

	@Mock
	private CarMapper carMapper;

	@InjectMocks
	private CarServiceImpl carService;

	private static final Long AN_ID = 10L;
	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final Long ANOTHER_ID = 13L;
	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final LocalDate A_START_DATE = LocalDate.now();
	private static final Integer A_NUMBER_OF_DAYS = 5;

	private Car car, anotherCar;
	private CarViewModel carViewModel, anotherCarViewModel;
	private Customer customer;

	@BeforeEach
	void setup() {
		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carViewModel = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);
		anotherCarViewModel = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);

		customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
	}

	// Required by the strict stubbing of MockitoExtension
	private CarServiceImplTest stubTransaction() {
		when(transactionContext.carRepository()).thenReturn(carRepository);
		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(transactionContext)));

		return this;
	}

	private void withRentalRepository() {
		when(transactionContext.rentalRepository()).thenReturn(rentalRepository);
	}

	@Nested
	class GetAllCars {

		@Test
		void testGetAllCarsWhenThereAreNoCarsReturnAnEmptyList() {
			stubTransaction();

			when(carRepository.findAllActive()).thenReturn(Collections.emptyList());

			List<CarViewModel> result = carService.getAllCars();

			assertThat(result).isEmpty();

			verify(carRepository).findAllActive();
			verifyNoMoreInteractions(carRepository);
			verifyNoInteractions(carMapper);
		}

		@Test
		void testGetAllCarsWhenThereIsOnlyOneCarReturnAListOfOneElement() {
			stubTransaction();

			when(carRepository.findAllActive()).thenReturn(List.of(car));
			when(carMapper.toViewModel(car)).thenReturn(carViewModel);

			List<CarViewModel> result = carService.getAllCars();

			assertThat(result).hasSize(1).first().isEqualTo(carViewModel);

			InOrder inOrder = inOrder(carRepository, carMapper);
			inOrder.verify(carRepository).findAllActive();
			inOrder.verify(carMapper).toViewModel(car);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllCarsWhenThereAreMultipleCarsReturnAListWithAllElements() {
			stubTransaction();

			when(carRepository.findAllActive()).thenReturn(List.of(car, anotherCar));
			when(carMapper.toViewModel(car)).thenReturn(carViewModel);
			when(carMapper.toViewModel(anotherCar)).thenReturn(anotherCarViewModel);

			List<CarViewModel> result = carService.getAllCars().stream()
					.sorted(Comparator.comparing(CarViewModel::getId)).collect(Collectors.toList());

			assertThat(result).hasSize(2).containsExactlyInAnyOrder(carViewModel, anotherCarViewModel);

			verify(carRepository).findAllActive();
			verify(carMapper).toViewModel(car);
			verify(carMapper).toViewModel(anotherCar);
			verifyNoMoreInteractions(carRepository, carMapper);
		}
	}

	@Nested
	class CreateCar {

		@Nested
		class InputValidation {
			@Test
			void testCreateCarWhenTheInputIsNullThrowIllegalArgumentException() {
				assertThatThrownBy(() -> carService.createCar(null)).isInstanceOf(IllegalArgumentException.class)
						.hasMessage("carViewModel must not be null");
			}

			@ParameterizedTest
			@NullSource
			@ValueSource(strings = { "", " ", " \t" })
			void testCreateCarWhenCarPlateIsNullOrBlankThrowIllegalArgumentException(String invalidPlate) {
				CarViewModel invalidCar = new CarViewModel(AN_ID, invalidPlate, A_BRAND, A_MODEL, A_DAILY_RATE);

				assertThatThrownBy(() -> carService.createCar(invalidCar)).isInstanceOf(IllegalArgumentException.class)
						.hasMessage("carPlate must not be blank");
			}

			@ParameterizedTest
			@NullSource
			@ValueSource(strings = { "", " ", " \t" })
			void testCreateCarWhenBrandIsNullOrBlankThrowIllegalArgumentException(String invalidBrand) {
				CarViewModel invalidCar = new CarViewModel(AN_ID, A_CAR_PLATE, invalidBrand, A_MODEL, A_DAILY_RATE);

				assertThatThrownBy(() -> carService.createCar(invalidCar)).isInstanceOf(IllegalArgumentException.class)
						.hasMessage("brand must not be blank");
			}

			@ParameterizedTest
			@NullSource
			@ValueSource(strings = { "", " ", " \t" })
			void testCreateCarWhenModelIsNullOrBlankThrowIllegalArgumentException(String invalidModel) {
				CarViewModel invalidCar = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, invalidModel, A_DAILY_RATE);

				assertThatThrownBy(() -> carService.createCar(invalidCar)).isInstanceOf(IllegalArgumentException.class)
						.hasMessage("model must not be blank");
			}

			@Test
			void testCreateCarWhenDailyRateIsNullThrowIllegalArgumentException() {
				CarViewModel invalidCar = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, null);
				assertThatThrownBy(() -> carService.createCar(invalidCar)).isInstanceOf(IllegalArgumentException.class)
						.hasMessage("dailyRate must not be null");
			}

			@ParameterizedTest
			@ValueSource(doubles = { 0.0, -0.01, -10.2 })
			void testCreateCarWhenDailyRateIsZeroOrNegativeThrowIllegalArgumentException(double invalidRate) {
				CarViewModel invalidCar = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL,
						BigDecimal.valueOf(invalidRate));

				assertThatThrownBy(() -> carService.createCar(invalidCar)).isInstanceOf(IllegalArgumentException.class)
						.hasMessage("dailyRate must be positive");
			}
		}

		@Test
		void testCreateCarWhenInputIsValidAddTheNewCar() {
			stubTransaction();

			CarViewModel inputViewModel = new CarViewModel(null, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

			when(carRepository.findActiveByCarPlate(A_CAR_PLATE)).thenReturn(Optional.empty());
			when(carMapper.toEntity(inputViewModel)).thenReturn(car);
			when(carRepository.save(car)).thenReturn(car);
			when(carMapper.toViewModel(car)).thenReturn(carViewModel);

			CarViewModel result = carService.createCar(inputViewModel);

			assertThat(result).isEqualTo(carViewModel);

			InOrder inOrder = inOrder(carRepository, carMapper);
			inOrder.verify(carRepository).findActiveByCarPlate(A_CAR_PLATE);
			inOrder.verify(carMapper).toEntity(inputViewModel);
			inOrder.verify(carRepository).save(car);
			inOrder.verify(carMapper).toViewModel(car);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testCreateCarWhenExistAnActiveCarWithSameCarPlateThrowsDuplicateCarPlateException() {
			stubTransaction();

			CarViewModel inputViewModel = new CarViewModel(null, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

			when(carRepository.findActiveByCarPlate(A_CAR_PLATE)).thenReturn(Optional.of(car));

			assertThatThrownBy(() -> carService.createCar(inputViewModel))
					.isInstanceOf(DuplicateCarPlateException.class)
					.hasMessage("A car with carPlate '" + A_CAR_PLATE + "' already exists");

			verify(carRepository).findActiveByCarPlate(A_CAR_PLATE);
			verifyNoMoreInteractions(carRepository);
			verifyNoInteractions(carMapper);
		}

	}

	@Nested
	class DeleteCar {

		@Test
		void testDeleteCarWhenInputIsNullThrowIllegalArgumentException() {
			assertThatThrownBy(() -> carService.deleteCar(null)).isInstanceOf(IllegalArgumentException.class)
					.hasMessage("carId must not be null");
		}

		@Test
		void testDeleteCarWhenIdIsValidDeleteTheCar() {
			stubTransaction().withRentalRepository();

			when(carRepository.findActiveById(AN_ID)).thenReturn(Optional.of(car));
			when(rentalRepository.existsActiveByCarId(AN_ID)).thenReturn(false);
			when(carRepository.save(car)).thenReturn(car);

			carService.deleteCar(AN_ID);

			ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);

			InOrder inOrder = inOrder(carRepository, rentalRepository);
			inOrder.verify(carRepository).findActiveById(AN_ID);
			inOrder.verify(rentalRepository).existsActiveByCarId(AN_ID);
			inOrder.verify(carRepository).save(carCaptor.capture());
			inOrder.verifyNoMoreInteractions();

			Car savedCar = carCaptor.getValue();
			assertThat(savedCar.getDeleted()).isTrue();
			assertThat(savedCar).isSameAs(car);

			verifyNoInteractions(carMapper);
		}

		@Test
		void testDeleteCarWhenThereIsNoActiveCarWithSameIdThrowsCarNotFoundException() {
			stubTransaction();

			when(carRepository.findActiveById(ANOTHER_ID)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> carService.deleteCar(ANOTHER_ID)).isInstanceOf(CarNotFoundException.class)
					.hasMessage("Car with id '" + ANOTHER_ID + "' not found");

			verify(carRepository).findActiveById(ANOTHER_ID);
			verifyNoMoreInteractions(carRepository);
			verifyNoInteractions(carMapper, rentalRepository);
		}

		@Test
		void testDeleteCarWhenIsCurrentlyRentedThrowCarCurrentlyRentedException() {
			stubTransaction().withRentalRepository();

			when(carRepository.findActiveById(AN_ID)).thenReturn(Optional.of(car));
			when(rentalRepository.existsActiveByCarId(AN_ID)).thenReturn(true);

			assertThatThrownBy(() -> carService.deleteCar(AN_ID)).isInstanceOf(CarCurrentlyRentedException.class)
					.hasMessage("Car with id '" + AN_ID + "' is currently rented and cannot be deleted");

			verify(carRepository).findActiveById(AN_ID);
			verify(rentalRepository).existsActiveByCarId(AN_ID);
			verifyNoMoreInteractions(carRepository, rentalRepository);
			verifyNoInteractions(carMapper);
		}

	}

	@Nested
	class GetAvailableCars {

		@Test
		void testGetAvailableCarsWhenThereAreNoCarsReturnsAnEmptyList() {
			stubTransaction().withRentalRepository();

			when(carRepository.findAllActive()).thenReturn(Collections.emptyList());
			when(rentalRepository.findAllActive()).thenReturn(Collections.emptyList());

			List<CarViewModel> result = carService.getAvailableCars();

			assertThat(result).isEmpty();

			verify(carRepository).findAllActive();
			verify(rentalRepository).findAllActive();
			verifyNoMoreInteractions(carRepository, rentalRepository);
			verifyNoInteractions(carMapper);
		}

		@Test
		void testGetAvailableCarsWhenThereAreNoActiveRentalsReturnsAllCars() {
			stubTransaction().withRentalRepository();

			when(carRepository.findAllActive()).thenReturn(List.of(car));
			when(rentalRepository.findAllActive()).thenReturn(Collections.emptyList());
			when(carMapper.toViewModel(car)).thenReturn(carViewModel);

			List<CarViewModel> result = carService.getAvailableCars();

			assertThat(result).hasSize(1).containsExactly(carViewModel);

			verify(carRepository).findAllActive();
			verify(rentalRepository).findAllActive();
			verify(carMapper).toViewModel(car);
			verifyNoMoreInteractions(carRepository, rentalRepository, carMapper);
		}

		@Test
		void testGetAvailableCarsWhenCarHasActiveRentalIsExcluded() {
			stubTransaction().withRentalRepository();

			Rental activeRental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);

			when(carRepository.findAllActive()).thenReturn(List.of(car));
			when(rentalRepository.findAllActive()).thenReturn(List.of(activeRental));

			List<CarViewModel> result = carService.getAvailableCars();

			assertThat(result).isEmpty();

			verify(carRepository).findAllActive();
			verify(rentalRepository).findAllActive();
			verifyNoMoreInteractions(carRepository, rentalRepository);
			verifyNoInteractions(carMapper);
		}

		@Test
		void testGetAvailableCarsWhenOnlySomeCarsAreRentedReturnsOnlyTheAvailableOnes() {
			stubTransaction().withRentalRepository();

			Rental activeRental = new Rental(anotherCar, customer, A_START_DATE, A_NUMBER_OF_DAYS);

			when(carRepository.findAllActive()).thenReturn(List.of(car, anotherCar));
			when(rentalRepository.findAllActive()).thenReturn(List.of(activeRental));
			when(carMapper.toViewModel(car)).thenReturn(carViewModel);

			List<CarViewModel> result = carService.getAvailableCars();

			assertThat(result).hasSize(1).containsExactly(carViewModel);

			verify(carRepository).findAllActive();
			verify(rentalRepository).findAllActive();
			verify(carMapper).toViewModel(car);
			verifyNoMoreInteractions(carRepository, rentalRepository, carMapper);
		}

		@Test
		void testGetAvailableCarsWhenAllCarsAreRentedReturnsAnEmptyList() {
			stubTransaction().withRentalRepository();

			Rental firstRental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);
			Rental secondRental = new Rental(anotherCar, customer, A_START_DATE, A_NUMBER_OF_DAYS);

			when(carRepository.findAllActive()).thenReturn(List.of(car, anotherCar));
			when(rentalRepository.findAllActive()).thenReturn(List.of(firstRental, secondRental));

			List<CarViewModel> result = carService.getAvailableCars();

			assertThat(result).isEmpty();

			verify(carRepository).findAllActive();
			verify(rentalRepository).findAllActive();
			verifyNoMoreInteractions(carRepository, rentalRepository);
			verifyNoInteractions(carMapper);
		}

	}

}
