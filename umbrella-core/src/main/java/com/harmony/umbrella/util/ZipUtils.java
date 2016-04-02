package com.harmony.umbrella.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩、解压工具类.
 */
public class ZipUtils {

    // do dest

    public static void zip(String src, String dest) throws IOException {
        zip(new File(src), new File(dest));
    }

    public static void zip(String src, FileFilter filter, String dest) throws IOException {
        zip(new File(src), filter, new File(dest));
    }

    public static void zip(String[] src, FileFilter filter, String dest) throws IOException {
        zip(toFiles(src), filter, new File(dest));
    }

    public static void zip(File file, File dest) throws IOException {
        zipToDest(new File[] { file }, null, dest);
    }

    public static void zip(File file, FileFilter filter, File dest) throws IOException {
        zipToDest(new File[] { file }, filter, dest);
    }

    public static void zip(File[] files, FileFilter filter, File dest) throws IOException {
        zipToDest(files, filter, dest);
    }

    private static void zipToDest(File[] files, FileFilter filter, File dest) throws IOException {
        ZipOutputStream zos = toZipOutputStream(new FileOutputStream(dest));
        zipToOutStream(files, filter, zos);
        zos.close();
    }

    // to byte

    public static byte[] zip(String... pathnames) throws IOException {
        return zip(toFiles(pathnames));
    }

    public static byte[] zip(String[] pathnames, FileFilter filter) throws IOException {
        return zip(toFiles(pathnames), filter);
    }

    public static byte[] zip(File... files) throws IOException {
        return zipToByte(files, null);
    }

    public static byte[] zip(File[] files, FileFilter filter) throws IOException {
        return zipToByte(files, filter);
    }

    private static byte[] zipToByte(File[] files, FileFilter filter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = toZipOutputStream(baos);
        zipToOutStream(files, filter, zos);
        return baos.toByteArray();
    }

    // zip to outstream
    public static void zip(String pathname, OutputStream os) throws IOException {
        zip(new File(pathname), os);
    }

    public static void zip(String[] pathnames, OutputStream os) throws IOException {
        zip(toFiles(pathnames), os);
    }

    public static void zip(String pathname, FileFilter filter, OutputStream os) throws IOException {
        zip(new File(pathname), filter, os);
    }

    public static void zip(String[] pathnames, FileFilter filter, OutputStream os) throws IOException {
        zip(toFiles(pathnames), filter, os);
    }

    public static void zip(File file, OutputStream os) throws IOException {
        zipToOutStream(new File[] { file }, null, toZipOutputStream(os));
    }

    public static void zip(File[] files, OutputStream os) throws IOException {
        zipToOutStream(files, null, toZipOutputStream(os));
    }

    public static void zip(File file, FileFilter filter, OutputStream os) throws IOException {
        zipToOutStream(new File[] { file }, filter, toZipOutputStream(os));
    }

    public static void zip(File[] files, FileFilter filter, OutputStream os) throws IOException {
        zipToOutStream(files, filter, toZipOutputStream(os));
    }

    private static void zipToOutStream(File[] files, FileFilter filter, ZipOutputStream zos) throws IOException {
        for (File file : files) {
            if (file.isDirectory()) {
                zipToOutStream(file.listFiles(), filter, zos);
            } else if (filter == null || filter.accept(file)) {
                zipSingleFile(file, zos);
            }
        }
    }

    //

    private static File[] toFiles(String... pathnames) {
        File[] files = new File[pathnames.length];
        for (int i = 0; i < pathnames.length; i++) {
            files[i] = new File(pathnames[i]);
        }
        return files;
    }

    private static ZipOutputStream toZipOutputStream(OutputStream os) {
        return os instanceof ZipOutputStream ? (ZipOutputStream) os : new ZipOutputStream(os);
    }

    private static void zipSingleFile(File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        zip(file.getPath(), fis, zos);
        fis.close();
    }

    private static void zip(String entryName, InputStream is, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry(entryName));
        IOUtils.copy(is, zos);
    }

}
