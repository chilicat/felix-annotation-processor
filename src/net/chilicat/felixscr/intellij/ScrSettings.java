package net.chilicat.felixscr.intellij;


import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;


@State(name = "ScrSettings",
        storages = {
                @Storage(file = "$PROJECT_FILE$"),
                @Storage(file = "$PROJECT_CONFIG_DIR$/felix-scr.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class ScrSettings implements PersistentStateComponent<ScrSettings> {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ScrSettings getState() {
        return this;
    }

    public void loadState(ScrSettings scrSettings) {
        XmlSerializerUtil.copyBean(scrSettings, this);
    }

    @NotNull
    public static ScrSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ScrSettings.class);
    }
}
