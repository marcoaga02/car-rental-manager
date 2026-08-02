package com.marcoaga02.carrentalmanager.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.marcoaga02.carrentalmanager.controller.CarController;
import com.marcoaga02.carrentalmanager.view.CarView;
import com.marcoaga02.carrentalmanager.view.swing.model.CarTableModel;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarPanel extends JPanel implements CarView, ActivablePanel {

	private static final String DIALOG_FONT = "Dialog";

	private static final long serialVersionUID = 1L;

	private transient CarController carController;

	private JTable carTable;
	private CarTableModel carTableModel;
	private JLabel errorLabel;
	private JPanel fieldPanel;
	private JLabel carPlateLabel;
	private JLabel brandLabel;
	private JTextField brandTextField;
	private JTextField carPlateTextField;
	private JTextField modelTextField;
	private JLabel modelLabel;
	private JLabel dailyRateLabel;
	private JSpinner dailyRateSpinner;
	private JLabel createCarTitleLabel;
	private JButton addCarButton;
	private JPanel fillerPanel;
	private JPanel leftPanel;
	private JPanel footerPanel;
	private JButton deleteCarButton;
	private JPanel rightPanel;

	/**
	 * Create the panel.
	 */
	public CarPanel() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(0, 0));

		carTableModel = new CarTableModel();

		leftPanel = new JPanel();
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		add(leftPanel, BorderLayout.CENTER);

		carTable = new JTable();
		carTable.setBorder(new LineBorder(new Color(0, 0, 0)));
		carTable.setName("carTable");
		carTable.setModel(carTableModel);
		carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		carTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
		carTable.getTableHeader().setForeground(Color.BLACK);
		leftPanel.setLayout(new BorderLayout(0, 0));

		carTable.getSelectionModel().addListSelectionListener(e -> {
				updateDeleteCarButtonState();
		});

		JScrollPane scrollPane = new JScrollPane();
		leftPanel.add(scrollPane);
		scrollPane.setViewportView(carTable);

		footerPanel = new JPanel();
		footerPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
		leftPanel.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new BorderLayout(0, 0));

		errorLabel = new JLabel(" ");
		errorLabel.setForeground(Color.RED);
		errorLabel.setName("errorLabel");
		footerPanel.add(errorLabel, BorderLayout.WEST);

		deleteCarButton = new JButton("Delete selected");
		deleteCarButton.setEnabled(false);
		footerPanel.add(deleteCarButton, BorderLayout.EAST);
		deleteCarButton.addActionListener(
				e -> carController.deleteCar(carTableModel.getCarAt(carTable.getSelectedRow()).getId()));

		rightPanel = new JPanel();
		rightPanel.setBorder(new LineBorder(Color.GRAY));
		add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new BorderLayout(0, 0));

		fieldPanel = new JPanel();
		rightPanel.add(fieldPanel, BorderLayout.CENTER);
		fieldPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		GridBagLayout gbl_fieldPanel = new GridBagLayout();
		gbl_fieldPanel.columnWidths = new int[] { 0, 0 };
		gbl_fieldPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_fieldPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_fieldPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		fieldPanel.setLayout(gbl_fieldPanel);

		carPlateLabel = new JLabel("Car plate");
		carPlateLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		carPlateLabel.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_lblCarPlate = new GridBagConstraints();
		gbc_lblCarPlate.gridwidth = 1;
		gbc_lblCarPlate.anchor = GridBagConstraints.WEST;
		gbc_lblCarPlate.insets = new Insets(0, 0, 5, 0);
		gbc_lblCarPlate.gridx = 0;
		gbc_lblCarPlate.gridy = 0;
		fieldPanel.add(carPlateLabel, gbc_lblCarPlate);

		carPlateTextField = new JTextField();
		carPlateTextField.setName("carPlateTextField");
		GridBagConstraints gbc_textFieldCarPlate = new GridBagConstraints();
		gbc_textFieldCarPlate.gridwidth = 1;
		gbc_textFieldCarPlate.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldCarPlate.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldCarPlate.gridx = 0;
		gbc_textFieldCarPlate.gridy = 1;
		fieldPanel.add(carPlateTextField, gbc_textFieldCarPlate);
		carPlateTextField.setColumns(15);

		brandLabel = new JLabel("Brand");
		brandLabel.setForeground(Color.DARK_GRAY);
		brandLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		GridBagConstraints gbc_lblBrand = new GridBagConstraints();
		gbc_lblBrand.gridwidth = 1;
		gbc_lblBrand.anchor = GridBagConstraints.WEST;
		gbc_lblBrand.insets = new Insets(0, 0, 5, 0);
		gbc_lblBrand.gridx = 0;
		gbc_lblBrand.gridy = 2;
		fieldPanel.add(brandLabel, gbc_lblBrand);

		brandTextField = new JTextField();
		brandTextField.setName("brandTextField");
		GridBagConstraints gbc_textFieldBrand = new GridBagConstraints();
		gbc_textFieldBrand.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldBrand.gridwidth = 1;
		gbc_textFieldBrand.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldBrand.gridx = 0;
		gbc_textFieldBrand.gridy = 3;
		fieldPanel.add(brandTextField, gbc_textFieldBrand);
		brandTextField.setColumns(15);

		modelLabel = new JLabel("Model");
		modelLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		modelLabel.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.anchor = GridBagConstraints.WEST;
		gbc_lblModel.insets = new Insets(0, 0, 5, 0);
		gbc_lblModel.gridx = 0;
		gbc_lblModel.gridy = 4;
		fieldPanel.add(modelLabel, gbc_lblModel);

		modelTextField = new JTextField();
		modelTextField.setName("modelTextField");
		GridBagConstraints gbc_textFieldModel = new GridBagConstraints();
		gbc_textFieldModel.gridwidth = 1;
		gbc_textFieldModel.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldModel.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldModel.gridx = 0;
		gbc_textFieldModel.gridy = 5;
		fieldPanel.add(modelTextField, gbc_textFieldModel);
		modelTextField.setColumns(15);

		dailyRateLabel = new JLabel("Daily rate (€)");
		dailyRateLabel.setForeground(Color.DARK_GRAY);
		dailyRateLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		GridBagConstraints gbc_lblDailyRate = new GridBagConstraints();
		gbc_lblDailyRate.anchor = GridBagConstraints.WEST;
		gbc_lblDailyRate.gridwidth = 1;
		gbc_lblDailyRate.insets = new Insets(0, 0, 5, 0);
		gbc_lblDailyRate.gridx = 0;
		gbc_lblDailyRate.gridy = 6;
		fieldPanel.add(dailyRateLabel, gbc_lblDailyRate);

		dailyRateSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 0.1));
		dailyRateSpinner.setName("dailyRateSpinner");
		GridBagConstraints gbc_spinnerDailyRate = new GridBagConstraints();
		gbc_spinnerDailyRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerDailyRate.anchor = GridBagConstraints.WEST;
		gbc_spinnerDailyRate.gridwidth = 1;
		gbc_spinnerDailyRate.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerDailyRate.gridx = 0;
		gbc_spinnerDailyRate.gridy = 7;
		fieldPanel.add(dailyRateSpinner, gbc_spinnerDailyRate);

		fillerPanel = new JPanel();
		fillerPanel.setOpaque(false);
		GridBagConstraints gbc_fillerPanel = new GridBagConstraints();
		gbc_fillerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_fillerPanel.weighty = 1.0;
		gbc_fillerPanel.fill = GridBagConstraints.VERTICAL;
		gbc_fillerPanel.gridx = 0;
		gbc_fillerPanel.gridy = 8;
		fieldPanel.add(fillerPanel, gbc_fillerPanel);

		addCarButton = new JButton("Add car");
		addCarButton.setEnabled(false);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.anchor = GridBagConstraints.SOUTHEAST;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 9;
		fieldPanel.add(addCarButton, gbc_btnNewButton);

		KeyAdapter addCarButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAddCarButtonState();
			}
		};

		carPlateTextField.addKeyListener(addCarButtonEnabler);
		brandTextField.addKeyListener(addCarButtonEnabler);
		modelTextField.addKeyListener(addCarButtonEnabler);
		dailyRateSpinner.addChangeListener(e -> updateAddCarButtonState());

		addCarButton.addActionListener(e -> carController.createCar(
				new CarViewModel(null, carPlateTextField.getText(), brandTextField.getText(), modelTextField.getText(),
						BigDecimal.valueOf(((Number) dailyRateSpinner.getValue()).doubleValue()))));

		createCarTitleLabel = new JLabel("Create Car");
		createCarTitleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
		rightPanel.add(createCarTitleLabel, BorderLayout.NORTH);
		createCarTitleLabel.setFont(getFont().deriveFont(Font.BOLD));
	}

	@Override
	public void showError(String errorMessage) {
		errorLabel.setText(errorMessage);
	}

	@Override
	public void clearFields() {
		carPlateTextField.setText("");
		brandTextField.setText("");
		modelTextField.setText("");
		dailyRateSpinner.setValue(0.0);

		updateAddCarButtonState();
	}

	@Override
	public void showAllCars(List<CarViewModel> cars) {
		carTableModel.setCars(cars);
		resetErrorMessage();
	}

	@Override
	public void onActivate() {
		if (carController != null) {
			carController.getAllCars();
		}
	}

	private void resetErrorMessage() {
		errorLabel.setText(" ");
	}

	private boolean isAddCarButtonEnabled() {
		return !carPlateTextField.getText().trim().isEmpty() && !brandTextField.getText().trim().isEmpty()
				&& !modelTextField.getText().trim().isEmpty()
				&& ((Number) dailyRateSpinner.getValue()).doubleValue() > 0;
	}

	private void updateAddCarButtonState() {
		addCarButton.setEnabled(isAddCarButtonEnabled());
	}

	private void updateDeleteCarButtonState() {
		deleteCarButton.setEnabled(carTable.getSelectedRow() != -1);
	}

	public void setCarController(CarController carController) {
		this.carController = carController;
	}

	// package-private for tests only
	JLabel getLblError() {
		return errorLabel;
	}

	CarTableModel getCarTableModel() {
		return carTableModel;
	}

}
