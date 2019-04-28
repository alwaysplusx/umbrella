package com.harmony.umbrella.util;

import org.springframework.core.convert.converter.Converter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩、解压工具类.
 *
 * @author wuxii@foxmail.com
 */
public class Zip {

    /**
     * 压缩文件或者目录
     *
     * @param dirOrFile 文件或者目录
     * @param output    输出文件
     * @throws IOException
     */
    public static void zip(String dirOrFile, String output) throws IOException {
        zip(new File(dirOrFile), new File(output));
    }

    /**
     * 将目录或文件压缩到置顶文件中
     *
     * @param dirOrFile 待压缩的文件或目录
     * @param output    压缩完后的文件
     * @throws IOException
     */
    public static void zip(File dirOrFile, File output) throws IOException {
        if (!dirOrFile.exists()) {
            throw new IOException("source " + dirOrFile.getPath() + " not exists");
        }
        if (!FileUtils.createFile(output)) {
            throw new IOException("output " + output.getPath() + " create failed");
        }
        ZipBuilder builder = newBuilder();
        if (dirOrFile.isDirectory()) {
            builder.addDir(dirOrFile);
        } else {
            builder.addFile(dirOrFile);
        }
        builder.setAutoClose(true).zip(new FileOutputStream(output));
    }

    /**
     * 压缩构建器
     *
     * @return
     */
    public static ZipBuilder newBuilder() {
        return new ZipBuilder();
    }

    protected static String qualifiedPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 压缩的配置创建工具
     *
     * @author wuxii@foxmail.com
     */
    public static class ZipBuilder {

        public static ZipBuilder newBuilder() {
            return new ZipBuilder();
        }

        protected ZipBuilder() {
        }

        /**
         * 当添加当前资源的父目录为资源时候自动替换为父资源的配置(原配置无效化)
         */
        private boolean replaceable = true;
        /**
         * TODO 是否在压缩文件中包括空的文件夹
         */
        private boolean includeEmptyDirectory = true;
        private boolean autoClose = true;

        private Map<String, Source> sources = new HashMap<>();

        public ZipBuilder setReplaceable(boolean replaceable) {
            this.replaceable = replaceable;
            return this;
        }

        public ZipBuilder setAutoClose(boolean autoClose) {
            this.autoClose = autoClose;
            return this;
        }

        public ZipBuilder setIncludeEmptyDirectory(boolean includeEmptyDirectory) {
            this.includeEmptyDirectory = includeEmptyDirectory;
            return this;
        }

        public DirZipConfigBuilder addDir(String directory) {
            return addDir(new File(directory));
        }

        public FileZipConfigBuilder addFile(String file) {
            return addFile(new File(file));
        }

        public DirZipConfigBuilder addDir(File dir) {
            String qualifiedPath = qualifiedPath(dir);
            checkAndRemoveChildren(qualifiedPath, dir, true);
            return (DirZipConfigBuilder) sources.computeIfAbsent(qualifiedPath, k -> new DirZipConfigBuilder(dir));
        }

        public FileZipConfigBuilder addFile(File file) {
            String qualifiedPath = qualifiedPath(file);
            checkAndRemoveChildren(qualifiedPath, file, false);
            return (FileZipConfigBuilder) sources.computeIfAbsent(qualifiedPath, k -> new FileZipConfigBuilder(file));
        }

        protected void checkAndRemoveChildren(String qualifiedPath, File source, boolean dir) {
            if (source == null) {
                throw new IllegalArgumentException("source must not null");
            }
            if (!source.exists()) {
                throw new IllegalArgumentException(source.getPath() + " source is not exists");
            }
            if (dir && !source.isDirectory()) {
                throw new IllegalArgumentException(source.getPath() + " is not directory");
            }
            if (!dir && !source.isFile()) {
                throw new IllegalArgumentException(source.getPath() + " is not file");
            }
            List<String> willRemoved = new ArrayList<>();
            for (String s : sources.keySet()) {
                if (s.equals(qualifiedPath)) {
                    return;
                }
                if (qualifiedPath.startsWith(s)) {
                    throw new IllegalArgumentException(source.getPath() + " is already under " + s);
                }
                if (s.startsWith(qualifiedPath)) {
                    if (!replaceable) {
                        throw new IllegalArgumentException(source.getPath() + " is parent source of " + s);
                    } else {
                        willRemoved.add(s);
                    }
                }
            }
            if (!willRemoved.isEmpty()) {
                willRemoved.forEach(sources::remove);
            }
        }

        public File zip(String output) throws IOException {
            File dest = new File(output);
            zip(dest);
            return dest;
        }

        public void zip(File output) throws IOException {
            if (!FileUtils.createFile(output)) {
                throw new IOException(output + " file not exists");
            }
            FileOutputStream fos = new FileOutputStream(output);
            zip(fos, true);
        }

