package com.harmony.umbrella.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩、解压工具类.
 *
 * @author Hal
 * @version 1.0
 * @date 2016-3-28 上午10:30:52
 */
public class ZipUtils {

    // FIXME 实现方法
    /**
     * 将文件或目录压缩成二进制字节数组
     * 
     * @param src
     *            文件或文件目录
     * @return 压缩后的字节数组
     * @throws IOException
     *             文件未找到
     */
    public static byte[] zip(String src) throws IOException {
        return null;
    }

    /**
     * 将文件或目录压缩成二进制字节数组
     * 
     * @param src
     *            文件或文件目录
     * @param filter
     *            文件或目录过滤选项
     * @return 压缩后的字节数组
     * @throws IOException
     *             文件未找到
     */
    public static byte[] zip(String src, FileFilter filter) throws IOException {
        return null;
    }

    public static byte[] zip(File src) throws IOException {
        return null;
    }

    public static byte[] zip(File src, FileFilter filter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        // TODO zip
        zos.close();
        return baos.toByteArray();
    }

    /**
     * 将指定文件或目录打成zip包
     * 
     * @param src
     *            文件或目录
     * @param dest
     *            zip的目标文件放置地方
     * @throws IOException
     */
    public static void zip(String src, String dest) throws IOException {
    }

    public static void zip(String src, String dest, FileFilter filter) throws IOException {
    }

    public static void zip(File file, File destFile) throws IOException {
    }

    public static void zip(File file, File destFile, FileFilter filter) throws IOException {

    }

}
