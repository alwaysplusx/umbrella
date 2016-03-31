package com.harmony.umbrella.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 */
public class FileUtils {

    public static final String ENCODING_UTF_8 = "UTF-8";

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
     * 创建文件, 如果文件为深层次目录下的文件则判断是否级联创建
     * 
     * @param file
     *            待创建的文件或文件夹
     * @param cascade
     *            true级联创建不存在的目录
     * @throws IOException
     *             文件为深层文件且cascade为false
     */
    public static void createFile(String pathname, boolean cascade) throws IOException {
        createFile(new File(pathname), cascade);
    }

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

    public static File createTempFile() throws IOException {
        File file = File.createTempFile("Umbrella_", null);
        file.deleteOnExit();// 在JVM退出时删除
        return file;
    }

    /**
     * 删除文件, 如果是文件夹根据级联标识判断是否删除文件夹
     * 
     * @param file
     *            待删除的文件或文件夹
     * @param cascade
     *            是否允许删除目录
     */
    public static boolean deleteFile(String pathname, boolean cascade) {
        return deleteFile(new File(pathname), cascade);
    }

    public static boolean deleteFile(File file, boolean cascade) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        return cascade ? delete(file) : false;
    }

    private static boolean delete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!delete(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /**
     * 判断文件的编码格式.
     * 
     * @param path
     *            文件路径.
     * 
     * @return 文件编码格式
     * @throws IOException
     */
    public static String getFileEncoding(String pathname) throws IOException {
        return getFileEncoding(new File(pathname));
    }

    /**
     * 判断文件的编码格式.
     * 
     * @param file
     *            文件对象.
     * 
     * @return 文件编码格式
     * @throws IOException
     */
    public static String getFileEncoding(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        FileInputStream fis = new FileInputStream(file);
        int p = (fis.read() << 8) + fis.read();
        fis.close();

        // 其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        String code = null;
        switch (p) {
        case 0xefbb:
            code = "UTF-8";
            break;
        case 0xfffe:
            code = "Unicode";
            break;
        case 0xfeff:
            code = "UTF-16BE";
            break;
        case 0x5c75:
            code = "ASCII";
            break;
        default:
            code = "GBK";
        }
        return code;
    }

    public static byte[] readFile(String pathname) throws IOException {
        return readFile(new File(pathname));
    }

    public static byte[] readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(fis.available());
        IOUtils.copy(fis, baos);
        return baos.toByteArray();
    }

    public static String readFileText(String pathname) throws IOException {
        return readFileText(new File(pathname), ENCODING_UTF_8);
    }

    public static String readFileText(String pathname, String encoding) throws IOException {
        return readFileText(new File(pathname), encoding);
    }

    public static String readFileText(File file) throws IOException {
        return readFileText(file, ENCODING_UTF_8);
    }

    public static String readFileText(File file, String encoding) throws IOException {
        return new String(readFile(file), encoding);
    }

    public static List<String> readLines(String pathname) throws IOException {
        return readLines(new File(pathname));
    }

    public static List<String> readLines(String pathname, String encoding) throws IOException {
        return readLines(new File(pathname));
    }

    public static List<String> readLines(File file) throws IOException {
        return readLines(file, ENCODING_UTF_8);
    }

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

    public static void writeText(String pathname, String text) throws IOException {
        writeByte(new File(pathname), text.getBytes());
    }

    public static void writeText(File file, String text) throws IOException {
        writeByte(file, text.getBytes());
    }

    public static void writeByte(String pathname, byte[] binary) throws IOException {
        writeByte(new File(pathname), binary);
    }

    public static void writeByte(File file, byte[] binary) throws IOException {
        if (file.exists()) {
            throw new IOException("cannot write a new file, file already exists. " + file.getAbsolutePath());
        }
        createFile(file, true);
        FileOutputStream fis = new FileOutputStream(file);
        IOUtils.write(binary, fis);
        fis.close();
    }

    public static void appendText(String pathname, String text) throws IOException {
        appendText(new File(pathname), text);
    }

    public static void appendText(File file, String text) throws IOException {
        appendByte(file, text.getBytes());
    }

    public static void appendByte(String pathname, byte[] binary) throws IOException {
        appendByte(new File(pathname), binary);
    }

    public static void appendByte(File file, byte[] binary) throws IOException {
        if (!file.exists()) {
            // 如果文件不存在则创建文件
            createFile(file, false);
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(raf.length());
        raf.write(binary);
        raf.close();
    }

    public static void insertByte(File file, int lineNumber, byte[] binary) throws IOException {
        if (!file.exists()) {
            // 文件不存在异常
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        long postion = getLinePostion(file, lineNumber);
        if (postion == -1) {
            throw new IOException("file line not found " + lineNumber);
        }
        insertByteToPostion(file, postion, binary);
    }

    public static void insertByteToPostion(File file, long position, byte[] binary) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        File tempFile = createTempFile();
        // 创建一个临时文件夹来保存插入点后的数据
        FileOutputStream tfos = new FileOutputStream(tempFile);
        FileInputStream tfis = new FileInputStream(tempFile);

        // 插入的位置
        raf.seek(position);

        // 将position后的文件内容写入临时文件
        byte[] readBuffer = new byte[1024];
        int hasRead = 0;
        while ((hasRead = raf.read(readBuffer)) > 0) {
            tfos.write(readBuffer, 0, hasRead);
        }

        // 返回原来的插入处
        raf.seek(position);

        // 插入新内容
        raf.write(binary);

        byte[] writeBuffer = new byte[1024];
        // 将原position后的文件重新写入
        while ((hasRead = tfis.read(writeBuffer)) > 0) {
            raf.write(writeBuffer, 0, hasRead);
        }

        tfos.close();
        tfis.close();
        raf.close();
    }

    /**
     * 读取指定行的seek指针位置, 如果为找到指定行返回-1
     * 
     * @param file
     *            查找的文件
     * @param lineNumber
     *            指定的文件行
     * @return 文件中对应的行的seek
     * @throws IOException
     * @see {@link RandomAccessFile#getFilePointer()}
     */
    public static long getLinePostion(File file, int lineNumber) throws IOException {
        // FIXME 文件从1开始, 不应该从0开始
        // 位移指针
        long currentPosition = -1L;
        // 只读
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // 移动文件指针到起始位置
        raf.seek(0);
        // 当前读取行号
        Integer curLine = 0;

        while (curLine < lineNumber && raf.readLine() != null) {
            curLine++;
        }

        if (curLine == lineNumber) {
            currentPosition = raf.getFilePointer();
        }

        raf.close();

        return currentPosition;
    }

}
