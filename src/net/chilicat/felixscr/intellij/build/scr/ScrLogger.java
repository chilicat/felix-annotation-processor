package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.apache.felix.scrplugin.Log;
import org.apache.felix.scrplugin.SCRDescriptorFailureException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dkuffner
 */
public final class ScrLogger implements Log {

    private final CompileContext context;
    private final static Logger LOG = Logger.getLogger(ScrLogger.class.getName());

    private Module module;
    private boolean errorPrinted = false;

    public ScrLogger(CompileContext context, Module module) {
        this.context = context;
        this.module = module;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isDebugEnabled() {
        return true;
    }


    public void debug(String s) {
        LOG.finest(s);
    }

    public void debug(String s, Throwable throwable) {
        LOG.log(Level.FINEST, s, throwable);
    }

    public void debug(Throwable throwable) {
        LOG.log(Level.FINEST, "", throwable);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String s) {
        if (logInfoMessage(s)) {
            context.addMessage(CompilerMessageCategory.INFORMATION, s, null, 0, 0);
            LOG.info(s);
        }
    }

    public void info(String s, Throwable throwable) {
        if (logInfoMessage(s)) {
            context.addMessage(CompilerMessageCategory.INFORMATION, s, null, 0, 0);
            LOG.log(Level.INFO, s, throwable);
        }
    }

    public void info(Throwable throwable) {
        context.addMessage(CompilerMessageCategory.INFORMATION, throwable.getMessage(), null, 0, 0);
        LOG.log(Level.INFO, "", throwable);
    }

    private boolean logInfoMessage(String message) {
        if (message == null ||
                // Message has no real value for the ide user.
                message.startsWith("Writing abstract service descriptor")) {
            return false;
        }
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String s) {
        LOG.log(Level.WARNING, s);
    }

    public void warn(String content, String location, int lineNumber) {
        warn(content, location, lineNumber, 0);
    }

    public void warn(String s, Throwable throwable) {
        LOG.log(Level.WARNING, s, throwable);
    }

    public void warn(Throwable throwable) {
        LOG.log(Level.WARNING, "", throwable);
    }

    public void warn(String content, String location, int lineNumber, int column) {
        context.addMessage(CompilerMessageCategory.WARNING, location, null, lineNumber, column);
        context.addMessage(CompilerMessageCategory.WARNING, content, location, lineNumber, column);
        LOG.log(Level.WARNING, content);
    }


    public void error(String s) {
        errorPrinted = true;
        context.addMessage(CompilerMessageCategory.ERROR, s, null, 0, 0);
        LOG.log(Level.SEVERE, s);
    }

    public void error(String s, String location, int i) {
        error(s, location, i, 0);
    }

    public void error(String s, String location, int i, int column) {
        errorPrinted = true;
        String url = urlForLocationString(location);

        if (url == null) {
            // Arrg. Location is something else... special handling needed:
            String className = extractClassNameForLocationString(location);
            url = urlForClassName(className);
        }

        if (url == null && location != null) {
            // Print location in case we cannot determine any location in the project.
            context.addMessage(CompilerMessageCategory.ERROR, location, null, i, column);
        }

        context.addMessage(CompilerMessageCategory.ERROR, s, url, i, column);

        LOG.log(Level.WARNING, s);
    }

    /**
     * Here is some special handling of the SCR annotation out.
     *
     * @param location the location.
     * @return a class name or null.
     */
    private String extractClassNameForLocationString(String location) {
        if (location != null && location.startsWith("Java annotations in ")) {
            return location.substring("Java annotations in ".length());
        }
        return null;
    }

    @Nullable
    private static String urlForLocationString(String location) {
        if (location != null) {
            final File f = new File(location);
            if (f.exists()) {
                // Location is a File! Get URL for it!
                VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(f);
                if (file != null) {
                    return file.getUrl();
                }
            }
        }
        return null;
    }

    @Nullable
    private String urlForClassName(final String className) {
        if (className == null) {
            return null;
        }

        return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                final PsiClass aClass = JavaPsiFacade.getInstance(module.getProject()).findClass(className, module.getModuleScope());
                if (aClass != null) {
                    PsiFile containingFile = aClass.getContainingFile();
                    if (containingFile != null) {
                        VirtualFile virtualFile = containingFile.getVirtualFile();
                        if (virtualFile != null) {
                            return virtualFile.getUrl();
                        }
                    }
                }
                return null;
            }
        });
    }

    public void error(String s, Throwable throwable) {
        errorPrinted = true;
        context.addMessage(CompilerMessageCategory.ERROR, s + " - " + throwable.getMessage(), null, 0, 0);
        LOG.log(Level.WARNING, s, throwable);
    }

    public void error(Throwable throwable) {
        if (!(throwable instanceof SCRDescriptorFailureException)) {
            errorPrinted = true;
            context.addMessage(CompilerMessageCategory.ERROR, throwable.getMessage(), null, 0, 0);
            LOG.log(Level.WARNING, throwable.getLocalizedMessage(), throwable);
        }
    }

    public boolean isErrorPrinted() {
        return errorPrinted;
    }
}
