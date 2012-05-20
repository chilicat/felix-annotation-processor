package net.chilicat.felixscr.intellij.refactoring;

import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author dkuffner
 */
public class ReferenceTag {

    private final String annotation = "@org.apache.felix.scr.annotations.Reference";

    private final XmlTag tag;
    private final static AttributeDescription attributeDesc = new AttributeDescription()
            .string("bind", "unbind", "target", "name")
            .add(KeyDescription.key("cardinality"), new CardinalityConverter())
            .add(KeyDescription.key("policy"), new PolicyConverter())
            .add(KeyDescription.key("strategy"), new StrategyConverter());

    public ReferenceTag(XmlTag tag) {
        this.tag = tag;
    }

    public String getServiceInterface() {
        return tag.getAttributeValue("interface");
    }

    /**
     * Get bind normalized
     *
     * @return the value or null
     */
    @Nullable
    public String getPossibleBindMemberName() {
        String bind = tag.getAttributeValue("bind");
        if (bind != null) {
            for (String prefix : Arrays.asList("set", "add", "bind")) {
                if (bind.startsWith(prefix)) {
                    String substring = bind.substring(prefix.length());
                    // Replace first character with lower case equal ( Property -> property)
                    return substring.substring(0, 1).toLowerCase() + substring.substring(1);
                }
            }
            return bind;
        }
        return null;
    }

    public String createAnnotation(boolean includeInterface) {
        AttributeBuilder builder = new AttributeBuilder();
        ComponentTag.buildAttributes(tag, attributeDesc, builder);
        if (includeInterface) {
            builder.add("referenceInterface", getServiceInterface() + ".class");
        }
        return annotation + builder.toString();
    }

    private static class StrategyConverter implements Converter {
        private static final Map<String, String> MAP = new HashMap<String, String>();

        static {
            MAP.put("event", "org.apache.felix.scr.annotations.ReferenceStrategy.EVENT");
            MAP.put("lookup", "org.apache.felix.scr.annotations.ReferenceStrategy.LOOKUP");
        }

        public String toValue(String value) {
            if (value != null) {
                String res = MAP.get(value);
                if (res == null) {
                    Logger.getLogger(getClass().getName()).warning("No mapping for: " + value);
                }
                return res;
            }
            return null;
        }
    }


    private static class PolicyConverter implements Converter {
        private static final Map<String, String> MAP = new HashMap<String, String>();

        static {
            MAP.put("dynamic", "org.apache.felix.scr.annotations.ReferencePolicy.DYNAMIC");
            MAP.put("static", "org.apache.felix.scr.annotations.ReferencePolicy.STATIC");
        }

        public String toValue(String value) {
            if (value != null) {
                String res = MAP.get(value);
                if (res == null) {
                    Logger.getLogger(getClass().getName()).warning("No mapping for: " + value);
                }
                return res;
            }
            return null;
        }
    }

    private static class CardinalityConverter implements Converter {
        private static final Map<String, String> MAP = new HashMap<String, String>();

        static {
            MAP.put("0..1", "org.apache.felix.scr.annotations.ReferenceCardinality.OPTIONAL_UNARY");
            MAP.put("1..1", "org.apache.felix.scr.annotations.ReferenceCardinality.MANDATORY_UNARY");
            MAP.put("0..n", "org.apache.felix.scr.annotations.ReferenceCardinality.OPTIONAL_MULTIPLE");
            MAP.put("1..n", "org.apache.felix.scr.annotations.ReferenceCardinality.MANDATORY_MULTIPLE");
        }

        public String toValue(String value) {
            if (value != null) {
                String res = MAP.get(value);
                if (res == null) {
                    Logger.getLogger(getClass().getName()).warning("No mapping for: " + value);
                }
                return res;
            }
            return null;
        }
    }
}
