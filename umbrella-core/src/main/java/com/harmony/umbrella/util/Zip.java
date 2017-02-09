package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.core.convert.converter.Converter;

/**
 * Zip压缩、解压工具类.
 * 
 * @author wuxii@foxmail.com
 */
public class Zip {

    public static void zipDirectory(String dir, OutputStream os) throws IOException {
        zipDirectory(dir, true, null, os);
    }

    public static void zipDirectory(String dir, boolean includeRootDir, OutputStream os) throws IOException {
        zipDirectory(dir, includeRootDir, null, os);
    }

    public static void zipDirectory(String dir, FileFilter ff, OutputStream os) throws IOException {
        zipDirectory(dir, true, ff, os);
    }

    public static void zipDirectory(String dir, boolean includeRootDir, FileFilter ff, OutputStream os) throws IOException {
        zipDirectory(new File(dir), true, ff, os);
    }

    public static void zipDirectory(File dir, OutputStream os) throws IOException {
        zipDirectory(dir, true, null, os);
    }

    public static void zipDirectory(File dir, FileFilter ff, OutputStream os) throws IOException {
        zipDirectory(dir, true, ff, os);
    }

    public static void zipDirectory(File dir, boolean includeRootDir, OutputStream os) throws IOException {
        zipDirectory(dir, includeRootDir, null, os);
    }

    public static void zipDirectory(final File dir, final boolean includeRootDir, FileFilter ff, OutputStream os) throws IOException {
        if (!dir.isDirectory()) {
            throw new IOException(dir.getAbsolutePath() + " not directory!");
        }

        ZipEntryResourceOutputStream zos = os instanceof ZipEntryResourceOutputStream ? (ZipEntryResourceOutputStream) os : new ZipEntryResourceOutputStream(os);

        zip(dir.listFiles(), new Converter<File, ZipEntry>() {

            String baseDir = getBaseDir();

            @Override
            public ZipEntry convert(File t) {
                return new ZipEntry(t.getAbsolutePath().substring(baseDir.length()));
            }

            String getBaseDir() {
                String baseDir = dir.getAbsolutePath();
                if (includeRootDir) {
                    int index = baseDir.lastIndexOf(File.separator);
                    baseDir = baseDir.substring(0, index != -1 ? index + File.separator.length() : baseDir.length());
                }
                return baseDir;
            }

        }, null, zos);
    }

    public static void zip(File[] files, Converter<File, ZipEntry> c, FileFilter ff, ZipEntryResourceOutputStream zos) throws IOException {
        for (File f : files) {
            if (ff != null && !ff.accept(f)) {
                continue;
            }
            if (f.isFile()) {
                zos.write(new ZipEntryResource(c.convert(f), FileUtils.read(f)));
            } else {
                zip(f.listFiles(), c, ff, zos);
            }
        }
    }

    public static ZipEntryResource[] unzip(File path, ZipEntryFilter zef) throws IOException {
        List<ZipEntryResource> zers = new ArrayList<ZipEntryResource>();
        if (!path.isFile()) {
            throw new IOException(path.getAbsolutePath() + " is not file!");
        }
        ZipEntryResourceInputStream zis = null;
        try {
            zis = new ZipEntryResourceInputStream(path);
            ZipEntryResource entry = null;
            while ((entry = zis.readEntry()) != null) {
                if (zef != null && !zef.accept(entry.zipEntry)) {
                    continue;
                }
                zers.add(entry);
            }
            return zers.toArray(new ZipEntryResource[zers.size()]);
        } finally {
            if (zis != null) {
                zis.close();
            }
        }
    }

    public static void unzip(String src, String dest) throws IOException {
        unzip(src, dest, null);
    }

    public static void unzip(String src, String dest, ZipEntryFilter zef) throws IOException {
        unzip(new File(src), new File(dest), zef);
    }

    public static void unzip(File src, File dest) throws IOException {
        unzip(src, dest, null);
    }

    public static void unzip(File src, File dest, ZipEntryFilter zef) throws IOException {
        if (!src.isFile()) {
            throw new IOException(src.getAbsolutePath() + " is not file!");
        }
        if ((dest.exists() && dest.list().length > 0) || (dest.exists() && !dest.isDirectory())) {
            throw new IOException(dest.getAbsolutePath() + " dest directory not empty or not a directory");
        }
        FileUtils.createDirectory(dest);
        ZipEntryResourceInputStream zis = null;
        try {
            zis = new ZipEntryResourceInputStream(src);
            ZipEntryResource entry = null;
            while ((entry = zis.readEntry()) != null) {
                if (!entry.zipEntry.isDirectory()) {
                    File file = new File(dest.getPath(), entry.zipEntry.getName());
                    FileUtils.createFile(file);
                    FileOutputStream os = new FileOutputStream(file);
                    IOUtils.write(entry.content, os);
                    os.close();
                }
            }
        } finally {
            if (zis != null) {
                zis.close();
            }
        }
    }

    /**
     * 
     * @author wuxii@foxmail.com
     */
    public interface ZipEntryFilter {

        boolean accept(ZipEntry entry);

    }

    public static final class ZipEntryResourceInputStream extends ZipInputStream {

        public ZipEntryResourceInputStream(String path) throws FileNotFoundException {
            this(new File(path));
        }

        public ZipEntryResourceInputStream(File file) throws FileNotFoundException {
            super(new FileInputStream(file));
        }

        public ZipEntryResourceInputStream(InputStream in) {
            super(in);
        }

        public ZipEntryResource readEntry() throws IOException {
            ZipEntry entry = this.getNextEntry();
            if (entry != null) {
                byte[] b = IOUtils.toByteArray(this);
                this.closeEntry();
                return new ZipEntryResource(entry, b);
            }
            return null;
        }

    }

    public static final class ZipEntryResourceOutputStream extends ZipOutputStream {

        public ZipEntryResourceOutputStream(String path) throws FileNotFoundException {
            this(new File(path));
        }

        public ZipEntryResourceOutputStream(File file) throws FileNotFoundException {
            super(new FileOutputStream(file));
        }

        public ZipEntryResourceOutputStream(OutputStream out) {
            super(out);
        }

        public void write(ZipEntryResource zer) throws IOException {
            this.putNextEntry(zer.getZipEntry());
            this.write(zer.content);
            this.closeEntry();
        }
    }

    public static final class ZipEntryResource {

        private final ZipEntry zipEntry;
        private final byte[] content;

        public ZipEntryResource(String name, byte[] b) {
            this(new ZipEntry(name), b);
        }

        public ZipEntryResource(ZipEntry zipEntry, byte[] b) {
            this.zipEntry = zipEntry;
            this.content = b;
        }

        public ZipEntry getZipEntry() {
            return zipEntry;
        }

        public byte[] getContent() {
            return content;
        }
    }

}
