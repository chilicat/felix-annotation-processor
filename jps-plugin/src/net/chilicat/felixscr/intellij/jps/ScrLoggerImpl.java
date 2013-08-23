package net.chilicat.felixscr.intellij.jps;

import net.chilicat.felixscr.intellij.build.scr.AbstractScrLogger;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

import java.io.File;
import java.util.logging.Logger;

public class ScrLoggerImpl extends AbstractScrLogger {

    private final CompileContext context;
    private final String compilerName;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String moduleName;
    private ModuleChunk moduleChunk;

    public ScrLoggerImpl(CompileContext context, ModuleChunk moduleChunk, String presentableName, boolean debug) {
        super(debug);
        this.context = context;
        this.compilerName = presentableName;
        this.moduleName = moduleChunk.getName();
        this.moduleChunk = moduleChunk;
    }

    @Override
    protected String getModuleName() {
        return moduleName;
    }

    @Override
    protected File getModuleOut() {
        return moduleChunk.representativeTarget().getOutputDir();
    }

    @Override
    protected File[] getModuleSourceRoots() {
        return ScrProcessor.getModuleSourceRoots(moduleChunk);
    }

    @Override
    protected void log(Level l, String message, Throwable t, String location, int row, int column) {
        BuildMessage.Kind kind = BuildMessage.Kind.ERROR;
        java.util.logging.Level jl = java.util.logging.Level.SEVERE;

        switch (l) {
            case ERROR:
                kind = BuildMessage.Kind.ERROR;
                jl = java.util.logging.Level.SEVERE;
                break;
            case WARN:
                kind = BuildMessage.Kind.WARNING;
                jl = java.util.logging.Level.WARNING;
                break;
            case INFO:
                kind = BuildMessage.Kind.INFO;
                jl = java.util.logging.Level.INFO;
                break;
        }
        context.processMessage(new CompilerMessage(compilerName, kind, message, location, (long) row, (long) row, (long) row, (long) column, (long) column));
        logger.log(jl, message, t);
    }
}
