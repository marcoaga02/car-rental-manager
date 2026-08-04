package com.marcoaga02.carrentalmanager.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.marcoaga02.carrentalmanager.controller.CustomerController;
import com.marcoaga02.carrentalmanager.view.CustomerView;
import com.marcoaga02.carrentalmanager.view.swing.model.CustomerTableModel;
import com.marcoaga02.carrentalmanager.viewmodel.CustomerViewModel;
import javax.swing.SwingConstants;

public class CustomerPanel extends JPanel implements CustomerView, ActivablePanel {

	private static final long serialVersionUID = 1L;
	
	private static final String DIALOG_FONT = "Dialog";

	private transient CustomerController customerController;

	private JTable customerTable;
	private CustomerTableModel customerTableModel;
	private JPanel footerPanel;
	private JLabel errorLabel;
	private JButton deleteCustomerButton;
	private JPanel rightPanel;
	private JScrollPane scrollPane;
	private JPanel leftPanel;
	private JLabel createCustomerTitleLabel;
	private JPanel fieldPanel;
	private JLabel taxIdCodeLabel;
	private JTextField taxIdCodeTextField;
	private JLabel firstnameLabel;
	private JTextField firstnameTextField;
	private JLabel lastnameLabel;
	private JTextField lastnameTextField;
	private JPanel fillerPanel;
	private JButton addCustomerButton;

