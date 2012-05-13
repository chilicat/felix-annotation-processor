package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import org.apache.felix.scrplugin.JavaClassDescriptorManager;
import org.apache.felix.scrplugin.SCRDescriptorException;
import org.apache.felix.scrplugin.SCRDescriptorFailureException;
import org.apache.felix.scrplugin.SCRDescriptorGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Manifest;

public class ScrProcessor {
    private final CompileContext context;
    private final Module module;
    private final String outputDir;

    public ScrProcessor(CompileContext context, Module module, String outputDir) {
        this.context = context;
        this.module = module;
        this.outputDir = outputDir;
    }

    public CompileContext getContext() {
        return context;
    }

    public Module getModule() {
        return module;
    }

    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Figures out if given module has felix SCR annotations in used.
     *
     * @param module the module.
     * @return true in case module has a runtime dependency to felix SCR annotations.
     */
    public static boolean accept(final Module module) {

        final PsiClass aClass = ApplicationManager.getApplication().runReadAction(new Computable<PsiClass>() {
            public PsiClass compute() {
                return JavaPsiFacade.getInstance(module.getProject()).findClass("org.apache.felix.scr.annotations.Component", module.getModuleRuntimeScope(false));
            }
        });

        return aClass != null;
    }

    public void execute() {
        final ScrLogger logger = new ScrLogger(this.getContext());

        final VirtualFile[] sourceRoots = ModuleRootManager.getInstance(this.getModule()).getSourceRoots();

        final SCRDescriptorGenerator gen = new SCRDescriptorGenerator(logger);

        try {
            final File classDir = new File(this.getOutputDir());
            final ClassLoader classLoader = createClassLoader(classDir);

            final FileSet sourceFiles = new FileSet(sourceRoots);
            final List<String> classPath = createClasspath(this.getModule());

            final JavaClassDescriptorManager descriptorManager = new ScrMrg(logger, classLoader, sourceFiles, classDir, classPath, new String[0], false, true);

            gen.setGenerateAccessors(true);
            gen.setDescriptorManager(descriptorManager);
            gen.setOutputDirectory(new File(this.getOutputDir()));
            gen.setStrictMode(false);
            gen.setProperties(new HashMap<String, String>());

            if (gen.execute()) {
                updateManifest(logger);
            } else {
                logger.warn("Couldn't create component descriptor for " + module.getName());
            }

        } catch (SCRDescriptorFailureException e) {
            logger.error(e);
        } catch (SCRDescriptorException e) {
            logger.error(e);
        } catch (MalformedURLException e) {
            logger.error(e);
        }
    }

    private void updateManifest(ScrLogger logger) {
        File manifest = new File(this.getOutputDir(), "/META-INF/MANIFEST.MF");
        if (manifest.exists()) {

            String serviceComponentXml = "OSGI-INF/serviceComponents.xml";

            try {
                FileInputStream in = new FileInputStream(manifest);
                Manifest m = null;
                try {
                    m = new Manifest(in);
                    String value = m.getMainAttributes().getValue("Service-Component");
                    if (value == null || value.isEmpty()) {

                        m.getMainAttributes().putValue("Service-Component", serviceComponentXml);
                    } else {
                        m.getMainAttributes().putValue("Service-Component", addServiceComponentTo(value, serviceComponentXml));
                    }
                } finally {
                    in.close();
                }

                FileOutputStream out = new FileOutputStream(manifest);
                try {
                    m.write(out);
                } finally {
                    out.close();
                }

            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            logger.warn("Module '" + module.getName() + "' has no manifest. Couldn't add component descriptor");
        }
    }

    private String addServiceComponentTo(String value, String serviceComponentXml) {
        String[] values = value.split(",");
        Set<String> all = new HashSet<String>();
        for (String v : values) {
            all.add(v.trim());
        }
        all.add(serviceComponentXml);

        StringBuilder finalValue = new StringBuilder();
        for (String a : all) {
            if (finalValue.length() > 0) {
                finalValue.append(",");
            }
            finalValue.append(a);
        }
        return finalValue.toString();
    }

    private ClassLoader createClassLoader(File classDir) throws MalformedURLException {
        return new URLClassLoader(new URL[]{classDir.toURI().toURL()}, getClass().getClassLoader());
    }

    private List<String> createClasspath(Module module) {
        final Set<String> classPath = new HashSet<String>();
        for (OrderEntry library : ModuleRootManager.getInstance(module).getOrderEntries()) {
            if (library instanceof LibraryOrderEntry) {
                final Library lib = ((LibraryOrderEntry) library).getLibrary();
                if (lib != null) {
                    final VirtualFile[] files = lib.getFiles(OrderRootType.CLASSES);
                    for (VirtualFile f : files) {
                        classPath.add(VfsUtil.virtualToIoFile(f).getAbsolutePath());
                    }
                }
            }
        }
        return new ArrayList<String>(classPath);
    }


}
