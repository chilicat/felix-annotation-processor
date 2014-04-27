package net.chilicat.felixscr.intellij.jps;

import net.chilicat.felixscr.intellij.settings.ManifestPolicy;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.ex.JpsElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

public class Settings extends JpsElementBase<Settings> implements ScrSettings {

    public static final JpsElementChildRole<Settings> ROLE =
            JpsElementChildRoleBase.create("src felix settings");

    private State state = new State();

    public Settings(State state) {
        this.state = state;
    }

    public Settings() {
    }

    public void setDebugLogging(boolean debug) {
        state.debugLogging = debug;
    }

    public boolean isDebugLogging() {
        return state.debugLogging;
    }

    public boolean isEnabled() {
        return state.enabled;
    }

    public void setEnabled(boolean enabled) {
        state.enabled = enabled;
    }

    public boolean isStrictMode() {
        return state.strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        state.strictMode = strictMode;
    }

    public boolean isGenerateAccessors() {
        return state.generateAccessors;
    }

    public void setGenerateAccessors(boolean generateAccessors) {
        state.generateAccessors = generateAccessors;
    }

    public boolean isOptimizedBuild() {
        return state.optimizedBuild;
    }

    public void setOptimizedBuild(boolean optimizedBuild) {
        state.optimizedBuild = optimizedBuild;
    }

    public boolean isScanClasses() {
        return state.scanClasses;
    }

    public void setScanClasses(boolean scanClasses) {
        state.scanClasses = scanClasses;
    }

    public String getSpec() {
        return state.spec;
    }

    public void setSpec(String spec) {
        state.spec = spec;
    }


    public boolean isSpec(String spec) {
        return state.spec.equals(spec);
    }

    public boolean isIncremental() {
        return state.incremental;
    }

    public void setIncremental(boolean incremental) {
        state.incremental = incremental;
    }

    public void setManifestPolicy(ManifestPolicy policy) {
        state.manifestPolicy = policy;

    }

    public ManifestPolicy getManifestPolicy() {
        return state.manifestPolicy;
    }

    @NotNull
    @Override
    public Settings createCopy() {
        return new Settings(state);
    }

    @Override
    public void applyChanges(@NotNull Settings settings) {
        throw new UnsupportedOperationException("applyChanges are not supported.");
    }


    public static class State {
        public boolean enabled = true;
        public boolean strictMode = true;
        public boolean generateAccessors = true;
        public boolean optimizedBuild = true;
        public boolean scanClasses = false;
        public ManifestPolicy manifestPolicy = ManifestPolicy.overwrite;
        public boolean debugLogging = false;
        private String spec = "1.1";
        public boolean incremental = false;
    }
}
