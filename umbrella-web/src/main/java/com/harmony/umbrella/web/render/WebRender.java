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
package com.harmony.umbrella.web.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.AbstractRender;

/**
 * @author wuxii@foxmail.com
 */
public class WebRender extends AbstractRender implements HttpRender {

    private static final String Content_Type = "Content-Type";
    // private static final String Content_Disposition = "Content-Disposition";
    // private static final String Content_Length = "Content-Length";

    private static final String charset = "charset=utf-8";

    public final static String WILDCARD = "*/*; " + charset;

    public static final String TEXT_HTML = "text/html; " + charset;
    public static final String TEXT_XML = "text/xml; " + charset;
    public static final String TEXT_PLAIN = "text/plain; " + charset;

    public static final String APPLICATION_JSON = "application/json; " + charset;

    private static final Map<String, String> JSON_HEADER;
    private static final Map<String, String> XML_HEADER;
    private static final Map<String, String> HTML_HEADER;
    private static final Map<String, String> PLAIN_HEADER;

    static {
        Map<String, String> header = new HashMap<String, String>();
        header.put(Content_Type, APPLICATION_JSON);
        JSON_HEADER = Collections.unmodifiableMap(header);

        header = new HashMap<String, String>();
        header.put(Content_Type, TEXT_XML);
        XML_HEADER = Collections.unmodifiableMap(header);

        header = new HashMap<String, String>();
        header.put(Content_Type, TEXT_PLAIN);
        PLAIN_HEADER = Collections.unmodifiableMap(header);

        header = new HashMap<String, String>();
        header.put(Content_Type, TEXT_HTML);
        HTML_HEADER = Collections.unmodifiableMap(header);
    }

    @Override
    public void renderJson(String text, HttpServletResponse response) throws IOException {
        render(text, response, JSON_HEADER);
    }

    @Override
    public void renderXml(String text, HttpServletResponse response) throws IOException {
        render(text, response, XML_HEADER);
    }

    @Override
    public void renderHtml(String text, HttpServletResponse response) throws IOException {
        render(text, response, HTML_HEADER);
    }

    @Override
    public void renderText(String text, HttpServletResponse response) throws IOException {
        render(text, response, PLAIN_HEADER);
    }

    @Override
    public void renderFile(File file, HttpServletResponse response) throws IOException {
        renderFile(file, response, new HashMap<String, String>());
    }

    @Override
    public void renderFile(File file, HttpServletResponse response, Map<String, String> heanders) throws IOException {
        if (!file.isFile()) {
            throw new IOException(file.getAbsolutePath() + " is not file");
        }
        applyIfAbsent(getFileHeader(file, false), heanders);
        FileInputStream fis = new FileInputStream(file);
        render(fis, response, heanders);
        fis.close();
    }

    @Override
    public void render(String text, HttpServletResponse response, Map<String, String> headers) throws IOException {
        for (Entry<String, String> entry : headers.entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }
        render(text, response.getWriter());
    }

    @Override
    public void render(InputStream is, HttpServletResponse response, Map<String, String> headers) throws IOException {
        for (Entry<String, String> entry : headers.entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }
        IOUtils.copy(is, response.getOutputStream());
    }

    protected Map<String, String> getFileHeader(File file, boolean download) {
        String extension = FileUtils.getExtension(file);
        Map<String, String> heanders = new HashMap<String, String>();
        if (".txt".equals(extension)) {
            heanders.putAll(PLAIN_HEADER);
        } else if (".html".equals(extension)) {
            heanders.putAll(HTML_HEADER);
        } else if (".xml".equals(extension)) {
            heanders.putAll(XML_HEADER);
        } else if (".xls".equals(extension) || ".xlsx".equals(extension)) {
            heanders.putAll(PLAIN_HEADER);
        } else if (".doc".equals(extension) || ".docx".equals(extension)) {
            heanders.putAll(PLAIN_HEADER);
        } else if (".ppt".equals(extension) || ".pptx".equals(extension)) {
            heanders.putAll(PLAIN_HEADER);
        } else if (".pdf".equals(extension)) {
            heanders.putAll(PLAIN_HEADER);
        }
        return heanders;
    }

    protected void applyIfAbsent(Map<String, String> origin, Map<String, String> target) {
        for (String key : origin.keySet()) {
            if (!target.containsKey(key)) {
                target.put(key, origin.get(key));
            }
        }
    }
}
