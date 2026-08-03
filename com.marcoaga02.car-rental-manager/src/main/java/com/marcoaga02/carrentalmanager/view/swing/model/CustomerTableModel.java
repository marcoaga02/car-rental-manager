package com.marcoaga02.carrentalmanager.view.swing.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;

public class CustomerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMN_NAMES = { "Tax Id Code", "Firstname", "Lastname" };

	private transient List<CustomerViewModel> customers = new ArrayList<>();

	public void setCustomers(List<CustomerViewModel> customers) {
		this.customers = List.copyOf(customers);
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		return customers.size();
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	public CustomerViewModel getCustomerAt(int rowIndex) {
		if (rowIndex >= customers.size()) {
			throw new IllegalArgumentException("Invalid row: " + rowIndex);
		}

		return customers.get(rowIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CustomerViewModel customer = getCustomerAt(rowIndex);
		switch (columnIndex) {
		case 0:
			return customer.getTaxIdCode();
		case 1:
			return customer.getFirstname();
		case 2:
			return customer.getLastname();
		default:
			throw new IllegalArgumentException("Invalid column: " + columnIndex);
		}
	}

	// package-private for tests only
	List<CustomerViewModel> getCustomers() {
		return customers;
	}

}
