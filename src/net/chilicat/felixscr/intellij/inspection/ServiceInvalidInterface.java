package net.chilicat.felixscr.intellij.inspection;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

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

        @Override
        public void visitAnnotation(final PsiAnnotation annotation) {
            if (isService(annotation)) {
                final Map<String, PsiNameValuePair> map = toAttributeMap(annotation.getParameterList());
                if (map.containsKey(null)) {
                    final PsiNameValuePair pair = map.get(null);
                    final List<PsiClass> classes = getClasses(pair);

                    if (!classes.isEmpty()) {
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
                                // Not all services are satisfied - continue with interfaces.  
                                /*PsiReferenceList implementsList = cls.getImplementsList();
                                if (implementsList != null) {
                                    for (PsiClassType type : implementsList.getReferencedTypes()) {
                                        PsiClass candidate = PsiTypesUtil.getPsiClass(type);
                                        if (candidate != null) {
                                            if(removeIfClassInherits(classes, candidate)) {
                                                break;
                                            }
                                        }
                                    }
                                }
                                
                                if(!classes.isEmpty()) {
                                    // Still not satified .
                                } */

                            }
                        }
                    }
                }
            }
        }

        private boolean resolveServiceInterfaces(List<PsiClass> classes, PsiClass cls) {
            for (Iterator<PsiClass> itr = classes.iterator(); itr.hasNext(); ) {
                PsiClass serviceInterface = itr.next();
                if (cls.isInheritor(serviceInterface, true)) {
                    itr.remove();
                }
            }

            return classes.isEmpty();
        }
    }
}
