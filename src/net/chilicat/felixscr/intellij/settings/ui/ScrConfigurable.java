package net.chilicat.felixscr.intellij.settings.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import net.chilicat.felixscr.intellij.settings.ScrSettingsImpl;
import net.chilicat.felixscr.intellij.settings.SettingsPage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author dkuffner
 */
public class ScrConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    private static final String TOPIC = ScrConfigurable.class.getClass().getName();

    private final Project project;

    private final SettingsPage page = new SettingsPage();

    public ScrConfigurable(@NotNull Project project) {
        this.project = project;
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
        return page.getComponent();
    }

    public boolean isModified() {
        return page.isModified();
    }

    public void apply() throws ConfigurationException {
        page.apply(ScrSettingsImpl.getInstance(project));
    }

    public void reset() {
        page.reset(ScrSettingsImpl.getInstance(project));
    }

    public void disposeUIResources() {
        // Noting to do.
    }
}
