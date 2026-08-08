package com.marcoaga02.carrentalmanager.view.swing;

import static com.marcoaga02.carrentalmanager.testutils.TableAssertionUtils.rowsOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.fixture.Containers.showInFrame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.marcoaga02.carrentalmanager.controller.RentalController;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

@RunWith(GUITestRunner.class)
public class RentalPanelTest extends AssertJSwingJUnitTestCase {

	private static final String RENTAL_TABLE = "rentalTable";

	private static final String ADD_RENTAL_BTN = "Add rental";
	private static final String DELETE_SELECTED_BTN = "Delete selected";

	private static final String CAR_COMBO_BOX = "carComboBox";
	private static final String CUSTOMER_COMBO_BOX = "customerComboBox";
	private static final String RENTAL_DAYS_SPINNER = "rentalDaysSpinner";

	private static final String ERROR_LABEL = "errorLabel";

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

	private static final String ANOTHER_FORMATTED_START_DATE = "10/07/2026";
	private static final String ANOTHER_FORMATTED_END_DATE = "25/07/2026";

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
	private RentalController rentalController;

	private FrameFixture window;

	private RentalPanel rentalPanel;

	private AutoCloseable closeable;

	private RentalViewModel rental, anotherRental;
	private CarViewModel car, anotherCar;
	private CustomerViewModel customer, anotherCustomer;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		rentalPanel = GuiActionRunner.execute(() -> {
			RentalPanel panel = new RentalPanel();
			panel.setRentalController(rentalController);
			return panel;
		});
		window = showInFrame(robot(), rentalPanel);

		rental = new RentalViewModel(A_RENTAL_ID, A_START_DATE, AN_END_DATE, A_NUMBER_OF_DAYS, A_CUSTOMER_FULLNAME,
				A_CAR_DESCRIPTION, A_TOTAL_AMOUNT);
		anotherRental = new RentalViewModel(ANOTHER_RENTAL_ID, ANOTHER_START_DATE, ANOTHER_END_DATE,
				ANOTHER_NUMBER_OF_DAYS, ANOTHER_CUSTOMER_FULLNAME, ANOTHER_CAR_DESCRIPTION, ANOTHER_TOTAL_AMOUNT);

