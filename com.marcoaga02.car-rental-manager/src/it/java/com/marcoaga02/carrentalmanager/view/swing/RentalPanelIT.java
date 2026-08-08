package com.marcoaga02.carrentalmanager.view.swing;

import static com.marcoaga02.carrentalmanager.testutils.TableAssertionUtils.rowsOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.fixture.Containers.showInFrame;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.marcoaga02.carrentalmanager.controller.RentalController;
import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.mapper.RentalMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.service.CarServiceImpl;
import com.marcoaga02.carrentalmanager.service.CustomerServiceImpl;
import com.marcoaga02.carrentalmanager.service.RentalServiceImpl;
import com.marcoaga02.carrentalmanager.testutils.BaseSwingPostgresTest;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.view.swing.model.RentalTableModel;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

@RunWith(GUITestRunner.class)
public class RentalPanelIT extends BaseSwingPostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final String A_RENTED_CAR_PLATE = "aRentedCarPlate";
	private static final String A_RENTED_BRAND = "aRentedBrand";
	private static final String A_RENTED_MODEL = "aRentedModel";
	private static final BigDecimal A_RENTED_DAILY_RATE = BigDecimal.valueOf(5.7);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final LocalDate A_START_DATE = LocalDate.of(2026, Month.JUNE, 24);
	private static final Integer A_NUMBER_OF_DAYS = 6;
	private static final String A_CUSTOMER_FULLNAME = "aFirstname aLastname";
	private static final String A_CAR_DESCRIPTION = "aBrand aModel [aCarPlate]";

	private static final LocalDate ANOTHER_START_DATE = LocalDate.of(2026, Month.JULY, 10);
	private static final Integer ANOTHER_NUMBER_OF_DAYS = 15;
	private static final String ANOTHER_CUSTOMER_FULLNAME = "anotherFirstname anotherLastname";
	private static final String ANOTHER_CAR_DESCRIPTION = "anotherBrand anotherModel [anotherCarPlate]";
	private static final BigDecimal ANOTHER_TOTAL_AMOUNT = BigDecimal.valueOf(64.5);

	private static final String A_FORMATTED_START_DATE = "24/06/2026";
	private static final String A_FORMATTED_END_DATE = "30/06/2026";

	private static final String ANOTHER_FORMATTED_START_DATE = "10/07/2026";
	private static final String ANOTHER_FORMATTED_END_DATE = "25/07/2026";

	private static final String A_RENTED_CAR_DESCRIPTION = "aRentedBrand aRentedModel [aRentedCarPlate]";
	private static final BigDecimal A_RENTED_TOTAL_AMOUNT = BigDecimal.valueOf(34.2);

	private static final String ADD_RENTAL_BTN = "Add rental";
	private static final String DELETE_SELECTED_BTN = "Delete selected";

	private static final String CAR_COMBO_BOX = "carComboBox";
	private static final String CUSTOMER_COMBO_BOX = "customerComboBox";
	private static final String RENTAL_DAYS_SPINNER = "rentalDaysSpinner";
	private static final String RENTAL_TABLE = "rentalTable";
	private static final String ERROR_LABEL = "errorLabel";

	private static final LocalDate TODAY = A_START_DATE;
	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private RentalPanel rentalPanel;
	private RentalController rentalController;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);

		GuiActionRunner.execute(() -> {
			rentalPanel = new RentalPanel();
			rentalController = new RentalController(
					new RentalServiceImpl(transactionManager, new RentalMapper(), fixedClock),
					new CarServiceImpl(transactionManager, new CarMapper()),
					new CustomerServiceImpl(transactionManager, new CustomerMapper()), rentalPanel);
			rentalPanel.setRentalController(rentalController);
			return rentalPanel;
		});

		window = showInFrame(robot(), rentalPanel);
	}

	@Test
	@GUITest
	public void testOnActivateShowsActiveRentalsAndPopulatesAvailableCarsAndCustomers() {
		persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
		Car rentedCar = persistCar(new Car(A_RENTED_CAR_PLATE, A_RENTED_BRAND, A_RENTED_MODEL, A_RENTED_DAILY_RATE));

		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistCustomer(new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		persistRental(new Rental(rentedCar, customer, TODAY, A_NUMBER_OF_DAYS));

		GuiActionRunner.execute(() -> rentalPanel.onActivate());

		assertThat(rowsOf(window.table(RENTAL_TABLE).contents()))
				.containsExactly(List.of(A_CUSTOMER_FULLNAME, A_RENTED_CAR_DESCRIPTION, A_FORMATTED_START_DATE,
						A_FORMATTED_END_DATE, A_NUMBER_OF_DAYS.toString(), A_RENTED_TOTAL_AMOUNT.toString()));

		String[] carComboContents = window.comboBox(CAR_COMBO_BOX).contents();
		assertThat(carComboContents).containsExactlyInAnyOrder(A_CAR_DESCRIPTION, ANOTHER_CAR_DESCRIPTION);

		String[] customerComboContents = window.comboBox(CUSTOMER_COMBO_BOX).contents();
		assertThat(customerComboContents).containsExactlyInAnyOrder(A_CUSTOMER_FULLNAME, ANOTHER_CUSTOMER_FULLNAME);
	}

	@Test
	@GUITest
	public void testAddRentalPersistsInDatabaseUpdatesTableAndRemovesCarFromAvailableCombo() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
		Car alreadyRentedCar = persistCar(
				new Car(A_RENTED_CAR_PLATE, A_RENTED_BRAND, A_RENTED_MODEL, A_RENTED_DAILY_RATE));

		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		Customer anotherCustomer = persistCustomer(
				new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		persistRental(new Rental(alreadyRentedCar, anotherCustomer, TODAY, A_NUMBER_OF_DAYS));

		GuiActionRunner.execute(() -> rentalPanel.onActivate());

		window.comboBox(CAR_COMBO_BOX).selectItem(A_CAR_DESCRIPTION);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(A_CUSTOMER_FULLNAME);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).click();

		assertThat(window.table(RENTAL_TABLE).rowCount()).isEqualTo(2);
		window.label(ERROR_LABEL).requireText(" ");

		assertThat(window.comboBox(CAR_COMBO_BOX).contents()).containsExactly(ANOTHER_CAR_DESCRIPTION);
		assertThat(window.comboBox(CUSTOMER_COMBO_BOX).contents()).containsExactlyInAnyOrder(A_CUSTOMER_FULLNAME,
				ANOTHER_CUSTOMER_FULLNAME);

		RentalTableModel tableModel = rentalPanel.getRentalTableModel();
		RentalViewModel createdRental = IntStream.range(0, tableModel.getRowCount()).mapToObj(tableModel::getRentalAt)
				.filter(r -> r.getCarDescription().equals(A_CAR_DESCRIPTION)).findFirst().orElseThrow();

		Rental persisted = entityManager.find(Rental.class, createdRental.getId());
		assertThat(persisted).isNotNull();
		assertThat(persisted.getCar().getId()).isEqualTo(car.getId());
		assertThat(persisted.getCustomer().getId()).isEqualTo(customer.getId());
		assertThat(persisted.getStartDate()).isEqualTo(TODAY);
		assertThat(persisted.getDays()).isEqualTo(A_NUMBER_OF_DAYS);

	}

	@Test
	@GUITest
	public void testAddRentalWithCarAlreadyRentedShowsError() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

		GuiActionRunner.execute(() -> rentalPanel.onActivate());

		window.comboBox(CAR_COMBO_BOX).selectItem(0);
		window.comboBox(CUSTOMER_COMBO_BOX).selectItem(0);
		window.spinner(RENTAL_DAYS_SPINNER).select(A_NUMBER_OF_DAYS);

		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		window.button(JButtonMatcher.withText(ADD_RENTAL_BTN)).click();

		window.label(ERROR_LABEL).requireText("Car with id '" + car.getId() + "' is already rented");
	}

	@Test
	@GUITest
	public void testDeleteRentalRemovesFromDatabaseAndCarBecomesAvailableAgain() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Car anotherCar = persistCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));

		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		Customer anotherCustomer = persistCustomer(
				new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		Rental rental = persistRental(new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS));
		persistRental(new Rental(anotherCar, anotherCustomer, ANOTHER_START_DATE, ANOTHER_NUMBER_OF_DAYS));

		GuiActionRunner.execute(() -> rentalPanel.onActivate());

		RentalTableModel tableModel = rentalPanel.getRentalTableModel();
		int rowToDelete = IntStream.range(0, tableModel.getRowCount())
				.filter(row -> tableModel.getRentalAt(row).getCarDescription().equals(A_CAR_DESCRIPTION)).findFirst()
				.orElseThrow();

		window.table(RENTAL_TABLE).selectRows(rowToDelete);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		assertThat(rowsOf(window.table(RENTAL_TABLE).contents())).containsExactly(List.of(ANOTHER_CUSTOMER_FULLNAME,
				ANOTHER_CAR_DESCRIPTION, ANOTHER_FORMATTED_START_DATE, ANOTHER_FORMATTED_END_DATE,
				ANOTHER_NUMBER_OF_DAYS.toString(), ANOTHER_TOTAL_AMOUNT.toString()));

		Rental persisted = entityManager.find(Rental.class, rental.getId());
		assertThat(persisted).isNull();

		assertThat(window.comboBox(CAR_COMBO_BOX).contents()).containsExactly(A_CAR_DESCRIPTION);
		assertThat(window.comboBox(CUSTOMER_COMBO_BOX).contents()).containsExactlyInAnyOrder(A_CUSTOMER_FULLNAME,
				ANOTHER_CUSTOMER_FULLNAME);
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
