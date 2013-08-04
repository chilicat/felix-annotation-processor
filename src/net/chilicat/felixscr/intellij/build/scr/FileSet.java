package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 */
public class FileSet implements Iterable<File> {
    final VirtualFile[] sourceRoots;
    final String extension;

    public FileSet(VirtualFile[] sourceRoots, String extension) {
        this.sourceRoots = sourceRoots;
        this.extension = extension;
    }

    public Iterator<File> iterator() {
        Set<File> files = new LinkedHashSet<File>();
        collect(sourceRoots, files);
        return files.iterator();
    }

    void collect(VirtualFile[] sourceRoots, Collection<File> sources) {
        for (VirtualFile f : sourceRoots) {
            if (f.isInLocalFileSystem()) {
                if (f.isDirectory()) {
                    collect(f.getChildren(), sources);
                } else if (f.isValid() && f.getName().endsWith(extension)) {
                    sources.add(VfsUtil.virtualToIoFile(f));
                }
            }
        }
    }

}
