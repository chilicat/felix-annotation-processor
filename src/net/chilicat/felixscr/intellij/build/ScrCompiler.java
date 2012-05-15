package net.chilicat.felixscr.intellij.build;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.felixscr.intellij.ScrSettings;
import net.chilicat.felixscr.intellij.build.scr.ScrProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dkuffner
 */
public class ScrCompiler implements ClassPostProcessingCompiler {

    @NotNull
    public ProcessingItem[] getProcessingItems(CompileContext context) {
        final List<ProcessingItem> processingItems = new LinkedList<ProcessingItem>();
        final CompileScope compileScope = getScope(context);
        return new ProcessingItemReadAction(processingItems, compileScope, context).execute().getResultObject();
    }

    public ProcessingItem[] process(CompileContext context, ProcessingItem[] processingItems) {
        final CompileScope compileScope = getScope(context);

        ScrSettings instance = ScrSettings.getInstance(context.getProject());
        final boolean strictMode = instance.isStrictMode();

        for (final Module module : compileScope.getAffectedModules()) {

            if (ScrProcessor.accept(module)) {

                context.getProgressIndicator().setText("Felix SCR generation for " + module.getName());


                final String outputDir = getOutputPath(context, module);

                if (outputDir == null) {
                    context.addMessage(CompilerMessageCategory.ERROR, "Compiler Output path must be set for: " + module.getName(), null, -1, -1);
                    continue;
                }

                try {
                    ScrProcessor scrProcessor = new ScrProcessor(context, module, outputDir);
                    scrProcessor.setStrictMode(strictMode);
                    scrProcessor.execute();
                } catch (RuntimeException e) {
                    context.addMessage(CompilerMessageCategory.ERROR, e.getLocalizedMessage(), null, 0, 0);
                }
            }
        }

        return processingItems;
    }

    public static String getOutputPath(CompileContext ctx, Module module) {
        final VirtualFile dir = ctx.getModuleOutputDirectory(module);
        return dir == null ? null : dir.getPath();
    }

    private CompileScope getScope(CompileContext context) {
        return context.isRebuild()
                ? context.getProjectCompileScope()
                : context.getCompileScope();
    }

    @NotNull
    public String getDescription() {
        return "Felix Annotation Processing";
    }

    public boolean validateConfiguration(CompileScope compileScope) {
        return true;
    }

    public ValidityState createValidityState(DataInput in) throws IOException {
        return TimestampValidityState.load(in);
    }

    private static class ProcessingItemReadAction extends ReadAction<ProcessingItem[]> {
        final List<ProcessingItem> processingItems;
        final CompileScope compileScope;
        final CompileContext context;

        private ProcessingItemReadAction(List<ProcessingItem> processingItems, CompileScope compileScope, CompileContext context) {
            this.processingItems = processingItems;
            this.compileScope = compileScope;
            this.context = context;
        }

        @Override
        protected void run(Result<ProcessingItem[]> result) throws Throwable {
            result.setResult(ProcessingItem.EMPTY_ARRAY);
            ScrSettings instance = ScrSettings.getInstance(context.getProject());
            if (instance.isEnabled()) {
                for (final Module module : compileScope.getAffectedModules()) {
                    if (ScrProcessor.accept(module)) {
                        result.setResult(new ProcessingItem[]{new MyProcessingItem(module)});
                    }
                }
            }
        }

    }

    private static class MyProcessingItem implements ProcessingItem {
        private final Module module;

        public MyProcessingItem(Module module) {
            this.module = module;
        }

        @NotNull
        public VirtualFile getFile() {
            return module.getModuleFile();
        }

        public ValidityState getValidityState() {
            return new TimestampValidityState(System.currentTimeMillis());
        }
    }
}
