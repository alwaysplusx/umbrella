package com.harmony.umbrella.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 */
public class FileUtils {

    public static final String UTF_8 = "UTF-8";

    public static final String UNIX_NEW_LINE = "\n";

    public static final String WINDOWS_NEW_LINE = "\r\n";

    public static final String NEW_LINE;

    static {
        if (Environments.isWindows()) {
            NEW_LINE = WINDOWS_NEW_LINE;
        } else {
            NEW_LINE = UNIX_NEW_LINE;
        }
    }

    /**
     * 判断文件(夹)是否存在
     *
     * @param pathname 文件路径
     * @return Boolean
     */
    public static boolean exists(String pathname) {
        return new File(pathname).exists();
    }

    /**
     * 创建文件, 如果文件为深层次目录下的文件则判断是否级联创建
     *
     * @param pathname 待创建的文件或文件夹
     * @param cascade  true级联创建不存在的目录
     * @throws IOException 文件为深层文件且cascade为false
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

    /**
     * 在临时目录中创建一个临时文件, 并根据输入设置是否在退出JVM后自动删除
     *
     * @param deleteOnExist 是否退出jvm后删除
     * @return 临时文件
     * @throws IOException
     */
    public static File createTempFile(boolean deleteOnExist) throws IOException {
        File file = File.createTempFile("Umbrella_", null);
        if (deleteOnExist) {
            file.deleteOnExit();
        }
        return file;
    }

    /**
     * 删除文件
     *
     * @see #deleteFile(File, boolean)
     */
    public static boolean deleteFile(String pathname, boolean cascade) {
        return deleteFile(new File(pathname), cascade);
    }

    /**
     * 删除文件, 如果是文件夹根据级联标识判断是否删除文件夹
     *
     * @param file    待删除的文件或文件夹
     * @param cascade 是否允许删除目录
     */
    public static boolean deleteFile(File file, boolean cascade) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        return cascade && delete(file);
    }

    /**
     * 级联删除文件
     */
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

    public static int getMaxLineNumber(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        int lines = 1;
        while (reader.readLine() != null) {
            lines++;
        }
        reader.close();
        return lines;
    }

    /**
     * 判断文件的编码格式.
     *
     * @param pathname 文件路径.
     * @return 文件编码格式
     * @throws IOException
     */
    public static String getFileEncoding(String pathname) throws IOException {
        return getFileEncoding(new File(pathname));
    }

    /**
     * 判断文件的编码格式.
     *
     * @param file 文件对象.
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

    /**
     * 将文件读取为二进制字节数组
     *
     * @param pathname 文件路径
     * @see #readByte(File)
     */
    public static byte[] readByte(String pathname) throws IOException {
        return readByte(new File(pathname));
    }

    /**
     * 将文件读取为二进制字节数组
     *
     * @param file 文件
     * @return 文件对应的字节数组
     * @throws IOException 不是文件或文件不存在
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
     * @param pathname 文件路径
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
     * @param file 文件
     * @return 文件的文本内容
     * @throws IOException
     */
    public static String readText(File file) throws IOException {
        return readText(file, UTF_8);
    }

    /**
     * 按指定编码encoding,读取文件文本
     *
     * @param file     文件路径
     * @param encoding 文件编码格式
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
     * <pre>
     *     第一行 index = 0
     *     第二行 index = 1
     *     第n行 index = n+1
     * </pre>
     *
     * @param file     待读取的文件
     * @param encoding 指定的读取编码格式
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
     * @param file   待写入的文件
     * @param binary 写入的字节数组
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
     * @param file     待追加的文件
     * @param text     追加的文本
     * @param encoding 文本编码
     * @param newLine  是否新行
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
     * @param file   待写入的文件
     * @param binary 字节内容
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

    /**
     * 插入文本
     *
     * @see #insertText(File, int, boolean, String, String)
     */
    public static void insertText(File file, int lineNumber, boolean autoNewLine, String text) throws IOException {
        insertText(file, lineNumber, autoNewLine, text, UTF_8);
    }

    /**
     * 在文件指定行插入文本
     * <p>
     * <pre>
     *     a.txt     insertText(a, 1, true, '1');   a.txt
     *     A                                        1
     *     B                                        A
     *     C                                        B
     *                                              C
     *     ------------------------------------------------
     *     insertText(a, 1, false, '1')
     *
     *     a.txt
     *     1A
     *     B
     *     C
     * </pre>
     *
     * @param file        待插入的文件
     * @param lineNumber  指定的行
     * @param autoNewLine 另取新行放置源文本
     * @param text        插入的文本
     * @param encoding    写入文件的字符编码
     * @throws IOException 文件不存在
     */
    public static void insertText(File file, int lineNumber, boolean autoNewLine, String text, String encoding) throws IOException {
        if (StringUtils.isBlank(text)) {
            return;
        }
        if (lineNumber < 0) {
            throw new IOException("illegal line number " + lineNumber);
        }
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("file not exists or is directory");
        }
        long position = getLinePosition(file, lineNumber);
        byte[] buf = autoNewLine ? (text + NEW_LINE).getBytes(encoding) : text.getBytes(encoding);
        if (position == -1) {
            // 文本长度未达到指定的行数, 自动插入空行
            insertEmptyLine(file, lineNumber - getMaxLineNumber(file));
            // 最后追加文本
            appendByte(file, buf);
        } else {
            // 找到了指定行的位置. 在指定行插入文本
            insertByteToPosition(file, position, buf);
        }
    }

    /**
     * 自动插入空行
     *
     * @param file  插入的文件
     * @param lines 插入的行数
     */
    private static void insertEmptyLine(File file, int lines) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("file not exists or is directory");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            sb.append(NEW_LINE);
        }
        appendByte(file, sb.toString().getBytes());
    }

    /**
     * 在指定seek位置插入字节内容
     *
     * @param file     指定文件
     * @param position 对应的seek位置
     * @param binary   插入的字节内容
     * @throws IOException 文件不存在
     */
    public static void insertByteToPosition(File file, long position, byte[] binary) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("file not exists or is directory");
        }
        FileOutputStream fos = new FileOutputStream(file, true);
        FileChannel channel = fos.getChannel();
        channel.write(ByteBuffer.wrap(binary), position);
        channel.close();
        fos.close();
    }

    /**
     * 读取指定行的seek指针位置, 如果为找到指定行返回-1
     *
     * @param file       查找的文件
     * @param lineNumber 指定的文件行
     * @return 文件中对应的行的seek
     * @throws IOException
     * @see {@link RandomAccessFile#getFilePointer()}
     */
    public static long getLinePosition(File file, int lineNumber) throws IOException {
        long currentPosition = -1;
        if (lineNumber > 0) {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            // 移动文件指针到起始位置
            raf.seek(0);
            // 当前读取行号
            int curLine = 1;
            while (curLine != lineNumber && raf.readLine() != null) {
                curLine++;
            }
            if (curLine == lineNumber) {
                currentPosition = raf.getFilePointer();
            }
            raf.close();
        }
        return currentPosition;
    }

}
