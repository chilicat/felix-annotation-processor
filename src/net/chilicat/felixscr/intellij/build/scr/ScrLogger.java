package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import org.apache.felix.scrplugin.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dkuffner
 */
public final class ScrLogger implements Log {

    private final CompileContext context;
    private final static Logger LOG = Logger.getLogger(ScrLogger.class.getName());

    public ScrLogger(CompileContext context) {
        this.context = context;
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
        context.addMessage(CompilerMessageCategory.INFORMATION, s, null, 0, 0);
        LOG.info(s);
    }

    public void info(String s, Throwable throwable) {
        context.addMessage(CompilerMessageCategory.INFORMATION, s, null, 0, 0);
        LOG.log(Level.INFO, s, throwable);
    }

    public void info(Throwable throwable) {
        context.addMessage(CompilerMessageCategory.INFORMATION, throwable.getMessage(), null, 0, 0);
        LOG.log(Level.FINEST, "", throwable);
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String s) {
        LOG.log(Level.WARNING, s);
    }

    public void warn(String content, String location, int lineNumber) {
        context.addMessage(CompilerMessageCategory.WARNING, content, location, lineNumber, 0);
        LOG.log(Level.WARNING, content);
    }

    public void warn(String s, Throwable throwable) {
        LOG.log(Level.WARNING, s, throwable);
    }

    public void warn(Throwable throwable) {
        LOG.log(Level.WARNING, "", throwable);
    }

    public boolean isErrorEnabled() {
        return false;
    }

    public void error(String s) {
        context.addMessage(CompilerMessageCategory.ERROR, s, null, 0, 0);
        LOG.log(Level.SEVERE, s);
    }

    public void error(String s, String s1, int i) {
        context.addMessage(CompilerMessageCategory.ERROR, s, s1, i, 0);
        LOG.log(Level.WARNING, s);
    }

    public void error(String s, Throwable throwable) {
        context.addMessage(CompilerMessageCategory.ERROR, s, null, 0, 0);
        LOG.log(Level.WARNING, s, throwable);
    }

    public void error(Throwable throwable) {
        context.addMessage(CompilerMessageCategory.ERROR, throwable.getMessage(), null, 0, 0);
        LOG.log(Level.WARNING, "", throwable);
    }
}
