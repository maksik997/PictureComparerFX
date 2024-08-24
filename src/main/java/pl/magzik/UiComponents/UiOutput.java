package pl.magzik.UiComponents;

import javax.swing.*;
import java.awt.*;

public class UiOutput extends JPanel {

    private final JList<String> mappedObjectList, duplicateList;

    public UiOutput() {
        this.setLayout(new GridLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel mappedObjectPanel = new JPanel(new BorderLayout());

        this.mappedObjectList = new JList<>();
        this.mappedObjectList.setFocusable(false);

        mappedObjectPanel.add(new JScrollPane(mappedObjectList));

        tabbedPane.addTab("view.comparer.tab.mapped_objects.title", mappedObjectPanel);

        JPanel duplicatePanel = new JPanel(new BorderLayout());

        this.duplicateList = new JList<>();
        this.duplicateList.setFocusable(false);

        duplicatePanel.add(new JScrollPane(duplicateList));

        tabbedPane.addTab("view.comparer.tab.duplicates.title", duplicatePanel);

        this.add(tabbedPane);
    }

    public JList<String> getMappedObjectList() {
        return mappedObjectList;
    }

    public JList<String> getDuplicateList() {
        return duplicateList;
    }

}
