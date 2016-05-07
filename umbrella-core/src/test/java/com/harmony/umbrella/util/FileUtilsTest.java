package com.harmony.umbrella.util;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class FileUtilsTest {

    private static final File file = new File("target/a.txt");

    @BeforeClass
    public static void setUp() throws IOException {
        if (!file.exists())
            file.createNewFile();
    }

    @Test
    public void testAppend() throws IOException {

    }

    @Test
    public void testInsertText() throws IOException {
        FileUtils.insertText(file, 3, false, "ABC");
    }

    public static void main(String[] args) throws Exception {
        /*File src = new File("target/src.txt");
        File dest = new File("target/dest.txt");

        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest, true);

        fos.getChannel().write(ByteBuffer.wrap("A".getBytes()), 2);

        fis.close();
        fos.close();*/

        String extension = FileUtils.getExtension("target/a.zip");
        System.out.println(extension);
    }

    @Test
    public void testWrite() throws IOException {
        FileUtils.writeByte("target/b.txt", "A\r\nB\rC\nD".getBytes());

        byte[] bytes = FileUtils.readByte("target/b.txt");
        for (byte b : bytes) {
            System.out.println(b);
        }
    }

}
