package com.harmony.umbrella.web.method;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.util.MemberUtils;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private String prefix = "filter_";
    private String separator = "_";

    private int defaultPageNumber = 1;
    private int defaultPageSize = 20;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(QueryBundle.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Map<String, String[]> queryParams = PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
        if (queryParams.isEmpty()) {
            return null;
        }
        Class<?> domainClass = null;
        Type gType = parameter.getGenericParameterType();
        if (gType instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType) gType).getActualTypeArguments();
            domainClass = (Class) actualTypes[0];
        }
        if (domainClass == null) {
            throw new IllegalArgumentException("QueryBundle not have generic(domain) type");
        }
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, null);
        return createWebQueryAssemble(domainClass, binder).assemble(webRequest).bundle();
    }

    protected <T> WebQueryAssemble<T> createWebQueryAssemble(Class<T> domainClass, TypeConverter typeConverter) {
        return new WebQueryAssemble<T>(domainClass, typeConverter);
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

    public class WebQueryAssemble<T> {

        protected final TypeConverter typeConverter;

        protected final Class<T> domainClass;

        protected WebQueryAssemble(Class<T> domainClass, TypeConverter typeConverter) {
            this.domainClass = domainClass;
            this.typeConverter = typeConverter;
        }

        public JpaQueryBuilder<T> assemble(NativeWebRequest webRequest) {
            JpaQueryBuilder builder = new JpaQueryBuilder();
            builder.from(domainClass);
            webRequest.getParameterMap();
            assembleParameters(webRequest, builder);
            assemblePaging(webRequest, builder);
            assembleGroupBy(webRequest, builder);
            assembleOrderBy(webRequest, builder);
            return builder;
        }

        protected void assembleParameters(NativeWebRequest webRequest, JpaQueryBuilder builder) {
            Map<String, String[]> queryParams = PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
            Iterator<Entry<String, String[]>> it = queryParams.entrySet().iterator();
            for (; it.hasNext();) {
                Entry<String, String[]> entry = it.next();
                String key = entry.getKey().substring(prefix.length());
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
                            Operator operator = Operator.forName(raw.nextToken(separator));
                            String propertyName = raw.nextToken().replaceAll(separator, ".");
                            Member rawMember = MemberUtils.accessMember(domainClass, propertyName);
                            Object value = typeConverter.convertIfNecessary(values[index++], rawMember.getType());
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

        protected void assemblePaging(NativeWebRequest webRequest, JpaQueryBuilder builder) {
            String page = webRequest.getParameter("page");
            String size = webRequest.getParameter("size");
            Integer pageNumber = typeConverter.convertIfNecessary(page == null ? defaultPageNumber : page, Integer.class);
            Integer pageSize = typeConverter.convertIfNecessary(size == null ? defaultPageSize : size, Integer.class);
            builder.paging(pageNumber, pageSize);
        }

        protected void assembleGroupBy(NativeWebRequest webRequest, JpaQueryBuilder builder) {

        }

        protected void assembleOrderBy(NativeWebRequest webRequest, JpaQueryBuilder builder) {
            String[] ss = webRequest.getParameterValues("asc");
            if (ss != null) {
                for (String p : ss) {
                    StringTokenizer st = new StringTokenizer(p, ",");
                    while (st.hasMoreTokens()) {
                        builder.asc(st.nextToken().trim());
                    }
                }
            }
            ss = webRequest.getParameterValues("desc");
            if (ss != null) {
                for (String p : ss) {
                    StringTokenizer st = new StringTokenizer(p, ",");
                    while (st.hasMoreTokens()) {
                        builder.desc(st.nextToken().trim());
                    }
                }
            }
        }

        public TypeConverter getTypeConverter() {
            return typeConverter;
        }

        public Class<T> getDomainClass() {
            return domainClass;
        }

    }
}
