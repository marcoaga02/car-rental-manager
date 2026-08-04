package com.marcoaga02.carrentalmanager.view.swing.model;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public class RentalTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = { "Customer", "Car", "Start Date", "End Date", "Days", "Total Amount (€)" };
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private transient List<RentalViewModel> rentals = new ArrayList<>();

	public void setRentals(List<RentalViewModel> rentals) {
		this.rentals = List.copyOf(rentals);
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		return rentals.size();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public RentalViewModel getRentalAt(int rowIndex) {
		if (rowIndex >= rentals.size()) {
			throw new IllegalArgumentException("Invalid row: " + rowIndex);
		}

		return rentals.get(rowIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		RentalViewModel rental = getRentalAt(rowIndex);
		switch (columnIndex) {
		case 0:
			return rental.getCustomerFullname();
		case 1:
			return rental.getCarDescription();
		case 2:
			return rental.getStartDate().format(DATE_FORMATTER);
		case 3:
			return rental.getEndDate().format(DATE_FORMATTER);
		case 4:
			return rental.getRentalDays();
		case 5:
			return rental.getTotalAmount();
		default:
			throw new IllegalArgumentException("Invalid column: " + columnIndex);
		}
	}

	// package-private for tests only
	List<RentalViewModel> getRentals() {
		return rentals;
	}

}
