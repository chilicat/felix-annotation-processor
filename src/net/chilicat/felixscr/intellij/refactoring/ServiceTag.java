package net.chilicat.felixscr.intellij.refactoring;

import com.intellij.psi.xml.XmlTag;

/**
 * @author dkuffner
 */
class ServiceTag {
    private final String annotation = "@org.apache.felix.scr.annotations.Service";

    private final XmlTag tag;

    private final static AttributeDescription attributeDesc = new AttributeDescription().add(KeyDescription.key("servicefactory", "serviceFactory"), new BoolConverter());

    ServiceTag(XmlTag tag) {
        this.tag = tag;
    }

    public String createAnnotation() {
        AttributeBuilder builder = new AttributeBuilder();

        ComponentTag.buildAttributes(tag, attributeDesc, builder);
        builder.add("value", exctractServiceInterfaces());

        return annotation + builder.toString();
    }

    private String exctractServiceInterfaces() {
        StringBuilder buf = new StringBuilder().append("{ ");
        for (XmlTag t : tag.findSubTags("provide")) {
            if (buf.length() > 2) {
                buf.append(", ");
            }
            buf.append(t.getAttributeValue("interface")).append(".class");
        }
        buf.append(" }");
        return buf.toString();
    }
}
