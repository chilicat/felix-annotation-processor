package net.chilicat.felixscr.intellij.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import net.chilicat.felixscr.intellij.settings.ScrSettingsImpl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

/**
 * @author dkuffner
 */
public class ReferenceNameMissingInClass extends BaseJavaLocalInspectionTool {
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
        return "Reference - Name Is Missing";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "ReferenceNameMissingInClass";  //To change body of implemented methods use File | Settings | File Templates.
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
                final Project project = annotation.getManager().getProject();
                final ScrSettings settings = ScrSettingsImpl.getInstance(project);
                if (settings.isSpec(ScrSettings.SPEC_1_0)) {
                    if (annotation.getParent() != null && annotation.getParent().getParent() instanceof PsiClass) {
                        Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                        if (!map.containsKey("name")) {
                            holder.registerProblem(annotation, "Name missing for reference.", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        }
                    }
                }
            }
        }
    }
}
