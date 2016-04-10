/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * 前台渲染工具
 * 
 * @author wuxii@foxmail.com
 */
public interface Render {

    /**
     * 将buf输出到out输出流中
     * 
     * @param buf
     *            待输出的字节
     * @param out
     *            输出流
     * @throws IOException
     *             if an I/O error occurs.
     */
    void render(byte[] buf, OutputStream out) throws IOException;

    /**
     * 将文本输出到writer中
     * 
     * @param text
     *            待输出的文本
     * @param writer
     *            输出自字符流
     * @return true输出成功
     * @throws IOException
     *             if an I/O error occurs.
     */
    boolean render(String text, Writer writer) throws IOException;

}
