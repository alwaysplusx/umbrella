package com.harmony.umbrella.web.method.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.Response;
import com.harmony.umbrella.web.method.annotation.BundleView.Behavior;
import com.harmony.umbrella.web.method.annotation.BundleView.PatternBehavior;
import com.harmony.umbrella.web.util.WebUtils;
import com.harmony.umbrella.web.util.WebUtils.PageImpl;

/**
 * 待渲染的视图片段(用于配置视图)
 * 
 * @author wuxii@foxmail.com
 */
public class ViewFragment {

    private static final Log log = Logs.getLog(ViewFragment.class);

    /**
     * ModelAndViewContainer中的key
     */
    public static final String VIEW_FRAGMENT = "viewFragment";
    public static final String ENCODING = "UTF-8";

    protected SerializerConfigBuilder serializerBuilder;
    protected final Map<String, List<String>> headers;
    protected String encoding = ENCODING;
    protected String contentType = MediaType.APPLICATION_JSON_UTF8_VALUE;
    private Behavior behavior;

    protected ViewFragment() {
        this.headers = new LinkedHashMap<>();
        this.serializerBuilder = SerializerConfigBuilder.newBuilder();
    }

    public ViewFragment setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public ViewFragment addExcludes(String... patterns) {
        this.serializerBuilder.addExcludePatterns(patterns);
        return this;
    }

    public ViewFragment addIncludes(String... patterns) {
        this.serializerBuilder.addIncludePatterns(patterns);
        return this;
    }

    public ViewFragment addExcludes(PatternBehavior converter, String... patterns) {
        this.serializerBuilder.addExcludePatterns(converter.convert(patterns));
        return this;
    }

    public ViewFragment addIncludes(PatternBehavior converter, String... patterns) {
        this.serializerBuilder.addIncludePatterns(converter.convert(patterns));
        return this;
    }

    public ViewFragment addFilters(SerializeFilter... filters) {
        this.serializerBuilder.addFilters(filters);
        return this;
    }

    public ViewFragment addFeatures(SerializerFeature... features) {
        serializerBuilder.addFeatures(features);
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

    public ViewFragment setRenderBehavior(Behavior behavior) {
        this.behavior = behavior;
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

    protected ViewFragment setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public void reset() {
        this.serializerBuilder = SerializerConfigBuilder.newBuilder();
        this.headers.clear();
        this.behavior = null;
        this.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE;
        this.encoding = ENCODING;
    }

    public SerializerConfigBuilder getSerializerConfigBuilder() {
        return this.serializerBuilder;
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
        render(o, contentType, createOutputMessage(response));
    }

    private void render(Object o, String contentType, ServletServerHttpResponse outputMessage) throws IOException {
        outputMessage.getHeaders().putAll(headers);
        HttpServletResponse response = outputMessage.getServletResponse();
        response.setContentType(contentType);

        String originEncoding = response.getCharacterEncoding();
        response.setCharacterEncoding(encoding);
        if (originEncoding != null && !originEncoding.equalsIgnoreCase(encoding)) {
            log.warn("response charset encoding already been set, expect set to {} but actual is {}", encoding, response.getCharacterEncoding());
        }

        if (o instanceof Page //
                && !(o instanceof PageImpl)//
                && Behavior.PAGE.equals(behavior)) {
            o = WebUtils.frontendPage((Page<?>) o);
        }

        if (o instanceof Response) {
            o = ((Response) o).toJson();
        }

        String text = o instanceof String ? (String) o : Json.toJson(o, serializerBuilder.build());
        IOUtils.write(text.getBytes(encoding), outputMessage.getBody());
        outputMessage.flush();
    }

    protected ServletServerHttpResponse createOutputMessage(HttpServletResponse response) {
        return new ServletServerHttpResponse(response);
    }

    private final class BundleView implements View {

        private Object obj;

        public BundleView(Object obj) {
            this.obj = obj;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            ViewFragment.this.render(obj, response);
        }

    }

}
