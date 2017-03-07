package com.harmony.umbrella.web.method;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.NativeWebRequest;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.util.MemberUtils;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.web.bind.annotation.Conjunction;
import com.harmony.umbrella.web.bind.annotation.Disjunction;
import com.harmony.umbrella.web.bind.annotation.RequestQueryBundle;
import com.harmony.umbrella.web.bind.annotation.RequestQueryBundle.Junction;

public class WebQueryAssamble {

    private String prefix;
    private String separator;
    private TypeConverter typeConverter;
    private Class<?> domainClass;
    private MethodParameter parameter;

    private JpaQueryBuilder builder;

    public WebQueryAssamble(String prefix, String separator, TypeConverter typeConverter, Class<?> domainClass, MethodParameter parameter) {
        this.prefix = prefix;
        this.separator = separator;
        this.typeConverter = typeConverter;
        this.domainClass = domainClass;
        this.parameter = parameter;
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
        for (String s : gouping) {
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                builder.groupBy(st.nextToken().trim());
            }
        }
    }

    protected void assembleAsc(String[] asc) {
        for (String s : asc) {
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                builder.asc(st.nextToken().trim());
            }
        }
    }

    protected void assembleDesc(String[] desc) {
        for (String s : desc) {
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                builder.desc(st.nextToken().trim());
            }
        }
    }

    public QueryBundle<?> bundle(NativeWebRequest webRequest) {
        // query params
        Map<String, String[]> queryParams = PropertiesUtils.filterStartWith(prefix, webRequest.getParameterMap());
        RequestQueryBundle ann = parameter.getParameterAnnotation(RequestQueryBundle.class);
        Conjunction conjunction = parameter.getParameterAnnotation(Conjunction.class);
        Disjunction disjunction = parameter.getParameterAnnotation(Disjunction.class);

        if (!queryParams.isEmpty()) {
            assembleParameters(queryParams);
        }
        /*else {
            Specification spec = requestQueryBundle != null || requestQueryBundle.bundle() == Junction.DISJUNCTION ? QueryUtils.disjunction()
                    : QueryUtils.conjunction();
            builder.withSpecification(spec);
        }*/
        // paging
        assemblePaging(//
                getParameter(webRequest, "page", ann == null ? -1 : ann.page()), //
                getParameter(webRequest, "size", ann == null ? -1 : ann.size())//
        );

        // asc
        assembleAsc(getParameterValues(webRequest, "asc", ann == null ? new String[0] : ann.asc()));

        // desc
        assembleDesc(getParameterValues(webRequest, "desc", ann == null ? new String[0] : ann.desc()));

        // gouping
        assembleGroupBy(getParameterValues(webRequest, "gouping", ann == null ? new String[0] : ann.gouping()));

        return builder.bundle();
    }

    private int getParameter(NativeWebRequest webRequest, String key, int defaultValues) {
        Integer v = typeConverter.convertIfNecessary(webRequest.getParameter(key), Integer.class);
        return v == null ? defaultValues : v;
    }

    private String[] getParameterValues(NativeWebRequest webRequest, String key, String[] defaultValues) {
        String[] v = webRequest.getParameterValues(key);
        return v == null ? defaultValues : v;
    }

}