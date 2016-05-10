package com.harmony.umbrella.web.render;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 文本渲染
 * 
 * @author wuxii@foxmail.com
 */
public interface HttpRender {

    /**
     * 渲染json
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderJson(String text, HttpServletResponse response, String... headers) throws IOException;

    /**
     * 渲染xml
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderXml(String text, HttpServletResponse response, String... headers) throws IOException;

    /**
     * 渲染html
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderHtml(String text, HttpServletResponse response, String... headers) throws IOException;

    /**
     * 渲染text
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderText(String text, HttpServletResponse response, String... headers) throws IOException;

    void render(String contentType, String text, HttpServletResponse response, String... headers) throws IOException;

    /**
     * 渲染文件
     * 
     * @param file
     *            渲染的文件
     * @param response
     *            http response
     * @throws IOException
     */
    void renderFile(File file, HttpServletResponse response, String... headers) throws IOException;

    /**
     * 渲染二进制流
     * 
     * @param is
     * @param response
     * @param headers
     * @throws IOException
     */
    void render(InputStream is, HttpServletResponse response, String... headers) throws IOException;

    void render(String contentType, InputStream is, HttpServletResponse response, String... headers) throws IOException;
}
