package com.harmony.umbrella.web.method.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.harmony.umbrella.json.KeyStyle;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.web.method.annotation.BundleController;
import com.harmony.umbrella.web.method.annotation.BundleView;
import com.harmony.umbrella.web.method.annotation.BundleView.Behavior;
import com.harmony.umbrella.web.method.annotation.BundleView.PatternBehavior;

/**
 * http request & response method processor
 * 
 * @author wuxii@foxmail.com
 * @see BundleView
 */
public class BundleViewMethodProcessor implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {

    private static final Log log = Logs.getLog(BundleViewMethodProcessor.class);

    public static final String[] DEFAULT_EXCLUDES = { "**.id", "**.new" };

    private Set<String> includes;
    private Set<String> excludes;

    public BundleViewMethodProcessor() {
        this(new HashSet<>(Arrays.asList(DEFAULT_EXCLUDES)));
    }

    public BundleViewMethodProcessor(Set<String> defaultExcludes) {
        this.excludes = defaultExcludes;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(ViewFragment.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        final ViewFragment viewFragment = getOrCreateViewFragment(mavContainer, true);
        fillViewFragment(parameter, viewFragment);
        return viewFragment;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), BundleView.class) //
                || returnType.hasMethodAnnotation(BundleView.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {
        mavContainer.setRequestHandled(true);
        boolean viewFragmentCreated = mavContainer.containsAttribute(ViewFragment.VIEW_FRAGMENT);
        ViewFragment viewFragment = getOrCreateViewFragment(mavContainer, false);
        if (!viewFragmentCreated) {
            fillViewFragment(returnType, viewFragment);
        }
        viewFragment.render(returnValue, webRequest.getNativeResponse(HttpServletResponse.class));
    }

    /**
     * 获取view fragment, 在mavContainer中获取view fragment. 如果未找到则直接创建
     * 
     * @param mavContainer
     *            mav container
     * @param addAttribute
     *            如果是新创建的情况是否直接添加到mav container中
     * @return view fragment
     */
    protected final ViewFragment getOrCreateViewFragment(ModelAndViewContainer mavContainer, boolean addAttribute) {
        Object o = mavContainer.getModel().get(ViewFragment.VIEW_FRAGMENT);
        if (o != null && !(o instanceof ViewFragment)) {
            throw new IllegalArgumentException("model attribute viewFragment not instance " + ViewFragment.class);
        }
        final ViewFragment viewFragment = (o == null) ? new ViewFragment() : (ViewFragment) o;
        if (addAttribute) {
            mavContainer.addAttribute(ViewFragment.VIEW_FRAGMENT, viewFragment);
        }
        return viewFragment;
    }

    /**
     * 根据方法上注有的{@code BundleView}注解来配置默认的{@code ViewFragment}
     * 
     * @param parameter
     *            方法参数
     * @param viewFragment
     *            viewFragment
     * @throws Exception
     *             can't create PatternConverter
     */
    protected void fillViewFragment(MethodParameter parameter, ViewFragment viewFragment) throws Exception {
        BundleView ann = parameter.getMethodAnnotation(BundleView.class);
        if (ann == null) {
            ann = BundleController.class.getAnnotation(BundleView.class);
        }
        if (ann == null) {
            log.info("{} no default bundle view", parameter);
            return;
        }

        SerializerConfigBuilder scb = viewFragment.getSerializerConfigBuilder();

        scb.addFeatures(ann.features());
        Class<? extends SerializeFilter>[] filtersClasses = ann.filters();
        for (Class<? extends SerializeFilter> c : filtersClasses) {
            scb.addFilters(c.newInstance());
        }

        // find behavior and set to view fragment
        Behavior behavior = ann.behavior();
        if (Behavior.AUTO.equals(behavior)) {
            Class<?> returnType = parameter.getMethod().getReturnType();
            behavior = findTypeBehavior(returnType);
        }
        viewFragment.setRenderBehavior(behavior);

        // configuration serializer include/exclude patterns
        PatternBehavior patterbBehavior = behavior;
        if (ann.patternBehavoirClass() != PatternBehavior.class) {
            patterbBehavior = ann.patternBehavoirClass().newInstance();
        }

        Set<String> excludes = asSet(ann.excludes());
        Set<String> includes = asSet(ann.includes());

        if (this.includes != null) {
            for (String s : this.includes) {
                if (!excludes.contains(s)) {
                    includes.add(s);
                }
            }
        }

        if (this.excludes != null) {
            for (String s : this.excludes) {
                if (!includes.contains(s)) {
                    excludes.add(s);
                }
            }
        }

        scb.addExcludePatterns(patterbBehavior.convert(excludes.toArray(new String[excludes.size()])))//
                .addIncludePatterns(patterbBehavior.convert(includes.toArray(new String[includes.size()])));

        KeyStyle style = ann.style();
        if (!KeyStyle.ORIGIN.equals(style)) {
            scb.setKeyStyle(style);
        }

    }

    protected Set<String> asSet(String... ss) {
        return new HashSet<>(Arrays.asList(ss));
    }

    protected Behavior findTypeBehavior(Class<?> type) {
        if (Page.class.isAssignableFrom(type)) {
            return Behavior.PAGE;
        } else if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            return Behavior.ARRAY;
        } else if (Persistable.class.isAssignableFrom(type)) {
            return Behavior.AUTO;
        }
        return Behavior.NONE;
    }

    protected Set<String> getIncludes() {
        return includes;
    }

    protected void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    protected Set<String> getExcludes() {
        return excludes;
    }

    protected void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

}
