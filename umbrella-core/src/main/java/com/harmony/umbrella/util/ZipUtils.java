package com.harmony.umbrella.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩、解压工具类.
 */
public class ZipUtils {

    // zip to byte

    /**
     * @see #zip(File...)
     */
    public static byte[] zip(String... pathnames) throws IOException {
        return zip(toFiles(pathnames));
    }

    /**
     * @see #zip(File[], FileFilter)
     */
    public static byte[] zip(String[] pathnames, FileFilter filter) throws IOException {
        return zip(toFiles(pathnames), filter);
    }

    /**
     * 将文件压缩为字节数组
     * 
     * @param files
     *            文件
     * @return 压缩有的字节数组
     * @throws IOException
     *             文件不存在
     */
    public static byte[] zip(File... files) throws IOException {
        return zipToByte(files, null);
    }

    /**
     * 带文件过滤功能的将文件压缩为字节数组
     * 
     * @param files
     *            待压缩的文件
     * @param filter
     *            文件过滤选项
     * @return 压缩后的字节数组
     * @throws IOException
     *             文件不存在
     */
    public static byte[] zip(File[] files, FileFilter filter) throws IOException {
        return zipToByte(files, filter);
    }

    private static byte[] zipToByte(File[] files, FileFilter filter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = toZipOutputStream(baos);
        zipToOutStream(files, zos, filter);
        return baos.toByteArray();
    }

    // zip to dest

    /**
     * 将文件或目录写入到目的地
     * 
     * @param src
     *            文件或目录
     * @param dest
     *            zip文件的目的地
     * @throws IOException
     *             如果文件不存在, 或目标地不可写入
     */
    public static void zip(String src, String dest) throws IOException {
        zip(new File(src), new File(dest));
    }

    /**
     * 带文件过滤功能的文件或目录写入目的地
     * 
     * @param src
     *            文件或目录
     * @param filter
     *            文件过滤功能
     * @param dest
     *            写入目标
     * @throws IOException
     *             源文件不存在, 或目标地不可写入
     */
    public static void zip(String src, String dest, FileFilter filter) throws IOException {
        zip(new File(src), new File(dest), filter);
    }

    /**
     * 带文件过滤功能的文件或目录写入目的地
     * 
     * @param src
     *            文件或目录
     * @param filter
     *            文件过滤功能
     * @param dest
     *            写入目标
     * @throws IOException
     *             源文件不存在, 或目标地不可写入
     */
    public static void zip(String[] src, String dest, FileFilter filter) throws IOException {
        zip(toFiles(src), new File(dest), filter);
    }

    /**
     * 将文件或目录写入到目的地
     * 
     * @param file
     *            源文件
     * @param dest
     *            写入的目标
     * @throws IOException
     *             源文件不存在, 或目标地不可写入
     */
    public static void zip(File file, File dest) throws IOException {
        zipToDest(new File[] { file }, dest, null);
    }

    /**
     * 带文件过滤功能的将文件写入目标
     * 
     * @param file
     *            源文件
     * @param filter
     *            文件过滤选项
     * @param dest
     *            目标
     * @throws IOException
     *             源文件不存在, 或目标地不可写入
     */
    public static void zip(File file, File dest, FileFilter filter) throws IOException {
        zipToDest(new File[] { file }, dest, filter);
    }

    /**
     * 带文件过滤功能将文件写入目标地
     * 
     * @param files
     *            源文件
     * @param filter
     *            文件过滤选项
     * @param dest
     *            写入目标
     * @throws IOException
     *             源文件不存在, 或目标地不可写入
     */
    public static void zip(File[] files, File dest, FileFilter filter) throws IOException {
        zipToDest(files, dest, filter);
    }

    private static void zipToDest(File[] files, File dest, FileFilter filter) throws IOException {
        ZipOutputStream zos = toZipOutputStream(new FileOutputStream(dest));
        zipToOutStream(files, zos, filter);
        zos.close();
    }

    // zip to outstream
    /**
     * @see #zip(File, OutputStream)
     */
    public static void zip(String pathname, OutputStream os) throws IOException {
        zip(new File(pathname), os);
    }

    /**
     * @see #zip(File[], OutputStream)
     */
    public static void zip(String[] pathnames, OutputStream os) throws IOException {
        zip(toFiles(pathnames), os);
    }

    /**
     * @see #zip(File, FileFilter, OutputStream)
     */
    public static void zip(String pathname, OutputStream os, FileFilter filter) throws IOException {
        zip(new File(pathname), os, filter);
    }

    /**
     * @see #zip(File[], FileFilter, OutputStream)
     */
    public static void zip(String[] pathnames, OutputStream os, FileFilter filter) throws IOException {
        zip(toFiles(pathnames), os, filter);
    }

    /**
     * 将文件压缩后写入输出流
     * 
     * @param file
     *            压缩的文件
     * @param os
     *            输出目的地
     * @throws IOException
     *             文件不存在, 输出流已经关闭
     */
    public static void zip(File file, OutputStream os) throws IOException {
        zipToOutStream(new File[] { file }, toZipOutputStream(os), null);
    }

