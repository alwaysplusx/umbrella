package com.harmony.umbrella.web.render;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.web.Render;

/**
 * 文本渲染
 * 
 * @author wuxii@foxmail.com
 */
public interface HttpRender extends Render {

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
    void renderJson(String text, HttpServletResponse response) throws IOException;

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
    void renderXml(String text, HttpServletResponse response) throws IOException;

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
    void renderHtml(String text, HttpServletResponse response) throws IOException;

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
    void renderText(String text, HttpServletResponse response) throws IOException;

    /**
     * 渲染文本
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @param headers
     *            http header
     */
    void render(String text, HttpServletResponse response, Map<String, String> headers) throws IOException;

    /**
     * 渲染文件
     * 
     * @param file
     *            渲染的文件
     * @param response
     *            http response
     * @throws IOException
     */
    void renderFile(File file, HttpServletResponse response) throws IOException;

    /**
     * 带有http header的文件渲染
     * 
     * @param file
     *            渲染的文件
     * @param response
     *            http response
     * @throws IOException
     */
    void renderFile(File file, HttpServletResponse response, Map<String, String> heanders) throws IOException;

}
