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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.util.FileUtils;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.AbstractRender;
import com.harmony.umbrella.web.MimeTypes;

/**
 * @author wuxii@foxmail.com
 */
public class WebRender extends AbstractRender implements HttpRender {

    public static final String Content_Type = "Content-Type";
    public static final String Content_Length = "Content-Length";
    public static final String Content_Disposition = "Content-Disposition";

    public final static String WILDCARD = "*/*; charset=utf-8";

    @Override
    public void renderJson(String text, HttpServletResponse response) throws IOException {
        render(text, response, getMimeHeader(".json"));
    }

    @Override
    public void renderXml(String text, HttpServletResponse response) throws IOException {
        render(text, response, getMimeHeader(".xml"));
    }

    @Override
    public void renderHtml(String text, HttpServletResponse response) throws IOException {
        render(text, response, getMimeHeader(".html"));
    }

    @Override
    public void renderText(String text, HttpServletResponse response) throws IOException {
        render(text, response, getMimeHeader(".txt"));
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
        applyIfAbsent(getMimeHeader(extension), heanders);
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

    protected String getMimeType(String extension) {
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

}
