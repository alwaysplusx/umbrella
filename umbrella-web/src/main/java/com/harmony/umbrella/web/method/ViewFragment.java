package com.harmony.umbrella.web.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.FilterMode;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.web.bind.annotation.PatternConverter;

/**
 * @author wuxii@foxmail.com
 */
public class ViewFragment {

    static final String SERIALER_IGNORE_SERIALIZATION = "serialer.ignoreSerialization";
    static final String SERIALER_SIMPLE_PAGE = "serialer.simplePage";
    static final String SERIALER_CONFIG = "serialer.config";
    static final String RENDER_HEADERS = "render.headers";

    private final SerializerConfigBuilder serializer;
    private final Map<String, List<String>> headers;
    private final ModelMap modelMap;
    private boolean ignoreSerialization;
    private boolean simplePage;

    private boolean finished;

    public ViewFragment(ModelMap modelMap, FilterMode mode) {
        this.serializer = SerializerConfigBuilder.create(mode);
        this.modelMap = modelMap;
        this.headers = new LinkedHashMap<>();
    }

    public SerializerConfigBuilder serializer() {
        return serializer;
    }

    public ViewFragment ignoreSerialization(boolean ignore) {
        this.ignoreSerialization = ignore;
        return this;
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

    private List<String> getRowHeader(String name) {
        List<String> rows = headers.get(name);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }

    void finish() {
        if (!finished) {
            modelMap.addAttribute(SERIALER_IGNORE_SERIALIZATION, ignoreSerialization);
            modelMap.addAttribute(SERIALER_SIMPLE_PAGE, simplePage);
            modelMap.addAttribute(SERIALER_CONFIG, serializer.build());
            modelMap.addAttribute(RENDER_HEADERS, Collections.unmodifiableMap(headers));
            finished = true;
        }
    }

}
