package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件工具
 * 
 * @author wuxii@foxmail.com
 */
public class FileUtils {

    /**
     * 修改文件名称，文件目录不变
     * 
     * @param file
     *            源文件
     * @param newName
     *            新文件名
     * @return 新文件
     */
    public static File rename(File file, String newName) throws IOException {
        File newFile = new File(file.getParent(), newName);
        createDirectory(newFile.getParent());
        if (!file.renameTo(newFile)) {
            throw new IOException("file can't rename to " + newName);
        }
        return newFile;
    }

    /**
     * 判断文件(夹)是否存在
     *
     * @param pathname
     *            文件路径
     * @return Boolean
     */
    public static boolean exists(String pathname) {
        return new File(pathname).exists();
    }

    /**
     * 判断文件是否存在并且是非目录文件
     * 
     * @param pathname
     *            文件路径
     * @return
     */
    public static boolean isFile(String pathname) {
        return new File(pathname).isFile();
    }

    /**
     * 判断文件是否存在并且是目录
     * 
     * @param pathname
     *            文件路径
     * @return
     */
    public static boolean isDirectory(String pathname) {
        return new File(pathname).isDirectory();
    }

    /**
     * 获取文件扩展名, 如果是文件夹/文件没有扩展名则返回null.
     * <p>
     * 返回的扩展名均为小写
     * 
     * @param pathname
     *            文件路径
     * @return 扩展名
     */
    public static String getExtension(String pathname) {
        return getExtension(new File(pathname));
    }

    /**
     * 获取文件扩展名, 如果是文件夹/文件没有扩展名则返回null.
     * <p>
     * 返回的扩展名均为小写
     * 
     * @param fiel
     *            文件
     * @return 扩展名
     */
    public static String getExtension(File file) {
        if (file.isDirectory()) {
            return null;
        }
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        return dotIndex > 0 ? name.substring(dotIndex).toLowerCase() : null;
    }

    /**
     * 创建文件, 如果文件为深层次目录下的文件则根据输入的cascade标志判断是否级联创建
     *
     * @param pathname
     *            待创建的文件或文件夹
     * @throws IOException
     */
    public static File createFile(String pathname) throws IOException {
        File file = new File(pathname);
        createFile(file);
        return file;
    }

    /**
     * 创建文件
     * 
     * @param file
     *            待创建的文件
     * @throws IOException
     */
    public static void createFile(File file) throws IOException {
        file = new File(file.getAbsolutePath());
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile.exists() && parentFile.isFile()) {
                throw new IOException(parentFile.getAbsolutePath() + "file parent not directory");
            }
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * 创建目录
     * 
     * @param pathname
     *            目录路径
     * @throws IOException
     * @see {@linkplain File#mkdirs()}
     */
    public static File createDirectory(String pathname) throws IOException {
        File dir = new File(pathname);
        createDirectory(dir);
        return dir;
    }

    /**
     * 创建目录
     * 
     * @param file
     *            目录路径
     * @throws IOException
     * @see {@linkplain File#mkdirs()}
     */
    public static void createDirectory(File file) throws IOException {
        if (file.exists() && !file.isDirectory()) {
            throw new IOException(file.getAbsolutePath() + " already exists and not a directory");
        }
        file.mkdirs();
    }

    /**
     * 通过uuid命名文件加在临时目录中创建一个临时文件夹
     * 
     * @return 临时目录下的临时文件夹
     * @throws IOException
     */

    public static File createTempDirectory() throws IOException {
        return createTempDirectory(UUID.randomUUID().toString());
    }

    /**
     * 通过指定名称在临时目录下创建临时目录
     * 
     * @param pathname
     *            目录
     * @return 临时目录
     * @throws IOException
     */
    public static File createTempDirectory(String pathname) throws IOException {
        File dir = new File(System.getProperty("java.io.tmpdir"), pathname);
        if (dir.exists()) {
            throw new IOException("temp directory " + dir.getAbsolutePath() + " already exists");
        }
        createDirectory(dir);
        return dir;
    }

    /**
     * 在指定的临时目录下创建临时文件
     * 
     * @param directory
     *            临时目录
     * @return 临时文件
     * @throws IOException
     */
    public static File createTempFile(String directory) throws IOException {
        return createTempFile("", ".tmp", createTempDirectory(directory), false);
    }

    /**
     * 创建临时文件
     * 
     * @param prefix
     *            临时文件前缀
     * @param directory
     *            临时目录下的目录
     * @return 临时文件
     * @throws IOException
     */
    public static File createTempFile(String prefix, File directory) throws IOException {
        return createTempFile(prefix, ".tmp", directory, false);
    }

    /**
     * 创建临时文件
     * 
     * @param prefix
     *            临时文件前缀
     * @param suffix
     *            临时文件后缀
     * @param directory
     *            临时目录下的目录
     * @param deleteOnExit
     *            是否jvm退出时候删除
     * @return 临时文件
     * @throws IOException
     */
    public static File createTempFile(String prefix, String suffix, File directory, boolean deleteOnExit) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix, directory);
        if (deleteOnExit) {
            tempFile.deleteOnExit();
        }
        return tempFile;
    }

    /**
     * 删除文件
     * 
     * @param pathname
     *            文件路径
     * @return true 删除成功
     */
    public static boolean deleteFile(String pathname) {
        return deleteFile(pathname, false);
    }

    /**
     * 删除文件, 如果文件路径是目录格局输入的cascade判断是否级联删除目录下的所有文件,否则不删除返回false
     * 
     * @param pathname
     *            文件路径
     * @param cascade
     *            是否级联删除
     * @see #deleteFile(File, boolean)
     * @return true 删除成功
     */
    public static boolean deleteFile(String pathname, boolean cascade) {
        return deleteFile(new File(pathname), cascade);
    }

    /**
     * 删除文件
     * 
     * @param file
     *            文件
     * @return true 删除成功
     */
    public static boolean deleteFile(File file) {
        return deleteFile(file, false);
    }

    /**
     * 删除文件, 如果是文件夹根据级联标识判断是否删除文件夹
     *
     * @param file
     *            待删除的文件或文件夹
     * @param cascade
     *            是否允许删除目录
     */
    public static boolean deleteFile(File file, boolean cascade) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        return cascade && _delete(file);
    }

    /**
     * 级联删除文件
     */
    private static boolean _delete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!_delete(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static byte[] read(File file) throws IOException {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            return IOUtils.toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
