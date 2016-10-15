package com.harmony.umbrella.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WebRender {

    private HttpServletResponse response;

    public WebRender(HttpServletResponse response) {
        this.response = response;
    }

    public WebRender withHeader(String key, String value) {
        response.setHeader(key, value);
        return this;
    }

    public WebRender withContentType(String contentType) {
        withContentType(contentType, "UTF-8");
        return this;
    }

    public WebRender withContentType(String contentType, String encoding) {
        response.setContentType(contentType + "; " + encoding);
        return this;
    }

    public WebRender withInline(String fileName) {
        withHeader("Content-Disposition", "inline;filename" + encodeFileName(fileName));
        return this;
    }

    public WebRender withAttarchment(String fileName) {
        withHeader("Content-Disposition", "attarchment;filename" + encodeFileName(fileName));
        return this;
    }

    public WebRender withCache(long maxAge) {
        if (maxAge < 0) {
            withHeader("Cache-Control", "no-cache");
        } else {
            withHeader("Cache-Control", "max-age=" + maxAge);
        }
        return this;
    }

    public WebRender withNoCache() {
        withCache(-1);
        return this;
    }

    public void render(String text) throws IOException {
        response.setContentLength(text.getBytes().length);
        response.getWriter().write(text);
    }

    public void render(String fileName, File file) throws IOException {
        withAttarchment(fileName);
        render(file);
    }

    public void render(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        render(is);
        is.close();
    }

    public void render(String fileName, InputStream is) throws IOException {
        withAttarchment(fileName);
        render(is);
    }

    public void render(InputStream is) throws IOException {
        ServletOutputStream os = response.getOutputStream();
        response.setContentLength(is.available());
        IOUtils.copy(is, os);
        os.flush();
    }

    private String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return fileName;
        }
    }
}
