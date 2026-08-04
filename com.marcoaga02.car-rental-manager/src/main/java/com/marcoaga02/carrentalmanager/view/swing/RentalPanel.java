package com.marcoaga02.carrentalmanager.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.marcoaga02.carrentalmanager.controller.RentalController;
import com.marcoaga02.carrentalmanager.view.RentalView;
import com.marcoaga02.carrentalmanager.view.swing.model.RentalTableModel;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;
import com.marcoaga02.carrentalmanager.viewmodel.RentalCreationRequest;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public class RentalPanel extends JPanel implements RentalView, ActivablePanel {

	private static final long serialVersionUID = 1L;

	private RentalController rentalController;

	private JTable rentalTable;
	private RentalTableModel rentalTableModel;
	private JPanel leftPanel;
	private JPanel footerPanel;
	private JLabel errorLabel;
	private JButton deleteRentalButton;
	private JPanel rightPanel;
	private JLabel createRentalTitleLabel;
	private JPanel fieldPanel;
	private JLabel carLabel;
	private JLabel customerLabel;
	private JLabel rentalDaysLabel;
	private JPanel fillerPanel;
	private JButton addRentalButton;
	private JComboBox<CarViewModel> carComboBox;
	private JComboBox<CustomerViewModel> customerComboBox;
	private JSpinner rentalDaysSpinner;

	/**
	 * Create the panel.
	 */
	public RentalPanel() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(0, 0));

		rentalTableModel = new RentalTableModel();

		leftPanel = new JPanel();
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		add(leftPanel, BorderLayout.CENTER);

		rentalTable = new JTable();
		rentalTable.setBorder(new LineBorder(new Color(0, 0, 0)));
		rentalTable.setName("rentalTable");
		rentalTable.setModel(rentalTableModel);
		rentalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rentalTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
		rentalTable.getTableHeader().setForeground(Color.BLACK);
		leftPanel.setLayout(new BorderLayout(0, 0));

		rentalTable.getSelectionModel().addListSelectionListener(e -> updateDeleteRentalButtonState());

		JScrollPane scrollPane = new JScrollPane();
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(rentalTable);

		footerPanel = new JPanel();
		footerPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
		leftPanel.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new BorderLayout(0, 0));

		errorLabel = new JLabel(" ");
		errorLabel.setName("errorLabel");
		errorLabel.setForeground(Color.RED);
		footerPanel.add(errorLabel, BorderLayout.WEST);

		deleteRentalButton = new JButton("Delete selected");
		deleteRentalButton.setEnabled(false);
		footerPanel.add(deleteRentalButton, BorderLayout.EAST);

		deleteRentalButton.addActionListener(
				e -> rentalController.deleteRental(rentalTableModel.getRentalAt(rentalTable.getSelectedRow()).getId()));

		rightPanel = new JPanel();
		rightPanel.setBorder(new LineBorder(Color.GRAY));
		add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new BorderLayout(0, 0));

		createRentalTitleLabel = new JLabel("Create Rental");
		createRentalTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createRentalTitleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
		rightPanel.add(createRentalTitleLabel, BorderLayout.NORTH);

		fieldPanel = new JPanel();
		fieldPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		rightPanel.add(fieldPanel, BorderLayout.CENTER);
		GridBagLayout gbl_fieldPanel = new GridBagLayout();
		gbl_fieldPanel.columnWidths = new int[] { 160, 0 };
		gbl_fieldPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_fieldPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_fieldPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		fieldPanel.setLayout(gbl_fieldPanel);

		carLabel = new JLabel("Car");
		carLabel.setForeground(Color.DARK_GRAY);
		carLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_carLabel = new GridBagConstraints();
		gbc_carLabel.gridwidth = 1;
		gbc_carLabel.anchor = GridBagConstraints.WEST;
		gbc_carLabel.insets = new Insets(0, 0, 5, 0);
		gbc_carLabel.gridx = 0;
		gbc_carLabel.gridy = 0;
		fieldPanel.add(carLabel, gbc_carLabel);

		carComboBox = new JComboBox<CarViewModel>();
		carComboBox.setEnabled(false);
		carComboBox.setBackground(new Color(255, 255, 255));
		carComboBox.setName("carComboBox");
		carComboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof CarViewModel) {
					CarViewModel car = (CarViewModel) value;
					setText(car.getBrand() + " " + car.getModel() + " [" + car.getCarPlate() + "]");
				}
				return this;
			}
		});
		GridBagConstraints gbc_carComboBox = new GridBagConstraints();
		gbc_carComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_carComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_carComboBox.gridx = 0;
		gbc_carComboBox.gridy = 1;
		fieldPanel.add(carComboBox, gbc_carComboBox);

		customerLabel = new JLabel("Customer");
		customerLabel.setForeground(Color.DARK_GRAY);
		customerLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_customerLabel = new GridBagConstraints();
		gbc_customerLabel.gridwidth = 1;
		gbc_customerLabel.anchor = GridBagConstraints.WEST;
		gbc_customerLabel.insets = new Insets(0, 0, 5, 0);
		gbc_customerLabel.gridx = 0;
		gbc_customerLabel.gridy = 2;
		fieldPanel.add(customerLabel, gbc_customerLabel);

		customerComboBox = new JComboBox<CustomerViewModel>();
		customerComboBox.setEnabled(false);
		customerComboBox.setBackground(new Color(255, 255, 255));
		customerComboBox.setName("customerComboBox");
		customerComboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof CustomerViewModel) {
					CustomerViewModel customer = (CustomerViewModel) value;
					setText(customer.getFirstname() + " " + customer.getLastname());
				}
				return this;
			}
		});
		GridBagConstraints gbc_customerComboBox = new GridBagConstraints();
		gbc_customerComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_customerComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_customerComboBox.gridx = 0;
		gbc_customerComboBox.gridy = 3;
		fieldPanel.add(customerComboBox, gbc_customerComboBox);

		rentalDaysLabel = new JLabel("Rental days");
		rentalDaysLabel.setForeground(Color.DARK_GRAY);
		rentalDaysLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_rentalDaysLabel = new GridBagConstraints();
		gbc_rentalDaysLabel.anchor = GridBagConstraints.WEST;
		gbc_rentalDaysLabel.insets = new Insets(0, 0, 5, 0);
		gbc_rentalDaysLabel.gridx = 0;
		gbc_rentalDaysLabel.gridy = 4;
		fieldPanel.add(rentalDaysLabel, gbc_rentalDaysLabel);

		rentalDaysSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
		rentalDaysSpinner.setName("rentalDaysSpinner");
		GridBagConstraints gbc_daysSpinner = new GridBagConstraints();
		gbc_daysSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_daysSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_daysSpinner.gridx = 0;
		gbc_daysSpinner.gridy = 5;
		fieldPanel.add(rentalDaysSpinner, gbc_daysSpinner);

		carComboBox.addActionListener(e -> updateAddRentalButtonState());
		customerComboBox.addActionListener(e -> updateAddRentalButtonState());
		rentalDaysSpinner.addChangeListener(e -> updateAddRentalButtonState());

		fillerPanel = new JPanel();
		fillerPanel.setOpaque(false);
		GridBagConstraints gbc_fillerPanel = new GridBagConstraints();
		gbc_fillerPanel.weighty = 1.0;
		gbc_fillerPanel.fill = GridBagConstraints.VERTICAL;
		gbc_fillerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_fillerPanel.gridx = 0;
		gbc_fillerPanel.gridy = 6;
		fieldPanel.add(fillerPanel, gbc_fillerPanel);

		addRentalButton = new JButton("Add rental");
		addRentalButton.setEnabled(false);
		GridBagConstraints gbc_addRentalButton = new GridBagConstraints();
		gbc_addRentalButton.insets = new Insets(0, 0, 5, 0);
		gbc_addRentalButton.anchor = GridBagConstraints.SOUTHEAST;
		gbc_addRentalButton.gridx = 0;
		gbc_addRentalButton.gridy = 7;
		fieldPanel.add(addRentalButton, gbc_addRentalButton);

		addRentalButton.addActionListener(e -> {
			CarViewModel selectedCar = (CarViewModel) carComboBox.getSelectedItem();
			CustomerViewModel selectedCustomer = (CustomerViewModel) customerComboBox.getSelectedItem();
			int rentalDays = ((Number) rentalDaysSpinner.getValue()).intValue();

			rentalController
					.createRental(new RentalCreationRequest(selectedCar.getId(), selectedCustomer.getId(), rentalDays));
		});
	}

	@Override
	public void showError(String errorMessage) {
		errorLabel.setText(errorMessage);
	}

	@Override
	public void clearFields() {
		carComboBox.setSelectedItem(null);
		customerComboBox.setSelectedItem(null);
		rentalDaysSpinner.setValue(0.0);
	}

	@Override
	public void showAllRentals(List<RentalViewModel> rentals) {
		rentalTableModel.setRentals(rentals);
		resetErrorMessage();
	}

	@Override
	public void showAvailableCars(List<CarViewModel> cars) {
		carComboBox.removeAllItems();
		cars.forEach(carComboBox::addItem);
		carComboBox.setEnabled(!cars.isEmpty());
	}

	@Override
	public void showAvailableCustomers(List<CustomerViewModel> customers) {
		customerComboBox.removeAllItems();
		customers.forEach(customerComboBox::addItem);
		customerComboBox.setEnabled(!customers.isEmpty());
	}

	@Override
	public void onActivate() {
		if (rentalController != null) {
			rentalController.getAllActiveRentals();
			rentalController.loadAvailableCars();
			rentalController.loadAvailableCustomers();
		}
	}

	private void resetErrorMessage() {
		errorLabel.setText(" ");
	}

	private boolean isAddRentalButtonEnabled() {
		return carComboBox.getSelectedItem() != null && customerComboBox.getSelectedItem() != null
				&& ((Number) rentalDaysSpinner.getValue()).intValue() > 0;
	}

	private void updateAddRentalButtonState() {
		addRentalButton.setEnabled(isAddRentalButtonEnabled());
	}

	private void updateDeleteRentalButtonState() {
		deleteRentalButton.setEnabled(rentalTable.getSelectedRow() != -1);
	}

	public void setRentalController(RentalController rentalController) {
		this.rentalController = rentalController;
	}

	// package-private for tests only
	JLabel getErrorLabel() {
		return errorLabel;
	}

	JComboBox<CarViewModel> getCarComboBox() {
		return carComboBox;
	}

	JComboBox<CustomerViewModel> getCustomerComboBox() {
		return customerComboBox;
	}

	RentalTableModel getRentalTableModel() {
		return rentalTableModel;
	}

}
