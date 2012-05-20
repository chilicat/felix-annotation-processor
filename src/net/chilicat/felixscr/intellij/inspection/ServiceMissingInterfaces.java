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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

/**
 * @author dkuffner
 */
public class ServiceMissingInterfaces extends BaseJavaLocalInspectionTool {
    @NotNull
    @Override
    public String getShortName() {
        return "ServiceMissingInterfaces";
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return InspectionUtils.GROUP_NAME;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Service - Missing service interface declaration";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new Visitor(holder);
    }


    private static class Visitor extends JavaElementVisitor {
        private final ProblemsHolder holder;


        public Visitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitAnnotation(final PsiAnnotation annotation) {
            if (isService(annotation)) {
                final Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                if (isEmptyDeclaration(null, map) && isEmptyDeclaration("value", map)) {
                    holder.registerProblem(annotation, "Specify service interfaces explicit", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }
        }

        private boolean isEmptyDeclaration(@Nullable String key, Map<String, PsiNameValuePair> map) {
            PsiNameValuePair pair = map.get(key);
            return pair == null || getClasses(pair).isEmpty();
        }
    }
}
