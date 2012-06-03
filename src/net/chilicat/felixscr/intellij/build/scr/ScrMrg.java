package net.chilicat.felixscr.intellij.build.scr;

import org.apache.felix.scrplugin.JavaClassDescriptorManager;
import org.apache.felix.scrplugin.Log;
import org.apache.felix.scrplugin.SCRDescriptorException;
import org.apache.felix.scrplugin.SCRDescriptorFailureException;
import org.apache.felix.scrplugin.tags.JavaClassDescription;

import java.io.File;
import java.util.Iterator;

/**
 * @author dkuffner
 */
public final class ScrMrg extends JavaClassDescriptorManager {

    private final FileSet sourceFiles;

    private final String classesDirectory;

    public ScrMrg(Log log, ClassLoader classLoader, FileSet sourceFiles, File classesDirectory,
                  String[] annotationTagProviders, boolean parseJavadocs, boolean processAnnotations)
            throws SCRDescriptorFailureException {
        super(log, classLoader, annotationTagProviders, parseJavadocs, processAnnotations);
        this.sourceFiles = sourceFiles;
        this.classesDirectory = classesDirectory.getAbsolutePath();
    }

    @Override
    public JavaClassDescription[] getSourceDescriptions() throws SCRDescriptorException {
        return super.getSourceDescriptions();
    }

    @Override
    protected Iterator<File> getSourceFiles() {
        return sourceFiles.iterator();
    }

    @Override
    public String getOutputDirectory() {
        return classesDirectory;
    }
}


