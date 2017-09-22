package com.harmony.umbrella.web.method.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryFeature;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.method.annotation.BundleQuery;

/**
 * http request query argument resolver
 * 
 * @author wuxii@foxmail.com
 * @see BundleQuery
 */
public class BundleQueryMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> rowType = parameter.getParameterType();
        return rowType == QueryBundle.class || QueryBuilder.class == rowType || JpaQueryBuilder.class == rowType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> domainClass = getDomainType(parameter);
        BundleQueryAnnotation ann = getBundleQueryAnnotation(parameter);
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, null);
        JpaQueryBuilder<?> builder = new WebQueryAssembly(ann, binder, webRequest, domainClass)//
                .assemble();

        return QueryBundle.class.isAssignableFrom(parameter.getParameterType()) ? builder.bundle() : builder;
    }

    public final Class<?> getDomainType(MethodParameter parameter) {
        Class<?> rowType = parameter.getParameterType();
        int gIndex = -1;
        if (QueryBundle.class.equals(rowType) || JpaQueryBuilder.class.equals(rowType)) {
            gIndex = 0;
        } else if (QueryBuilder.class.equals(rowType)) {
            gIndex = 1;
        }
        if (gIndex != -1) {
            Type gType = parameter.getGenericParameterType();
            if (gType instanceof ParameterizedType) {
                Type[] actualTypes = ((ParameterizedType) gType).getActualTypeArguments();
                if (actualTypes.length <= gIndex) {
                    throw new IllegalArgumentException("not give generic(domain) type");
                }
                return (Class) actualTypes[gIndex];
            }
        }
        throw new IllegalArgumentException("QueryBundle not have generic(domain) type");
    }

    protected BundleQueryAnnotation getBundleQueryAnnotation(MethodParameter parameter) {
        BundleQuery ann = parameter.getParameterAnnotation(BundleQuery.class) != null //
                ? parameter.getParameterAnnotation(BundleQuery.class) //
                : parameter.getMethodAnnotation(BundleQuery.class);
        return ann == null ? BundleQueryAnnotation.EMPTY_QUERY_ANNOTATION : BundleQueryAnnotation.create(ann);
    }

    public static final class BundleQueryAnnotation {

        private static final String QUERY_PARAM_PREFIX = "filter_";
        private static final String QUERY_PARAM_SEPARATOR = "_";
        private static final BundleQueryAnnotation EMPTY_QUERY_ANNOTATION = //
                new BundleQueryAnnotation(//
                        QUERY_PARAM_PREFIX, //
                        QUERY_PARAM_SEPARATOR, //
                        0, //
                        20, //
                        Collections.emptyList(), //
                        Collections.emptyList(), //
                        Collections.emptyList(), //
                        Collections.emptyList()//
                );

        public final String prefix;
        public final String separator;
        public final int page;
        public final int size;
        public final List<String> grouping;
        public final List<String> asc;
        public final List<String> desc;
        public final List<QueryFeature> feature;

        private BundleQueryAnnotation(String prefix, String separator, int page, int size, List<String> grouping,
                List<String> asc, List<String> desc, List<QueryFeature> feature) {
            this.prefix = prefix;
            this.separator = separator;
            this.page = page;
            this.size = size;
            this.grouping = grouping;
            this.asc = asc;
            this.desc = desc;
            this.feature = feature;
        }

        public static BundleQueryAnnotation create(BundleQuery ann) {
            Assert.notNull(ann, "bundle query annotation is null");
            String prefix = StringUtils.isBlank(ann.prefix()) ? QUERY_PARAM_PREFIX : ann.prefix();
            String separator = StringUtils.isBlank(ann.separator()) ? QUERY_PARAM_SEPARATOR : ann.separator();
            List<String> grouping = Collections.unmodifiableList(Arrays.asList(ann.grouping()));
            List<String> asc = Collections.unmodifiableList(Arrays.asList(ann.asc()));
            List<String> desc = Collections.unmodifiableList(Arrays.asList(ann.desc()));
            List<QueryFeature> feature = Collections.unmodifiableList(Arrays.asList(ann.feature()));
            return new BundleQueryAnnotation(prefix, separator, ann.page(), ann.size(), grouping, asc, desc, feature);
        }

    }

}
