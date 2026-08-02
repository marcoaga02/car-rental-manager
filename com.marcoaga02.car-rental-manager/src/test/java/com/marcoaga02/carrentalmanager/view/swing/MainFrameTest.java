package com.marcoaga02.carrentalmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.marcoaga02.carrentalmanager.controller.CarController;

@RunWith(GUITestRunner.class)
public class MainFrameTest extends AssertJSwingJUnitTestCase {

	private static final int CAR_TAB_INDEX = 0;

	@Mock
	private CarController carController;

	private AutoCloseable closeable;
	private FrameFixture window;
	private MainFrame mainFrame;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			mainFrame = new MainFrame();
			mainFrame.getCarPanel().setCarController(carController);
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
	public void testCarTabIsPresentAndContainsCarPanelAtIndexZero() {
		window.tabbedPane().selectTab("Car");
		assertThat(mainFrame.getCarPanel()).isNotNull();
		assertThat(mainFrame.getTabbedPane().getComponentAt(CAR_TAB_INDEX)).isSameAs(mainFrame.getCarPanel());
	}

	@Test
	@GUITest
	public void testSelectingCarTabTriggersOnActivateOnCarController() {
		GuiActionRunner.execute(() -> {
			JTabbedPane tabbedPane = mainFrame.getTabbedPane();
			int temporaryTabIndex = tabbedPane.getTabCount();

			tabbedPane.addTab("Other", new JPanel());
			tabbedPane.setSelectedIndex(temporaryTabIndex);
			tabbedPane.setSelectedIndex(CAR_TAB_INDEX); 
		});

		verify(carController).getAllCars();
	}

	@Test
	@GUITest
	public void testNoInteractionsWithControllerBeforeTabSelection() {
		verifyNoInteractions(carController);
	}
}
