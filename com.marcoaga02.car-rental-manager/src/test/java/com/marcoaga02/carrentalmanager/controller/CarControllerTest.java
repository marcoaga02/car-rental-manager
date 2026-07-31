package com.marcoaga02.carrentalmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marcoaga02.carrentalmanager.exception.CarCurrentlyRentedException;
import com.marcoaga02.carrentalmanager.exception.CarNotFoundException;
import com.marcoaga02.carrentalmanager.exception.DuplicateCarPlateException;
import com.marcoaga02.carrentalmanager.service.CarService;
import com.marcoaga02.carrentalmanager.view.CarView;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

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

	@Mock
	private CarService carService;

	@Mock
	private CarView carView;

	@InjectMocks
	private CarController carController;

	@Nested
	class GetAllCars {

		@Test
		void testGetAllCarsWhenThereAreNoCarsCallsShowAllCarsWithEmptyList() {
			when(carService.getAllCars()).thenReturn(Collections.emptyList());

			carController.getAllCars();

			InOrder inOrder = inOrder(carService, carView);
			inOrder.verify(carService).getAllCars();
			inOrder.verify(carView).showAllCars(Collections.emptyList());
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllCarsWhenThereIsOnlyOneCarCallsShowAllCarsWithAListWithOneElement() {
			CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			when(carService.getAllCars()).thenReturn(List.of(car));

			carController.getAllCars();

			InOrder inOrder = inOrder(carService, carView);
			inOrder.verify(carService).getAllCars();
			inOrder.verify(carView).showAllCars(List.of(car));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testGetAllCarsWhenThereAreSeveralCarsCallsShowAllCarsWithAListWithAllElements() {
			CarViewModel firstCar = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			CarViewModel secondCar = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
					ANOTHER_DAILY_RATE);
			when(carService.getAllCars()).thenReturn(List.of(firstCar, secondCar));

			carController.getAllCars();

			InOrder inOrder = inOrder(carService, carView);
			inOrder.verify(carService).getAllCars();
			inOrder.verify(carView).showAllCars(List.of(firstCar, secondCar));
			inOrder.verifyNoMoreInteractions();
		}

	}

	@Nested
	class CreateCar {

		@Test
		void testCreateCarWhenSuccessfulRefreshesTheCarList() {
			CarViewModel request = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			when(carService.getAllCars()).thenReturn(List.of(request));

			carController.createCar(request);

			InOrder inOrder = inOrder(carService, carView);
			inOrder.verify(carService).createCar(request);
			inOrder.verify(carService).getAllCars();
			inOrder.verify(carView).showAllCars(List.of(request));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testCreateCarWhenDuplicateCarPlateShowsErrorAndDoesNotRefreshList() {
			CarViewModel request = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			DuplicateCarPlateException exception = new DuplicateCarPlateException(A_CAR_PLATE);
			doThrow(exception).when(carService).createCar(request);

			carController.createCar(request);

			verify(carService).createCar(request);
			verify(carView).showError(exception.getMessage());
			verify(carService, never()).getAllCars();
			verify(carView, never()).showAllCars(any());
		}

		@Test
		void testCreateCarWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			CarViewModel request = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			IllegalArgumentException exception = new IllegalArgumentException("carPlate must not be blank");
			doThrow(exception).when(carService).createCar(request);

			carController.createCar(request);

			verify(carService).createCar(request);
			verify(carView).showError(exception.getMessage());
			verify(carService, never()).getAllCars();
			verify(carView, never()).showAllCars(any());
		}

	}

	@Nested
	class DeleteCar {

		@Test
		void testDeleteCarWhenSuccessfulRefreshesTheCarList() {
			CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
			when(carService.getAllCars()).thenReturn(List.of(car));

			carController.deleteCar(ANOTHER_ID);

			InOrder inOrder = inOrder(carService, carView);
			inOrder.verify(carService).deleteCar(ANOTHER_ID);
			inOrder.verify(carService).getAllCars();
			inOrder.verify(carView).showAllCars(List.of(car));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void testDeleteCarWhenCarNotFoundShowsErrorAndDoesNotRefreshList() {
			CarNotFoundException exception = new CarNotFoundException(AN_ID);
			doThrow(exception).when(carService).deleteCar(AN_ID);

			carController.deleteCar(AN_ID);

			verify(carService).deleteCar(AN_ID);
			verify(carView).showError(exception.getMessage());
			verify(carService, never()).getAllCars();
			verify(carView, never()).showAllCars(any());
		}

		@Test
		void testDeleteCarWhenCarCurrentlyRentedShowsErrorAndDoesNotRefreshList() {
			CarCurrentlyRentedException exception = new CarCurrentlyRentedException(AN_ID);
			doThrow(exception).when(carService).deleteCar(AN_ID);

			carController.deleteCar(AN_ID);

			verify(carService).deleteCar(AN_ID);
			verify(carView).showError(exception.getMessage());
			verify(carService, never()).getAllCars();
			verify(carView, never()).showAllCars(any());
		}

		@Test
		void testDeleteCarWhenInvalidInputShowsErrorAndDoesNotRefreshList() {
			IllegalArgumentException exception = new IllegalArgumentException("carId must not be null");
			doThrow(exception).when(carService).deleteCar(null);

			carController.deleteCar(null);

			verify(carService).deleteCar(null);
			verify(carView).showError(exception.getMessage());
			verify(carService, never()).getAllCars();
			verify(carView, never()).showAllCars(any());
		}

	}
}
