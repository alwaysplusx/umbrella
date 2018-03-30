package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.convert.converter.Converter;

/**
 * Zip压缩、解压工具类.
 * 
 * @author wuxii@foxmail.com
 */
public class Zip {

    /**
     * 压缩文件或者目录
     * 
     * @param dirOrFile
     *            文件或者目录
     * @param output
     *            输出文件
     * @throws IOException
     */
    public static void zip(String dirOrFile, String output) throws IOException {
        zip(new File(dirOrFile), new File(output));
    }

    public static void zip(File dirOrFile, File output) throws IOException {
        if (!dirOrFile.exists()) {
            throw new IOException("source " + dirOrFile.getPath() + " not exists");
        }
        if (!FileUtils.createFile(output)) {
            throw new IOException("output " + output.getPath() + " create failed");
        }
        ZipBuilder builder = newBuilder();
        if (dirOrFile.isDirectory()) {
            builder.addSourceDirectory(dirOrFile);
        } else {
            builder.addSourceFile(dirOrFile);
        }
        builder.setAutoClose(true)//
                .zip(new FileOutputStream(output));
    }

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
        private boolean autoClose = true;

        private Map<String, SourceZip> sources = new HashMap();

        public ZipBuilder setReplaceable(boolean replaceable) {
            this.replaceable = replaceable;
            return this;
        }

        public ZipBuilder setAutoClose(boolean autoClose) {
            this.autoClose = autoClose;
            return this;
        }

        public DirZipConfigBuilder addSourceDirectory(File dir) {
            checkSource(dir, true);
            String path = qualifiedPath(dir);
            DirZipConfigBuilder builder = (DirZipConfigBuilder) sources.get(path);
            if (builder == null) {
                builder = new DirZipConfigBuilder(dir);
                sources.put(path, builder);
            }
            return builder;
        }

        public DirZipConfigBuilder addSourceDirectory(String directory) {
            return addSourceDirectory(new File(directory));
        }

        public FileZipConfigBuilder addSourceFile(String file) {
            return addSourceFile(new File(file));
        }

        public FileZipConfigBuilder addSourceFile(File file) {
            checkSource(file, false);
            String path = qualifiedPath(file);
            FileZipConfigBuilder builder = (FileZipConfigBuilder) sources.get(path);
            if (builder == null) {
                builder = new FileZipConfigBuilder(file);
                sources.put(path, builder);
            }
            return builder;
        }

        protected void checkSource(File source, boolean dir) {
            if (source == null) {
                throw new IllegalArgumentException("source must not null");
            }
            if (!source.exists()) {
                throw new IllegalArgumentException("source is not exists");
            }
            if (dir && !source.isDirectory()) {
                throw new IllegalArgumentException(source.getPath() + " is not directory");
            }

            String path = qualifiedPath(source);
            List<String> willRemoved = new ArrayList<>();

            for (String s : sources.keySet()) {
                if (s.equals(path)) {
                    return;
                }
                if (path.startsWith(s)) {
                    throw new IllegalArgumentException(source.getPath() + " is already under " + s);
                }
                if (s.startsWith(path) && !replaceable) {
                    throw new IllegalArgumentException(source.getPath() + " is parent source of " + s);
                }
                if (path.startsWith(s)) {
                    willRemoved.add(s);
                }
            }

            if (!willRemoved.isEmpty()) {
                for (String key : willRemoved) {
                    sources.remove(key);
                }
            }

        }

        public File zip(String name) throws IOException {
            File dest = new File(name);
            zip(dest);
            return dest;
        }

        public void zip(File file) throws IOException {
            if (!FileUtils.createFile(file)) {
                throw new IOException(file + " file not exists");
            }
            FileOutputStream fos = new FileOutputStream(file);
            zip(fos, true);
        }

        public void zip(OutputStream os) throws IOException {
            zip(os, autoClose);
        }

        protected void zip(OutputStream os, boolean autoClose) throws IOException {
            ZipOutputStream zos = os instanceof ZipOutputStream ? (ZipOutputStream) os : new ZipOutputStream(os);
            for (SourceZip source : sources.values()) {
                source.zip(zos);
            }
            if (autoClose) {
                zos.close();
            }
        }

        public File gzip(String name) throws IOException {
            File dest = new File(name);
            gzip(dest);
            return dest;
        }

