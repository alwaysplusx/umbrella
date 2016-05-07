package com.harmony.umbrella.util;

import java.io.IOException;
import java.util.zip.ZipEntry;

import com.harmony.umbrella.util.ZipUtils.ZipEntryFilter;

/**
 * @author wuxii@foxmail.com
 */
public class ZipUtilsTest {

    public static void main(String[] args) throws IOException {
        ZipUtils.unzip("a.zip", "D:/unzip", new ZipEntryFilter() {
            @Override
            public boolean accept(ZipEntry entry) {
                return false;
            }
        });
    }
}
