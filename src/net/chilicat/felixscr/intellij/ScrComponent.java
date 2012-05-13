package net.chilicat.felixscr.intellij;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 */
public class ScrComponent implements ProjectComponent {

    private final Project project;

    public ScrComponent(Project p) {
        this.project = p;
    }

    public void projectOpened() {

    }

    public void projectClosed() {

    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "Felix SCR Annotation Processor";
    }
}
