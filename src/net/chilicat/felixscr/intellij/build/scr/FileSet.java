package net.chilicat.felixscr.intellij.build.scr;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class FileSet {
    final VirtualFile[] sourceRoots;

    public FileSet(VirtualFile[] sourceRoots) {
        this.sourceRoots = sourceRoots;
    }

    public Iterator<File> iterator() {
        List<File> files = new LinkedList<File>();
        collect(sourceRoots, files);
        return files.iterator();
    }

    static void collect(VirtualFile[] sourceRoots, Collection<File> sources) {
        for (VirtualFile f : sourceRoots) {
            if (f.isInLocalFileSystem()) {
                if (f.isDirectory()) {
                    collect(f.getChildren(), sources);
                } else if (f.isValid() && f.getName().endsWith("java")) {
                    sources.add(VfsUtil.virtualToIoFile(f));
                }
            }
        }
    }

}
