package net.chilicat.felixscr.intellij.refactoring;

/**
 * @author dkuffner
 */
public class BoolConverter implements Converter {
    public String toValue(String value) {
        if (value != null) {
            return Boolean.valueOf(value).toString();
        }
        return null;
    }
}
