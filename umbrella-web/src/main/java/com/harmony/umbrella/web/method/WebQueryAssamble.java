package com.harmony.umbrella.web.method;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.beans.TypeConverter;
import org.springframework.web.context.request.NativeWebRequest;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.util.MemberUtils;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.web.bind.annotation.RequestQueryBundle;

public class WebQueryAssamble {

    private String prefix;
    private String separator;
    private TypeConverter typeConverter;
    private Class<?> domainClass;

    RequestQueryBundle ann;
    private JpaQueryBuilder builder;

    public WebQueryAssamble(String prefix, String separator, TypeConverter typeConverter, Class<?> domainClass, RequestQueryBundle ann) {
        this.prefix = prefix;
        this.separator = separator;
        this.typeConverter = typeConverter;
        this.domainClass = domainClass;
        this.ann = ann;
        this.builder = new JpaQueryBuilder<>(domainClass);
    }

    protected void assembleParameters(Map<String, String[]> queryParams) {
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

    protected void assemblePaging(int page, int size) {
        builder.paging(page, size);
    }

    protected void assembleGroupBy(String[] gouping) {

    }

    protected void assembleOrderBy(String[] asc, String[] desc) {

    }

    public QueryBundle<?> bundle(NativeWebRequest webRequest) {
        Map<String, String[]> queryParams = PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
        if (!queryParams.isEmpty()) {
            assembleParameters(queryParams);
        }
        // TODO if query if null add default junction

        Integer page = typeConverter.convertIfNecessary(webRequest.getParameter("page"), Integer.class);
        Integer size = typeConverter.convertIfNecessary(webRequest.getParameter("size"), Integer.class);
        // if null use default
        final int pageNumber = (page == null && ann != null) ? ann.page() : -1;
        final int pageSize = (size == null && ann != null) ? ann.size() : -1;

        assemblePaging(pageNumber, pageSize);

        String[] asc = webRequest.getParameterValues("asc");
        String[] desc = webRequest.getParameterValues("desc");
        if (asc != null || desc != null) {

        }
        return builder.bundle();
    }

}