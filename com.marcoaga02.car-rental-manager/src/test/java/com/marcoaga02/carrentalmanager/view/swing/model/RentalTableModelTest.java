package com.marcoaga02.carrentalmanager.view.swing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

class RentalTableModelTest {

	private static final Long A_RENTAL_ID = 10L;
	private static final LocalDate A_START_DATE = LocalDate.of(2026, Month.JUNE, 24);
	private static final LocalDate AN_END_DATE = LocalDate.of(2026, Month.JUNE, 30);
	private static final Integer A_NUMBER_OF_DAYS = 6;
	private static final String A_CUSTOMER_FULLNAME = "aFirstname aLastname";
	private static final String A_CAR_DESCRIPTION = "aBrand aModel [aCarPlate]";
	private static final BigDecimal A_TOTAL_AMOUNT = BigDecimal.valueOf(61.2);

	private static final Long ANOTHER_RENTAL_ID = 11L;
	private static final LocalDate ANOTHER_START_DATE = LocalDate.of(2026, Month.JULY, 10);
	private static final LocalDate ANOTHER_END_DATE = LocalDate.of(2026, Month.JULY, 25);
	private static final Integer ANOTHER_NUMBER_OF_DAYS = 15;
	private static final String ANOTHER_CUSTOMER_FULLNAME = "anotherFirstname anotherLastname";
	private static final String ANOTHER_CAR_DESCRIPTION = "anotherBrand anotherModel [anotherCarPlate]";
	private static final BigDecimal ANOTHER_TOTAL_AMOUNT = BigDecimal.valueOf(123.5);

	private static final String A_FORMATTED_START_DATE = "24/06/2026";
	private static final String A_FORMATTED_END_DATE = "30/06/2026";

	private static final String CUSTOMER_COLUMN = "Customer";
	private static final String CAR_COLUMN = "Car";
	private static final String START_DATE_COLUMN = "Start Date";
	private static final String END_DATE_COLUMN = "End Date";
	private static final String DAYS_COLUMN = "Days";
	private static final String TOTAL_AMOUNT_COLUMN = "Total Amount (€)";

	private RentalTableModel rentalTableModel;

	private RentalViewModel rental, anotherRental;

	@BeforeEach
	void setUp() {
		rentalTableModel = new RentalTableModel();
		rental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS, A_CUSTOMER_FULLNAME,
				A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
		anotherRental = new RentalViewModel(ANOTHER_RENTAL_ID, ANOTHER_START_DATE, ANOTHER_END_DATE,
				ANOTHER_NUMBER_OF_DAYS, ANOTHER_CUSTOMER_FULLNAME, ANOTHER_CAR_DESCRIPTION, ANOTHER_TOTAL_AMOUNT);
	}

	@Test
	void testInitialState() {
		assertThat(rentalTableModel.getRowCount()).isZero();
		assertThat(rentalTableModel.getColumnCount()).isEqualTo(6);
	}

	@Test
	void testColumnNames() {
		assertThat(rentalTableModel.getColumnName(0)).isEqualTo(CUSTOMER_COLUMN);
		assertThat(rentalTableModel.getColumnName(1)).isEqualTo(CAR_COLUMN);
		assertThat(rentalTableModel.getColumnName(2)).isEqualTo(START_DATE_COLUMN);
		assertThat(rentalTableModel.getColumnName(3)).isEqualTo(END_DATE_COLUMN);
		assertThat(rentalTableModel.getColumnName(4)).isEqualTo(DAYS_COLUMN);
		assertThat(rentalTableModel.getColumnName(5)).isEqualTo(TOTAL_AMOUNT_COLUMN);
	}

	@Test
	void testSetRentalsWithEmptyListShouldClearTable() {
		rentalTableModel.getRentals().add(rental);

		rentalTableModel.setRentals(List.of());

		assertThat(rentalTableModel.getRentals()).isEmpty();
	}

	@Test
	void testSetRentalsWithAListOfOneElementAddTheElement() {
		rentalTableModel.setRentals(List.of(rental));

		assertThat(rentalTableModel.getRentals()).containsExactly(rental);
	}

	@Test
	void testSetRentalsWithAListOfMultipleElementsAddAllTheElements() {
		rentalTableModel.setRentals(List.of(rental, anotherRental));

		assertThat(rentalTableModel.getRentals()).containsExactlyInAnyOrder(rental, anotherRental);
	}

	@Test
	void testSetRentalsShouldReplacePreviousRentals() {
		rentalTableModel.getRentals().add(rental);

		rentalTableModel.setRentals(List.of(anotherRental));

		assertThat(rentalTableModel.getRentals()).containsExactly(anotherRental);
	}

	@Test
	void testSetRentalsShouldDefensivelyCopyTheGivenList() {
		List<RentalViewModel> originalList = new ArrayList<>(List.of(rental));

		rentalTableModel.setRentals(originalList);

		originalList.add(anotherRental);

		assertThat(rentalTableModel.getRentals()).containsExactly(rental);
	}

	@Test
	void testGetRentalAtShouldReturnCorrectRental() {
		List<RentalViewModel> rentals = rentalTableModel.getRentals();
		rentals.add(rental);
		rentals.add(anotherRental);

		assertThat(rentalTableModel.getRentalAt(1)).isEqualTo(anotherRental);
	}

	@Test
	void testGetRentalAtWithInvalidIndexShouldThrowIllegalArgumentException() {
		rentalTableModel.getRentals().add(rental);

		assertThatThrownBy(() -> rentalTableModel.getRentalAt(4)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetRentalAtWithInvalidBoundaryIndexShouldThrowIllegalArgumentException() {
		rentalTableModel.getRentals().add(rental);

		assertThatThrownBy(() -> rentalTableModel.getRentalAt(1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

	@Test
	void testGetValueAtShouldReturnCorrectValues() {
		rentalTableModel.getRentals().add(rental);

		assertThat(rentalTableModel.getValueAt(0, 0)).isEqualTo(A_CUSTOMER_FULLNAME);
		assertThat(rentalTableModel.getValueAt(0, 1)).isEqualTo(A_CAR_DESCRIPTION);
		assertThat(rentalTableModel.getValueAt(0, 2)).isEqualTo(A_FORMATTED_START_DATE);
		assertThat(rentalTableModel.getValueAt(0, 3)).isEqualTo(A_FORMATTED_END_DATE);
		assertThat(rentalTableModel.getValueAt(0, 4)).isEqualTo(A_NUMBER_OF_DAYS);
		assertThat(rentalTableModel.getValueAt(0, 5)).isEqualTo(A_TOTAL_AMOUNT);
	}

	@Test
	void testGetValueAtWithInvalidColumnShouldThrowIllegalArgumentException() {
		rentalTableModel.getRentals().add(rental);

		assertThatThrownBy(() -> rentalTableModel.getValueAt(0, 6)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid column: 6");
	}

	@Test
	void testGetValueAtWithInvalidRowShouldThrowIllegalArgumentException() {
		rentalTableModel.getRentals().add(rental);

		assertThatThrownBy(() -> rentalTableModel.getValueAt(4, 0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetValueAtWithInvalidBoundaryRowShouldThrowIllegalArgumentException() {
		rentalTableModel.getRentals().add(rental);

		assertThatThrownBy(() -> rentalTableModel.getValueAt(1, 0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}
}
