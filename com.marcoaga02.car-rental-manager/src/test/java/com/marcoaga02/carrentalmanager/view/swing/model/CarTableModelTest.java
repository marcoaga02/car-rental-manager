package com.marcoaga02.carrentalmanager.view.swing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

class CarTableModelTest {

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

	private static final String CAR_PLATE_COLUMN = "Car Plate";
	private static final String BRAND_COLUMN = "Brand";
	private static final String MODEL_PLATE_COLUMN = "Model";
	private static final String DAILY_RATE_PLATE_COLUMN = "Daily Rate (€)";

	private CarTableModel carTableModel;

	@BeforeEach
	void setUp() {
		carTableModel = new CarTableModel();
	}

	@Test
	void testInitialState() {
		assertThat(carTableModel.getRowCount()).isZero();
		assertThat(carTableModel.getColumnCount()).isEqualTo(4);
	}

	@Test
	void testColumnNames() {
		assertThat(carTableModel.getColumnName(0)).isEqualTo(CAR_PLATE_COLUMN);
		assertThat(carTableModel.getColumnName(1)).isEqualTo(BRAND_COLUMN);
		assertThat(carTableModel.getColumnName(2)).isEqualTo(MODEL_PLATE_COLUMN);
		assertThat(carTableModel.getColumnName(3)).isEqualTo(DAILY_RATE_PLATE_COLUMN);
	}

	@Test
	void testSetCarsWithEmptyListShouldClearTable() {
		carTableModel.getCars().add(new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		carTableModel.setCars(List.of());

		assertThat(carTableModel.getCars()).isEmpty();
	}

	@Test
	void testSetCarsWithAListOfOneElementAddTheElement() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.setCars(List.of(car));

		assertThat(carTableModel.getCars()).containsExactly(car);
	}

	@Test
	void testSetCarsWithAListOfMultipleElementsAddAllTheElements() {
		CarViewModel car1 = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		CarViewModel car2 = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);
		carTableModel.setCars(List.of(car1, car2));

		assertThat(carTableModel.getCars()).containsExactlyInAnyOrder(car1, car2);
	}

	@Test
	void testSetCarsShouldReplacePreviousCars() {
		CarViewModel car1 = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car1);

		CarViewModel car2 = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);
		carTableModel.setCars(List.of(car2));

		assertThat(carTableModel.getCars()).containsExactly(car2);
	}

	@Test
	void testSetCarsShouldDefensivelyCopyTheGivenList() {
		CarViewModel car1 = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		List<CarViewModel> originalList = new ArrayList<>(List.of(car1));

		carTableModel.setCars(originalList);

		CarViewModel car2 = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);
		originalList.add(car2);

		assertThat(carTableModel.getCars()).containsExactly(car1);
	}

	@Test
	void testGetCarAtShouldReturnCorrectCar() {
		CarViewModel car1 = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		CarViewModel car2 = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);
		List<CarViewModel> cars = carTableModel.getCars();
		cars.add(car1);
		cars.add(car2);

		assertThat(carTableModel.getCarAt(1)).isEqualTo(car2);
	}

	@Test
	void testGetCarAtWithInvalidIndexShouldThrowIllegalArgumentException() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car);

		assertThatThrownBy(() -> carTableModel.getCarAt(4))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}
	
	@Test
	void testGetCarAtWithInvalidBoundaryIndexShouldThrowIllegalArgumentException() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car);

		assertThatThrownBy(() -> carTableModel.getCarAt(1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

	@Test
	void testGetValueAtShouldReturnCorrectValues() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car);

		assertThat(carTableModel.getValueAt(0, 0)).isEqualTo(A_CAR_PLATE);
		assertThat(carTableModel.getValueAt(0, 1)).isEqualTo(A_BRAND);
		assertThat(carTableModel.getValueAt(0, 2)).isEqualTo(A_MODEL);
		assertThat(carTableModel.getValueAt(0, 3)).isEqualTo(A_DAILY_RATE);
	}

	@Test
	void testGetValueAtWithInvalidColumnShouldThrowIllegalArgumentException() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car);

		assertThatThrownBy(() -> carTableModel.getValueAt(0, 4))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid column: 4");
	}
	
	@Test
	void testGetValueAtWithInvalidRowShouldThrowIllegalArgumentException() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car);
		
		assertThatThrownBy(() -> carTableModel.getValueAt(4, 0))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetValueAtWithInvalidBoundaryRowShouldThrowIllegalArgumentException() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		carTableModel.getCars().add(car);
		
		assertThatThrownBy(() -> carTableModel.getValueAt(1, 0))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

}
