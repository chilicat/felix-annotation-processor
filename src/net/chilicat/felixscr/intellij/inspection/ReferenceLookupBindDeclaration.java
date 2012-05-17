package net.chilicat.felixscr.intellij.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

/**
 * @author dkuffner
 */
public class ReferenceLookupBindDeclaration extends BaseJavaLocalInspectionTool {

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GROUP_NAME;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Reference - Lookup obsolete Bind Declaration";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "ReferenceLookupBindDeclaration";
    }

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new Visitor(holder);
    }

    private static class Visitor extends JavaElementVisitor {
        private final ProblemsHolder holder;

        public Visitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitAnnotation(final PsiAnnotation annotation) {
            if (isReference(annotation)) {
                final Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                if (isLookupStrategy(map)) {
                    if (map.containsKey("bind")) {
                        holder.registerProblem(annotation, "bind should not be declared if 'LOOKUP' strategy is in use.", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
                    if (map.containsKey("unbind")) {
                        holder.registerProblem(annotation, "unbind should not be declared if 'LOOKUP' strategy is in use.", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
                }
            }
        }
    }
}
