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

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.AbstractRender;
import com.harmony.umbrella.web.MimeTypes;

/**
 * @author wuxii@foxmail.com
 */
public class WebRender extends AbstractRender implements HttpRender {

    private static final String Content_Type = "Content-Type";
    // private static final String Content_Disposition = "Content-Disposition";
    // private static final String Content_Length = "Content-Length";

    public final static String WILDCARD = "*/*; charset=utf-8";

    public static final String TEXT_HTML = "text/html; charset=utf-8";
    public static final String TEXT_XML = "text/xml; charset=utf-8";
    public static final String TEXT_PLAIN = "text/plain; charset=utf-8";

    public static final String APPLICATION_JSON = "application/json; charset=utf-8";

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
        String extension = FileUtils.getExtension(file);
        Map<String, String> mimeHeader = getMimeHeader(extension);
        applyIfAbsent(mimeHeader, heanders);
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

    protected Map<String, String> getMimeHeader(String extension) {
        Map<String, String> heanders = new HashMap<String, String>();
        heanders.put(Content_Type, getMimeType(extension));
        return heanders;
    }

    private String getMimeType(String extension) {
        String mimeType = MimeTypes.getMimeType(extension);
        return mimeType == null ? WILDCARD : mimeType + "; charset=utf-8";
    }

    protected void applyIfAbsent(Map<String, String> origin, Map<String, String> target) {
        for (String key : origin.keySet()) {
            if (!target.containsKey(key)) {
                target.put(key, origin.get(key));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Resource resource = ResourceManager.getInstance().getResource("mime.json");
        String jsonText = IOUtils.toString(resource.getInputStream());
        Map<String, Object> map = Json.toMap(jsonText);
        System.out.println(map);
    }

}
