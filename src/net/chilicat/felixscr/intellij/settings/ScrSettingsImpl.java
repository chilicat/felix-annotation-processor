package net.chilicat.felixscr.intellij.settings;


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
public class ScrSettingsImpl implements PersistentStateComponent<ScrSettingsImpl>, ScrSettings {

    private boolean enabled = true;
    private boolean strictMode = true;
    private boolean generateAccessors = true;
    private boolean optimizedBuild = true;
    private boolean incremental = false;
    private boolean debugLogging;

    private String spec = SPEC_1_1;


    public void setDebugLogging(boolean debug) {
        this.debugLogging = debug;
    }

    public boolean isDebugLogging() {
        return this.debugLogging;
    }

    public boolean isOptimizedBuild() {
        return optimizedBuild;
    }

    public void setOptimizedBuild(boolean optimizedBuild) {
        this.optimizedBuild = optimizedBuild;
    }

    public String getSpec() {
        return spec;
    }

    public boolean isGenerateAccessors() {
        return generateAccessors;
    }

    public void setGenerateAccessors(boolean generateAccessors) {
        this.generateAccessors = generateAccessors;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ScrSettingsImpl getState() {
        return this;
    }

    public void loadState(ScrSettingsImpl scrSettings) {
        XmlSerializerUtil.copyBean(scrSettings, this);
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    @NotNull
    public static ScrSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ScrSettingsImpl.class);
    }

    public boolean isSpec(String spec) {
        return spec.equals(getSpec());
    }
}


