package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 */
public class FileUtils {

    public static final String UTF_8 = "UTF-8";

    public static final String UNIX_NEW_LINE = IOUtils.LINE_SEPARATOR_UNIX;

    public static final String WINDOWS_NEW_LINE = IOUtils.LINE_SEPARATOR_WINDOWS;

    public static final String NEW_LINE;

    static {
        if (Environments.isWindows()) {
            NEW_LINE = WINDOWS_NEW_LINE;
        } else {
            NEW_LINE = UNIX_NEW_LINE;
        }
    }

    /**
     * 修改文件名称，文件目录不变
     * 
     * @param file
     *            源文件
     * @param newName
     *            新文件名
     * @return 新文件
     */
    public static File rename(File file, String pathname) throws IOException {
        File newFile = new File(file.getParent(), pathname);
        createDirectory(newFile.getParent());
        if (!file.renameTo(newFile)) {
            throw new IOException("file can't rename to " + pathname);
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
     * 获取文件扩展名, 如果是文件夹/文件没有扩展名则返回空字符文本.
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
     * 获取文件扩展名, 如果是文件夹/文件没有扩展名则返回空字符文本.
     * <p>
     * 返回的扩展名均为小写
     * 
     * @param fiel
     *            文件
     * @return 扩展名
     */
    public static String getExtension(File file) {
        if (file.isDirectory()) {
            return "";
        }
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        return dotIndex > 0 ? name.substring(dotIndex).toLowerCase() : "";
    }

    /**
     * 创建文件, 如果文件为深层次目录下的文件则根据输入的cascade标志判断是否级联创建
     *
     * @param pathname
     *            待创建的文件或文件夹
     * @param cascade
     *            true级联创建不存在的目录
     * @throws IOException
     *             文件为深层文件且cascade为false
     */
    public static File createFile(String pathname, boolean cascade) throws IOException {
        File file = new File(pathname);
        createFile(file, cascade);
        return file;
    }

    /**
     * 创建文件
     * 
     * @param file
     * @param cascade
     * @throws IOException
     * @see {@link #createFile(String, boolean)}
     */
    public static void createFile(File file, boolean cascade) throws IOException {
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            // 上层目录存在且是文件
            if (parentFile.exists() && parentFile.isFile()) {
                throw new IOException("file parent not directory, " + parentFile.getPath());
            }
            // 上层目录不存在
            if (!parentFile.exists()) {
                if (!cascade) {
                    // 不允许级联创建
                    throw new IOException("file parent not exists, " + parentFile.getPath());
                }
                // 创建目录
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
    public static void createDirectory(String pathname) throws IOException {
        createDirectory(new File(pathname));
    }

    /**
     * 创建目录
     * 
     * @param pathname
     *            目录路径
     * @throws IOException
     * @see {@linkplain File#mkdirs()}
     */
    public static void createDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException("file is directory, " + file.getAbsolutePath());
            }
            return;
        }
        file.mkdirs();
    }

    /**
     * 在临时目录中创建一个临时文件, 并根据输入设置是否在退出JVM后自动删除
     *
     * @return 临时文件
     * @throws IOException
     */
    public static File createTempFile() throws IOException {
        return createTempFile(true);
    }

    public static File createTempFile(boolean deleteOnExist) throws IOException {
        return createTempFile("HUIJU", deleteOnExist);
    }

    public static File createTempFile(String directory, String prefix, String suffix, boolean deleteOnExist) throws IOException {
        File file = File.createTempFile(prefix, suffix, createTmpDir(directory));
        if (deleteOnExist) {
            file.deleteOnExit();
        }
        return file;
    }

    public static File createTempFile(String directory, boolean deleteOnExist) throws IOException {
        return createTempFile(directory, "Huiju_", null, deleteOnExist);
    }

    private static File createTmpDir(String directory) throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        StringBuilder dir = new StringBuilder(tmpDir);
        if (!tmpDir.endsWith(File.separator)) {
            dir.append(File.separator);
        }
        File result = new File(dir + directory);
        createDirectory(result);
        return result;
    }

