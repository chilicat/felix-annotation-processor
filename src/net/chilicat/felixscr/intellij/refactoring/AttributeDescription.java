package net.chilicat.felixscr.intellij.refactoring;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dkuffner
 */
public class AttributeDescription {
    private static final BoolConverter BOOL = new BoolConverter();
    private static final StringConverter STRING = new StringConverter();

    private final Map<String, Element> map = new HashMap<String, Element>();

    public Element lookup(String tagKey) {
        return map.get(tagKey);
    }

    public AttributeDescription bool(String... names) {
        for (String name : names) {
            add(KeyDescription.key(name), BOOL);
        }
        return this;
    }

    public AttributeDescription string(String... names) {
        for (String name : names) {
            add(KeyDescription.key(name), STRING);
        }
        return this;
    }


    public AttributeDescription add(KeyDescription key, Converter converter) {
        map.put(key.tagKey(), new Element(key, converter));
        return this;
    }

    static class Element {
        private final KeyDescription key;
        private final Converter converter;

        Element(KeyDescription key, Converter converter) {
            this.key = key;
            this.converter = converter;
        }

        public KeyDescription getKey() {
            return key;
        }

        public Converter getConverter() {
            return converter;
        }
    }
}
