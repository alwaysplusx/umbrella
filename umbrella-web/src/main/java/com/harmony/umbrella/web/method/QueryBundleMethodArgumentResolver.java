package com.harmony.umbrella.web.method;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.web.bind.annotation.RequestQueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private String prefix = "filter_";
    private String separator = "_";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(QueryBundle.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Map<String, String[]> queryParams = PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
        Class<?> domainClass = getMethodParameterDomainClass(parameter);
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, null);
        return new WebQueryAssamble(prefix, separator, binder, domainClass, parameter).bundle(webRequest);
    }

    private Class<?> getMethodParameterDomainClass(MethodParameter parameter) {
        Type gType = parameter.getGenericParameterType();
        if (gType instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType) gType).getActualTypeArguments();
            return (Class) actualTypes[0];
        }
        throw new IllegalArgumentException("QueryBundle not have generic(domain) type");
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

}
