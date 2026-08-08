package com.marcoaga02.carrentalmanager.view.swing;

import static com.marcoaga02.carrentalmanager.testutils.TableAssertionUtils.rowsOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.fixture.Containers.showInFrame;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
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

import com.marcoaga02.carrentalmanager.controller.CustomerController;
import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.service.CustomerServiceImpl;
import com.marcoaga02.carrentalmanager.testutils.BaseSwingPostgresTest;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.view.swing.model.CustomerTableModel;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

@RunWith(GUITestRunner.class)
public class CustomerPanelIT extends BaseSwingPostgresTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final Integer A_NUMBER_OF_DAYS = 6;

	private static final String CUSTOMER_TABLE = "customerTable";

	private static final String ADD_CUSTOMER_BTN = "Add customer";
	private static final String DELETE_SELECTED_BTN = "Delete selected";

	private static final String TAX_ID_CODE_TEXT_FIELD = "taxIdCodeTextField";
	private static final String FIRSTNAME_TEXT_FIELD = "firstnameTextField";
	private static final String LASTNAME_TEXT_FIELD = "lastnameTextField";

	private static final String ERROR_LABEL = "errorLabel";

	private static final LocalDate TODAY = LocalDate.parse("2026-05-10");
	private final Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

	private CustomerPanel customerPanel;
	private CustomerController customerController;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		TransactionManagerJpa transactionManager = new TransactionManagerJpa(entityManagerFactory, fixedClock);

		GuiActionRunner.execute(() -> {
			customerPanel = new CustomerPanel();
			customerController = new CustomerController(
					new CustomerServiceImpl(transactionManager, new CustomerMapper()), customerPanel);
			customerPanel.setCustomerController(customerController);
			return customerPanel;
		});

		window = showInFrame(robot(), customerPanel);
	}

	@Test
	@GUITest
	public void testOnActivateShowsOnlyActiveCustomersFromDatabase() {
		persistDeletedCustomer();
		persistActiveCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistActiveCustomer(new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		GuiActionRunner.execute(() -> customerPanel.onActivate());

		assertThat(rowsOf(window.table(CUSTOMER_TABLE).contents())).containsExactlyInAnyOrder(
				List.of(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME),
				List.of(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));
	}

	@Test
	@GUITest
	public void testAddCustomerPersistsInDatabaseAndKeepsPreviouslyExistingCustomers() {
		persistActiveCustomer(new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));
		GuiActionRunner.execute(() -> customerPanel.onActivate());

		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).click();

		assertThat(rowsOf(window.table(CUSTOMER_TABLE).contents())).containsExactlyInAnyOrder(
				List.of(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME),
				List.of(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		window.label(ERROR_LABEL).requireText(" ");

		CustomerTableModel tableModel = customerPanel.getCustomerTableModel();
		CustomerViewModel createdCustomer = IntStream.range(0, tableModel.getRowCount())
				.mapToObj(tableModel::getCustomerAt).filter(c -> c.getTaxIdCode().equals(A_TAX_ID_CODE)).findFirst()
				.orElseThrow();

		Customer persisted = entityManager.find(Customer.class, createdCustomer.getId());
		assertThat(persisted).isNotNull();
		assertThat(persisted.getTaxIdCode()).isEqualTo(A_TAX_ID_CODE);
		assertThat(persisted.getFirstname()).isEqualTo(A_FIRSTNAME);
		assertThat(persisted.getLastname()).isEqualTo(A_LASTNAME);
		assertThat(persisted.getDeleted()).isFalse();
	}

	@Test
	@GUITest
	public void testAddCustomerWithDuplicateTaxIdCodeShowsError() {
		persistActiveCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));

		GuiActionRunner.execute(() -> customerPanel.onActivate());

		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(ANOTHER_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(ANOTHER_LASTNAME);

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).click();

		window.label(ERROR_LABEL).requireText("A customer with taxIdCode '" + A_TAX_ID_CODE + "' already exists");
	}

	@Test
	@GUITest
	public void testDeleteCustomerSoftDeletesInDatabaseAndRemovesFromTable() {
		Customer customer = persistActiveCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistActiveCustomer(new Customer(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		GuiActionRunner.execute(() -> customerPanel.onActivate());

		window.table(CUSTOMER_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		assertThat(rowsOf(window.table(CUSTOMER_TABLE).contents()))
				.containsExactly(List.of(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		Customer persisted = entityManager.find(Customer.class, customer.getId());
		assertThat(persisted.getDeleted()).isTrue();
	}

	@Test
	@GUITest
	public void testDeleteCustomerWithActiveRentalShowsErrorAndCustomerStaysActive() {
		Car car = persistCar(new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE));
		Customer customer = persistActiveCustomer(new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		persistRental(new Rental(car, customer, TODAY, A_NUMBER_OF_DAYS));

		GuiActionRunner.execute(() -> customerPanel.onActivate());

		window.table(CUSTOMER_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		window.label(ERROR_LABEL)
				.requireText("Customer with id '" + customer.getId() + "' has an active rental and cannot be deleted");

		Customer persisted = entityManager.find(Customer.class, customer.getId());
		assertThat(persisted.getDeleted()).isFalse();
	}

	private Customer persistActiveCustomer(Customer customer) {
		return persistCustomer(customer, false);
	}

	private Customer persistDeletedCustomer() {
		Customer deletedCustomer = new Customer("aDeletedTaxIdCode", "aDeletedFirstname", "aDeletedLastname");
		return persistCustomer(deletedCustomer, true);
	}

	private Customer persistCustomer(Customer customer, boolean deleted) {
		customer.setDeleted(deleted);
		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return customer;
	}

	private Car persistCar(Car car) {
		entityManager.getTransaction().begin();
		entityManager.persist(car);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return car;
	}

	private Rental persistRental(Rental rental) {
		entityManager.getTransaction().begin();
		entityManager.persist(rental);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return rental;
	}

}
