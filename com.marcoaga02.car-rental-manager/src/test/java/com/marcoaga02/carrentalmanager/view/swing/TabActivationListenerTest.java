package com.marcoaga02.carrentalmanager.view.swing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TabActivationListenerTest {

	private static final int INDEX_A = 0;
	private static final int INDEX_B = 1;

	private JTabbedPane tabbedPane;
	private ActivablePanel panelA;
	private ActivablePanel panelB;

	@BeforeEach
	void setUp() {
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("A", new JPanel());
		tabbedPane.addTab("B", new JPanel());

		panelA = mock(ActivablePanel.class);
		panelB = mock(ActivablePanel.class);
	}

	private Map<Integer, ActivablePanel> mapWithPanels() {
		Map<Integer, ActivablePanel> map = new HashMap<>();
		map.put(INDEX_A, panelA);
		map.put(INDEX_B, panelB);
		return map;
	}

	@Test
	void testSelectingTabCallsOnActivateOnTheCorrespondingPanel() {
		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, mapWithPanels()));

		tabbedPane.setSelectedIndex(INDEX_B);

		verify(panelB, times(1)).onActivate();
		verify(panelA, never()).onActivate();
	}

	@Test
	void testSwitchingBackAndForthCallsOnActivateEachTime() {
		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, mapWithPanels()));

		tabbedPane.setSelectedIndex(INDEX_B);
		tabbedPane.setSelectedIndex(INDEX_A);
		tabbedPane.setSelectedIndex(INDEX_B);

		verify(panelA, times(1)).onActivate();
		verify(panelB, times(2)).onActivate();
	}

	@Test
	void testSelectingSameIndexAgainDoesNotFireAnotherChangeEvent() {
		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, mapWithPanels()));

		tabbedPane.setSelectedIndex(INDEX_B);
		tabbedPane.setSelectedIndex(INDEX_B);

		verify(panelB, times(1)).onActivate();
	}

	@Test
	void testDoesNothingWhenSelectedIndexHasNoRegisteredPanel() {
		Map<Integer, ActivablePanel> mapWithOnlyA = new HashMap<>();
		mapWithOnlyA.put(INDEX_A, panelA);

		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, mapWithOnlyA));

		tabbedPane.setSelectedIndex(INDEX_B);

		verifyNoInteractions(panelA);
	}

	@Test
	void testDoesNotFireForTabSelectedBeforeListenerWasAdded() {
		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, mapWithPanels()));

		verifyNoInteractions(panelA);
		verifyNoInteractions(panelB);
	}

	@Test
	void testEmptyMapNeverCallsOnActivate() {
		tabbedPane.addChangeListener(new TabActivationListener(tabbedPane, new HashMap<>()));

		tabbedPane.setSelectedIndex(INDEX_B);
		tabbedPane.setSelectedIndex(INDEX_A);

		verifyNoInteractions(panelA);
		verifyNoInteractions(panelB);
	}
}
