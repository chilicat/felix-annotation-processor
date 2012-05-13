package net.chilicat.felixscr.intellij;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author dkuffner
 */
public class ScrConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    private static final String TOPIC = ScrConfigurable.class.getClass().getName();

    private boolean modified = false;
    private final Project project;

    private JCheckBox enabledBox;
    private JComponent container;

    public ScrConfigurable(@NotNull Project project) {

        this.project = project;

        enabledBox = new JCheckBox("Enable Felix Annotation Processor");
        enabledBox.setSelected(true);
        enabledBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                modified = true;
            }
        });

        Box box = Box.createVerticalBox();
        box.add(enabledBox);
        container = box;
    }

    @NotNull
    public String getId() {
        return TOPIC;
    }

    public Runnable enableSearch(String s) {
        return null;
    }

    @Nls
    public String getDisplayName() {
        return "Felix Annotation Processor";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return container;
    }

    public boolean isModified() {
        return modified;
    }

    public void apply() throws ConfigurationException {
        ScrSettings settingsState = ScrSettings.getInstance(project);
        settingsState.setEnabled(enabledBox.isSelected());
        modified = false;
    }

    public void reset() {
        ScrSettings settingsState = ScrSettings.getInstance(project);
        enabledBox.setSelected(settingsState.isEnabled());
        modified = false;
    }

    public void disposeUIResources() {

    }
}
