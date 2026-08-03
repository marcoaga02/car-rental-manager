package com.marcoaga02.carrentalmanager.main;

import java.awt.Component;
import java.awt.EventQueue;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.marcoaga02.carrentalmanager.controller.CarController;
import com.marcoaga02.carrentalmanager.controller.CustomerController;
import com.marcoaga02.carrentalmanager.mapper.CarMapper;
import com.marcoaga02.carrentalmanager.mapper.CustomerMapper;
import com.marcoaga02.carrentalmanager.service.CarService;
import com.marcoaga02.carrentalmanager.service.CarServiceImpl;
import com.marcoaga02.carrentalmanager.service.CustomerService;
import com.marcoaga02.carrentalmanager.service.CustomerServiceImpl;
import com.marcoaga02.carrentalmanager.transaction.jpa.TransactionManagerJpa;
import com.marcoaga02.carrentalmanager.view.swing.ActivablePanel;
import com.marcoaga02.carrentalmanager.view.swing.MainFrame;
import com.marcoaga02.carrentalmanager.view.swing.TabActivationListener;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class CarRentalManagerSwingApp implements Callable<Void> {

	private static final int CAR_TAB_INDEX = 0;
	private static final int CUSTOMER_TAB_INDEX = 1;

	@Option(names = { "--db-host" }, description = "Database host addess")
	private String dbHost = "localhost";

	@Option(names = { "--db-port" }, description = "Database port")
	private int dbPort = 5432;

	@Option(names = { "--db-name" }, description = "Database name")
	private String dbName = "carrentalmanager";

	@Option(names = { "--db-user" }, description = "Database user")
	private String dbUser = "root";

	@Option(names = { "--db-password" }, description = "Database password")
	private String dbPassword = "password";

	private EntityManagerFactory entityManagerFactory;
	private Clock clock;
	private TransactionManagerJpa transactionManager;

	public static void main(String[] args) {
		new CommandLine(new CarRentalManagerSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				Map<String, String> properties = new HashMap<>();
				properties.put("javax.persistence.jdbc.url",
						String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbName));
				properties.put("javax.persistence.jdbc.user", dbUser);
				properties.put("javax.persistence.jdbc.password", dbPassword);
				properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
				properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
				properties.put("hibernate.hbm2ddl.auto", "update");
				properties.put("hibernate.show_sql", "false");

				entityManagerFactory = Persistence.createEntityManagerFactory("carrental-pu", properties);
				clock = Clock.systemDefaultZone();
				transactionManager = new TransactionManagerJpa(entityManagerFactory, clock);

				CarService carService = new CarServiceImpl(transactionManager, new CarMapper());
				CustomerService customerService = new CustomerServiceImpl(transactionManager, new CustomerMapper());

				MainFrame mainFrame = new MainFrame();

				CarController carController = new CarController(carService, mainFrame.getCarPanel());
				CustomerController customerController = new CustomerController(customerService,
						mainFrame.getCustomerPanel());

				mainFrame.getCarPanel().setCarController(carController);
				mainFrame.getCustomerPanel().setCustomerController(customerController);

				Map<Integer, ActivablePanel> activablePanelsByIndex = Map.of(
						CAR_TAB_INDEX, mainFrame.getCarPanel(),
						CUSTOMER_TAB_INDEX, mainFrame.getCustomerPanel()
				);
				
				mainFrame.getTabbedPane().addChangeListener(
						new TabActivationListener(mainFrame.getTabbedPane(), activablePanelsByIndex));

				Component initiallySelected = mainFrame.getTabbedPane().getSelectedComponent();
				if (initiallySelected instanceof ActivablePanel) {
					((ActivablePanel) initiallySelected).onActivate();
				}

				mainFrame.setVisible(true);

			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception in CarRentalManager Application",
						e);
			}
		});
		return null;
	}

}
