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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.repository.CarRepository;
import com.marcoaga02.carrentalmanager.transaction.TransactionCode;
import com.marcoaga02.carrentalmanager.transaction.TransactionContext;
import com.marcoaga02.carrentalmanager.transaction.TransactionManager;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceImplTest {

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private TransactionContext transactionContext;

	@Mock
	private CarRepository carRepository;

	@Mock
	private CarMapper carMapper;

	@InjectMocks
	private CarServiceImpl carService;

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";

	private static final String A_BRAND = "aBrand";
	private static final String ANOTHER_BRAND = "anotherBrand";

	private static final String A_MODEL = "aModel";
	private static final String ANOTHER_MODEL = "anotherModel";

	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final Long AN_ID = 10L;
	private static final Long ANOTHER_ID = 13L;

	private Car car, anotherCar;
	private CarViewModel carViewModel, anotherCarViewModel;

	@Before
	public void setup() {
		when(transactionContext.carRepository()).thenReturn(carRepository);
		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(transactionContext)));

		car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carViewModel = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		anotherCar = new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE);
		anotherCarViewModel = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);
	}

	@Test
	public void testGetAllCarsWhenThereAreNoCarsReturnAnEmptyList() {
		when(carRepository.findAllActive()).thenReturn(Collections.emptyList());

		List<CarViewModel> result = carService.getAllCars();

		assertThat(result).isEmpty();

		verify(carRepository).findAllActive();
		verifyNoMoreInteractions(carRepository);
		verifyNoInteractions(carMapper);
	}

	@Test
	public void testGetAllCarsWhenThereIsOnlyOneCarReturnAListOfOneElement() {
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
	public void testGetAllCarsWhenThereAreMultipleCarsReturnAListWithAllElements() {
		when(carRepository.findAllActive()).thenReturn(List.of(car, anotherCar));
		when(carMapper.toViewModel(car)).thenReturn(carViewModel);
		when(carMapper.toViewModel(anotherCar)).thenReturn(anotherCarViewModel);

		List<CarViewModel> result = carService
				.getAllCars()
				.stream()
				.sorted(Comparator.comparing(CarViewModel::getId))
				.collect(Collectors.toList());

		assertThat(result).hasSize(2).containsExactlyInAnyOrder(carViewModel, anotherCarViewModel);

		InOrder inOrder = inOrder(carRepository, carMapper);
		inOrder.verify(carRepository).findAllActive();
		inOrder.verify(carMapper).toViewModel(car);
		inOrder.verify(carMapper).toViewModel(anotherCar);
	}

}
