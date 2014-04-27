package net.chilicat.felixscr.intellij.settings;

public interface ScrSettings {
    String SPEC_1_0 = "1.0";
    String SPEC_1_1 = "1.1";
    String SPEC_1_2 = "1.2";

    boolean isOptimizedBuild();

    void setOptimizedBuild(boolean optimizedBuild);

    String getSpec();

    boolean isGenerateAccessors();

    void setGenerateAccessors(boolean generateAccessors);

    void setSpec(String spec);

    boolean isStrictMode();

    void setStrictMode(boolean strictMode);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isSpec(String spec);

    void setDebugLogging(boolean debug);

    boolean isDebugLogging();

    boolean isIncremental();

    void setIncremental(boolean incremental);

}
