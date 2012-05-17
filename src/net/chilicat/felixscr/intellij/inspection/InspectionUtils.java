package net.chilicat.felixscr.intellij.inspection;

import com.intellij.openapi.util.Comparing;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dkuffner
 */
public class InspectionUtils {
    public final static String GROUP_NAME = "Felix SCR Annotations";

    public static Map<String, PsiNameValuePair> toAttributeMap(PsiAnnotationParameterList list) {
        final Map<String, PsiNameValuePair> map = new HashMap<String, PsiNameValuePair>();
        for (PsiNameValuePair p : list.getAttributes()) {
            map.put(p.getName(), p);
        }
        return map;
    }

    public static boolean isLookupStrategy(Map<String, PsiNameValuePair> map) {
        PsiNameValuePair strategy = map.get("strategy");
        boolean lookup = false;
        if (strategy != null) {
            PsiAnnotationMemberValue value = strategy.getValue();
            if (value != null) {
                String text = value.getText();
                lookup = text.endsWith("LOOKUP");
            }
        }
        return lookup;
    }

    public static boolean doesMethodExist(PsiAnnotation annotation, PsiNameValuePair p) {
        final PsiClass containingClass = PsiTreeUtil.getParentOfType(annotation, PsiClass.class);
        if (containingClass != null) {
            String value = getStringValue(p);
            if (value != null) {
                final PsiMethod[] method = containingClass.findMethodsByName(value, true);
                return method.length > 0;
            }
        }
        return false;
    }

    /**
     * A String values in a annotation contains the quotes. This method return the value without quotes.
     *
     * @param p the pair.
     * @return the value.
     */
    public static String getStringValue(PsiNameValuePair p) {
        final PsiAnnotationMemberValue value = p.getValue();
        if (value != null) {
            String text = value.getText();
            return text.substring(1, text.length() - 1);
        }
        return null;
    }

    /*
    public static String getStringValue(Map<String, PsiNameValuePair> map, String key) {
        PsiNameValuePair psiNameValuePair = map.get(key);
        if(psiNameValuePair != null) {
            return getStringValue(psiNameValuePair);
        }
        return null;
    } */

    public static boolean isReference(PsiAnnotation annotation) {
        final String qualifiedName = annotation.getQualifiedName();
        return (Comparing.strEqual(qualifiedName, "org.apache.felix.scr.annotations.Reference"));
    }
}
