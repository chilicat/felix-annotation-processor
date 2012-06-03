package net.chilicat.felixscr.intellij.references;

import com.intellij.patterns.PsiJavaElementPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.REFERENCE_CLS;

/**
 * @author dkuffner
 */
public class ScrReferenceProvidersRegistry extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        PsiJavaElementPattern.Capture<PsiElement> bindPattern = PsiJavaPatterns.psiElement().annotationParam(REFERENCE_CLS, "bind");
        PsiJavaElementPattern.Capture<PsiElement> unbindPattern = PsiJavaPatterns.psiElement().annotationParam(REFERENCE_CLS, "unbind");
        registrar.registerReferenceProvider(bindPattern, new MethodReferenceProvider());
        registrar.registerReferenceProvider(unbindPattern, new MethodReferenceProvider());
    }
}