    /**
     * 删除文件, 如果文件路径是目录格局输入的cascade判断是否级联删除目录下的所有文件,否则不删除返回false
     *
     * @see #deleteFile(File, boolean)
     */
    public static boolean deleteFile(String pathname, boolean cascade) {
        return deleteFile(new File(pathname), cascade);
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

    /**
     * 将文件读取为二进制字节数组
     *
     * @param pathname
     *            文件路径
     * @see #readByte(File)
     */
    public static byte[] readByte(String pathname) throws IOException {
        return readByte(new File(pathname));
    }

    /**
     * 将文件读取为二进制字节数组
     *
     * @param file
     *            文件
     * @return 文件对应的字节数组
     * @throws IOException
     *             不是文件或文件不存在
     */
    public static byte[] readByte(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return IOUtils.toByteArray(fis);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 按文本方式读取文件
     *
     * @param pathname
     *            文件路径
     * @return 文件的文本内容
     * @throws IOException
     */
    public static String readText(String pathname) throws IOException {
        return readText(new File(pathname), UTF_8);
    }

    /**
     * 按指定编码格式读取文件文本
     *
     * @see #readText(File, String)
     */
    public static String readText(String pathname, String encoding) throws IOException {
        return readText(new File(pathname), encoding);
    }

    /**
     * 按文本方式读取文件
     *
     * @param file
     *            文件
     * @return 文件的文本内容
     * @throws IOException
     */
    public static String readText(File file) throws IOException {
        return readText(file, UTF_8);
    }

    /**
     * 按指定编码encoding,读取文件文本
     *
     * @param file
     *            文件路径
     * @param encoding
     *            文件编码格式
     * @return 文件的文本内容
     * @throws IOException
     */
    public static String readText(File file, String encoding) throws IOException {
        return new String(readByte(file), encoding);
    }

    /**
     * 将文件按行读取, 每行为一条文本信息
     */
    public static List<String> readLines(String pathname) throws IOException {
        return readLines(new File(pathname));
    }

    /**
     * 指定编码方式按行读取文件
     */
    public static List<String> readLines(String pathname, String encoding) throws IOException {
        return readLines(new File(pathname), encoding);
    }

    /**
     * 按行读取文件
     */
    public static List<String> readLines(File file) throws IOException {
        return readLines(file, UTF_8);
    }

    /**
     * 按行读取文件文本内容
     * <p>
     * 
     * <pre>
     *     第一行 index = 0
     *     第二行 index = 1
     *     第n行 index = n+1
     * </pre>
     *
     * @param file
     *            待读取的文件
     * @param encoding
     *            指定的读取编码格式
     * @return 文件的文本
     * @throws IOException
     */
    public static List<String> readLines(File file, String encoding) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return IOUtils.readLines(fis, encoding);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 将文本写入文件
     */
    public static void writeText(String pathname, String text) throws IOException {
        writeText(new File(pathname), text, UTF_8);
    }

    public static void writeText(String pathname, String text, String encoding) throws IOException {
        writeText(new File(pathname), text, encoding);
    }

    /**
     * 将文本写入文件
     */
    public static void writeText(File file, String text) throws IOException {
        writeText(file, text, UTF_8);
    }

    public static void writeText(File file, String text, String encoding) throws IOException {
        writeByte(file, text.getBytes(encoding));
    }

    /**
     * 将字节数组写入文件
     */
    public static void writeByte(String pathname, byte[] binary) throws IOException {
        writeByte(new File(pathname), binary);
    }

    /**
     * 将字节数组写入文件, 如果文件不存在将自动创建
     *
     * @param file
     *            待写入的文件
     * @param binary
     *            写入的字节数组
     * @throws IOException
     */
    public static void writeByte(File file, byte[] binary) throws IOException {
        if (!file.exists()) {
            createFile(file, true);
        }
        FileOutputStream fis = new FileOutputStream(file);
        try {
            IOUtils.write(binary, fis);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 在文件末尾中插入文本
     */
    public static void appendText(String pathname, String text) throws IOException {
        appendText(new File(pathname), text, UTF_8, false);
    }

    /**
     * 插入文本,并通过newLine选项决定是否在新行中插入
     */
    public static void appendText(String pathname, String text, boolean newLine) throws IOException {
        appendText(new File(pathname), text, UTF_8, newLine);
    }

    public static void appendText(String pathname, String text, String encoding, boolean newLine) throws IOException {
        appendText(new File(pathname), text, encoding, newLine);
    }

    /**
     * 在文件末尾中插入文本
     */
    public static void appendText(File file, String text) throws IOException {
        appendText(file, text, UTF_8, false);
    }

    /**
     * 插入文本, 并通过newLine选项决定是否在新行中插入
     */
    public static void appendText(File file, String text, boolean newLine) throws IOException {
        appendText(file, text, UTF_8, newLine);
    }

    /**
     * 通过字符编码追加文本, 可以通过newLine选项是否行
     *
     * @param file
     *            待追加的文件
     * @param text
     *            追加的文本
     * @param encoding
     *            文本编码
     * @param newLine
     *            是否新行
     * @throws IOException
     */
    public static void appendText(File file, String text, String encoding, boolean newLine) throws IOException {
        byte[] buf = ((newLine ? NEW_LINE : "") + text).getBytes(encoding);
        appendByte(file, buf);
    }

    /**
     * 在文件末尾中插入字节数组的内容
     */
    public static void appendByte(String pathname, byte[] binary) throws IOException {
        appendByte(new File(pathname), binary);
    }

    /**
     * 在文件插入对应的字节内容,如果文件不存在则创建
     *
     * @param file
     *            待写入的文件
     * @param binary
     *            字节内容
     * @throws IOException
     */
    public static void appendByte(File file, byte[] binary) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("file is directory, " + file.getAbsolutePath());
        }
        if (!file.exists()) {
            // 如果文件不存在则创建文件
            createFile(file, false);
        }
        FileOutputStream fos = null;
        FileChannel channel = null;
        try {
            fos = new FileOutputStream(file, true);
            channel = fos.getChannel();
            channel.write(ByteBuffer.wrap(binary));
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
