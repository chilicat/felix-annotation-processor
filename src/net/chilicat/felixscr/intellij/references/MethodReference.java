package net.chilicat.felixscr.intellij.references;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

/**
 * @author dkuffner
 */
class MethodReference extends PsiReferenceBase<PsiElement> {
    MethodReference(@NotNull PsiElement element) {
        super(element);
    }

    public PsiElement resolve() {
        PsiClass aClass = findClass(getElement());
        PsiAnnotation annotation = findParent(PsiAnnotation.class, getElement());

        if (aClass != null && annotation != null) {
            final String methodName = stripFirstAndLast(getElement().getText());

            PsiMethod[] methodsByName = aClass.findMethodsByName(methodName, true);

            if (methodsByName.length > 0) {
                return methodsByName[0];
            }
        }
        return null;
    }

    @NotNull
    public Object[] getVariants() {
        return new Object[0];
    }

    /*
   private String resolveReferenceInterface(PsiAnnotation annotation) {
       PsiAnnotationMemberValue memberValue = annotation.findAttributeValue("referenceInterface");
       String iface = null;
       if(memberValue != null) {
           iface = memberValue.getText();
       }
       if (iface == null) {
           iface = getQFTypeElement(annotation);
       }
       return iface;
   } */


}
