package com.harmony.umbrella.web.method.support;

import com.harmony.umbrella.web.method.annotation.BundleModel;
import org.springframework.beans.BeanUtils;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletRequest;

/**
 * http request model param argument resolver
 *
 * @author wuxii@foxmail.com
 * @author wuxii@foxmail.com
 * @see BundleModel
 */
public class BundleModelMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BundleModel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        BundleModel ann = parameter.getParameterAnnotation(BundleModel.class);
        String name = "".equals(ann.model()) ? Conventions.getVariableNameForParameter(parameter) : ann.model();

        Object attribute = (mavContainer.containsAttribute(name) //
                ? mavContainer.getModel().get(name)//
                : createAttribute(name, parameter, binderFactory, webRequest));

        WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);

        bindRequestParameters(binder, ann, webRequest);

        return binder.getTarget();
    }

    protected void bindRequestParameters(WebDataBinder binder, BundleModel ann, NativeWebRequest webRequest) {
        binder.bind(//
                new ServletRequestParameterPropertyValues(//
                        webRequest.getNativeRequest(ServletRequest.class), //
                        ann.prefix(), //
                        "".equals(ann.prefix()) ? "" : ann.separator()//
                )//
        );
    }

    protected Object createAttribute(String name, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) {
        return BeanUtils.instantiateClass(parameter.getParameterType());
    }

}
