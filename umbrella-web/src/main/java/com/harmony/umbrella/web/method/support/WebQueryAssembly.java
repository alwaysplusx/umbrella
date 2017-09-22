package com.harmony.umbrella.web.method.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.IOUtils;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.method.annotation.BundleQuery;
import com.harmony.umbrella.web.method.support.BundleQueryMethodArgumentResolver.BundleQueryAnnotation;

/**
 * web查询条件组装器
 * 
 * @author wuxii@foxmail.com
 * @see BundleQuery
 * @see JpaQueryBuilder
 */
class WebQueryAssembly {

    private static final Log log = Logs.getLog(WebQueryAssembly.class);

    protected BundleQueryAnnotation ann;
    protected Class<?> domainClass;
    protected WebDataBinder binder;
    protected JpaQueryBuilder builder;
    protected HttpServletRequest request;

    public WebQueryAssembly(BundleQueryAnnotation ann, WebDataBinder binder, NativeWebRequest webRequest,
            Class domainClass) {
        this.ann = ann;
        this.builder = new JpaQueryBuilder<>(domainClass);
        this.domainClass = domainClass;
        this.binder = binder;
        this.request = webRequest.getNativeRequest(HttpServletRequest.class);
    }

    public JpaQueryBuilder<?> assemble() {
        builder.paging(getPage(), getSize())//
                .groupBy(getGrouping())//
                .asc(getAsc())//
                .desc(getDesc())//
                .enable(getQueryFeature());
        applyRequestQueryParameters();
        return builder;
    }

    protected void applyRequestQueryParameters() {
        String requestMethod = request.getMethod();
        List<QueryParameter> parameters = new ArrayList<>();
        if (requestMethod.equalsIgnoreCase("GET")) {
            Set<Entry<String, String[]>> entries = getRequestParameters().entrySet();
            for (Entry<String, String[]> entry : entries) {
                parameters.add(parseEntry(entry.getKey(), entry.getValue()));
            }
        } else if (requestMethod.equalsIgnoreCase("POST")) {
            try {
                InputStream is = request.getInputStream();
                String json = IOUtils.toString(is);
                if (isArray(json)) {
                    parameters.addAll(Json.parseArray(json, QueryParameter.class));
                } else {
                    parameters.add(Json.parse(IOUtils.toString(is), QueryParameter.class));
                }
            } catch (IOException e) {
            }
        }

        for (QueryParameter p : parameters) {
            p.apply(builder);
        }
    }

    private boolean isArray(String json) {
        json = json.trim();
        return json.startsWith("[") && json.endsWith("]");
    }

    protected int getPage() {
        int page = getInteger("page", -1);
        return page == -1 ? ann.page : page == 0 ? 0 : page - 1;
    }

    protected int getSize() {
        return getInteger("size", ann.size);
    }

    protected String[] getGrouping() {
        List<String> grouping = getParameters("grouping", ann.grouping);
        return grouping.toArray(new String[grouping.size()]);
    }

    protected String[] getAsc() {
        List<String> asc = getParameters("asc", ann.asc);
        return asc.toArray(new String[asc.size()]);
    }

    protected String[] getDesc() {
        List<String> desc = getParameters("desc", ann.desc);
        return desc.toArray(new String[desc.size()]);
    }

    protected QueryFeature[] getQueryFeature() {
        return ann.feature.toArray(new QueryFeature[ann.feature.size()]);
    }

    /**
     * 过滤web请求中的参数, 返回的参数已经去除了prefix
     * 
     * @return 请求参数集合
     */
    protected Map<String, String[]> getRequestParameters() {
        String prefix = ann.prefix;
        Map<String, String[]> result = new LinkedHashMap<>();
        Map<String, String[]> parameters = PropertiesUtils.filterStartWith(prefix, request.getParameterMap());
        for (String s : parameters.keySet()) {
            String[] value = parameters.get(s);
            String key = s.substring(prefix.length());
            if (StringUtils.isNotBlank(key)) {
                result.put(key, value);
            }
        }
        return result;
    }

    private List<String> getParameters(String name, List<String> def) {
        List<String> result = new ArrayList<>();
        String[] values = request.getParameterValues(name);
        if (values != null) {
            for (String s : values) {
                String[] vs = StringUtils.tokenizeToStringArray(s, ",", true, true);
                if (vs != null) {
                    for (String v : vs) {
                        if (!result.contains(v)) {
                            result.add(v);
                        }
                    }
                }
            }
        }
        return result.isEmpty() ? def : result;
    }

    private int getInteger(String name, int def) {
        String value = request.getParameter(name);
        try {
            return value == null ? def : binder.convertIfNecessary(value, Integer.class);
        } catch (TypeMismatchException e) {
            log.warn("request {} value type mismatch", name);
            return def;
        }
    }

    /**
     * 解析key, key的格式:
     * <ul>
     * <li>Operator_name
     * <li>CompositionType_Operator__name
     * </ul>
     * 
     * @param entry
     * @return
     */
    private QueryParameter parseEntry(String key, String[] value) {
        QueryParameter queryParameter = new QueryParameter();
        String lowerCaseKey = key.toLowerCase();
        CompositionType compositionType = CompositionType.AND;

        if (lowerCaseKey.startsWith("and_")) {
            compositionType = CompositionType.AND;
            key = key.substring(4);
        } else if (lowerCaseKey.startsWith("or_")) {
            compositionType = CompositionType.OR;
            key = key.substring(3);
        }

        StringTokenizer st = new StringTokenizer(key);
        queryParameter.setOperator(Operator.forName(st.nextToken(ann.prefix).toUpperCase()).toString());
        queryParameter.setName(st.nextToken(""));
        queryParameter.setValue(value.length == 1 ? value[0] : value);
        queryParameter.setComposition(compositionType.toString());
        return queryParameter;
    }

}
