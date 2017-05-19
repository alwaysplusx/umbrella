package com.harmony.umbrella.web.method.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.method.annotation.BundleView.PatternConverter;

/**
 * @author wuxii@foxmail.com
 */
public class ViewFragment {

    private static final Log log = Logs.getLog(ViewFragment.class);

    public static final String VIEW_FRAGMENT = "viewFragment";

    public static final String ENCODING = "UTF-8";
    public static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8_VALUE;

    protected SerializerConfigBuilder serializer;
    protected final Map<String, List<String>> headers;
    protected String contentType;
    protected boolean wrappage;
    protected String encoding;

    protected ViewFragment() {
        this.headers = new LinkedHashMap<>();
        this.serializer = SerializerConfigBuilder.create();
    }

    public ViewFragment encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public ViewFragment contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ViewFragment wrappage(boolean wrappage) {
        this.wrappage = wrappage;
        return this;
    }

    public ViewFragment camelCase(boolean camelCase) {
        serializer.camelCase(camelCase);
        return this;
    }

    public ViewFragment safeFetch(boolean safeFetch) {
        serializer.safeFetch(safeFetch);
        return this;
    }

    public ViewFragment fetchLazy(boolean fetchLazy) {
        serializer.fetchLazyAttribute(fetchLazy);
        return this;
    }

    public ViewFragment excludes(String... patterns) {
        serializer.excludePatterns(patterns);
        return this;
    }

    public ViewFragment excludes(PatternConverter converter, String... patterns) {
        return excludes(converter.convert(patterns));
    }

    public ViewFragment includes(String... patterns) {
        serializer.includePatterns(patterns);
        return this;
    }

    public ViewFragment includes(PatternConverter converter, String... patterns) {
        return includes(converter.convert(patterns));
    }

    public ViewFragment filters(SerializeFilter... filters) {
        serializer.withFilter(filters);
        return this;
    }

    public ViewFragment features(SerializerFeature... features) {
        serializer.withFeature(features);
        return this;
    }

    public ViewFragment setHeader(String name, String value) {
        return setHeader(name, new String[] { value });
    }

    public ViewFragment setHeader(String name, String... value) {
        List<String> rowHeader = getRowHeader(name);
        rowHeader.clear();
        rowHeader.addAll(Arrays.asList(value));
        return this;
    }

    public ViewFragment addHeader(String name, String value) {
        return addHeader(name, new String[] { value });
    }

    public ViewFragment addHeader(String name, String... value) {
        getRowHeader(name).addAll(Arrays.asList(value));
        return this;
    }

    public ViewFragment disableCaching() {
        setHeader("Pragma", "no-cache");
        setHeader("Cache-Control", "no-cache, no-store, max-age=0");
        setHeader("Expires", "1");
        return this;
    }

    public ViewFragment caching(long maxAge) {
        headers.remove("Pragma");
        headers.remove("Expires");
        setHeader("Cache-Control", "max-age=" + maxAge);
        return this;
    }

    public void reset() {
        this.serializer = SerializerConfigBuilder.create();
        this.headers.clear();
        this.wrappage = true;
        this.contentType = null;
    }

    private List<String> getRowHeader(String name) {
        List<String> rows = headers.get(name);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }

    public View toView(Object obj) {
        return new BundleView(obj);
    }

    protected void render(Object o, HttpServletResponse response) throws IOException {
        String type = contentType == null ? MediaType.APPLICATION_JSON_UTF8_VALUE : contentType;
        render(o, type, createOutputMessage(response));
    }

    private void render(Object o, String contentType, ServletServerHttpResponse outputMessage) throws IOException {
        String encoding = StringUtils.isBlank(this.encoding) ? ENCODING : this.encoding;
        outputMessage.getHeaders().putAll(headers);
        HttpServletResponse response = outputMessage.getServletResponse();
        response.setContentType(contentType);
        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding(encoding);
        } else if (!response.getCharacterEncoding().equalsIgnoreCase(encoding)) {
            log.warn("response charset encoding already set, expect set to {} but actual is {}", encoding, response.getCharacterEncoding());
        }
        if (o instanceof org.springframework.data.domain.Page && wrappage) {
            o = new Page((org.springframework.data.domain.Page) o);
        }
        String text = Json.toJson(o, serializer.build());
        IOUtils.write(text.getBytes(encoding), outputMessage.getBody());
        outputMessage.flush();
    }

    protected static ServletServerHttpResponse createOutputMessage(HttpServletResponse response) {
        return new ServletServerHttpResponse(response);
    }

    public static final class Page {

        private org.springframework.data.domain.Page<?> page;

        public Page(org.springframework.data.domain.Page<?> page) {
            this.page = page;
        }

        @JSONField(ordinal = 4)
        public List<?> getContent() {
            return page.getContent();
        }

        @JSONField(ordinal = 1)
        public int getPage() {
            return page.getNumber();
        }

        @JSONField(ordinal = 2)
        public int getSize() {
            return page.getSize();
        }

        @JSONField(ordinal = 3)
        public long getTotal() {
            return page.getTotalElements();
        }

    }

    private final class BundleView implements View {

        private Object obj;

        public BundleView(Object obj) {
            this.obj = obj;
        }

        @Override
        public String getContentType() {
            return MediaType.APPLICATION_JSON_UTF8_VALUE;
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            ViewFragment.this.render(obj, getContentType(), createOutputMessage(response));
        }

    }

}
