package net.chilicat.felixscr.intellij.refactoring;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dkuffner
 */
public class AttributeBuilder {

    private final Map<String, Object> map = new HashMap<String, Object>();

    public void add(String name, Object value) {
        if (value != null) {
            map.put(name, value);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (!map.isEmpty()) {

            String prefix = map.size() > 1 ? "(\n" : "(";
            String postfix = map.size() > 1 ? "\n)" : ")";

            buf.append(prefix);
            for (Map.Entry<String, Object> e : map.entrySet()) {

                if (buf.length() > prefix.length()) { // consider (
                    buf.append(",\n ");
                }

                buf.append(e.getKey());
                buf.append(" = ");
                buf.append(e.getValue());
            }
            buf.append(postfix);

        }
        return buf.toString();
    }
}
