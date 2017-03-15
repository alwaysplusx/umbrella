package com.harmony.umbrella.web.method;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.json.SerializerConfig;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.bind.annotation.PatternConverter;
import com.harmony.umbrella.web.method.RequestResponseBundleMethodProcessor.Page;

/**
 * @author wuxii@foxmail.com
 */
public class ViewFragment {

    SerializerConfigBuilder serializer;
    final Map<String, List<String>> headers;
    boolean simplePage = true;

    ViewFragment() {
        this.serializer = SerializerConfigBuilder.create();
        this.headers = new LinkedHashMap<>();
    }

    public ViewFragment simplePage(boolean simplePage) {
        this.simplePage = simplePage;
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
        this.simplePage = true;
    }

    private List<String> getRowHeader(String name) {
        List<String> rows = headers.get(name);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }

    public View finish(Object obj) {
        return new BundleView(obj, this);
    }

    private static final class BundleView implements View {

        private Object obj;
        private SerializerConfig serializerConfig;
        private Map<String, List<String>> headers;

        public BundleView(Object obj, ViewFragment vf) {
            this.serializerConfig = vf.serializer.build();
            this.headers = Collections.unmodifiableMap(vf.headers);
            this.obj = vf.simplePage && obj instanceof org.springframework.data.domain.Page ? new Page((org.springframework.data.domain.Page<?>) obj) : obj;
        }

        @Override
        public String getContentType() {
            return MediaType.APPLICATION_JSON_UTF8_VALUE;
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            ServletServerHttpResponse outputMessage = createOutputMessage(response);
            outputMessage.getHeaders().putAll(headers);
            outputMessage.getServletResponse().setContentType(getContentType());
            OutputStream body = outputMessage.getBody();
            String text = Json.toJson(obj, serializerConfig);
            IOUtils.write(text, body);
            body.flush();
        }

        protected ServletServerHttpResponse createOutputMessage(HttpServletResponse response) {
            return new ServletServerHttpResponse(response);
        }
    }

}
