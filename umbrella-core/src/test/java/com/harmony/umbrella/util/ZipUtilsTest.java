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
