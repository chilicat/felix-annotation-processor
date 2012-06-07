package net.chilicat.felixscr.intellij.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.chilicat.felixscr.intellij.inspection.InspectionUtils.*;

/**
 * @author dkuffner
 */
public class ServiceInvalidInterface extends BaseJavaLocalInspectionTool {
    @NotNull
    @Override
    public String getShortName() {
        return "ServiceInvalidInterface";
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
        return "Service - declares service interface which is not implemented class";
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

        private boolean isEmptyDeclaration(@Nullable String key, Map<String, PsiNameValuePair> map) {
            PsiNameValuePair pair = map.get(key);
            return pair == null || getClasses(pair).isEmpty();
        }

        private List<PsiClass> resolveClasses(Map<String, PsiNameValuePair> map) {
            for (String key : Arrays.asList(null, "value")) {
                if (!isEmptyDeclaration(key, map)) {
                    PsiNameValuePair pair = map.get(key);
                    return getClasses(pair);
                }
            }
            return null;
        }

        @Override
        public void visitAnnotation(final PsiAnnotation annotation) {
            if (isService(annotation)) {
                final Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                final List<PsiClass> classes = resolveClasses(map);

                if (classes != null && !classes.isEmpty()) {
                    PsiClass cls = findClass(annotation);
                    if (cls != null) {
                        // First check if owner class inherits the service classes!
                        if (!resolveServiceInterfaces(classes, cls)) {
                            for (PsiClass serviceCls : classes) {
                                String qualifiedName = serviceCls.getQualifiedName();
                                if (qualifiedName != null) {
                                    holder.registerProblem(annotation, "Class must implement provided interface '" + qualifiedName + "'", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                }
                            }
                        }
                    }
                }
            }
        }

        private boolean resolveServiceInterfaces(List<PsiClass> classes, PsiClass cls) {

            final String thisClassName = cls.getQualifiedName();

            for (Iterator<PsiClass> itr = classes.iterator(); itr.hasNext(); ) {
                PsiClass serviceInterface = itr.next();

                String ifaceClassName = serviceInterface.getQualifiedName();

                if (thisClassName != null && thisClassName.equals(ifaceClassName)) {
                    itr.remove();
                } else if (cls.isInheritor(serviceInterface, true)) {
                    itr.remove();
                }
            }

            return classes.isEmpty();
        }
    }
}
