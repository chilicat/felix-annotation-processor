package net.chilicat.felixscr.intellij.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

/**
 * @author dkuffner
 */
public class ReferenceInterfaceMissingInClass extends BaseJavaLocalInspectionTool {
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
        return "Reference - Interface Is Missing";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "ReferenceInterfaceMissingInClass";  //To change body of implemented methods use File | Settings | File Templates.
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
                if (annotation.getParent() != null && annotation.getParent().getParent() instanceof PsiClass) {
                    Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                    if (!map.containsKey("referenceInterface")) {
                        holder.registerProblem(annotation, "Interface missing for reference.", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
                }
            }
        }
    }
}
