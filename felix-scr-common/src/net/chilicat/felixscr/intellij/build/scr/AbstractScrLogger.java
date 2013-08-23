package net.chilicat.felixscr.intellij.build.scr;

import java.io.File;
import java.util.logging.Logger;

public abstract class AbstractScrLogger implements ScrLogger {

    private boolean errorPrinted = false;
    private final boolean debugLogging;

    protected AbstractScrLogger(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }

    public void info(String s) {
        info(s, null);
    }

    public void info(String s, Throwable throwable) {
        logImpl(Level.INFO, withModuleName(s, throwable), throwable, null, -1, -1);
    }

    public void info(Throwable throwable) {
        info(null, throwable);
    }

    public void error(String s) {
        error(s, null);
    }

    public void error(String s, String location, int row, int column) {
        errorPrinted = true;
        logImpl(Level.ERROR, withModuleName(s, null), null, location, row, column);
    }

    public void error(String s, String location, int row) {
        error(s, location, row, 0);
    }

    public void error(String s, Throwable throwable) {
        errorPrinted = true;
        logImpl(Level.ERROR, withModuleName(s, throwable), throwable, null, -1, -1);
    }

    public void error(Throwable throwable) {
        error(null, throwable);
    }

    public void warn(String s) {
        warn(s, null);
    }

    public void warn(String s, String s2, int i) {
        warn(s, s2, i, 0);
    }

    public void warn(String s, String location, int row, int column) {
        logImpl(Level.WARN, withModuleName(s, null), null, location, row, column);
    }

    public void warn(String s, Throwable throwable) {
        logImpl(Level.WARN, withModuleName(s, throwable), throwable, null, -1, -1);
    }

    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public void debug(String s) {
        debug(s, null);
    }

    public void debug(String s, Throwable throwable) {
        Logger.getLogger(getClass().getName()).log(java.util.logging.Level.FINE, s, throwable);
        if (debugLogging) {
            info(s, throwable);
        }
    }

    public void debug(Throwable throwable) {
        debug(throwable.getMessage(), throwable);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isErrorPrinted() {
        return errorPrinted;
    }

    private void logImpl(Level l, String message, Throwable t, String location, int row, int column) {
        log(l, message, t, toSourceLocation(location), row, column);
    }

    private String withModuleName(String s, Throwable t) {
        String m = "[" + getModuleName() + "] ";
        if (s == null) {
            return t != null ? m + t.getMessage() : m;
        }
        return m + s;
    }

    protected abstract String getModuleName();

    public static enum Level {
        ERROR, WARN, INFO
    }

    protected abstract void log(Level l, String message, Throwable t, String location, int row, int column);

    protected abstract File getModuleOut();

    protected abstract File[] getModuleSourceRoots();

    private String toSourceLocation(String location) {
        if (location != null && location.endsWith(".class")) {
            File[] moduleSourceRoots = getModuleSourceRoots();
            File out = getModuleOut();
            if (out != null) {
                String loc = new File(location).getAbsolutePath();

                String substring = loc.substring(out.getAbsolutePath().length(), loc.length() - 6) + ".java";

                for (File f : moduleSourceRoots) {
                    File file = new File(f, substring);
                    if (file.exists()) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        return location;
    }


}
