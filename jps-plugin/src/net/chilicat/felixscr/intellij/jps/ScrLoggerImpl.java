package net.chilicat.felixscr.intellij.jps;

import net.chilicat.felixscr.intellij.build.scr.ScrLogger;
import org.jetbrains.jps.incremental.MessageHandler;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ScrLoggerImpl implements ScrLogger {

    private final MessageHandler messageHandler;
    private final String compilerName;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private boolean errorPrinted = false;

    public ScrLoggerImpl(MessageHandler messageHandler, String presentableName) {
        this.messageHandler = messageHandler;
        this.compilerName = presentableName;
    }

    public boolean isErrorPrinted() {
        return errorPrinted;
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void debug(String s) {
        logger.log(Level.FINE, s);
    }

    public void debug(String s, Throwable throwable) {
        logger.log(Level.FINE, s, throwable);
    }

    public void debug(Throwable throwable) {
        logger.log(Level.FINE, throwable.getMessage(), throwable);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String s) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.INFO, s));
    }

    public void info(String s, Throwable throwable) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.INFO, s + " - Message: " + throwable.getMessage()));
    }

    public void info(Throwable throwable) {
        info(throwable.getMessage());
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(String s) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.WARNING, s));
    }

    public void warn(String s, String s2, int i) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.WARNING, s, s2, i, i, 0, 0, 0));
    }

    public void warn(String s, String s2, int i, int i2) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.WARNING, s, s2, i, i, 0, 0, i2));
    }

    public void warn(String s, Throwable throwable) {
        warn(s + " - Message: " + throwable.getMessage());
    }

    public void warn(Throwable throwable) {
        warn(throwable.getMessage());
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public void error(String s) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.ERROR, s));
        errorPrinted = true;
    }

    public void error(String s, String s2, int i) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.ERROR, s));
        errorPrinted = true;
    }

    public void error(String s, String s2, int i, int i2) {
        messageHandler.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.ERROR, s));
        errorPrinted = true;
    }

    public void error(String s, Throwable throwable) {
        error(s + " - Message: " + throwable.getMessage());
    }

    public void error(Throwable throwable) {
        error(throwable.getMessage());
    }
}
