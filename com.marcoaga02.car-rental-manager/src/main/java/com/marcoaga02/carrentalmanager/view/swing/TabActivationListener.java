package com.marcoaga02.carrentalmanager.view.swing;

import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabActivationListener implements ChangeListener {

    private final JTabbedPane tabbedPane;
    private final Map<Integer, ActivablePanel> activablePanelsByIndex;

    public TabActivationListener(JTabbedPane tabbedPane, Map<Integer, ActivablePanel> activablePanelsByIndex) {
        this.tabbedPane = tabbedPane;
        this.activablePanelsByIndex = activablePanelsByIndex;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ActivablePanel panel = activablePanelsByIndex.get(tabbedPane.getSelectedIndex());
        if (panel != null) {
            panel.onActivate();
        }
    }
}
