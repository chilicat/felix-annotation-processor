package net.chilicat.felixscr.intellij.refactoring;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author dkuffner
 */
public class PolicyConverter implements Converter {
    private final Set<String> allowedConfig = new HashSet<String>(asList("OPTIONAL", "IGNORE", "REQUIRE"));

    public String toValue(String value) {
        if (value != null) {
            value = value.toUpperCase();
            String base = "org.apache.felix.scr.annotations.ConfigurationPolicy.";
            if (allowedConfig.contains(value)) {
                return base + value;
            }
        }
        return null;
    }
}
