package com.marcoaga02.carrentalmanager.view.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	private JPanel contentPane;
	private CarPanel carPanel;
	private CustomerPanel customerPanel;

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 829, 406);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPane.add(tabbedPane);

		carPanel = new CarPanel();
		tabbedPane.addTab("Car", null, carPanel, null);
		
		customerPanel = new CustomerPanel();
		tabbedPane.addTab("Customer", null, customerPanel, null);
	}

	public CarPanel getCarPanel() {
		return carPanel;
	}
	
	public CustomerPanel getCustomerPanel() {
		return customerPanel;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

}
