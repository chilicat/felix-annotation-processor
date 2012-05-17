package net.chilicat.felixscr.intellij.ui;

import net.chilicat.felixscr.intellij.settings.ManifestPolicy;
import net.chilicat.felixscr.intellij.settings.ScrSettings;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author dkuffner
 */
public class SettingsPage {
    private JCheckBox enabledBox;
    private JCheckBox strictModeBox;
    private JComboBox specBox;
    private JPanel page;
    private JComboBox manifestPolicyBox;
    private boolean modified = false;

    public SettingsPage() {
        createUIComponents();
    }

    public JComponent getComponent() {
        return page;
    }

    private void createUIComponents() {

        ModifyUpdateListener l = new ModifyUpdateListener();
        enabledBox.addItemListener(l);
        strictModeBox.addItemListener(l);
        specBox.addItemListener(l);
        manifestPolicyBox.addItemListener(l);

        enabledBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                boolean enabled = enabledBox.isSelected();
                strictModeBox.setEnabled(enabled);
                specBox.setEnabled(enabled);
                manifestPolicyBox.setEnabled(enabled);
            }
        });

    }

    public boolean isModified() {
        return modified;
    }

    public void apply(ScrSettings settingsState) {
        settingsState.setEnabled(enabledBox.isSelected());
        settingsState.setStrictMode(strictModeBox.isSelected());
        settingsState.setSpec(specBox.getSelectedItem().toString());

        ManifestPolicy manifestPolicy = ManifestPolicy.valueOf(manifestPolicyBox.getSelectedItem().toString());
        settingsState.setManifestPolicy(manifestPolicy);
        modified = false;
    }

    public void reset(ScrSettings settingsState) {
        enabledBox.setSelected(settingsState.isEnabled());
        strictModeBox.setSelected(settingsState.isStrictMode());
        specBox.setSelectedItem(settingsState.getSpec());
        manifestPolicyBox.setSelectedItem(settingsState.getManifestPolicy().name());
        modified = false;
    }

    private class ModifyUpdateListener implements ItemListener {
        public void itemStateChanged(ItemEvent itemEvent) {
            modified = true;
        }
    }
}
