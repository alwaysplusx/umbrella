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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.AbstractRender;
import com.harmony.umbrella.web.WebConstants.MediaType;

/**
 * @author wuxii@foxmail.com
 */
public class WebRender extends AbstractRender implements HttpTextRender, HttpBinaryRender {

    private static final String Content_Type = "Content-Type";
    private static final String Content_Disposition = "Content-Disposition";
    private static final String Content_Length = "Content-Length";

    protected static final Map<String, String> HTTP_RESPONSE_JSON_HEADER = new HashMap<String, String>();
    protected static final Map<String, String> HTTP_RESPONSE_XML_HEADER = new HashMap<String, String>();
    protected static final Map<String, String> HTTP_RESPONSE_HTML_HEADER = new HashMap<String, String>();
    protected static final Map<String, String> HTTP_RESPONSE_TEXT_HEADER = new HashMap<String, String>();

    static {

        HTTP_RESPONSE_JSON_HEADER.put(Content_Type, MediaType.TEXT_JSON);

        HTTP_RESPONSE_XML_HEADER.put(Content_Type, MediaType.TEXT_XML);

        HTTP_RESPONSE_HTML_HEADER.put(Content_Type, MediaType.TEXT_HTML);

        HTTP_RESPONSE_TEXT_HEADER.put(Content_Type, MediaType.TEXT_PLAIN);
    }

    private final Map<String, String> headerProperties = new HashMap<String, String>();

    public WebRender(Map<String, String> headProperties) {
        headerProperties.putAll(headProperties);
    }

    @Override
    public void renderFile(File file, HttpServletResponse response) throws IOException {
        Assert.notNull(file, "fiel must not be null");
        if (!file.exists() || !file.isFile()) {
            throw new IOException("file " + file.getName() + " not exists or is not fiel");
        }
        byte[] buf = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            buf = new byte[fis.available()];
            fis.read(buf);
        } finally {
            if (fis != null)
                fis.close();
        }
        headerProperties.put(Content_Length, String.valueOf(buf.length));
        headerProperties.put(Content_Disposition, "attachment;filename=" + file.getName() + "." + getFileExtensions(file.getName()));
        applyHeaders(headerProperties, response);
        render(buf, response.getOutputStream());
    }

    @Override
    public void renderJson(String text, HttpServletResponse response) throws IOException {
        headerProperties.putAll(HTTP_RESPONSE_JSON_HEADER);
        headerProperties.put(Content_Length, String.valueOf(text.getBytes().length));
        applyHeaders(headerProperties, response);
        render(text, response.getWriter());
    }

    @Override
    public void renderXml(String text, HttpServletResponse response) throws IOException {
        headerProperties.putAll(HTTP_RESPONSE_XML_HEADER);
        headerProperties.put(Content_Length, String.valueOf(text.getBytes().length));
        applyHeaders(headerProperties, response);
        render(text, response.getWriter());
    }

    @Override
    public void renderHtml(String text, HttpServletResponse response) throws IOException {
        headerProperties.putAll(HTTP_RESPONSE_HTML_HEADER);
        headerProperties.put(Content_Length, String.valueOf(text.getBytes().length));
        applyHeaders(headerProperties, response);
        render(text, response.getWriter());
    }

    @Override
    public void renderText(String text, HttpServletResponse response) throws IOException {
        headerProperties.putAll(HTTP_RESPONSE_TEXT_HEADER);
        headerProperties.put(Content_Length, String.valueOf(text.getBytes().length));
        applyHeaders(headerProperties, response);
        render(text, response.getWriter());
    }

    /**
     * 将header信息设置到http返回中
     */
    protected void applyHeaders(Map<String, String> header, HttpServletResponse response) {
        Iterator<Entry<String, String>> it = header.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            response.setHeader(entry.getKey(), entry.getValue());
        }
    }

    private static String getFileExtensions(String fileName) {
        if (StringUtils.isBlank(fileName) || fileName.endsWith("."))
            return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase().trim();
    }

}
