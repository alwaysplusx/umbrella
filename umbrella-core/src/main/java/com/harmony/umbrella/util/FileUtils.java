package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * 文件工具
 * 
 * @author wuxii@foxmail.com
 */
public class FileUtils {

    public static Path toPath(File file) {
        return file.toPath().normalize();
    }

    public static File getDefaultTmpDir() throws IOException {
        String tmpdir = System.getProperty("harmony.io.tmpdir");
        File dir = tmpdir == null ? new File(System.getProperty("java.io.tmpdir"), "harmony") : new File(tmpdir);
        if (!dir.exists() && !createDir(dir)) {
            throw new IOException("tmp dir not found " + dir);
        }
        return dir;
    }

    /**
     * 修改文件名称
     * 
     * @param src
     *            源文件
     * @param to
     *            新文件名
     * @return 新文件
     */
    public static File renameTo(String src, String to, CopyOption... opts) throws IOException {
        return renameTo(new File(src), new File(to), opts);
    }

    /**
     * 修改文件名称
     * 
     * @param src
     *            源文件
     * @param to
     *            新文件名
     * @return 新文件
     */
    public static File renameTo(File src, File to, CopyOption... opts) throws IOException {
        if (!to.exists() && !createFile(to)) {
            throw new IOException("rename failed failed, " + to + " file create failed");
        }
        Files.move(toPath(src), toPath(to), opts);
        return to;
    }

    /**
     * 移动文件到指定目录
     * 
     * @param src
     *            源文件
     * @param to
     *            目标文件夹
     * @return 在目标文件夹中的文件
     * @throws IOException
     */
    public static File moveTo(String src, String to, CopyOption... opts) throws IOException {
        return moveTo(new File(src), new File(to), opts);
    }

    /**
     * 移动文件到指定目录
     * 
     * @param src
     *            源文件
     * @param to
     *            目标文件夹
     * @return 在目标文件夹中的文件
     * @throws IOException
     */
    public static File moveTo(File src, File to, CopyOption... opts) throws IOException {
        if (!to.isDirectory() && !createDir(to)) {
            throw new IOException("move file failed, " + to + " dir create failed");
        }
        File result = new File(to, src.getName());
        Files.move(toPath(src), toPath(result), opts);
        return result;
    }

    /**
     * 拷贝文件到指定目录
     * 
     * @param src
     *            源文件
     * @param to
     *            目标文件夹
     * @param opts
     *            拷贝选项
     * @return 目标文件夹中的文件
     * @throws IOException
     */
    public static File copyTo(String src, String to, CopyOption... opts) throws IOException {
        return copyTo(new File(src), new File(to), opts);
    }

    /**
     * 拷贝文件到指定目录
     * 
     * @param src
     *            源文件
     * @param to
     *            目标文件夹
     * @param opts
     *            拷贝选项
     * @return 目标文件夹中的文件
     * @throws IOException
     */
    public static File copyTo(File src, File to, CopyOption... opts) throws IOException {
        if (!to.isDirectory() && !createDir(to)) {
            throw new IOException("copy file failed, " + to + " dir create failed");
        }
        File result = new File(to, src.getName());
        Files.copy(toPath(src), toPath(result), opts);
        return result;
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
    public static boolean isDir(String pathname) {
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
     * @param file
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
     * 创建文件
     *
     * @param pathname
     *            待创建的文件
     * @throws IOException
     */
    public static File createFile(String pathname) throws IOException {
        File file = new File(pathname);
        if (!createFile(file)) {
            throw new IOException(pathname + " create failed");
        }
        return file;
    }

    /**
     * 创建文件
     * 
     * @param file
     *            待创建的文件
     * @throws IOException
     */
    public static boolean createFile(File file) {
        if (file.isDirectory()) {
            return false;
        }
        try {
            return createParentDir(file) && (file.isFile() || file.createNewFile());
        } catch (IOException e) {
            return false;
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
    public static File createDir(String pathname) throws IOException {
        File dir = new File(pathname);
        if (!createDir(dir)) {
            throw new IOException(pathname + " mkdirs failed");
        }
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
    public static boolean createDir(File file) {
        return file.isDirectory() || file.mkdirs();
    }

    static boolean createParentDir(File file) {
        File parent = getParentFile(file);
        return parent == null || createDir(parent);
    }

    static File getParentFile(File file) {
        File parent = file.getParentFile();
        return parent == null ? file.getAbsoluteFile().getParentFile() : parent;
    }

    static Path getParentPath(File file) {
        Path path = file.toPath();
        Path parent = path.getParent();
        return parent == null ? path.toAbsolutePath().getParent() : parent;
    }

    public static File createTmpFile() throws IOException {
        String prefix = System.getProperty("harmony.tmp.prefix", "harmony_");
        String suffix = System.getProperty("harmony.tmp.suffix", ".tmp");
        return File.createTempFile(prefix, suffix, getDefaultTmpDir());
    }

    /**
     * 创建一个自定义名称的临时文件(此文件通过临时一个上层目录来使得当前的文件名可进行自定义)
     * 
     * @param name
     *            临时文件的名称
     * @return 临时文件
     * @throws IOException
     *             i/o error
     */
    public static File createTmpFile(String name) throws IOException {
        return createTmpDir().getTmpFile(name);
    }

    /**
     * 通过uuid命名文件夹在临时目录中创建一个临时文件夹
     * 
     * @return 临时目录下的临时文件夹
     * @throws IOException
     */
    public static TmpDir createTmpDir() throws IOException {
        return createTmpDir(UUID.randomUUID().toString());
    }

    /**
     * 在临时目录下建立一个特定名称的目录
     * 
     * @param pathname
     *            目录
     * @return 临时目录
     * @throws IOException
     */
    public static TmpDir createTmpDir(String pathname) throws IOException {
        File dir = new File(getDefaultTmpDir(), pathname);
        if (!createDir(dir)) {
            throw new IOException(pathname + " tmp dir create failed");
        }
        return new TmpDir(dir);
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

    /**
     * 临时目录
     * 
     * @author wuxii@foxmail.com
     */
    public static class TmpDir implements Serializable {

        private static final long serialVersionUID = 3757424787193302679L;

        private final File dir;

        private TmpDir(File dir) {
            this.dir = dir;
        }

        public boolean exists(String pathname) {
            return getTmpFile(pathname).exists();
        }

        public File getTmpFile(String pathname) {
            return new File(dir, pathname);
        }

        public File getTmpDir() {
            return dir;
        }

    }

}
