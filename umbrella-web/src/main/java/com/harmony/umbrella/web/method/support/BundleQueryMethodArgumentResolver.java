package com.harmony.umbrella.web.method.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryFeature;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.method.annotation.BundleQuery;

/**
 * @author wuxii@foxmail.com
 */
public class BundleQueryMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String QUERY_PARAM_PREFIX = "filter_";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(QueryBundle.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Class<?> domainClass = null;
        Type gType = parameter.getGenericParameterType();
        if (gType instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType) gType).getActualTypeArguments();
            domainClass = (Class) actualTypes[0];
        }
        if (domainClass == null) {
            throw new IllegalArgumentException("QueryBundle not have generic(domain) type");
        }

        // WebDataBinder binder = binderFactory.createBinder(webRequest, null, null);

        BundleQuery ann = parameter.getParameterAnnotation(BundleQuery.class);
        if (ann == null) {
            ann = parameter.getMethodAnnotation(BundleQuery.class);
        }

        if (ann != null) {
            String prefix = StringUtils.isNotBlank(ann.prefix()) ? ann.prefix() : QUERY_PARAM_PREFIX;
            Map<String, String[]> queryParams = PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
            // TODO 设置查询参数, grouping, order by
            QueryFeature[] features = ann.feature();
            String[] gouping = ann.gouping();
            String[] asc = ann.asc();
            String[] desc = ann.desc();
        }

        return null;
    }

}
