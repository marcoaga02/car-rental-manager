package com.marcoaga02.carrentalmanager.view.swing;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int CAR_TAB_INDEX = 0;
	private static final int CUSTOMER_TAB_INDEX = 1;
	private static final int RENTAL_TAB_INDEX = 2;

	private JTabbedPane tabbedPane;
	private JPanel contentPane;
	private CarPanel carPanel;
	private CustomerPanel customerPanel;
	private RentalPanel rentalPanel;

	private transient Map<Integer, ActivablePanel> activablePanelsByIndex;

	public MainFrame() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 1500, 800);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPane.add(tabbedPane);

		carPanel = new CarPanel();
		tabbedPane.addTab("Cars", null, carPanel, null);

		customerPanel = new CustomerPanel();
		tabbedPane.addTab("Customers", null, customerPanel, null);

		rentalPanel = new RentalPanel();
		tabbedPane.addTab("Rentals", null, rentalPanel, null);

		activablePanelsByIndex = Map.of(CAR_TAB_INDEX, carPanel, CUSTOMER_TAB_INDEX, customerPanel, RENTAL_TAB_INDEX,
				rentalPanel);

		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, activablePanelsByIndex));
	}

	public CarPanel getCarPanel() {
		return carPanel;
	}

	public CustomerPanel getCustomerPanel() {
		return customerPanel;
	}

	public RentalPanel getRentalPanel() {
		return rentalPanel;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	// package-private for tests only
	Map<Integer, ActivablePanel> getActivablePanelsByIndex() {
		return activablePanelsByIndex;
	}

}
