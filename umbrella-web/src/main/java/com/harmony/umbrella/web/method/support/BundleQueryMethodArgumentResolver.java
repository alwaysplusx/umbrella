package com.harmony.umbrella.web.method.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryFeature;
import com.harmony.umbrella.util.MemberUtils;
import com.harmony.umbrella.util.PropertiesUtils;
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
        return rowType.isAssignableFrom(QueryBundle.class) || rowType.isAssignableFrom(QueryBuilder.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        // 不同的参数类型选择的泛型位置不同
        Class<?> domainClass = getDomainType(parameter);
        JpaQueryBuilder builder = new JpaQueryBuilder<>(domainClass);

        BundleQueryAnnotation ann = getBundleQueryAnnotation(parameter);

        apply(ann, builder);

        Map<String, String[]> queryParams = filterOut(ann.prefix, webRequest);

        WebDataBinder binder = binderFactory.createBinder(webRequest, null, null);

        if (!queryParams.isEmpty()) {
            WebQueryBuilder queryBuilder = new WebQueryBuilder(domainClass, ann, binder, builder);
            queryBuilder.apply(queryParams);
        }

        return QueryBundle.class.isAssignableFrom(parameter.getParameterType()) ? builder.bundle() : builder;
    }

    protected void apply(BundleQueryAnnotation ann, JpaQueryBuilder builder) {
        builder.enable(ann.feature)//
                .paging(ann.page, ann.size)//
                .groupBy(ann.grouping)//
                .asc(ann.asc)//
                .desc(ann.desc);
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

    protected Map<String, String[]> filterOut(String prefix, NativeWebRequest webRequest) {
        return PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
    }

    protected BundleQueryAnnotation getBundleQueryAnnotation(MethodParameter parameter) {
        BundleQuery ann = parameter.getParameterAnnotation(BundleQuery.class) != null //
                ? parameter.getParameterAnnotation(BundleQuery.class) //
                : parameter.getMethodAnnotation(BundleQuery.class);
        return ann == null ? BundleQueryAnnotation.EMPTY_QUERY_ANNOTATION : BundleQueryAnnotation.create(ann);
    }

    private static final class WebQueryBuilder {

        private BundleQueryAnnotation ann;
        private WebDataBinder binder;
        private JpaQueryBuilder builder;
        private Class domainClass;

        public WebQueryBuilder(Class domainClass, BundleQueryAnnotation ann, WebDataBinder binder, JpaQueryBuilder builder) {
            this.binder = binder;
            this.domainClass = domainClass;
            this.builder = builder;
            this.ann = ann;
        }

        public void apply(Map<String, String[]> queryParams) {
            Iterator<Entry<String, String[]>> it = queryParams.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String[]> entry = it.next();
                String key = entry.getKey().substring(ann.prefix.length());
                String[] values = entry.getValue();
                StringTokenizer st = new StringTokenizer(key, "AND");
                int index = 0;
                for (; st.hasMoreTokens();) {
                    builder.and();
                    StringTokenizer rawConditionTokenizer = new StringTokenizer(st.nextToken(), "OR");
                    builder.start();
                    try {
                        for (; rawConditionTokenizer.hasMoreTokens();) {
                            StringTokenizer raw = new StringTokenizer(rawConditionTokenizer.nextToken());
                            Operator operator = Operator.forName(raw.nextToken(ann.separator));
                            String propertyName = raw.nextToken().replaceAll(ann.separator, ".");
                            Member rawMember = MemberUtils.accessMember(domainClass, propertyName);
                            Object value = binder.convertIfNecessary(values[index++], rawMember.getType());
                            builder.addCondition(propertyName, value, operator);
                            if (rawConditionTokenizer.hasMoreTokens()) {
                                builder.or();
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("not enough query parameters", e);
                    }
                    builder.end();
                }
            }
        }

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

        private BundleQueryAnnotation(String prefix, String separator, int page, int size, List<String> grouping, List<String> asc, List<String> desc,
                List<QueryFeature> feature) {
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
