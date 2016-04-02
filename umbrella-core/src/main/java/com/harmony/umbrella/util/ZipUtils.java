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

    public static void zip(File file, File dest) {

    }

    public static void zip(File file, FileFilter filter, File dest) {

    }

    public static void zip(File[] files, FileFilter filter, File dest) throws IOException {
        doZip(files, filter, dest);
    }

    private static void doZip(File[] files, FileFilter filter, File dest) throws IOException {
        ZipOutputStream zos = toZipOutputStream(new FileOutputStream(dest));
        doZip(files, filter, zos);
        zos.close();
    }

    public static byte[] zip(File... files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (File file : files) {
            zip(file, baos);
        }
        return baos.toByteArray();
    }

    public static void zip(File file, OutputStream os) throws IOException {
        doZip(new File[] { file }, null, toZipOutputStream(os));
    }

    public static void zip(File[] files, OutputStream os) throws IOException {
        doZip(files, null, toZipOutputStream(os));
    }

    public static void zip(File file, FileFilter filter, OutputStream os) throws IOException {
        doZip(new File[] { file }, filter, toZipOutputStream(os));
    }

    public static void zip(File[] files, FileFilter filter, OutputStream os) throws IOException {
        doZip(files, filter, toZipOutputStream(os));
    }

    private static void doZip(File[] files, FileFilter filter, ZipOutputStream zos) throws IOException {
        for (File file : files) {
            if (file.isDirectory()) {
                doZip(file.listFiles(), filter, zos);
            } else if (filter == null || filter.accept(file)) {
                zipSingleFile(file, zos);
            }
        }
    }

    //
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

    public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream("a.zip");
        ZipUtils.zip(new File("target"), null, fos);
    }

}
