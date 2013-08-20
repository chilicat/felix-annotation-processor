package net.chilicat.felixscr.intellij.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.ProgressMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Builder extends BuilderService {
    @NotNull
    @Override
    public List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
        return Arrays.asList(new MyModuleLevelBuilder());

    }

    private static class MyModuleLevelBuilder extends ModuleLevelBuilder {
        private MyModuleLevelBuilder() {
            super(BuilderCategory.CLASS_POST_PROCESSOR);
        }

        @Override
        public ExitCode build(CompileContext compileContext, ModuleChunk moduleChunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> holder, OutputConsumer outputConsumer) throws ProjectBuildException, IOException {
            JPSSCRExtensionService instance = JPSSCRExtensionService.getInstance();
            Settings settings = instance.getSettings(compileContext.getProjectDescriptor().getProject());

            if (settings.isEnabled() && (holder.hasDirtyFiles() || holder.hasRemovedFiles())) {

                compileContext.processMessage(new ProgressMessage(getPresentableName() + " [" + moduleChunk.getName() + "]"));

                ScrProcessor p = new ScrProcessor();
                p.setLogger(new ScrLoggerImpl(compileContext, moduleChunk, getPresentableName()));
                p.setSettings(settings);
                p.setModuleChunk(moduleChunk);

                if (p.execute()) {
                    return ExitCode.OK;
                }

                return ExitCode.ABORT;

            }

            return ExitCode.NOTHING_DONE;

        }

        @NotNull
        @Override
        public String getPresentableName() {
            return "Felix SCR";
        }
    }
}
