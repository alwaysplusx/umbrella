package com.harmony.umbrella.web.method.support;

import java.lang.reflect.Constructor;

import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.harmony.umbrella.web.method.annotation.BundleParam;

/**
 * 模型数据处理器
 *
 * @author wuxii@foxmail.com
 * @see ModelFragment
 * @see com.harmony.umbrella.web.servlet.view.ModelResourceView
 * @see com.harmony.umbrella.web.servlet.handler.ModelFragmentInterceptor
 */
public class BundleParamMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ModelFragment.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, null);
        Class<?> rowType = parameter.getParameterType();
        ModelFragment fragment = getFragment(mavContainer, rowType);
        if (fragment == null) {
            try {
                Constructor constructor = rowType.getConstructor(TypeConverter.class);
                fragment = (ModelFragment) constructor.newInstance(binder);
            } catch (Exception e) {
                throw new IllegalArgumentException("can't create fragment, no public <init>(TypeConverter) " + rowType, e);
            }
            mavContainer.addAttribute(ModelFragment.MODEL_FRAGMENT, fragment);
        }
        BundleParam ann = parameter.getParameterAnnotation(BundleParam.class);
        if (ann != null) {
            applyParams(ann.params(), webRequest, fragment);
        }
        return fragment;
    }

    protected void applyParams(String[] names, NativeWebRequest webRequest, ModelFragment fragment) {
        for (String param : names) {
            String[] values = webRequest.getParameterValues(param);
            fragment.setData(param, values != null && values.length == 1 ? values[0] : values);
        }
    }

    protected final <T extends ModelFragment> T getFragment(ModelAndViewContainer mavContainer, Class<?> requireType) {
        Object o = mavContainer.getModel().get(ModelFragment.MODEL_FRAGMENT);
        if (o != null && !(requireType.isInstance(o))) {
            throw new IllegalArgumentException("model attribute modelFragment not instance of " + requireType);
        }
        return (T) o;
    }

}