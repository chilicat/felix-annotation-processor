package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.util.PathUtil;
import net.chilicat.felixscr.intellij.settings.ScrSettings;
import org.apache.felix.scrplugin.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Manifest;

public abstract class AbstractScrProcessor {

    private ScrSettings settings;
    private ScrLogger logger;

    public AbstractScrProcessor() {

    }

    public void setLogger(ScrLogger logger) {
        this.logger = logger;
    }

    public ScrLogger getLogger() {
        return logger;
    }

    public void setSettings(ScrSettings settings) {
        this.settings = settings;
    }

    public boolean execute() {
        try {
            return executeImpl();
        } catch (RuntimeException e) {
            getLogger().error("[" + getModuleName() + "] ScrProcessing Failed: " + e.getMessage(), e);
            e.printStackTrace();

        }
        return false;
    }

    private boolean executeImpl() {

        try {
            final File classDir = this.getClassOutDir();
            if (classDir == null) {
                getLogger().error("Compiler Output path must be set for: " + getModuleName(), null, -1, -1);
                return false;
            }

            deleteServiceComponentXMLFiles(classDir, logger);


            final Collection<String> classPath = new LinkedHashSet<String>();
            classPath.add(classDir.getPath());
            classPath.add(PathUtil.getJarPathForClass(Component.class));
            classPath.add(PathUtil.getJarPathForClass(BundleContext.class));
            collectClasspath(classPath);

            Options opt = new Options();
            opt.setGenerateAccessors(settings.isGenerateAccessors());
            opt.setSpecVersion(SpecVersion.fromName(settings.getSpec()));
            opt.setStrictMode(settings.isStrictMode());
            opt.setProperties(new HashMap<String, String>());
            opt.setOutputDirectory(classDir);

            Project project = new Project();
            project.setClassLoader(createClassLoader(classPath));
            project.setClassesDirectory(classDir.getAbsolutePath());
            project.setSources(getSources());
            project.setDependencies(toFileCollection(classPath));

            SCRDescriptorGenerator gen = new SCRDescriptorGenerator(logger);
            gen.setOptions(opt);
            gen.setProject(project);

            Result result = gen.execute();
            if (result.getScrFiles() != null) {
                updateManifest(result, logger);
            }

            return !logger.isErrorPrinted();

        } catch (SCRDescriptorFailureException e) {
            logger.error(e.getMessage(), e);
        } catch (SCRDescriptorException e) {
            logger.error(e.getMessage(), e.getSourceLocation(), 0);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private void deleteServiceComponentXMLFiles(File classDir, ScrLogger logger) {
        File xmlDir = new File(classDir, "OSGI-INF");
        if (xmlDir.exists() && xmlDir.isDirectory()) {
            File[] files = xmlDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".xml")) {
                        logger.debug("Delete service xml: " + file.getAbsolutePath());
                        if (!file.delete()) {
                            logger.warn("Cannot delete service xml: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    private Collection<Source> getSources() {
        final Collection<Source> sources;


        if (settings.isScanClasses()) {

            File out = getClassOutDir();
            if (out != null) {
                sources = ScrSource.toSourcesCollection(new File[]{out}, ".class");
            } else {
                sources = Collections.emptyList();
            }
        } else {
            final File[] sourceRoots = getModuleSourceRoots();
            sources = ScrSource.toSourcesCollection(sourceRoots, ".java");
        }
        return sources;
    }

    protected abstract File[] getModuleSourceRoots();

    protected abstract File getClassOutDir();

    private Collection<File> toFileCollection(Collection<String> classPath) {
        Collection<File> files = new ArrayList<File>(classPath.size());
        boolean first = true;
        for (String a : classPath) {
            if (!first) {
                files.add(new File(a));
            }
            first = false;
        }
        return files;
    }

    private void updateManifest(Result result, ScrLogger logger) {
        File manifest = new File(this.getClassOutDir(), "/META-INF/MANIFEST.MF");
        if (manifest.exists() && !result.getScrFiles().isEmpty()) {
            final String componentLine = "OSGI-INF/*";

            try {
                FileInputStream in = new FileInputStream(manifest);
                Manifest m = null;
                try {
                    m = new Manifest(in);
                    switch (settings.getManifestPolicy()) {
                        case overwrite:
                            m.getMainAttributes().putValue("Service-Component", componentLine);
                            break;
                        case merge:
                            String value = m.getMainAttributes().getValue("Service-Component");
                            if (value == null || value.isEmpty()) {
                                m.getMainAttributes().putValue("Service-Component", componentLine);
                            } else {
                                m.getMainAttributes().putValue("Service-Component", addServiceComponentTo(value, componentLine));
                            }

                            break;
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
            logger.warn("Module '" + getModuleName() + "' has no manifest. Couldn't add component descriptor");
        }
    }

    protected abstract String getModuleName();

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

    private ClassLoader createClassLoader(Collection<String> classPath) throws MalformedURLException {
        final URL[] urls = new URL[classPath.size()];
        final List<String> list = new ArrayList<String>(classPath);

        for (int i = 0; i < classPath.size(); i++) {
            urls[i] = new File(list.get(i)).toURI().toURL();
        }

        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    protected abstract void collectClasspath(Collection<String> classPath);
}
