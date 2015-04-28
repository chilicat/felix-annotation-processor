package net.chilicat.felixscr.intellij.jps;

import net.chilicat.felixscr.intellij.build.scr.AbstractScrProcessor;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.model.java.JpsJavaDependenciesEnumerator;
import org.jetbrains.jps.model.java.JpsJavaDependenciesRootsEnumerator;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ScrProcessor extends AbstractScrProcessor {

    private ModuleChunk moduleChunk;

    @Override
    protected File[] getModuleSourceRoots() {
        return getModuleSourceRoots(getModuleChunk());
    }

    protected static File[] getModuleSourceRoots(ModuleChunk module) {
        List<JpsModuleSourceRoot> sourceRoots = module.representativeTarget().getModule().getSourceRoots();
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
        JpsJavaExtensionService service = JpsJavaExtensionService.getInstance();
        JpsJavaDependenciesEnumerator enr = service.enumerateDependencies(Collections.singleton(moduleChunk.representativeTarget().getModule()));
        JpsJavaDependenciesRootsEnumerator classes = enr.productionOnly().withoutSdk().recursively().classes();
        for (File f : classes.getRoots()) {
            // filter out non-Java classpath entries, because Felix fails processing them
            if (f.getName().endsWith(".class") || f.getName().endsWith(".jar") || f.isDirectory()) {
                classPath.add(f.getAbsolutePath());
            }
        }
    }

    public void setModuleChunk(ModuleChunk moduleChunk) {
        this.moduleChunk = moduleChunk;
    }

    public ModuleChunk getModuleChunk() {
        return moduleChunk;
    }
}