package com.harmony.umbrella.web.method;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.harmony.umbrella.json.FilterMode;
import com.harmony.umbrella.json.SerializerConfig;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.bind.annotation.BundleController;
import com.harmony.umbrella.web.bind.annotation.PatternConverter;
import com.harmony.umbrella.web.bind.annotation.ResponseBundle;
import com.harmony.umbrella.web.bind.annotation.Serialization;

/**
 * @author wuxii@foxmail.com
 */
public class ResponseBundleReturnValueHandler implements HandlerMethodReturnValueHandler {

    List<HttpMessageConverter<?>> messageConverters;

    private boolean fetchLazy = true;

    private boolean safeFetch = true;

    private boolean camelCase = false;

    private boolean simplePage = true;

    private String contentType = MediaType.APPLICATION_JSON_UTF8_VALUE;

    public ResponseBundleReturnValueHandler() {
    }

    public ResponseBundleReturnValueHandler(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getContainingClass().getAnnotation(BundleController.class) != null //
                || returnType.hasMethodAnnotation(ResponseBundle.class);
        /*|| (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class)//
        || returnType.hasMethodAnnotation(ResponseBody.class));*/
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {
        mavContainer.setRequestHandled(true);
        ModelMap modelMap = mavContainer.getModel();
        if (returnValue == null) {
            returnValue = modelMap.get("result");
            if (returnValue == null) {
                return;
            }
        }
        // 
        FilterMode defaultFilterMode = (FilterMode) modelMap.getOrDefault("serialer.defaultFilterMode", FilterMode.EXCLUDE);

        SerializerConfigBuilder serialer = SerializerConfigBuilder.create(defaultFilterMode)//
                .camelCase(camelCase)//
                .fetchLazyAttribute(fetchLazy)//
                .safeFetch(safeFetch);

        boolean ignore = (boolean) modelMap.getOrDefault("serialer.ignoreSerializationAnnotation", true);
        Serialization ann = returnType.getMethodAnnotation(Serialization.class);

        if (!ignore && ann != null) {
            PatternConverter converter = ann.converter();
            if (PatternConverter.AUTO.equals(converter)) {
                converter = PatternConverter.suitableConverter(returnValue.getClass());
            }

            serialer.withFeature(ann.features())//
                    .includePatterns(converter.convert(ann.includes()))//
                    .excludePatterns(converter.convert(ann.excludes()))//
                    .fetchLazyAttribute(ann.fetchLazy())//
                    .safeFetch(ann.safeFetch())//
                    .camelCase(ann.camelCase());

            Class<? extends SerializeFilter>[] filtersClasses = ann.filters();
            for (Class<? extends SerializeFilter> c : filtersClasses) {
                serialer.withFilter(c.newInstance());
            }
        }

        if (returnValue instanceof org.springframework.data.domain.Page) {
            // TODO simple page
            returnValue = new Page((org.springframework.data.domain.Page<?>) returnValue);
        }

        SerializerConfig serialConfig = (SerializerConfig) modelMap.get("serialer.config");
        if (serialConfig != null) {
            serialer.withFilter(serialConfig.getFilters())//
                    .withSerializeConfig(serialConfig.getFastjsonSerializeConfig())//
                    .withFeature(serialConfig.getFeatures());
        }

        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        Map<String, List<String>> headers = (Map<String, List<String>>) modelMap.get("render.headers");
        if (headers != null && headers.isEmpty()) {
            outputMessage.getHeaders().putAll(headers);
        }
        outputMessage.getServletResponse().setContentType(contentType);
        OutputStream body = outputMessage.getBody();
        IOUtils.write(serialer.toJson(returnValue), body);
        body.flush();
    }

    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(response);
    }

    public boolean isFetchLazy() {
        return fetchLazy;
    }

    public void setFetchLazy(boolean fetchLazy) {
        this.fetchLazy = fetchLazy;
    }

    public boolean isSafeFetch() {
        return safeFetch;
    }

    public void setSafeFetch(boolean safeFetch) {
        this.safeFetch = safeFetch;
    }

    public boolean isCamelCase() {
        return camelCase;
    }

    public void setCamelCase(boolean camelCase) {
        this.camelCase = camelCase;
    }

    public boolean isSimplePage() {
        return simplePage;
    }

    public void setSimplePage(boolean simplePage) {
        this.simplePage = simplePage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

}
