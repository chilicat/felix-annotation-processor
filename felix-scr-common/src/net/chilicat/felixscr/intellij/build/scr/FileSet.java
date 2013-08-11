package net.chilicat.felixscr.intellij.build.scr;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 */
public class FileSet implements Iterable<File> {
    final File[] sourceRoots;
    final String extension;

    public FileSet(File[] sourceRoots, String extension) {
        this.sourceRoots = sourceRoots;
        this.extension = extension;
    }

    public Iterator<File> iterator() {
        Set<File> files = new LinkedHashSet<File>();
        collect(sourceRoots, files);
        return files.iterator();
    }

    void collect(File[] sourceRoots, Collection<File> sources) {
        if (sourceRoots != null) {
            for (File f : sourceRoots) {
                if (f.isDirectory()) {
                    collect(f.listFiles(), sources);
                } else if (f.getName().endsWith(extension)) {
                    sources.add(f);
                }
            }
        }
    }

}
