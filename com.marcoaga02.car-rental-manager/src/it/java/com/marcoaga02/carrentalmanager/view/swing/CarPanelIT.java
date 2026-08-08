package com.marcoaga02.carrentalmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.fixture.Containers.showInFrame;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.IntStream;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.marcoaga02.carrentalmanager.controller.CarController;
import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.service.CarServiceImpl;
import com.marcoaga02.carrentalmanager.testutils.BaseSwingPostgresTest;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.view.swing.model.CarTableModel;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

@RunWith(GUITestRunner.class)
public class CarPanelIT extends BaseSwingPostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String ANOTHER_CAR_PLATE = "anotherCarPlate";
	private static final String ANOTHER_BRAND = "anotherBrand";
	private static final String ANOTHER_MODEL = "anotherModel";
	private static final BigDecimal ANOTHER_DAILY_RATE = BigDecimal.valueOf(4.3);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final Integer A_NUMBER_OF_DAYS = 6;

	private static final String ADD_CAR_BTN = "Add car";
	private static final String DELETE_SELECTED_BTN = "Delete selected";

	private static final String CAR_PLATE_TEXT_FIELD = "carPlateTextField";
	private static final String BRAND_TEXT_FIELD = "brandTextField";
	private static final String MODEL_TEXT_FIELD = "modelTextField";
	private static final String DAILY_RATE_SPINNER = "dailyRateSpinner";
	private static final String CAR_TABLE = "carTable";
	private static final String ERROR_LABEL = "errorLabel";

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");
	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private CarPanel carPanel;
	private CarController carController;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);

		GuiActionRunner.execute(() -> {
			carPanel = new CarPanel();
			carController = new CarController(new CarServiceImpl(transactionManager, new CarMapper()), carPanel);
			carPanel.setCarController(carController);
			return carPanel;
		});

		window = showInFrame(robot(), carPanel);
	}

	@Test
	@GUITest
	public void testOnActivateShowsOnlyActiveCarsFromDatabase() {
		persistDeletedCar();
		persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistActiveCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));

		GuiActionRunner.execute(() -> carPanel.onActivate());

		String[][] contents = window.table(CAR_TABLE).contents();
		assertThat(contents).isDeepEqualTo(new String[][] { { A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE.toString() },
				{ ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE.toString() } });
	}

	@Test
	@GUITest
	public void testAddCarPersistsInDatabaseAndKeepsPreviouslyExistingCars() {
		persistActiveCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));
		GuiActionRunner.execute(() -> carPanel.onActivate());

		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(A_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(A_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(A_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).click();

		String[][] contents = window.table(CAR_TABLE).contents();
		assertThat(contents).isDeepEqualTo(
				new String[][] { { ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE.toString() },
						{ A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE.toString() } });

		window.label(ERROR_LABEL).requireText(" ");

		CarTableModel tableModel = carPanel.getCarTableModel();
		CarViewModel createdCar = IntStream.range(0, tableModel.getRowCount()).mapToObj(tableModel::getCarAt)
				.filter(c -> c.getCarPlate().equals(A_CAR_PLATE)).findFirst().orElseThrow();

		Car persisted = entityManager.find(Car.class, createdCar.getId());
		assertThat(persisted).isNotNull();
		assertThat(persisted.getCarPlate()).isEqualTo(A_CAR_PLATE);
		assertThat(persisted.getBrand()).isEqualTo(A_BRAND);
		assertThat(persisted.getModel()).isEqualTo(A_MODEL);
		assertThat(persisted.getDailyRate()).isEqualByComparingTo(A_DAILY_RATE);
		assertThat(persisted.getDeleted()).isFalse();
	}

	@Test
	@GUITest
	public void testAddCarWithDuplicatePlateShowsError() {
		persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));

		GuiActionRunner.execute(() -> carPanel.onActivate());

		window.textBox(CAR_PLATE_TEXT_FIELD).enterText(A_CAR_PLATE);
		window.textBox(BRAND_TEXT_FIELD).enterText(ANOTHER_BRAND);
		window.textBox(MODEL_TEXT_FIELD).enterText(ANOTHER_MODEL);
		window.spinner(DAILY_RATE_SPINNER).select(ANOTHER_DAILY_RATE);

		window.button(JButtonMatcher.withText(ADD_CAR_BTN)).click();

		window.label(ERROR_LABEL).requireText("A car with carPlate '" + A_CAR_PLATE + "' already exists");
	}

	@Test
	@GUITest
	public void testDeleteCarSoftDeletesInDatabaseAndRemovesFromTable() {
		Car car = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		persistActiveCar(new Car(ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE));

		GuiActionRunner.execute(() -> carPanel.onActivate());

		window.table(CAR_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		String[][] contents = window.table(CAR_TABLE).contents();
		assertThat(contents).isDeepEqualTo(
				new String[][] { { ANOTHER_CAR_PLATE, ANOTHER_BRAND, ANOTHER_MODEL, ANOTHER_DAILY_RATE.toString() } });

		Car persisted = entityManager.find(Car.class, car.getId());
		assertThat(persisted.getDeleted()).isTrue();
	}

	@Test
	@GUITest
	public void testDeleteCarWithActiveRentalShowsErrorAndCarStaysActive() {
		Car car = persistActiveCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		GuiActionRunner.execute(() -> carPanel.onActivate());

		window.table(CAR_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		window.label(ERROR_LABEL)
				.requireText("Car with id '" + car.getId() + "' is currently rented and cannot be deleted");

		Car persisted = entityManager.find(Car.class, car.getId());
		assertThat(persisted.getDeleted()).isFalse();
	}

	private Car persistActiveCar(Car car) {
		return persistCar(car, false);
	}

	private Car persistDeletedCar() {
		Car deletedCar = new Car("aDeletedPlate", "aBrand", "aModel", BigDecimal.valueOf(10.0));
		return persistCar(deletedCar, true);
	}

	private Car persistCar(Car car, boolean deleted) {
		car.setDeleted(deleted);
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
