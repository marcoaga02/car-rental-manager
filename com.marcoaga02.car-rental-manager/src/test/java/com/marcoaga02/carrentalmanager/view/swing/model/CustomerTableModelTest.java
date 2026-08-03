package com.marcoaga02.carrentalmanager.view.swing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

class CustomerTableModelTest {

	private static final Long AN_ID = 10L;
	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final Long ANOTHER_ID = 15L;
	private static final String ANOTHER_TAX_ID_CODE = "anotherTaxIdCode";
	private static final String ANOTHER_FIRSTNAME = "anotherFirstname";
	private static final String ANOTHER_LASTNAME = "anotherLastname";

	private static final String TAX_ID_CODE_COLUMN = "Tax Id Code";
	private static final String FIRSTNAME_COLUMN = "Firstname";
	private static final String LASTNAME_COLUMN = "Lastname";

	private CustomerTableModel customerTableModel;

	@BeforeEach
	void setUp() {
		customerTableModel = new CustomerTableModel();
	}

	@Test
	void testInitialState() {
		assertThat(customerTableModel.getRowCount()).isZero();
		assertThat(customerTableModel.getColumnCount()).isEqualTo(3);
	}

	@Test
	void testColumnNames() {
		assertThat(customerTableModel.getColumnName(0)).isEqualTo(TAX_ID_CODE_COLUMN);
		assertThat(customerTableModel.getColumnName(1)).isEqualTo(FIRSTNAME_COLUMN);
		assertThat(customerTableModel.getColumnName(2)).isEqualTo(LASTNAME_COLUMN);
	}

	@Test
	void testSetCustomersWithEmptyListShouldClearTable() {
		customerTableModel.getCustomers().add(new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME));
		customerTableModel.setCustomers(List.of());

		assertThat(customerTableModel.getCustomers()).isEmpty();
	}

	@Test
	void testSetCustomersWithAListOfOneElementAddTheElement() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.setCustomers(List.of(customer));

		assertThat(customerTableModel.getCustomers()).containsExactly(customer);
	}

	@Test
	void testSetCustomersWithAListOfMultipleElementsAddAllTheElements() {
		CustomerViewModel customer1 = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		CustomerViewModel customer2 = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME,
				ANOTHER_LASTNAME);
		customerTableModel.setCustomers(List.of(customer1, customer2));

		assertThat(customerTableModel.getCustomers()).containsExactlyInAnyOrder(customer1, customer2);
	}

	@Test
	void testSetCustomersShouldReplacePreviousCustomers() {
		CustomerViewModel customer1 = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer1);

		CustomerViewModel customer2 = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME,
				ANOTHER_LASTNAME);
		customerTableModel.setCustomers(List.of(customer2));

		assertThat(customerTableModel.getCustomers()).containsExactly(customer2);
	}

	@Test
	void testSetCustomersShouldDefensivelyCopyTheGivenList() {
		CustomerViewModel customer1 = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		List<CustomerViewModel> originalList = new ArrayList<>(List.of(customer1));

		customerTableModel.setCustomers(originalList);

		CustomerViewModel customer2 = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME,
				ANOTHER_LASTNAME);
		originalList.add(customer2);

		assertThat(customerTableModel.getCustomers()).containsExactly(customer1);
	}

	@Test
	void testGetCustomerAtShouldReturnCorrectCustomer() {
		CustomerViewModel customer1 = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		CustomerViewModel customer2 = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME,
				ANOTHER_LASTNAME);
		List<CustomerViewModel> customers = customerTableModel.getCustomers();
		customers.add(customer1);
		customers.add(customer2);

		assertThat(customerTableModel.getCustomerAt(1)).isEqualTo(customer2);
	}

	@Test
	void testGetCustomerAtWithInvalidIndexShouldThrowIllegalArgumentException() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getCustomerAt(4)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetCustomerAtWithInvalidBoundaryIndexShouldThrowIllegalArgumentException() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getCustomerAt(1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

	@Test
	void testGetValueAtShouldReturnCorrectValues() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer);

		assertThat(customerTableModel.getValueAt(0, 0)).isEqualTo(A_TAX_ID_CODE);
		assertThat(customerTableModel.getValueAt(0, 1)).isEqualTo(A_FIRSTNAME);
		assertThat(customerTableModel.getValueAt(0, 2)).isEqualTo(A_LASTNAME);
	}

	@Test
	void testGetValueAtWithInvalidColumnShouldThrowIllegalArgumentException() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getValueAt(0, 3)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid column: 3");
	}

	@Test
	void testGetValueAtWithInvalidRowShouldThrowIllegalArgumentException() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getValueAt(4, 0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetValueAtWithInvalidBoundaryRowShouldThrowIllegalArgumentException() {
		CustomerViewModel customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getValueAt(1, 0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

}
