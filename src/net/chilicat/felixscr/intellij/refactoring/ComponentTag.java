package net.chilicat.felixscr.intellij.refactoring;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author dkuffner
 */
public class ComponentTag {
    private final XmlTag tag;

    private final String annotation = "@org.apache.felix.scr.annotations.Component";

    private final static AttributeDescription attributeDesc = new AttributeDescription()
            .bool("enabled", "immediate")
            .string("factory", "name")
            .add(KeyDescription.key("configuration-policy", "policy"), new PolicyConverter());

    public ComponentTag(XmlTag tag) {
        this.tag = tag;
    }

    public String getActivate() {
        return tag.getAttributeValue("activate");
    }

    public String getDeactivate() {
        return tag.getAttributeValue("deactivate");
    }

    @Nullable
    public ServiceTag getServices() {
        XmlTag serviceTag = tag.findFirstSubTag("service");
        if (serviceTag != null) {
            return new ServiceTag(serviceTag);
        }
        return null;
    }

    public List<ReferenceTag> getReferences() {
        List<ReferenceTag> list = new ArrayList<ReferenceTag>();
        for (XmlTag sub : tag.findSubTags("reference")) {
            list.add(new ReferenceTag(sub));
        }
        return list;
    }

    @Nullable
    public String getImplementation() {
        XmlTag implementation = tag.findFirstSubTag("implementation");
        if (implementation != null) {
            return implementation.getAttributeValue("class");
        }
        return null;
    }

    public String createAnnotation() {
        AttributeBuilder builder = new AttributeBuilder();
        buildAttributes(tag, attributeDesc, builder);
        return annotation + builder.toString();
    }

    public List<String> createProperties() {
        final List<String> list = new ArrayList<String>();
        for (XmlTag t : tag.findSubTags("property")) {
            AttributeBuilder builder = new AttributeBuilder();
            buildAttributes(t, new AttributeDescription().string("name", "value"), builder);
            list.add("@org.apache.felix.scr.annotations.Property" + builder.toString());
        }
        return list;
    }

    static void buildAttributes(XmlTag tag, AttributeDescription attributeDesc, AttributeBuilder builder) {
        for (XmlAttribute attribute : tag.getAttributes()) {
            AttributeDescription.Element element = attributeDesc.lookup(attribute.getName());
            if (element != null) {
                String finalValue = element.getConverter().toValue(attribute.getValue());
                builder.add(element.getKey().annotationKey(), finalValue);
            } else {
                Logger.getLogger(ComponentTag.class.getName()).warning("No handler for attribute: " + attribute.getName());
            }
        }
    }
}
