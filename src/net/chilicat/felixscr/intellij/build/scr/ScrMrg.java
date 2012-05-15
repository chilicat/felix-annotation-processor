package net.chilicat.felixscr.intellij.build.scr;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import org.apache.felix.scrplugin.JavaClassDescriptorManager;
import org.apache.felix.scrplugin.Log;
import org.apache.felix.scrplugin.SCRDescriptorException;
import org.apache.felix.scrplugin.SCRDescriptorFailureException;
import org.apache.felix.scrplugin.om.Component;
import org.apache.felix.scrplugin.tags.JavaClassDescription;
import org.apache.felix.scrplugin.tags.annotation.AnnotationJavaClassDescription;
import org.apache.felix.scrplugin.tags.cl.ClassLoaderJavaClassDescription;

import java.io.File;
import java.util.*;

/**
 * @author dkuffner
 */
public final class ScrMrg extends JavaClassDescriptorManager {

    private final FileSet sourceFiles;

    private final String classesDirectory;

    private final List<String> classPath;

    public ScrMrg(Log log, ClassLoader classLoader, FileSet sourceFiles, File classesDirectory,
                  List<String> classPath, String[] annotationTagProviders, boolean parseJavadocs, boolean processAnnotations)
            throws SCRDescriptorFailureException {
        super(log, classLoader, annotationTagProviders, parseJavadocs, processAnnotations);
        this.sourceFiles = sourceFiles;
        this.classesDirectory = classesDirectory.getAbsolutePath();
        this.classPath = classPath;
    }

    @Override
    protected Iterator<File> getSourceFiles() {
        return sourceFiles.iterator();
    }

    /*
    @Override
    protected List<File> getDependencies() {
        ArrayList<File> files = new ArrayList<File>();
        for (String entry : classPath) {
            File file = new File(entry);
            if (file.isFile()) {
                files.add(file);
            }
        }
        return files;
    } */

    @Override
    public String getOutputDirectory() {
        return classesDirectory;
    }
}


