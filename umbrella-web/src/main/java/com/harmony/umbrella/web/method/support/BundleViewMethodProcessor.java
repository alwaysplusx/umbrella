package com.harmony.umbrella.web.method.support;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.harmony.umbrella.json.KeyStyle;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.web.method.annotation.BundleView;
import com.harmony.umbrella.web.method.annotation.BundleView.Behavior;
import com.harmony.umbrella.web.method.annotation.BundleView.PatternBehavior;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * http request & response method processor
 *
 * @author wuxii@foxmail.com
 * @see BundleView
 */
public class BundleViewMethodProcessor implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {

    private int defaultSerializerFeatures;
    private String dateFormat;
    private KeyStyle keyStyle;

    private final Set<String> globalExcludeFields = new HashSet<>();
    private final Set<String> globalIncludeFields = new HashSet<>();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(ViewFragment.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ViewFragment viewFragment = createViewFragment(parameter);
        mavContainer.addAttribute(ViewFragment.VIEW_FRAGMENT, viewFragment);
        return viewFragment;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), BundleView.class) //
                || returnType.hasMethodAnnotation(BundleView.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        ViewFragment viewFragment = (ViewFragment) mavContainer.getModel().get(ViewFragment.VIEW_FRAGMENT);
        if (viewFragment == null) {
            viewFragment = createViewFragment(returnType);
        }
        viewFragment.render(returnValue, webRequest.getNativeResponse(HttpServletResponse.class));
    }

    protected ViewFragment createViewFragment(MethodParameter parameter) throws Exception {
        ViewFragment viewFragment = new ViewFragment();

        BundleView viewConfig = getBundleViewConfig(parameter);
        if (viewConfig != null) {
            SerializerConfigBuilder scb = viewFragment.getSerializerConfigBuilder();
            KeyStyle keyStyle = getViewKeyStyle(viewConfig);
            scb.addFeatures(viewConfig.features())
                    .addFilters(instanceFilters(viewConfig.filters()))
                    .setKeyStyle(keyStyle);

            Behavior behavior = viewConfig.behavior();
            if (Behavior.AUTO == behavior) {
                behavior = findTypeBehavior(parameter.getMethod());
            }
            viewFragment.setRenderBehavior(behavior);

            PatternBehavior patternBehavior = behavior;
            if (viewConfig.patternBehaviorClass() != PatternBehavior.class) {
                patternBehavior = viewConfig.patternBehaviorClass().newInstance();
            }

            merge(this.globalIncludeFields, viewConfig.includes())
                    .stream()
                    .map(e -> keyStyle.namingStrategy().translate(e))
                    .map(patternBehavior::convert)
                    .forEach(scb::addIncludePatterns);

            merge(this.globalExcludeFields, viewConfig.excludes())
                    .stream()
                    .map(e -> keyStyle.namingStrategy().translate(e))
                    .map(patternBehavior::convert)
                    .forEach(scb::addExcludePatterns);
        }

        return viewFragment;
    }

    private KeyStyle getViewKeyStyle(BundleView viewConfig) {
        KeyStyle keyStyle = viewConfig.style();
        return keyStyle == KeyStyle.NONE ? this.keyStyle : keyStyle;
    }

    private Collection<SerializeFilter> instanceFilters(Class<? extends SerializeFilter>[] filterClasses)
            throws IllegalAccessException, InstantiationException {
        List<SerializeFilter> filters = new ArrayList<>();
        for (Class<? extends SerializeFilter> filterClass : filterClasses) {
            filters.add(filterClass.newInstance());
        }
        return filters;
    }

    private BundleView getBundleViewConfig(MethodParameter methodParameter) {
        BundleView bundleView = methodParameter.getMethodAnnotation(BundleView.class);
        if (bundleView == null) {
            AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
            bundleView = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, BundleView.class);
        }
        return bundleView;
    }

    private Set<String> merge(Set<String> set, String... ss) {
        Set<String> result = new HashSet<>(set);
        result.addAll(Arrays.asList(ss));
        return result;
    }

    private Behavior findTypeBehavior(Method method) {
        if (method == null) {
            return Behavior.NONE;
        }
        Class<?> returnType = method.getReturnType();
        if (Page.class.isAssignableFrom(returnType)) {
            return Behavior.PAGE;
        } else if (Collection.class.isAssignableFrom(returnType) || returnType.isArray()) {
            return Behavior.ARRAY;
        } else if (Persistable.class.isAssignableFrom(returnType)) {
            return Behavior.AUTO;
        }
        return Behavior.NONE;
    }

    public void setKeyStyle(KeyStyle keyStyle) {
        this.keyStyle = keyStyle;
    }

    public Set<String> getGlobalExcludeFields() {
        return globalExcludeFields;
    }

    public Set<String> getGlobalIncludeFields() {
        return globalIncludeFields;
    }

    public void setDefaultSerializerFeatures(int defaultSerializerFeatures) {
        this.defaultSerializerFeatures = defaultSerializerFeatures;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
