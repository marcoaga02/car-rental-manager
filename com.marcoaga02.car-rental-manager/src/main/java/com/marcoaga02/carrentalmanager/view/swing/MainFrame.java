package com.marcoaga02.carrentalmanager.view.swing;

import java.awt.BorderLayout;
import java.util.HashMap;
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

	private JPanel contentPane;
	private CarPanel carPanel;
	private JTabbedPane tabbedPane;

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

		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, buildActivablePanelsByIndex()));
	}

	private Map<Integer, ActivablePanel> buildActivablePanelsByIndex() {
		Map<Integer, ActivablePanel> map = new HashMap<>();
		map.put(CAR_TAB_INDEX, carPanel);
		return map;
	}

	public CarPanel getCarPanel() {
		return carPanel;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

}
