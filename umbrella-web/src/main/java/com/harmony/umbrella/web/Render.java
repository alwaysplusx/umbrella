package com.harmony.umbrella.web;

import java.io.IOException;
import java.io.InputStream;
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
    void render(byte[] buf, OutputStream os) throws IOException;

    /**
     * 将输如流内的信息输出到输出流中
     * 
     * @param is
     *            输入流
     * @param os
     *            输出流
     * @throws IOException
     *             if an I/O error occurs.
     */
    void render(InputStream is, OutputStream os) throws IOException;

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
