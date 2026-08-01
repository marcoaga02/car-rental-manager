package com.marcoaga02.carrentalmanager.view.swing.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = { "Car Plate", "Brand", "Model", "Daily Rate (€)" };

	private transient List<CarViewModel> cars = new ArrayList<>();

	public void setCars(List<CarViewModel> cars) {
		this.cars = List.copyOf(cars);
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		return cars.size();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public CarViewModel getCarAt(int rowIndex) {
		if (rowIndex >= cars.size()) {
			throw new IllegalArgumentException("Invalid row: " + rowIndex);
		}
		
	    return cars.get(rowIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CarViewModel car = getCarAt(rowIndex);
		switch (columnIndex) {
		case 0:
			return car.getCarPlate();
		case 1:
			return car.getBrand();
		case 2:
			return car.getModel();
		case 3:
			return car.getDailyRate();
		default:
			throw new IllegalArgumentException("Invalid column: " + columnIndex);
		}
	}

	// package-private for tests only
	List<CarViewModel> getCars() {
		return cars;
	}

}
