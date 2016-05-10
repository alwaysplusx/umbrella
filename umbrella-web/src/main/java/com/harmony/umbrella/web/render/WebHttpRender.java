package com.harmony.umbrella.web.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WebHttpRender implements HttpRender {

    private String encoding = "utf-8";
    private String headerSeparator = ":";

    // writer

    @Override
    public void renderJson(String text, HttpServletResponse response, String... headers) throws IOException {
        render("application/json", text, response, headers);
    }

    @Override
    public void renderXml(String text, HttpServletResponse response, String... headers) throws IOException {
        render("application/xml", text, response, headers);
    }

    @Override
    public void renderHtml(String text, HttpServletResponse response, String... headers) throws IOException {
        render("text/html", text, response, headers);
    }

    @Override
    public void renderText(String text, HttpServletResponse response, String... headers) throws IOException {
        render("text/plain", text, response, headers);
    }

    @Override
    public void render(String contentType, String text, HttpServletResponse response, String... headers) throws IOException {
        render(contentType, text, response, toHeaderMap(headers));
    }

    public void render(String contentType, String text, HttpServletResponse response, Map<String, String> headerMap) throws IOException {
        String encoding = headerMap.get("encoding");
        contentType = contentType + "; charset=" + (StringUtils.isBlank(encoding) ? this.encoding : encoding);
        // 设置response报文头
        applyHeader(response, headerMap);
        response.setContentType(contentType);
        response.getWriter().write(text);
        response.getWriter().flush();
    }

    // output stream

    @Override
    public void renderFile(File file, HttpServletResponse response, String... headers) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        render(fis, response, headers);
        fis.close();
    }

    @Override
    public void render(InputStream is, HttpServletResponse response, String... headers) throws IOException {
        render("application/octet-stream", is, response, headers);
    }

    @Override
    public void render(String contentType, InputStream is, HttpServletResponse response, String... headers) throws IOException {
        applyHeader(response, toHeaderMap(headers));
        response.setContentType(contentType);
        response.setContentLength(is.available());

        IOUtils.copy(is, response.getOutputStream());
        response.getOutputStream().flush();
    }

    // 

    protected void applyHeader(HttpServletResponse response, Map<String, String> header) {
        for (Entry<String, String> e : header.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if ("no-cache".equalsIgnoreCase(key) && Boolean.valueOf(value)) {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
            } else {
                response.setHeader(key, value);
            }
        }
    }

    private Map<String, String> toHeaderMap(String[] headers) {
        return toHeaderMap(headers, headerSeparator);
    }

    protected Map<String, String> toHeaderMap(String[] headers, String headerSeparator) {
        Map<String, String> headersMap = new HashMap<String, String>();
        for (String header : headers) {
            String key = StringUtils.substringBefore(header, headerSeparator);
            String value = StringUtils.substringAfter(header, headerSeparator);
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                headersMap.put(key, value);
            } else {
                throw new IllegalArgumentException("illegal header expression " + header);
            }
        }
        return headersMap;
    }
}
