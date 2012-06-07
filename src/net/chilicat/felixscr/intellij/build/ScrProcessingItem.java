package net.chilicat.felixscr.intellij.build;

import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.felixscr.intellij.build.scr.ScrProcessor;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import org.jetbrains.annotations.NotNull;

/**
 * @author dkuffner
 */
class ScrProcessingItem implements FileProcessingCompiler.ProcessingItem {
    private final Module module;
    private final ScrSettings settings;
    private final long latestModified;

    public ScrProcessingItem(@NotNull Module module, @NotNull ScrSettings settings, long latestModified) {
        this.module = module;
        this.settings = settings;
        this.latestModified = latestModified;
    }

    @NotNull
    public VirtualFile getFile() {
        return module.getModuleFile();
    }

    public ValidityState getValidityState() {
        return new TimestampValidityState(latestModified);
    }

    public boolean execute(CompileContext context) {
        context.getProgressIndicator().setText("Felix SCR for " + module.getName());

        final String outputDir = ScrCompiler.getOutputPath(context, module);

        if (outputDir == null) {
            context.addMessage(CompilerMessageCategory.ERROR, "Compiler Output path must be set for: " + module.getName(), null, -1, -1);
            return false;
        }

        try {
            ScrProcessor scrProcessor = new ScrProcessor(context, module, outputDir);
            scrProcessor.setSettings(settings);
            return scrProcessor.execute();

        } catch (RuntimeException e) {
            context.addMessage(CompilerMessageCategory.ERROR, e.getLocalizedMessage(), null, 0, 0);
        }
        return false;
    }
}
