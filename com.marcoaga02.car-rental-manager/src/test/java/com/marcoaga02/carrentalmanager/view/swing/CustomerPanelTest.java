package com.marcoaga02.carrentalmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.fixture.Containers.showInFrame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

import com.marcoaga02.carrentalmanager.controller.CustomerController;
import com.marcoaga02.carrentalmanager.testutils.TableAssertionUtils;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

@RunWith(GUITestRunner.class)
public class CustomerPanelTest extends AssertJSwingJUnitTestCase {

	private static final String CUSTOMER_TABLE = "customerTable";

	private static final String ADD_CUSTOMER_BTN = "Add customer";
	private static final String DELETE_SELECTED_BTN = "Delete selected";

	private static final String TAX_ID_CODE_TEXT_FIELD = "taxIdCodeTextField";
	private static final String FIRSTNAME_TEXT_FIELD = "firstnameTextField";
	private static final String LASTNAME_TEXT_FIELD = "lastnameTextField";

	private static final String ERROR_LABEL = "errorLabel";

	private static final Long AN_ID = 10L;
	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final Long ANOTHER_ID = 15L;
	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	@Mock
	private CustomerController customerController;

	private FrameFixture window;

	private CustomerPanel customerPanel;

	private AutoCloseable closeable;

	private CustomerViewModel customer, anotherCustomer;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		customerPanel = GuiActionRunner.execute(() -> {
			CustomerPanel panel = new CustomerPanel();
			panel.setCustomerController(customerController);
			return panel;
		});
		window = showInFrame(robot(), customerPanel);

		customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Tax id code"));
		window.textBox(TAX_ID_CODE_TEXT_FIELD).requireEnabled();

		window.label(JLabelMatcher.withText("Firstname"));
		window.textBox(FIRSTNAME_TEXT_FIELD).requireEnabled();

		window.label(JLabelMatcher.withText("Lastname"));
		window.textBox(LASTNAME_TEXT_FIELD).requireEnabled();

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();

		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();

		window.table(CUSTOMER_TABLE);

		verifyNoInteractions(customerController);
	}

	@Test
	@GUITest
	public void testShowAllCustomersShouldAddCustomersToTheTable() {
		GuiActionRunner.execute(() -> {
			customerPanel.getErrorLabel().setText("error message");
			customerPanel.showAllCustomers(List.of(customer, anotherCustomer));
		});

		assertThat(TableAssertionUtils.rowsOf(window.table(CUSTOMER_TABLE).contents())).containsExactlyInAnyOrder(
				List.of(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME),
				List.of(ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME));

		window.label(ERROR_LABEL).requireText(" ");
	}

	@Test
	@GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		String errorMessage = "error message";
		GuiActionRunner.execute(() -> customerPanel.showError(errorMessage));
		window.label(ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	@GUITest
	public void testAddCustomerButtonDisabledWhenAllFieldsAreEmpty() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText("");
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText("");
		window.textBox(LASTNAME_TEXT_FIELD).enterText("");

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCustomerButtonDisabledWhenAllFieldsAreBlank() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(" ");
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(" ");
		window.textBox(LASTNAME_TEXT_FIELD).enterText(" ");

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCustomerButtonDisabledWhenTaxIdCodeIsInvalid() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(" ");
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCustomerButtonDisabledWhenFirstnameIsInvalid() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(" ");
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCustomerButtonDisabledWhenLastnameIsInvalid() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(" ");

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCustomerButtonEnabledWhenAllFieldsAreValid() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireEnabled();
	}

	@Test
	@GUITest
	public void testDeleteCustomerButtonDisabledWhenTableIsEmpty() {
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteCustomerButtonDisabledWhenNoTableRowIsSelected() {
		GuiActionRunner.execute(() -> customerPanel.getCustomerTableModel().setCustomers(List.of(customer)));

		window.table(CUSTOMER_TABLE).selectRows(0);
		window.table(CUSTOMER_TABLE).unselectRows(0);

		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteCustomerButtonEnabledWhenATableRowIsSelected() {
		GuiActionRunner.execute(() -> customerPanel.getCustomerTableModel().setCustomers(List.of(customer)));

		window.table(CUSTOMER_TABLE).selectRows(0);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).requireEnabled();
	}

	@Test
	@GUITest
	public void testClearFieldsShouldResetAllFieldsAndDisableAddCustomerButton() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);

		GuiActionRunner.execute(() -> customerPanel.clearFields());

		window.textBox(TAX_ID_CODE_TEXT_FIELD).requireEmpty();
		window.textBox(FIRSTNAME_TEXT_FIELD).requireEmpty();
		window.textBox(LASTNAME_TEXT_FIELD).requireEmpty();
		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddCustomerButtonShouldDelegateToCustomerControllerCreateCustomer() {
		window.textBox(TAX_ID_CODE_TEXT_FIELD).enterText(A_TAX_ID_CODE);
		window.textBox(FIRSTNAME_TEXT_FIELD).enterText(A_FIRSTNAME);
		window.textBox(LASTNAME_TEXT_FIELD).enterText(A_LASTNAME);

		window.button(JButtonMatcher.withText(ADD_CUSTOMER_BTN)).click();

		verify(customerController).createCustomer(new CustomerViewModel(null, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		verifyNoMoreInteractions(customerController);
	}

	@Test
	@GUITest
	public void testDeleteCustomerButtonShouldDelegateToCustomerControllerDeleteCustomer() {
		GuiActionRunner
				.execute(() -> customerPanel.getCustomerTableModel().setCustomers(List.of(customer, anotherCustomer)));

		window.table(CUSTOMER_TABLE).selectRows(1);
		window.button(JButtonMatcher.withText(DELETE_SELECTED_BTN)).click();

		verify(customerController).deleteCustomer(ANOTHER_ID);
		verifyNoMoreInteractions(customerController);
	}

	@Test
	@GUITest
	public void testOnActivateShouldDelegateToCustomerControllerGetAllCustomers() {
		GuiActionRunner.execute(() -> customerPanel.onActivate());

		verify(customerController).getAllCustomers();
		verifyNoMoreInteractions(customerController);
	}

	@Test
	@GUITest
	public void testOnActivateDoesNothingWhenControllerNotSet() {
		CustomerPanel freshPanel = GuiActionRunner.execute(CustomerPanel::new);

		assertDoesNotThrow(() -> GuiActionRunner.execute(freshPanel::onActivate));
	}

}
