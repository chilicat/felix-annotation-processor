package net.chilicat.felixscr.intellij.refactoring;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BaseRefactoringAction;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author dkuffner
 */
public class ToFelixSCRAction extends BaseRefactoringAction {

    public ToFelixSCRAction() {

    }

    @Override
    protected boolean isAvailableForLanguage(Language language) {
        return language.getID().equals("XML");
    }

    @Override
    protected boolean isAvailableInEditorOnly() {
        return true;
    }

    @Override
    protected boolean isEnabledOnElements(PsiElement[] psiElements) {
        return false;
    }


    @Override
    protected RefactoringActionHandler getHandler(DataContext dataContext) {

        // CommonRefactoringUtil

        return new RefactoringActionHandler() {
            public void invoke(final @NotNull Project project, final Editor editor, final PsiFile psiFile, DataContext dataContext) {


                if (psiFile instanceof XmlFile) {
                    XmlFile f = (XmlFile) psiFile;
                    XmlTag rootTag = f.getRootTag();
                    if (rootTag != null) {
                        String prefix = rootTag.getPrefixByNamespace("http://www.osgi.org/xmlns/scr/v1.1.0");
                        final VirtualFile virtualFile = psiFile.getVirtualFile();
                        if (virtualFile == null) {
                            CommonRefactoringUtil.showErrorMessage("File not found", "Cannot resolve file", null, project);
                            return;
                        }
                        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
                        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
                        final Module module = fileIndex.getModuleForFile(virtualFile);

                        if (module == null) {
                            CommonRefactoringUtil.showErrorMessage("Module not found", "Cannot resolve Module", null, project);
                            return;
                        }

                        PsiClass componentClass = JavaPsiFacade.getInstance(module.getProject()).findClass("org.apache.felix.scr.annotations.Component", module.getModuleRuntimeScope(false));

                        if (componentClass == null) {
                            CommonRefactoringUtil.showErrorMessage("Felix Annotations are not available", "Felix annotations are not available for module '" + module.getName() + "'. Please add module dependency to Felix Annotation library.", null, project);
                            return;
                        }

                        GlobalSearchScope scope = GlobalSearchScope.moduleScope(module);

                        PsiDocumentManager.getInstance(project).commitAllDocuments();

                        if (rootTag.getName().equals("components")) {
                            for (final XmlTag sub : rootTag.getSubTags()) {
                                if (sub.getName().equals(prefix + ":component")) {
                                    processComponent(project, scope, sub);
                                }
                            }
                        } else if (rootTag.getLocalName().equals("component")) {
                            processComponent(project, scope, rootTag);
                        } else {
                            CommonRefactoringUtil.showErrorMessage("Invalid Format", "The file seems not to be a service component.", null, project);
                        }

                        PsiDocumentManager.getInstance(project).commitAllDocuments();
                    }
                }
            }

            private void processComponent(final Project project, GlobalSearchScope scope, XmlTag sub) {
                final ComponentTag componentTag = new ComponentTag(sub);
                String clazz = componentTag.getImplementation();

                if (clazz != null) {
                    final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
                    final PsiClass implementationClass = psiFacade.findClass(clazz, scope);

                    if (implementationClass != null) {
                        PsiFile containingFile = implementationClass.getContainingFile();
                        VirtualFile file = containingFile.getVirtualFile();

                        if (file != null) {
                            FileEditorManager.getInstance(project).openFile(file, true, true);
                        }

                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            public void run() {
                                PsiElementFactory factory = psiFacade.getElementFactory();
                                PsiModifierList modifierList = implementationClass.getModifierList();

                                if (modifierList != null) {
                                    PsiAnnotation componentAnnotation = factory.createAnnotationFromText(componentTag.createAnnotation(), modifierList);
                                    modifierList.addAfter(componentAnnotation, null);

                                    ServiceTag servicesTag = componentTag.getServices();

                                    addService(factory, modifierList, servicesTag);
                                    addProperties(factory, modifierList);


                                    for (ReferenceTag t : componentTag.getReferences()) {
                                        String possibleBindMemberName = t.getPossibleBindMemberName();
                                        boolean added = false;
                                        if (possibleBindMemberName != null) {
                                            PsiField field = implementationClass.findFieldByName(possibleBindMemberName, false);
                                            if (field != null) {
                                                PsiType type = field.getType();
                                                PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
                                                // If fqn is not available than the class is not fully resolved.
                                                if (psiClass != null && psiClass.getQualifiedName() != null) {
                                                    PsiModifierList modList = field.getModifierList();
                                                    if (modList != null) {
                                                        added = true;
                                                        if (t.getServiceInterface().equals(psiClass.getQualifiedName())) {
                                                            PsiAnnotation refAnnotation = factory.createAnnotationFromText(t.createAnnotation(false), modList);
                                                            modList.addAfter(refAnnotation, null);
                                                        } else {
                                                            PsiAnnotation refAnnotation = factory.createAnnotationFromText(t.createAnnotation(true), modList);
                                                            modList.addAfter(refAnnotation, null);
                                                        }
                                                        JavaCodeStyleManager.getInstance(project).shortenClassReferences(modList);
                                                        CodeStyleManager.getInstance(project).reformat(modList);
                                                    }
                                                }
                                            }
                                        }

                                        if (!added) {
                                            componentAnnotation = factory.createAnnotationFromText(t.createAnnotation(true), modifierList);
                                            modifierList.addAfter(componentAnnotation, null);
                                        }
                                    }

                                    JavaCodeStyleManager.getInstance(project).shortenClassReferences(modifierList);
                                    CodeStyleManager.getInstance(project).reformat(modifierList);

                                    PsiImportList importList = ((PsiJavaFile) implementationClass.getContainingFile()).getImportList();
                                    if (importList != null) {
                                        CodeStyleManager.getInstance(project).reformat(importList);
                                    }
                                }
                            }

                            private void addService(PsiElementFactory factory, PsiModifierList modifierList, ServiceTag servicesTag) {
                                if (servicesTag != null) {
                                    PsiAnnotation serviceAnnotation = factory.createAnnotationFromText(servicesTag.createAnnotation(), modifierList);
                                    modifierList.addAfter(serviceAnnotation, null);
                                }
                            }

                            private void addProperties(PsiElementFactory factory, PsiModifierList modifierList) {
                                // Properties.
                                for (String property : componentTag.createProperties()) {
                                    PsiAnnotation an = factory.createAnnotationFromText(property, modifierList);
                                    modifierList.addAfter(an, null);
                                }
                            }
                        });
                    } else {
                        CommonRefactoringUtil.showErrorMessage("Implementation class not found", "Cannot resolve implementation class: " + clazz, null, project);
                    }
                } else {
                    CommonRefactoringUtil.showErrorMessage("Invalid Service Component", "Service Component XML doesn't specify a implementation class", null, project);
                }
            }

            public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
                CommonRefactoringUtil.showErrorMessage("Not supported", "unsupported call", null, project);
            }
        };
    }
}
