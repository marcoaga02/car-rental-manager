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

	private CustomerViewModel customer, anotherCustomer;

	@BeforeEach
	void setUp() {
		customerTableModel = new CustomerTableModel();
		customer = new CustomerViewModel(AN_ID, A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		anotherCustomer = new CustomerViewModel(ANOTHER_ID, ANOTHER_TAX_ID_CODE, ANOTHER_FIRSTNAME, ANOTHER_LASTNAME);
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
		customerTableModel.getCustomers().add(customer);
		customerTableModel.setCustomers(List.of());

		assertThat(customerTableModel.getCustomers()).isEmpty();
	}

	@Test
	void testSetCustomersWithAListOfOneElementAddTheElement() {
		customerTableModel.setCustomers(List.of(customer));

		assertThat(customerTableModel.getCustomers()).containsExactly(customer);
	}

	@Test
	void testSetCustomersWithAListOfMultipleElementsAddAllTheElements() {
		customerTableModel.setCustomers(List.of(customer, anotherCustomer));

		assertThat(customerTableModel.getCustomers()).containsExactlyInAnyOrder(customer, anotherCustomer);
	}

	@Test
	void testSetCustomersShouldReplacePreviousCustomers() {
		customerTableModel.getCustomers().add(customer);

		customerTableModel.setCustomers(List.of(anotherCustomer));

		assertThat(customerTableModel.getCustomers()).containsExactly(anotherCustomer);
	}

	@Test
	void testSetCustomersShouldDefensivelyCopyTheGivenList() {
		List<CustomerViewModel> originalList = new ArrayList<>(List.of(customer));

		customerTableModel.setCustomers(originalList);

		originalList.add(anotherCustomer);

		assertThat(customerTableModel.getCustomers()).containsExactly(customer);
	}

	@Test
	void testGetCustomerAtShouldReturnCorrectCustomer() {
		List<CustomerViewModel> customers = customerTableModel.getCustomers();
		customers.add(customer);
		customers.add(anotherCustomer);

		assertThat(customerTableModel.getCustomerAt(1)).isEqualTo(anotherCustomer);
	}

	@Test
	void testGetCustomerAtWithInvalidIndexShouldThrowIllegalArgumentException() {
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getCustomerAt(4)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetCustomerAtWithInvalidBoundaryIndexShouldThrowIllegalArgumentException() {
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getCustomerAt(1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

	@Test
	void testGetValueAtShouldReturnCorrectValues() {
		customerTableModel.getCustomers().add(customer);

		assertThat(customerTableModel.getValueAt(0, 0)).isEqualTo(A_TAX_ID_CODE);
		assertThat(customerTableModel.getValueAt(0, 1)).isEqualTo(A_FIRSTNAME);
		assertThat(customerTableModel.getValueAt(0, 2)).isEqualTo(A_LASTNAME);
	}

	@Test
	void testGetValueAtWithInvalidColumnShouldThrowIllegalArgumentException() {
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getValueAt(0, 3)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid column: 3");
	}

	@Test
	void testGetValueAtWithInvalidRowShouldThrowIllegalArgumentException() {
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getValueAt(4, 0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 4");
	}

	@Test
	void testGetValueAtWithInvalidBoundaryRowShouldThrowIllegalArgumentException() {
		customerTableModel.getCustomers().add(customer);

		assertThatThrownBy(() -> customerTableModel.getValueAt(1, 0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid row: 1");
	}

}
