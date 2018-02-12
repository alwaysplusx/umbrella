package com.harmony.umbrella.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

import com.harmony.umbrella.util.FileUtils.TmpDir;

/**
 * @author wuxii@foxmail.com
 */
public class FileUtilsTest {

    @Test
    public void testCreateFile() throws IOException {
        File file = FileUtils.createFile("./target/files/a/b/c/a.txt");
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void testCopyMoveRename() throws IOException {
        File source = new File("pom.xml");
        File copyTo = FileUtils.copyTo(source, new File("target/origin"), StandardCopyOption.REPLACE_EXISTING);
        File moveTo = FileUtils.moveTo(copyTo, new File("target/copy"), StandardCopyOption.REPLACE_EXISTING);
        FileUtils.renameTo(moveTo, new File("target/rename/umbrella-core.pom"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testContentType() throws IOException {
        String contentType = Files.probeContentType(new File("pom.xml").toPath());
        System.out.println(contentType);
    }

    @Test
    public void testCreateTmp() throws IOException {
        File file = FileUtils.createTmpFile();
        TmpDir dir = FileUtils.createTmpDir();
        System.out.println(dir.getTmpDir().getAbsolutePath());
        System.out.println(file.getAbsolutePath());
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.io.tmpdir"));
    }

}
