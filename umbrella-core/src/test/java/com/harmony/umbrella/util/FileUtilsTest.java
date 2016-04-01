/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
        File src = new File("target/src.txt");
        File dest = new File("target/dest.txt");

        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest, true);

        fos.getChannel().write(ByteBuffer.wrap("A".getBytes()), 2);

        fis.close();
        fos.close();
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
