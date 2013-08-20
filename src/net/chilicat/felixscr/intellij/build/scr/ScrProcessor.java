package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.felixscr.intellij.build.ScrCompiler;

import java.io.File;
import java.util.Collection;

public class ScrProcessor extends AbstractScrProcessor {
    private final CompileContext context;
    private final Module module;

    public ScrProcessor(CompileContext context, Module module) {
        this.context = context;
        this.module = module;
    }

    @Override
    protected File[] getModuleSourceRoots() {
        return getModuleSourceRoots(module);
    }

    protected static File[] getModuleSourceRoots(Module module) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(false);
        File[] files = new File[sourceRoots.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = VfsUtil.virtualToIoFile(sourceRoots[i]);
        }
        return files;
    }

    @Override
    protected File getClassOutDir() {
        VirtualFile moduleOutputDirectory = context.getModuleOutputDirectory(module);
        if (moduleOutputDirectory != null)
            return VfsUtil.virtualToIoFile(moduleOutputDirectory);
        return null;
    }

    @Override
    protected String getModuleName() {
        return module.getName();
    }


    @Override
    protected void collectClasspath(Collection<String> classPath) {
        collectClasspath(module, classPath);
    }

    private void collectClasspath(Module module, Collection<String> classPath) {
        for (OrderEntry library : ModuleRootManager.getInstance(module).getOrderEntries()) {
            if (library instanceof LibraryOrderEntry) {
                LibraryOrderEntry libEntry = (LibraryOrderEntry) library;
                if (libEntry.getScope().isForProductionCompile() || libEntry.getScope().isForProductionRuntime()) {
                    final Library lib = libEntry.getLibrary();

                    if (lib != null) {
                        final VirtualFile[] files = lib.getFiles(OrderRootType.CLASSES);
                        for (VirtualFile f : files) {
                            classPath.add(VfsUtil.virtualToIoFile(f).getAbsolutePath());
                        }
                    }
                }
            }
        }

        for (Module m : ModuleRootManager.getInstance(module).getDependencies()) {
            String outputPath = ScrCompiler.getOutputPath(context, m);
            if (!classPath.contains(outputPath)) {
                classPath.add(outputPath);
                collectClasspath(m, classPath);
            }
        }
    }

}
