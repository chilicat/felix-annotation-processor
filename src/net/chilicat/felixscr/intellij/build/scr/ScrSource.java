package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.felix.scrplugin.Source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

class ScrSource implements Source {

    private final static Logger logger = Logger.getLogger(ScrSource.class.getName());

    private final File f;
    private final VirtualFile[] sourceRoots;
    private String extension;

    public ScrSource(File f, VirtualFile[] sourceRoots, String extension) {
        this.f = f;
        this.sourceRoots = sourceRoots;
        this.extension = extension;
    }

    public String getClassName() {
        return toClassName(f, sourceRoots);
    }

    public File getFile() {
        return f;
    }

    private String toClassName(File f, VirtualFile[] sourceRoots) {
        try {
            String path = normalizePath(f.getCanonicalPath());
            if (path.endsWith(extension)) {
                for (VirtualFile root : sourceRoots) {
                    String rootPath = normalizePath(root.getCanonicalPath());
                    if (startsWithIgnoreCase(path, rootPath)) {
                        String cls = path.substring(rootPath.length() + 1, path.length() - extension.length());
                        return cls.replace('/', '.');
                    }
                }
            }

            throw new RuntimeException("Cannot find source root for " + f.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Cannot resolve canonical path: " + f.getAbsolutePath() + " Message: " + e.getMessage());
        }
    }

    private boolean startsWithIgnoreCase(String path, String rootPath) {
        return path.toLowerCase().startsWith(rootPath.toLowerCase());
    }

    private String normalizePath(String path) {
        return path.replace(File.separatorChar, '/');
    }

    @Override
    public String toString() {
        return getClassName() + " File: " + getFile().getName();
    }


    static Collection<Source> toSourcesCollection(VirtualFile[] sourceRoots, String extension) {
        final FileSet sourceFiles = new FileSet(sourceRoots, extension);
        final Collection<Source> sources = new ArrayList<Source>();
        for (final File f : sourceFiles) {
            sources.add(new ScrSource(f, sourceRoots, extension));
        }
        return sources;
    }
}
