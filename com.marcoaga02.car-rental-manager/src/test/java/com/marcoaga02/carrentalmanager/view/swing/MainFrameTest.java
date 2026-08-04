package com.marcoaga02.carrentalmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

@RunWith(GUITestRunner.class)
public class MainFrameTest extends AssertJSwingJUnitTestCase {

	private static final int CAR_TAB_INDEX = 0;
	private static final int CUSTOMER_TAB_INDEX = 1;
	private static final int RENTALS_TAB_INDEX = 2;

	private static final String CARS_TAB = "Cars";
	private static final String CUSTOMERS_TAB = "Customers";
	private static final String RENTALS_TAB = "Rentals";

	private AutoCloseable closeable;
	private FrameFixture window;
	private MainFrame mainFrame;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			mainFrame = new MainFrame();
			return mainFrame;
		}));

		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testCarsTabIsPresentAndContainsCarPanelAtIndexZero() {
		window.tabbedPane().selectTab(CARS_TAB);
		assertThat(mainFrame.getCarPanel()).isNotNull();
		assertThat(mainFrame.getTabbedPane().getComponentAt(CAR_TAB_INDEX)).isSameAs(mainFrame.getCarPanel());
	}

	@Test
	@GUITest
	public void testCustomersTabIsPresentAndContainsCustomerPanelAtIndexOne() {
		window.tabbedPane().selectTab(CUSTOMERS_TAB);
		assertThat(mainFrame.getCarPanel()).isNotNull();
		assertThat(mainFrame.getTabbedPane().getComponentAt(CUSTOMER_TAB_INDEX)).isSameAs(mainFrame.getCustomerPanel());
	}

	@Test
	@GUITest
	public void testRentalsTabIsPresentAndContainsRentalPanelAtIndexTwo() {
		window.tabbedPane().selectTab(RENTALS_TAB);
		assertThat(mainFrame.getCarPanel()).isNotNull();
		assertThat(mainFrame.getTabbedPane().getComponentAt(RENTALS_TAB_INDEX)).isSameAs(mainFrame.getRentalPanel());
	}

	@Test
	public void testActivablePanelsByIndexIsComposedCorrectly() {
		assertThat(mainFrame.getActivablePanelsByIndex()).containsExactly(entry(0, mainFrame.getCarPanel()),
				entry(1, mainFrame.getCustomerPanel()), entry(2, mainFrame.getRentalPanel()));
	}

}
