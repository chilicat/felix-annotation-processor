package net.chilicat.felixscr.intellij.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.service.JpsServiceManager;

public class JPSSCRExtensionService {
    public static JPSSCRExtensionService getInstance() {
        return JpsServiceManager.getInstance().getService(JPSSCRExtensionService.class);
    }

    public Settings getSettings(@NotNull JpsProject project) {
        final Settings config = project.getContainer().getChild(Settings.ROLE);
        return config != null ? config : new Settings();

    }

    public Settings setSettings(@NotNull JpsProject project, Settings settings) {
        return project.getContainer().setChild(Settings.ROLE, settings);
    }
}
