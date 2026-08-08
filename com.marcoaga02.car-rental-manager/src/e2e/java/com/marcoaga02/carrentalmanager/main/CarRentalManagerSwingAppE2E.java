package com.marcoaga02.carrentalmanager.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.awt.Point;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.data.Index;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.testutils.BaseSwingPostgresTest;
import com.marcoaga02.carrentalmanager.view.swing.MainFrame;

@RunWith(GUITestRunner.class)
public class CarRentalManagerSwingAppE2E extends BaseSwingPostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);
	private static final String A_CAR_DESCRIPTION = "aBrand aModel [aCarPlate]";

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";
	private static final String A_CUSTOMER_FULLNAME = A_FIRSTNAME + " " + A_LASTNAME;

	private static final Integer A_NUMBER_OF_DAYS = 6;
	private static final BigDecimal A_TOTAL_AMOUNT = BigDecimal.valueOf(61.2); // 10.2 * 6

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// Cars tab
	private static final int CAR_TAB_INDEX = 0;
	private static final String CARS_TAB = "Cars";
	private static final String CAR_PLATE_TEXT_FIELD = "carPlateTextField";
	private static final String BRAND_TEXT_FIELD = "brandTextField";
	private static final String MODEL_TEXT_FIELD = "modelTextField";
	private static final String DAILY_RATE_SPINNER = "dailyRateSpinner";
	private static final String CAR_TABLE = "carTable";
	private static final String ADD_CAR_BTN = "Add car";

	// Customers tab
	private static final String CUSTOMERS_TAB = "Customers";
	private static final String TAX_ID_CODE_TEXT_FIELD = "taxIdCodeTextField";
	private static final String FIRSTNAME_TEXT_FIELD = "firstnameTextField";
	private static final String LASTNAME_TEXT_FIELD = "lastnameTextField";
	private static final String CUSTOMER_TABLE = "customerTable";
	private static final String ADD_CUSTOMER_BTN = "Add customer";

	// Rentals tab
	private static final String RENTALS_TAB = "Rentals";
	private static final String CAR_COMBO_BOX = "carComboBox";
	private static final String CUSTOMER_COMBO_BOX = "customerComboBox";
	private static final String RENTAL_DAYS_SPINNER = "rentalDaysSpinner";
	private static final String RENTAL_TABLE = "rentalTable";
	private static final String ADD_RENTAL_BTN = "Add rental";

	private static final String DELETE_SELECTED_BTN = "Delete selected";
	private static final String ERROR_LABEL = "errorLabel";

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		super.onSetUp();
	}

	private void launchApp() {
		application(CarRentalManagerSwingApp.class).withArgs("--db-host=" + postgres.getHost(),
				"--db-port=" + postgres.getMappedPort(5432), "--db-name=" + postgres.getDatabaseName(),
				"--db-user=" + postgres.getUsername(), "--db-password=" + postgres.getPassword()).start();

		window = WindowFinder.findFrame(MainFrame.class).using(robot());
		window.moveTo(new Point(0, 0));
	}

	@Override
	protected void onTearDown() {
		if (window != null) {
			window.cleanUp();
		}

		super.onTearDown();
	}

	@Test
	@GUITest
	public void testAppStartsOnCarsTabWithCarsAlreadyLoaded() {
		persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));

		launchApp();

		window.tabbedPane().requireSelectedTab(Index.atIndex(CAR_TAB_INDEX));
		String[][] contents = window.table(CAR_TABLE).contents();
		assertThat(contents).isDeepEqualTo(new String[][] { { A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE.toString() },
				{ ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE.toString() } });
	}

	@Test
	@GUITest
	public void testCreatingCarCustomerAndRentalPropagatesAcrossAllTabs() {
		launchApp();

		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);
		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).click();

		window.tabbedPane().selectTab(CUSTOMERS_TAB);
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);
		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).click();

		window.tabbedPane().selectTab(RENTALS_TAB);
		window.comboBox(CAR_COMBO_BOX).selectItem(A_CAR_DESCRIPTION);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(A_CUSTOMER_FULLNAME);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);
		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).click();

		LocalDate today = LocalDate.now();
		String formattedStartDate = today.format(DATE_FORMATTER);
		String formattedEndDate = today.plusDays(A_NUMBER_OF_DAYS).format(DATE_FORMATTER);

		String[][] contents = window.table(RENTAL_TABLE).contents();
		assertThat(contents).isDeepEqualTo(new String[][] { { A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION,
				formattedStartDate, formattedEndDate, A_NUMBER_OF_DAYS.toString(), A_TOTAL_AMOUNT.toString() } });

		assertThat(window.comboBox(CAR_COMBO_BOX).contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeletingRentalMakesCarAvailableAgainAcrossTabs() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistRental(new Rental(car, customer, LocalDate.now(), A_NUMBER_OF_DAYS));

		launchApp();
		window.tabbedPane().selectTab(RENTALS_TAB);

		assertThat(window.comboBox(CAR_COMBO_BOX).contents()).isEmpty();

		window.table(RENTAL_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		assertThat(window.table(RENTAL_TABLE).rowCount()).isZero();
		assertThat(window.comboBox(CAR_COMBO_BOX).contents()).containsExactly(A_CAR_DESCRIPTION);
	}

	@Test
	@GUITest
	public void testDeletingCarWithActiveRentalCreatedViaGuiShowsErrorOnCarsTabUntilRentalDelete() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

		launchApp();

		window.tabbedPane().selectTab(RENTALS_TAB);
		window.comboBox(CAR_COMBO_BOX).selectItem(A_CAR_DESCRIPTION);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(A_CUSTOMER_FULLNAME);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);
		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN).andShowing()).click();

		LocalDate today = LocalDate.now();
		String formattedStartDate = today.format(DATE_FORMATTER);
		String formattedEndDate = today.plusDays(A_NUMBER_OF_DAYS).format(DATE_FORMATTER);

		assertThat(window.table(RENTAL_TABLE).contents())
				.isDeepEqualTo(new String[][] { { A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, formattedStartDate,
						formattedEndDate, A_NUMBER_OF_DAYS.toString(), A_TOTAL_AMOUNT.toString() } });

		window.tabbedPane().selectTab(CARS_TAB);
		window.table(CAR_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		window.label(JLabelMatcher.withName(ERROR_LABEL).andShowing())
				.requireText("Car with id '" + car.getId() + "' is currently rented and cannot be deleted");

		window.tabbedPane().selectTab(RENTALS_TAB);
		window.table(RENTAL_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		assertThat(window.table(RENTAL_TABLE).rowCount()).isZero();

		window.tabbedPane().selectTab(CARS_TAB);
		window.table(CAR_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		window.label(JLabelMatcher.withName(ERROR_LABEL).andShowing()).requireText(" ");
		assertThat(window.table(CAR_TABLE).rowCount()).isZero();
	}

	@Test
	@GUITest
	public void testDeletingCustomerWithActiveRentalCreatedViaGuiShowsErrorOnCustomersTabUntilRentalDelete() {
		persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

		launchApp();

		window.tabbedPane().selectTab(RENTALS_TAB);
		window.comboBox(CAR_COMBO_BOX).selectItem(A_CAR_DESCRIPTION);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(A_CUSTOMER_FULLNAME);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);
		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN).andShowing()).click();

		LocalDate today = LocalDate.now();
		String formattedStartDate = today.format(DATE_FORMATTER);
		String formattedEndDate = today.plusDays(A_NUMBER_OF_DAYS).format(DATE_FORMATTER);

		assertThat(window.table(RENTAL_TABLE).contents())
				.isDeepEqualTo(new String[][] { { A_CUSTOMER_FULLNAME, A_CAR_DESCRIPTION, formattedStartDate,
						formattedEndDate, A_NUMBER_OF_DAYS.toString(), A_TOTAL_AMOUNT.toString() } });

		window.tabbedPane().selectTab(CUSTOMERS_TAB);
		window.table(CUSTOMER_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		window.label(JLabelMatcher.withName(ERROR_LABEL).andShowing())
				.requireText("Customer with id '" + customer.getId() + "' has an active rental and cannot be deleted");

		window.tabbedPane().selectTab(RENTALS_TAB);
		window.table(RENTAL_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		assertThat(window.table(RENTAL_TABLE).rowCount()).isZero();

		window.tabbedPane().selectTab(CUSTOMERS_TAB);
		window.table(CUSTOMER_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN).andShowing()).click();

		window.label(JLabelMatcher.withName(ERROR_LABEL).andShowing()).requireText(" ");
		assertThat(window.table(CUSTOMER_TABLE).rowCount()).isZero();
	}

	private Car persistCar(Car car) {
		entityManager.getTransaction().begin();
		entityManager.persist(car);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return car;
	}

	private Customer persistCustomer(Customer customer) {
		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return customer;
	}

	private Rental persistRental(Rental rental) {
		entityManager.getTransaction().begin();
		entityManager.persist(rental);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return rental;
	}

}
