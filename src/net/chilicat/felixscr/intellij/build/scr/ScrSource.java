package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.felix.scrplugin.Source;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

class ScrSource implements Source {
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
        String path = f.getAbsolutePath();

        if (path.endsWith(extension)) {
            for (VirtualFile root : sourceRoots) {
                if (path.startsWith(root.getPath())) {
                    String cls = path.substring(root.getPath().length() + 1, path.length() - extension.length());
                    return cls.replace(File.separator, ".");
                }
            }
        }
        throw new RuntimeException("Cannot find source root for " + f.getAbsolutePath());
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
