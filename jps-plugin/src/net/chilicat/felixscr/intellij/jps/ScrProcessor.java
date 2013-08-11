package net.chilicat.felixscr.intellij.jps;

import net.chilicat.felixscr.intellij.build.scr.AbstractScrProcessor;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class ScrProcessor extends AbstractScrProcessor {

    private ModuleChunk moduleChunk;
    private CompileContext compileContext;


    @Override
    protected File[] getModuleSourceRoots() {
        List<JpsModuleSourceRoot> sourceRoots = getModuleChunk().representativeTarget().getModule().getSourceRoots();
        File[] files = new File[sourceRoots.size()];
        for (int i = 0; i < sourceRoots.size(); i++) {
            files[i] = sourceRoots.get(i).getFile();
        }
        return files;
    }

    @Override
    protected File getClassOutDir() {
        return moduleChunk.representativeTarget().getOutputDir();
    }

    @Override
    protected String getModuleName() {
        return moduleChunk.getName();
    }

    @Override
    protected void collectClasspath(Collection<String> classPath) {
        // Collect all transitive dependencies.
    }

    public void setModuleChunk(ModuleChunk moduleChunk) {
        this.moduleChunk = moduleChunk;
    }

    public ModuleChunk getModuleChunk() {
        return moduleChunk;
    }

    public void setCompileContext(CompileContext compileContext) {
        this.compileContext = compileContext;
    }

    public CompileContext getCompileContext() {
        return compileContext;
    }
}
