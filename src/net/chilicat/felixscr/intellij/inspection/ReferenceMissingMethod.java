package net.chilicat.felixscr.intellij.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

public class ReferenceMissingMethod extends BaseJavaLocalInspectionTool {

    private static final String DISPLAY_NAME = "Reference - Missing Method";

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return GROUP_NAME;
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "ReferenceMissingMethod";
    }

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(final PsiAnnotation annotation) {
                if (isReference(annotation)) {
                    final Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                    check(annotation, map, "bind");
                    check(annotation, map, "unbind");
                }
            }

            private void check(PsiAnnotation annotation, Map<String, PsiNameValuePair> map, String methodName) {
                PsiNameValuePair pair = map.get(methodName);
                if (pair != null && !doesMethodExist(annotation, pair)) {
                    holder.registerProblem(annotation, "Missing " + methodName + " method '" + getStringValue(pair) + "'", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }
        };
    }
}
