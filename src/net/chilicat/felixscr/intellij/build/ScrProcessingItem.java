package net.chilicat.felixscr.intellij.build;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.FileProcessingCompiler;
import com.intellij.openapi.compiler.TimestampValidityState;
import com.intellij.openapi.compiler.ValidityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.felixscr.intellij.build.scr.ScrLogger;
import net.chilicat.felixscr.intellij.build.scr.ScrLoggerImpl;
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

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public VirtualFile getFile() {
        return module.getModuleFile();
    }

    public ValidityState getValidityState() {
        return new TimestampValidityState(latestModified);
    }

    public boolean execute(CompileContext context) {
        context.getProgressIndicator().setText("Felix SCR for " + module.getName());
        ScrProcessor scrProcessor = new ScrProcessor(context, module);
        scrProcessor.setLogger(new ScrLoggerImpl(context, module, settings.isDebugLogging()));
        scrProcessor.setSettings(settings);

        ScrLogger logger = scrProcessor.getLogger();
        logger.warn("Felix SCR annotation compiler has problems with JDK 7. It is recommended to enable external build. Please change compiler settings to use external builds");

        return scrProcessor.execute();
    }
}
