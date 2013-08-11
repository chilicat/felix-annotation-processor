package net.chilicat.felixscr.intellij.build;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import net.chilicat.felixscr.intellij.settings.ScrSettingsImpl;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;
import java.util.*;

/**
 * @author dkuffner
 */
public class ScrCompiler implements ClassPostProcessingCompiler {

    @NotNull
    public ProcessingItem[] getProcessingItems(CompileContext context) {

        final ScrSettings settings = ScrSettingsImpl.getInstance(context.getProject());

        if (settings.isEnabled()) {
            final CompileScope compileScope = getScope(context);

            VirtualFile[] files = compileScope.getFiles(StdFileTypes.JAVA, true);
            Set<Module> modules = new HashSet<Module>();
            for (VirtualFile f : files) {
                modules.add(context.getModuleByFile(f));
            }

            final List<ProcessingItem> items = new ArrayList<ProcessingItem>();

            for (final Module module : modules) {
                if (accept(context, module)) {
                    VirtualFile outputFile = context.getModuleOutputDirectory(module);

                    if (outputFile != null) {
                        VirtualFile osgiInf = outputFile.findChild("OSGI-INF");

                        if (osgiInf == null || context.isRebuild() || !settings.isOptimizedBuild()) {
                            items.add(new ScrProcessingItem(module, settings, System.currentTimeMillis()));
                        } else {
                            long latestModified = findLatestModified(outputFile);
                            long latestInOSGIn = findLatestModified(osgiInf);
                            if (latestModified > latestInOSGIn) {
                                items.add(new ScrProcessingItem(module, settings, latestModified));
                            }
                        }
                    }
                }
            }

            return toArray(items);
        }

        return ProcessingItem.EMPTY_ARRAY;
    }

    private long findLatestModified(VirtualFile root) {
        long cur = -1;
        for (VirtualFile child : root.getChildren()) {
            if (child.isDirectory()) {
                long timeStamp = findLatestModified(child);
                if (timeStamp > cur) {
                    cur = timeStamp;
                }
            } else if (child.isInLocalFileSystem()) {
                long timeStamp = child.getTimeStamp();
                if (timeStamp > cur) {
                    cur = timeStamp;
                }
            }
        }
        return cur;
    }

    /**
     * Enable the plugin only in case no other plugin will manager the Annotation aspect.
     *
     * @param context the context.
     * @param module  the module
     * @return true if no other facet will care about the annotations.
     */
    private boolean accept(CompileContext context, Module module) {
        for (Facet f : FacetManager.getInstance(module).getAllFacets()) {
            if (f.getTypeId().toString().equals("osgiBundleFacet")) {
                // The Felix Maven plugin will case about annotations.
                context.addMessage(CompilerMessageCategory.INFORMATION,
                        "Felix SCR Annotation Processor disabled for Module: '" +
                                module.getName() +
                                "' because Apache Felix Maven plugin has been detected", null, 0, 0);
                return false;
            }
        }
        return true;
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
