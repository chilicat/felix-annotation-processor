package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class ScrLoggerImpl extends AbstractScrLogger {

    private final CompileContext context;

    private Module module;

    public ScrLoggerImpl(CompileContext context, Module module, boolean debug) {
        super(debug);
        this.context = context;
        this.module = module;
    }

    @Override
    protected String getModuleName() {
        return module.getName();
    }

    @Override
    protected void log(Level l, String message, Throwable t, String location, int row, int column) {

        if (location != null) {
            VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(location));
            if (fileByIoFile != null) {
                location = fileByIoFile.getUrl();
            }
        }

        CompilerMessageCategory level = CompilerMessageCategory.ERROR;

        switch (l) {
            case ERROR:
                level = CompilerMessageCategory.ERROR;
                break;
            case WARN:
                level = CompilerMessageCategory.WARNING;
                break;
            case INFO:
                level = CompilerMessageCategory.INFORMATION;
                break;
        }

        context.addMessage(level, message, location, row, column);
    }

    @Override
    protected File getModuleOut() {
        VirtualFile moduleOutputDirectory = context.getModuleOutputDirectory(module);
        if (moduleOutputDirectory != null) {
            return VfsUtil.virtualToIoFile(moduleOutputDirectory);
        }
        return null;
    }

    @Override
    protected File[] getModuleSourceRoots() {
        return ScrProcessor.getModuleSourceRoots(module);
    }
}
