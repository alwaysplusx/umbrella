package com.harmony.umbrella.web.method.support;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.web.method.annotation.BundleQuery;
import com.harmony.umbrella.web.method.annotation.BundleResponse;
import com.harmony.umbrella.web.method.annotation.PatternConverter;

/**
 * @author wuxii@foxmail.com
 */
public class BundleRequestResponseMethodProcessor implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {

    public static final String VIEW_FRAGMENT = "viewFragment";

    private final String contentType;

    public BundleRequestResponseMethodProcessor() {
        this(MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    public BundleRequestResponseMethodProcessor(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(ViewFragment.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        // create and user default
        final ViewFragment viewFragment;
        if (parameter.hasMethodAnnotation(BundleQuery.class)) {
            viewFragment = buildViewFragment(parameter.getMethodAnnotation(BundleQuery.class), parameter.getMethod().getReturnType());
        } else {
            viewFragment = new ViewFragment();
        }
        mavContainer.addAttribute(VIEW_FRAGMENT, viewFragment);
        return viewFragment;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), BundleResponse.class) //
                || returnType.hasMethodAnnotation(BundleResponse.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {
        mavContainer.setRequestHandled(true);
        if (returnValue == null) {
            return;
        }

        final ModelMap modelMap = mavContainer.getModel();
        ViewFragment viewFragment = (ViewFragment) modelMap.get(VIEW_FRAGMENT);
        if (viewFragment == null) {
            if (returnType.hasMethodAnnotation(BundleQuery.class)) {
                viewFragment = buildViewFragment(returnType.getMethodAnnotation(BundleQuery.class), returnType.getParameterType());
            } else {
                viewFragment = new ViewFragment();
            }
        }

        if (viewFragment.simplePage && returnValue instanceof org.springframework.data.domain.Page) {
            returnValue = new Page((org.springframework.data.domain.Page<?>) returnValue);
        }

        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        outputMessage.getHeaders().putAll(viewFragment.headers);

        outputMessage.getServletResponse().setContentType(contentType);
        OutputStream body = outputMessage.getBody();
        IOUtils.write(viewFragment.serializer.toJson(returnValue), body);
        body.flush();
    }

    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(response);
    }

    private ViewFragment buildViewFragment(BundleQuery ann, Class<?> returnType) throws Exception {
        ViewFragment viewFragment = new ViewFragment();
        PatternConverter converter = ann.converter();
        if (PatternConverter.AUTO.equals(converter)) {
            converter = PatternConverter.suitableConverter(returnType);
        }
        viewFragment.features(ann.features())//
                .includes(converter, ann.includes())//
                .excludes(converter, ann.excludes())//
                .fetchLazy(ann.fetchLazy())//
                .safeFetch(ann.safeFetch())//
                .camelCase(ann.camelCase())//
                .simplePage(ann.simplePage());
        Class<? extends SerializeFilter>[] filtersClasses = ann.filters();
        for (Class<? extends SerializeFilter> c : filtersClasses) {
            viewFragment.filters(c.newInstance());
        }
        return viewFragment;
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
