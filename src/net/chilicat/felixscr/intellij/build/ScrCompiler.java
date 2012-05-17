package net.chilicat.felixscr.intellij.build;

import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.felixscr.intellij.build.scr.ScrProcessor;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author dkuffner
 */
public class ScrCompiler implements ClassPostProcessingCompiler {

    @NotNull
    public ProcessingItem[] getProcessingItems(CompileContext context) {
        final ScrSettings settings = ScrSettings.getInstance(context.getProject());

        if (settings.isEnabled()) {
            final CompileScope compileScope = getScope(context);

            if (settings.isEnabled()) {
                final List<ProcessingItem> items = new ArrayList<ProcessingItem>();

                for (final Module module : compileScope.getAffectedModules()) {
                    if (ScrProcessor.accept(module)) {
                        items.add(new ScrProcessingItem(module, settings));
                    }
                }

                return toArray(items);
            }
        }

        return ProcessingItem.EMPTY_ARRAY;
    }

    public ProcessingItem[] process(CompileContext context, ProcessingItem[] processingItems) {
        final List<ProcessingItem> result = new ArrayList<ProcessingItem>();
        for (ProcessingItem i : processingItems) {
            if (i instanceof ScrProcessingItem) {
                final ScrProcessingItem item = (ScrProcessingItem) i;
                if (item.execute(context)) {
                    result.add(item);
                }
            }
        }
        return toArray(result);
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

    private static ProcessingItem[] toArray(Collection<ProcessingItem> items) {
        return items.toArray(new ProcessingItem[items.size()]);
    }
}
