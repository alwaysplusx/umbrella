package com.harmony.umbrella.util;

import java.io.IOException;

import org.junit.Test;

import com.harmony.umbrella.util.Zip.ZipBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class ZipTest {

    public static void main(String[] args) throws IOException {
        new ZipTest().testZipBuilder();
    }

    @Test
    public void testZip() throws IOException {
        Zip.zip("target/classes", "target/classes.zip");
    }

    @Test
    public void testZipBuilder() throws IOException {
        ZipBuilder builder = ZipBuilder//
                .newBuilder()//
                .addSourceDirectory("target/classes")//
                .setIncludeRoot(true)//
                .up();
        //
        builder.zip("target/classes.zip");
        builder.gzip("target/classes.zip.gz");
    }

}