    /**
     * 将文件压缩后写入输出流
     * 
     * @param files
     *            压缩的文件
     * @param os
     *            输出目的地
     * @throws IOException
     *             文件不存在, 输出流已经关闭
     */
    public static void zip(File[] files, OutputStream os) throws IOException {
        zipToOutStream(files, toZipOutputStream(os), null);
    }

    /**
     * 带文件过滤功能将文件压缩后写入输出流
     * 
     * @param file
     *            压缩的文件
     * @param filter
     *            文件过滤选项
     * @param os
     *            输出目的地
     * @throws IOException
     *             文件不存在, 输出流已经关闭
     */
    public static void zip(File file, OutputStream os, FileFilter filter) throws IOException {
        zipToOutStream(new File[] { file }, toZipOutputStream(os), filter);
    }

    /**
     * 带文件过滤功能将文件压缩后写入输出流
     * 
     * @param file
     *            压缩的文件
     * @param filter
     *            文件过滤选项
     * @param os
     *            输出目的地
     * @throws IOException
     *             文件不存在, 输出流已经关闭
     */
    public static void zip(File[] files, OutputStream os, FileFilter filter) throws IOException {
        zipToOutStream(files, toZipOutputStream(os), filter);
    }

    private static void zipToOutStream(File[] files, ZipOutputStream zos, FileFilter filter) throws IOException {
        for (File file : files) {
            if (file.isDirectory()) {
                zipToOutStream(file.listFiles(), zos, filter);
            } else if (filter == null || filter.accept(file)) {
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getPath()));
                IOUtils.copy(fis, zos);
                fis.close();
            }
        }
    }

    // unzip to dest
    public static void unzip(String pathname, String dest) throws IOException {
        unzipToFile(new File(pathname), new File(dest), null);
    }

    public static void unzip(String pathname, String dest, ZipEntryFilter filter) throws IOException {
        unzipToFile(new File(pathname), new File(dest), filter);
    }

    public static void unzip(File file, File dest) throws IOException {
        unzipToFile(file, dest, null);
    }

    public static void unzip(File file, File dest, ZipEntryFilter filter) throws IOException {
        unzipToFile(file, dest, filter);
    }

    private static void unzipToFile(File file, final File dest, ZipEntryFilter filter) throws IOException {
        if (!dest.exists()) {
            FileUtils.createDirectory(dest);
        } else if (dest.isFile()) {
            throw new IOException("unzip destination is not directory");
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        final String basePath = dest.getPath();
        unzipToOutStream(zis, filter, new Converter<ZipEntry, FileOutputStream>() {
            @Override
            public FileOutputStream convert(ZipEntry t) {
                try {
                    return new FileOutputStream(createFile(basePath, t.getName()));
                } catch (Exception e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
                throw new RuntimeException("impossible convert exception");
            }

        });
        zis.close();
    }

    // unzip to byte

    public static ByteArrayOutputStream[] unzip(String pathname) throws IOException {
        return unzipToByte(new File(pathname), null);
    }

    public static ByteArrayOutputStream[] unzip(String pathname, ZipEntryFilter filter) throws IOException {
        return unzipToByte(new File(pathname), filter);
    }

    public static ByteArrayOutputStream[] unzip(File file) throws IOException {
        return unzipToByte(file, null);
    }

    public static ByteArrayOutputStream[] unzip(File file, ZipEntryFilter filter) throws IOException {
        return unzipToByte(file, filter);
    }

    private static ByteArrayOutputStream[] unzipToByte(File file, ZipEntryFilter filter) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        List<ByteArrayOutputStream> oss = unzipToOutStream(zis, filter, new Converter<ZipEntry, ByteArrayOutputStream>() {
            @Override
            public ByteArrayOutputStream convert(ZipEntry t) {
                return new ByteArrayOutputStream();
            }
        });
        zis.close();
        return oss.toArray(new ByteArrayOutputStream[oss.size()]);
    }

    private static <T extends OutputStream> List<T> unzipToOutStream(ZipInputStream zis, ZipEntryFilter filter, Converter<ZipEntry, T> etos) throws IOException {
        List<T> oss = new ArrayList<T>();
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            if (!entry.isDirectory() && (filter == null || filter.accept(entry))) {
                T os = etos.convert(entry);
                IOUtils.copy(zis, os);
                os.close();
                oss.add(os);
            }
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
        return oss;
    }

    private static ZipOutputStream toZipOutputStream(OutputStream os) {
        return os instanceof ZipOutputStream ? (ZipOutputStream) os : new ZipOutputStream(os);
    }

    private static File[] toFiles(String... pathnames) {
        File[] files = new File[pathnames.length];
        for (int i = 0; i < pathnames.length; i++) {
            files[i] = new File(pathnames[i]);
        }
        return files;
    }

    private static File createFile(String basePath, String name) throws IOException {
        String fileName = basePath + File.separator + name;
        /*if ((basePath.endsWith(File.separator) && !name.startsWith(File.separator)) //
                || (!basePath.endsWith(File.separator) && name.startsWith(File.separator))) {
            fileName = basePath + name;
        } else {

        }*/
        return FileUtils.createFile(fileName, true);
    }

    /**
     * @author wuxii@foxmail.com
     */
    public interface ZipEntryFilter {

        boolean accept(ZipEntry entry);

    }

}
