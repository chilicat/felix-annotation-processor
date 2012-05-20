package net.chilicat.felixscr.intellij.refactoring;

/**
 * @author dkuffner
 */
public class StringConverter implements Converter {
    public String toValue(String value) {
        if (value != null) {
            return "\"" + value + "\"";
        }
        return null;
    }
}
