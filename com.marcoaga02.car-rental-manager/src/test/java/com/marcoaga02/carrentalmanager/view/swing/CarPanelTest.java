package com.marcoaga02.carrentalmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

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

import com.marcoaga02.carrentalmanager.controller.CarController;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

@RunWith(GUITestRunner.class)
public class CarPanelTest extends AssertJSwingJUnitTestCase {

	private static final String CAR_TABLE = "carTable";

	private static final String ADD_CAR_BTN = "Add car";
	private static final String DELETE_SELECTED_BTN = "Delete selected";

	private static final String CAR_PLATE_TEXT_FIELD = "carPlateTextField";
	private static final String BRAND_TEXT_FIELD = "brandTextField";
	private static final String MODEL_TEXT_FIELD = "modelTextField";
	private static final String DAILY_RATE_SPINNER = "dailyRateSpinner";

	private static final String ERROR_LABEL = "errorLabel";

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
	private CarController carController;

	private FrameFixture window;

	private CarPanel carPanel;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			carPanel = new CarPanel();
			carPanel.setCarController(carController);
			return wrapInFrame(carPanel);
		}));

		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	private JFrame wrapInFrame(JPanel panel) {
		JFrame frame = new JFrame();
		frame.add(panel);
		return frame;
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Car plate"));
		window.textBox(CAR_PLATE_TEXT_FIELD).requireEnabled();

		window.label(JLabelMatcher.withText("Brand"));
		window.textBox(BRAND_TEXT_FIELD).requireEnabled();

		window.label(JLabelMatcher.withText("Model"));
		window.textBox(MODEL_TEXT_FIELD).requireEnabled();

		window.label(JLabelMatcher.withText("Daily rate (€)"));
		window.spinner(DAILY_RATE_SPINNER).requireEnabled();

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();

		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();

		window.table(CAR_TABLE);

		verifyNoInteractions(carController);
	}

	@Test
	@GUITest
	public void testShowAllCarsShouldAddCarsToTheTable() {
		CarViewModel car1 = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		CarViewModel car2 = new CarViewModel(ANOTHER_ID, ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL,
				ANOTHER_DAILY_RATE);

		GuiActionRunner.execute(() -> {
			carPanel.getLblError().setText("error message");
			carPanel.showAllCars(List.of(car1, car2));
		});

		String[][] tableContents = window.table(CAR_TABLE).contents();
		assertThat(tableContents)
				.isDeepEqualTo(new String[][] { { A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE.toString() },
						{ ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE.toString() } });

		window.label(ERROR_LABEL).requireText(" ");
	}

	@Test
	@GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		String errorMessage = "error message";
		GuiActionRunner.execute(() -> carPanel.showError(errorMessage));
		window.label(ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	@GUITest
	public void testAddCarButtonDisabledWhenAllFieldsAreEmptyAndDailyRateIsZero() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText("");
		window.textBox(BRAND_TEXT_FIELD).enterText("");
		window.textBox(MODEL_TEXT_FIELD).enterText("");
		window.spinner(DAILY_RATE_SPINNER).select(0.0);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonDisabledWhenAllFieldsAreBlankAndDailyRateIsZero() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(" ");
		window.textBox(BRAND_TEXT_FIELD).enterText(" ");
		window.textBox(MODEL_TEXT_FIELD).enterText(" ");
		window.spinner(DAILY_RATE_SPINNER).select(0.0);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonDisabledWhenCarPlateIsInvalid() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(" ");
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonDisabledWhenBrandIsInvalid() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(" ");
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonDisabledWhenModelIsInvalid() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(" ");
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonDisabledWhenDailyRateIsInvalid() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(0.0);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonEnabledWhenAllFieldsAreValid() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireEnabled();
	}

	@Test
	@GUITest
	public void testDeleteCarButtonDisabledWhenTableIsEmpty() {
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteCarButtonDisabledWhenNoTableRowIsSelected() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		GuiActionRunner.execute(() -> carPanel.getCarTableModel().setCars(List.of(car)));

		window.table(CAR_TABLE).selectRows(0);
		window.table(CAR_TABLE).unselectRows(0);

		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteCarButtonEnabledWhenATableRowIsSelected() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		GuiActionRunner.execute(() -> carPanel.getCarTableModel().setCars(List.of(car)));

		window.table(CAR_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireEnabled();
	}
	
	@Test
	@GUITest
	public void testClearFieldsShouldResetAllFieldsAndDisableAddCarButton() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		GuiActionRunner.execute(() -> carPanel.clearFields());

		window.textBox(CAR_PLATE_TEXT_FIELD).requireEmpty();
		window.textBox(BRAND_TEXT_FIELD).requireEmpty();
		window.textBox(MODEL_TEXT_FIELD).requireEmpty();
		window.spinner(DAILY_RATE_SPINNER).requireValue(0.0);
		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCarButtonShouldDelegateToCarControllerCreateCar() {
		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).click();

		verify(carController).createCar(new CarViewModel(null, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		verifyNoMoreInteractions(carController);
	}

	@Test
	@GUITest
	public void testDeleteCarButtonShouldDelegateToCarControllerDeleteCar() {
		CarViewModel car = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		GuiActionRunner.execute(() -> carPanel.getCarTableModel().setCars(List.of(car)));

		window.table(CAR_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		verify(carController).deleteCar(AN_ID);
		verifyNoMoreInteractions(carController);
	}

	@Test
	@GUITest
	public void testOnActivateShouldDelegateToCarControllerGetAllCars() {
		GuiActionRunner.execute(() -> carPanel.onActivate());

		verify(carController).getAllCars();
		verifyNoMoreInteractions(carController);
	}

	@Test
	@GUITest
	public void testOnActivateDoesNothingWhenControllerNotSet() {
		CarPanel freshPanel = GuiActionRunner.execute(CarPanel::new);

		assertDoesNotThrow(() -> GuiActionRunner.execute(freshPanel::onActivate));
	}
}