        public void gzip(File file) throws IOException {
            if (!FileUtils.createFile(file)) {
                throw new IOException(file + " file not exists");
            }
            FileOutputStream fos = new FileOutputStream(file);
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

        protected abstract class SourceZip<T extends SourceZip<T>> {
            /**
             * 文件与zipEntry只间的转换器
             */
            protected Converter<File, ZipEntry> fileEntryConverter;

            protected abstract void zip(ZipOutputStream os) throws IOException;

            public T setFileEntryConverter(Converter<File, ZipEntry> fileEntryConverter) {
                this.fileEntryConverter = fileEntryConverter;
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
                        }
                    }
                }
            }

            /**
             * 将文件转为zipEntry, 如果fileEntryConverter为空则返回null
             * 
             * @param file
             *            文件
             * @return zip entry
             */
            protected ZipEntry convert(File file) {
                return fileEntryConverter == null ? null : fileEntryConverter.convert(file);
            }

            public final ZipBuilder up() {
                return ZipBuilder.this;
            }

        }

        public class FileZipConfigBuilder extends SourceZip {

            private File file;
            /**
             * 放置在压缩文件中的目录前缀
             */
            private String entryDir;

            protected FileZipConfigBuilder(File file) {
                this.file = file;
            }

            /**
             * 放置在压缩文件中的目录前缀
             * 
             * @param entryDir
             *            压缩文件中的文件前缀
             * @return this builder
             */
            public FileZipConfigBuilder setZipEntryDirectory(String entryDir) {
                entryDir = new File(entryDir).getPath();
                if (entryDir.startsWith(File.separator)) {
                    entryDir = entryDir.substring(1);
                }
                this.entryDir = entryDir;
                return this;
            }

            @Override
            protected void zip(ZipOutputStream os) throws IOException {
                zipFile(toZipEntry(file), file, os);
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
                return entryDir == null ? entryName : entryDir + File.separator + entryName;
            }
        }

        public class DirZipConfigBuilder extends SourceZip {

            /**
             * 自定义的文件过滤器
             */
            private List<FileFilter> filters;

            /**
             * 路径过滤器(可以设置需要排除或者include的文件或者目录)
             */
            private PatternResourceFilter<String> resourceFilter;

            /**
             * 压缩的目录
             */
            private File dir;
            /**
             * 是否包含当前路径在压缩文件中
             */
            private boolean includeRoot = false;
            /**
             * 放置在压缩文件中的目录前缀
             */
            private String entryDir;
            /**
             * 当前文件所在的基础目录, 转化为entry名称时需要将此部分去除
             */
            private String willRemoved;

            protected DirZipConfigBuilder(File dir) {
                this.dir = dir;
            }

            public DirZipConfigBuilder addFileFilter(FileFilter filter) {
                if (filters == null) {
                    this.filters = new ArrayList<>();
                }
                this.filters.add(filter);
                return this;
            }

            public DirZipConfigBuilder setFileFilter(List<FileFilter> filters) {
                this.filters = filters;
                return this;
            }

            public DirZipConfigBuilder setIncludeRoot(boolean include) {
                this.includeRoot = include;
                return this;
            }

            public DirZipConfigBuilder addExcludes(String... excludes) {
                this.getResourceFilter().addExcludes(excludes);
                return this;
            }

            public DirZipConfigBuilder setExcludes(Set<String> excludes) {
                this.getResourceFilter().setExcludes(excludes);
                return this;
            }

            public DirZipConfigBuilder addIncludes(String... includes) {
                this.getResourceFilter().addIncludes(includes);
                return this;
            }

            public DirZipConfigBuilder setIncludes(Set<String> includes) {
                this.getResourceFilter().setIncludes(includes);
                return this;
            }

            /**
             * 放置在压缩文件中的目录前缀
             * 
             * @param entryDir
             *            压缩文件中的文件前缀
             * @return this builder
             */
            public DirZipConfigBuilder setZipEntryDirectory(String entryDir) {
                entryDir = new File(entryDir).getPath();
                if (entryDir.startsWith(File.separator)) {
                    entryDir = entryDir.substring(1);
                }
                // if (entryDir.endsWith(File.separator)) {
                // entryDir = entryDir.substring(0, entryDir.length() - 1);
                // }
                this.entryDir = entryDir;
                return this;
            }

            @Override
            protected void zip(ZipOutputStream os) throws IOException {
                zip(dir, os);
            }

            private final void zip(File file, ZipOutputStream os) throws IOException {
                if (!accept(file)) {
                    return;
                }
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        zip(f, os);
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
                if (willRemoved == null) {
                    willRemoved = !includeRoot || dir.getParentFile() == null //
                            ? dir.getPath() //
                            : dir.getParentFile().getPath();
                }
                String entryName = file.getPath().substring(willRemoved.length());
                if (entryName.startsWith(File.separator)) {
                    entryName = entryName.substring(1);
                }
                return entryDir == null ? entryName : entryDir + File.separator + entryName;
            }

            private boolean accept(File file) {

                // filters具有一票否决权, filters == null = accept
                if (filters != null) {
                    for (FileFilter filter : filters) {
                        if (!filter.accept(file)) {
                            return false;
                        }
                    }
                }

                // include == null, exclude != null: 纯exclude模式
                // include == null, exclude == null: 全量接收模式
                // include != null, exclude == null: 纯include模式
                // include != null, exclude != null: 取并集
                if (resourceFilter != null) {
                    // 构建include/exclude计算所需要的路径
                    String path = file.getPath().substring(dir.getPath().length());
                    if (path.startsWith(File.separator)) {
                        path = path.substring(1);
                    }
                    return StringUtils.isBlank(path) || resourceFilter.accept(path);
                }

                return true;
            }

            private PatternResourceFilter<String> getResourceFilter() {
                if (resourceFilter == null) {
                    this.resourceFilter = new PatternResourceFilter<>();
                }
                return resourceFilter;
            }

        }
    }

}