		car = new CarViewModel(A_CAR_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		anotherCar = new CarViewModel(ANOTHER_CAR_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);

		customer = new CustomerViewModel(A_CUSTOMER_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new CustomerViewModel(ANOTHER_CUSTOMER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME,
				ANOTHER_LASTNAME);
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Car"));
		window.comboBox(CAR_COMBO_BOX).requireDisabled();

		window.label(JLabelMatcher.withText("Customer"));
		window.comboBox(CUSTOMER_COMBO_BOX).requireDisabled();

		window.label(JLabelMatcher.withText("Rental days"));
		window.spinner(RENTAL_DAYS_SPINNER).requireEnabled();

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireDisabled();
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();

		window.table(RENTAL_TABLE);

		verifyNoInteractions(rentalController);
	}

	@Test
	@GUITest
	public void testShowAllRentalsShouldAddRentalsToTheTable() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getErrorLabel().setText("error message");
			rentalPanel.showAllRentals(List.of(rental, anotherRental));
		});

		assertThat(rowsOf(window.table(RENTAL_TABLE).contents())).containsExactlyInAnyOrder(
				List.of(A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, A_FORMATTED_START_DATE, A_FORMATTED_END_DATE,
						A_NUMBER_OF_DAYS.toString(), A_TOTAL_AMOUNT.toString()),
				List.of(ANOTHER_CUSTOMER_FULLNAME, ANOTHER_CAR_DESCRIPTION, ANOTHER_FORMATTED_START_DATE,
						ANOTHER_FORMATTED_END_DATE, ANOTHER_NUMBER_OF_DAYS.toString(),
						ANOTHER_TOTAL_AMOUNT.toString()));

		window.label(ERROR_LABEL).requireText(" ");
	}

	@Test
	@GUITest
	public void testShowAvailableCarsShouldAddCarsToTheCarComboBoxAndEnableIt() {
		GuiActionRunner.execute(() -> rentalPanel.showAvailableCars(List.of(car, anotherCar)));

		String[] comboContents = window.comboBox(CAR_COMBO_BOX).contents();
		assertThat(comboContents).containsExactly(A_CAR_DESCRIPTION, ANOTHER_CAR_DESCRIPTION);
		window.comboBox(CAR_COMBO_BOX).requireEnabled();
	}

	@Test
	@GUITest
	public void testShowAvailableCarsShouldDisableTheCarComboBoxWhenListIsEmpty() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCarComboBox().addItem(car);
			rentalPanel.getCarComboBox().setEnabled(true);

			rentalPanel.showAvailableCars(Collections.emptyList());
		});

		String[] comboContents = window.comboBox(CAR_COMBO_BOX).contents();
		assertThat(comboContents).isEmpty();
		window.comboBox(CAR_COMBO_BOX).requireDisabled();
	}

	@Test
	@GUITest
	public void testShowAvailableCustomersShouldAddCustomersToTheCustomersComboBoxAndEnableIt() {
		GuiActionRunner.execute(() -> rentalPanel.showAvailableCustomers(List.of(customer, anotherCustomer)));

		String[] comboContents = window.comboBox(CUSTOMER_COMBO_BOX).contents();
		assertThat(comboContents).containsExactly(A_CUSTOMER_FULLNAME, ANOTHER_CUSTOMER_FULLNAME);
		window.comboBox(CUSTOMER_COMBO_BOX).requireEnabled();
	}

	@Test
	@GUITest
	public void testShowAvailableCustomersShouldDisableTheCustomerComboBoxWhenListIsEmpty() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCustomerComboBox().addItem(customer);
			rentalPanel.getCustomerComboBox().setEnabled(true);

			rentalPanel.showAvailableCustomers(Collections.emptyList());
		});

		String[] comboContents = window.comboBox(CUSTOMER_COMBO_BOX).contents();
		assertThat(comboContents).isEmpty();
		window.comboBox(CAR_COMBO_BOX).requireDisabled();
	}

	@Test
	@GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		String errorMessage = "error message";
		GuiActionRunner.execute(() -> rentalPanel.showError(errorMessage));
		window.label(ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	@GUITest
	public void testAddRentalButtonDisabledWhenAllComboBoxAreUnselectedAndRentalDaysIsZero() {
		window.comboBox(CAR_COMBO_BOX).clearSelection();
		window.comboBox(CUSTOMER_COMBO_BOX).clearSelection();
		window.spinner(RENTAL_DAYS_SPINNER).select(0.0);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddRentalButtonDisabledWhenCarComboBoxIsInvalid() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCustomerComboBox().addItem(customer);
			rentalPanel.getCustomerComboBox().setEnabled(true);
		});

		window.comboBox(CAR_COMBO_BOX).clearSelection();
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(0);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddRentalButtonDisabledWhenCustomerComboBoxIsInvalid() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCarComboBox().addItem(car);
			rentalPanel.getCarComboBox().setEnabled(true);
		});

		window.comboBox(CAR_COMBO_BOX).selectItem(0);
		window.comboBox(CUSTOMER_COMBO_BOX).clearSelection();
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddRentalButtonDisabledWhenRentalDaysIsZero() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCarComboBox().addItem(car);
			rentalPanel.getCustomerComboBox().addItem(customer);

			rentalPanel.getCarComboBox().setEnabled(true);
			rentalPanel.getCustomerComboBox().setEnabled(true);
		});

		window.comboBox(CAR_COMBO_BOX).selectItem(0);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(0);
		window.spinner(RENTAL_DAYS_SPINNER).select(0.0);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddRentalButtonEnabledWhenAllFieldsAreValid() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCarComboBox().addItem(car);
			rentalPanel.getCustomerComboBox().addItem(customer);

			rentalPanel.getCarComboBox().setEnabled(true);
			rentalPanel.getCustomerComboBox().setEnabled(true);
		});

		window.comboBox(CAR_COMBO_BOX).selectItem(0);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(0);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireEnabled();
	}

	@Test
	@GUITest
	public void testDeleteRentalButtonDisabledWhenTableIsEmpty() {
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteRentalButtonDisabledWhenNoTableRowIsSelected() {
		;
		GuiActionRunner.execute(() -> rentalPanel.getRentalTableModel().setRentals(List.of(rental)));

		window.table(RENTAL_TABLE).selectRows(0);
		window.table(RENTAL_TABLE).unselectRows(0);

		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteCarButtonEnabledWhenATableRowIsSelected() {
		GuiActionRunner.execute(() -> rentalPanel.getRentalTableModel().setRentals(List.of(rental)));

		window.table(RENTAL_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireEnabled();
	}

	@Test
	@GUITest
	public void testClearFieldsShouldResetAllFieldsAndDisableAddRentalButton() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCarComboBox().addItem(car);
			rentalPanel.getCustomerComboBox().addItem(customer);

			rentalPanel.getCarComboBox().setEnabled(true);
			rentalPanel.getCustomerComboBox().setEnabled(true);
		});

		window.comboBox(CAR_COMBO_BOX).selectItem(0);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(0);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_DAILY_RATE);

		GuiActionRunner.execute(() -> rentalPanel.clearFields());

		window.comboBox(CAR_COMBO_BOX).requireNoSelection();
		window.comboBox(CUSTOMER_COMBO_BOX).requireNoSelection();
		window.spinner(RENTAL_DAYS_SPINNER).requireValue(0.0);
		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddRentalButtonShouldDelegateToRentalControllerCreateRental() {
		GuiActionRunner.execute(() -> {
			rentalPanel.getCarComboBox().addItem(car);
			rentalPanel.getCustomerComboBox().addItem(customer);

			rentalPanel.getCarComboBox().setEnabled(true);
			rentalPanel.getCustomerComboBox().setEnabled(true);
		});

		window.comboBox(CAR_COMBO_BOX).selectItem(0);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(0);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).click();

		verify(rentalController).createRental(new RentalCreationRequest(A_CAR_ID, A_CUSTOMER_ID, A_NUMBER_OF_DAYS));
		verifyNoMoreInteractions(rentalController);
	}

	@Test
	@GUITest
	public void testDeleteRentalButtonShouldDelegateToRentalControllerDeleteCar() {
		GuiActionRunner.execute(() -> rentalPanel.getRentalTableModel().setRentals(List.of(rental, anotherRental)));

		window.table(RENTAL_TABLE).selectRows(1);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		verify(rentalController).deleteRental(ANOTHER_RENTAL_ID);
		verifyNoMoreInteractions(rentalController);
	}

	@Test
	@GUITest
	public void testOnActivateShouldDelegateToCustomerControllerToPopulateThePanel() {
		GuiActionRunner.execute(() -> rentalPanel.onActivate());

		verify(rentalController).getAllActiveRentals();
		verify(rentalController).loadAvailableCars();
		verify(rentalController).loadAvailableCustomers();
		verifyNoMoreInteractions(rentalController);
	}

	@Test
	@GUITest
	public void testOnActivateDoesNothingWhenControllerNotSet() {
		RentalPanel freshPanel = GuiActionRunner.execute(RentalPanel::new);

		assertDoesNotThrow(() -> GuiActionRunner.execute(freshPanel::onActivate));
	}

}