	/**
	 * Create the panel.
	 */
	public CustomerPanel() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(0, 0));

		customerTableModel = new CustomerTableModel();

		leftPanel = new JPanel();
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		add(leftPanel, BorderLayout.CENTER);
		leftPanel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		leftPanel.add(scrollPane, BorderLayout.CENTER);

		customerTable = new JTable();
		customerTable.setName("customerTable");
		customerTable.setModel(customerTableModel);
		customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
		customerTable.getTableHeader().setForeground(Color.BLACK);
		customerTable.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setViewportView(customerTable);

		customerTable.getSelectionModel().addListSelectionListener(e -> updateDeleteCustomerButtonState());

		footerPanel = new JPanel();
		footerPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
		leftPanel.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new BorderLayout(0, 0));

		errorLabel = new JLabel(" ");
		errorLabel.setName("errorLabel");
		errorLabel.setForeground(Color.RED);
		footerPanel.add(errorLabel, BorderLayout.WEST);

		deleteCustomerButton = new JButton("Delete selected");
		deleteCustomerButton.setEnabled(false);
		footerPanel.add(deleteCustomerButton, BorderLayout.EAST);
		
		deleteCustomerButton.addActionListener(
				e -> customerController.deleteCustomer(customerTableModel.getCustomerAt(customerTable.getSelectedRow()).getId()));

		rightPanel = new JPanel();
		rightPanel.setBorder(new LineBorder(new Color(128, 128, 128)));
		add(rightPanel, BorderLayout.EAST);
		rightPanel.setLayout(new BorderLayout(0, 0));

		fieldPanel = new JPanel();
		fieldPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		rightPanel.add(fieldPanel, BorderLayout.CENTER);

		GridBagLayout gbl_fieldPanel = new GridBagLayout();
		gbl_fieldPanel.columnWidths = new int[] { 160, 0 };
		gbl_fieldPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_fieldPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_fieldPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
		fieldPanel.setLayout(gbl_fieldPanel);

		taxIdCodeLabel = new JLabel("Tax id code");
		taxIdCodeLabel.setForeground(Color.DARK_GRAY);
		taxIdCodeLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		GridBagConstraints gbc_taxIdCodeLabel = new GridBagConstraints();
		gbc_taxIdCodeLabel.gridwidth = 1;
		gbc_taxIdCodeLabel.anchor = GridBagConstraints.WEST;
		gbc_taxIdCodeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_taxIdCodeLabel.gridx = 0;
		gbc_taxIdCodeLabel.gridy = 0;
		fieldPanel.add(taxIdCodeLabel, gbc_taxIdCodeLabel);

		taxIdCodeTextField = new JTextField();
		taxIdCodeTextField.setName("taxIdCodeTextField");
		GridBagConstraints gbc_taxIdCodeTextField = new GridBagConstraints();
		gbc_taxIdCodeTextField.gridwidth = 1;
		gbc_taxIdCodeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_taxIdCodeTextField.insets = new Insets(0, 0, 5, 0);
		gbc_taxIdCodeTextField.gridx = 0;
		gbc_taxIdCodeTextField.gridy = 1;
		fieldPanel.add(taxIdCodeTextField, gbc_taxIdCodeTextField);

		firstnameLabel = new JLabel("Firstname");
		firstnameLabel.setForeground(Color.DARK_GRAY);
		firstnameLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		GridBagConstraints gbc_firstnameLabel = new GridBagConstraints();
		gbc_firstnameLabel.gridwidth = 1;
		gbc_firstnameLabel.anchor = GridBagConstraints.WEST;
		gbc_firstnameLabel.insets = new Insets(0, 0, 5, 0);
		gbc_firstnameLabel.gridx = 0;
		gbc_firstnameLabel.gridy = 2;
		fieldPanel.add(firstnameLabel, gbc_firstnameLabel);

		firstnameTextField = new JTextField();
		firstnameTextField.setName("firstnameTextField");
		GridBagConstraints gbc_firstnameTextField = new GridBagConstraints();
		gbc_firstnameTextField.gridwidth = 1;
		gbc_firstnameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstnameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_firstnameTextField.gridx = 0;
		gbc_firstnameTextField.gridy = 3;
		fieldPanel.add(firstnameTextField, gbc_firstnameTextField);

		lastnameLabel = new JLabel("Lastname");
		lastnameLabel.setForeground(Color.DARK_GRAY);
		lastnameLabel.setFont(new Font(DIALOG_FONT, Font.PLAIN, 10));
		GridBagConstraints gbc_lastnameLabel = new GridBagConstraints();
		gbc_lastnameLabel.anchor = GridBagConstraints.WEST;
		gbc_lastnameLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lastnameLabel.gridx = 0;
		gbc_lastnameLabel.gridy = 4;
		fieldPanel.add(lastnameLabel, gbc_lastnameLabel);

		lastnameTextField = new JTextField();
		lastnameTextField.setName("lastnameTextField");
		GridBagConstraints gbc_lastnameTextField = new GridBagConstraints();
		gbc_lastnameTextField.gridwidth = 1;
		gbc_lastnameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_lastnameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_lastnameTextField.gridx = 0;
		gbc_lastnameTextField.gridy = 5;
		fieldPanel.add(lastnameTextField, gbc_lastnameTextField);

		fillerPanel = new JPanel();
		fillerPanel.setOpaque(false);
		GridBagConstraints gbc_fillerPanel = new GridBagConstraints();
		gbc_fillerPanel.weighty = 1.0;
		gbc_fillerPanel.fill = GridBagConstraints.VERTICAL;
		gbc_fillerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_fillerPanel.gridx = 0;
		gbc_fillerPanel.gridy = 6;
		fieldPanel.add(fillerPanel, gbc_fillerPanel);

		addCustomerButton = new JButton("Add customer");
		addCustomerButton.setEnabled(false);
		GridBagConstraints gbc_addCustomerButton = new GridBagConstraints();
		gbc_addCustomerButton.insets = new Insets(0, 0, 5, 0);
		gbc_addCustomerButton.anchor = GridBagConstraints.SOUTHEAST;
		gbc_addCustomerButton.gridx = 0;
		gbc_addCustomerButton.gridy = 7;
		fieldPanel.add(addCustomerButton, gbc_addCustomerButton);

		KeyAdapter addCustomerButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAddCustomerButtonState();
			}
		};

		taxIdCodeTextField.addKeyListener(addCustomerButtonEnabler);
		firstnameTextField.addKeyListener(addCustomerButtonEnabler);
		lastnameTextField.addKeyListener(addCustomerButtonEnabler);

		addCustomerButton.addActionListener(e -> customerController.createCustomer(new CustomerViewModel(null,
				taxIdCodeTextField.getText(), firstnameTextField.getText(), lastnameTextField.getText())));
		
		createCustomerTitleLabel = new JLabel("Create Customer");
		createCustomerTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		createCustomerTitleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
		rightPanel.add(createCustomerTitleLabel, BorderLayout.NORTH);
	}

	@Override
	public void showError(String errorMessage) {
		errorLabel.setText(errorMessage);
	}

	@Override
	public void clearFields() {
		taxIdCodeTextField.setText("");
		firstnameTextField.setText("");
		lastnameTextField.setText("");

		updateAddCustomerButtonState();
	}

	@Override
	public void showAllCustomers(List<CustomerViewModel> customers) {
		customerTableModel.setCustomers(customers);
		resetErrorMessage();
	}

	@Override
	public void onActivate() {
		if (customerController != null) {
			customerController.getAllCustomers();
		}
	}

	private void resetErrorMessage() {
		errorLabel.setText(" ");
	}

	private boolean isAddCustomerButtonEnabled() {
		return !taxIdCodeTextField.getText().trim().isEmpty() && !firstnameTextField.getText().trim().isEmpty()
				&& !lastnameTextField.getText().trim().isEmpty();
	}

	private void updateAddCustomerButtonState() {
		addCustomerButton.setEnabled(isAddCustomerButtonEnabled());
	}

	private void updateDeleteCustomerButtonState() {
		deleteCustomerButton.setEnabled(customerTable.getSelectedRow() != -1);
	}

	public void setCustomerController(CustomerController customerController) {
		this.customerController = customerController;
	}

	// package-private for tests only
	JLabel getErrorLabel() {
		return errorLabel;
	}

	CustomerTableModel getCustomerTableModel() {
		return customerTableModel;
	}

}
