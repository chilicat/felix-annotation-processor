package net.chilicat.felixscr.intellij.refactoring;

/**
 * @author dkuffner
 */
public class KeyDescription {
    private final String tagKey;
    private final String annotationKey;

    public static KeyDescription key(String key) {
        return new KeyDescription(key, key);
    }

    public static KeyDescription key(String tagKey, String annotationKey) {
        return new KeyDescription(tagKey, annotationKey);
    }

    KeyDescription(String tagKey, String annotationKey) {
        this.tagKey = tagKey;
        this.annotationKey = annotationKey;
    }

    public String tagKey() {
        return tagKey;
    }

    public String annotationKey() {
        return annotationKey;
    }
}