        public void zip(OutputStream os) throws IOException {
            zip(os, autoClose);
        }

        protected void zip(OutputStream os, boolean autoClose) throws IOException {
            ZipOutputStream zos = os instanceof ZipOutputStream ? (ZipOutputStream) os : new ZipOutputStream(os);
            for (Source source : sources.values()) {
                source.zip(zos);
            }
            if (autoClose) {
                zos.close();
            }
        }

        public File gzip(String output) throws IOException {
            File dest = new File(output);
            gzip(dest);
            return dest;
        }

        public void gzip(File output) throws IOException {
            if (!FileUtils.createFile(output)) {
                throw new IOException(output + " file not exists");
            }
            FileOutputStream fos = new FileOutputStream(output);
            gzip(fos, true);
        }

        public void gzip(OutputStream os) throws IOException {
            gzip(os, autoClose);
        }

        protected void gzip(OutputStream os, boolean autoClose) throws IOException {
            File file = FileUtils.createTmpFile();
            zip(new FileOutputStream(file));
            FileInputStream fis = null;
            try {
                GZIPOutputStream gos = os instanceof GZIPOutputStream ? (GZIPOutputStream) os : new GZIPOutputStream(os);
                fis = new FileInputStream(file);
                IOUtils.copy(fis, gos);
                if (autoClose) {
                    gos.close();
                }
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }

        protected abstract class Source<T extends Source<T>> {
            /**
             * 文件与zipEntry只间的转换器
             */
            protected Converter<File, ZipEntry> converter;

            /**
             * 输出目录前缀
             */
            protected String destPrefix;

            protected abstract void zip(ZipOutputStream os) throws IOException;

            public T setConverter(Converter<File, ZipEntry> converter) {
                this.converter = converter;
                return (T) this;
            }

            public T setDestPrefix(String prefix) {
                this.destPrefix = prefix;
                return (T) this;
            }

            protected void zipFile(ZipEntry entry, File file, ZipOutputStream zos) throws IOException {
                InputStream in = null;
                try {
                    in = new FileInputStream(file);
                    zos.putNextEntry(entry);
                    IOUtils.copy(in, zos);
                    zos.closeEntry();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }

            /**
             * 将文件转为zipEntry, 如果fileEntryConverter为空则返回null
             *
             * @param file 文件
             * @return zip entry
             */
            protected ZipEntry convert(File file) {
                return converter == null ? null : converter.convert(file);
            }

            public final ZipBuilder up() {
                return ZipBuilder.this;
            }

        }

        public class FileZipConfigBuilder extends Source<FileZipConfigBuilder> {

            private File source;

            protected FileZipConfigBuilder(File file) {
                this.source = file;
            }

            @Override
            protected void zip(ZipOutputStream os) throws IOException {
                zipFile(toZipEntry(source), source, os);
            }

            protected ZipEntry toZipEntry(File file) {
                ZipEntry entry = convert(file);
                if (entry == null) {
                    entry = new ZipEntry(toZipEntryName(file));
                }
                return entry;
            }

            protected String toZipEntryName(File file) {
                String entryName = file.getName();
                return destPrefix == null ? entryName : destPrefix + File.separator + entryName;
            }
        }

        public class DirZipConfigBuilder extends Source<DirZipConfigBuilder> {

            /**
             * 自定义的文件过滤器
             */
            private FileFilter filter;

            /**
             * 待压缩的目录
             */
            private File source;
            /**
             * 是否包含当前路径在压缩文件中
             */
            private boolean includeRoot = false;

            protected DirZipConfigBuilder(File source) {
                this.source = source;
            }

            public DirZipConfigBuilder setIncludeRoot(boolean include) {
                this.includeRoot = include;
                return this;
            }

            public DirZipConfigBuilder setFileFilter(FileFilter fileFilter) {
                this.filter = fileFilter;
                return this;
            }

            @Override
            protected void zip(ZipOutputStream os) throws IOException {
                zip(source, os);
            }

            private void zip(File file, ZipOutputStream os) throws IOException {
                if (!accept(file)) {
                    return;
                }
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            zip(f, os);
                        }
                    }
                } else {
                    zipFile(toZipEntry(file), file, os);
                }
            }

            protected ZipEntry toZipEntry(File file) {
                ZipEntry entry = convert(file);
                if (entry == null) {
                    entry = new ZipEntry(toZipEntryName(file));
                }
                return entry;
            }

            protected String toZipEntryName(File file) {
                String willRemoved = !includeRoot || source.getParentFile() == null
                        ? source.getPath()
                        : source.getParentFile().getPath();
                String entryName = file.getPath().substring(willRemoved.length());
                if (entryName.startsWith(File.separator)) {
                    entryName = entryName.substring(1);
                }
                return destPrefix == null ? entryName : destPrefix + File.separator + entryName;
            }

            private boolean accept(File file) {
                return filter == null || filter.accept(file);
            }

        }
    }

}
