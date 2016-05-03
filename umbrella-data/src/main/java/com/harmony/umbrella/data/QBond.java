package com.harmony.umbrella.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.harmony.umbrella.data.query.SpecificationTransform.JpaUtils;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * 辅助查询工具类, 包含了查询的语句, 统计的语句, 删除的语句. 以及各个查询条件的where语句(where 语句为准备语句)
 * <p>
 * 使用时候通过{@linkplain QBond#getQuery()}获取查询条件并可以将查询的条件{@linkplain #getParams()}
 * 设置为查询的参数
 * 
 * @author wuxii@foxmail.com
 */
public class QBond {

    public static final String SELECT_QUERY_STRING = "select x from %s x";

    public static final String COUNT_QUERY_STRING = "select count(x) from %s x";

    public static final String DELETE_QUERY_STRING = "delete from %s x";

    private static final int Q = 0;
    private static final int C = 1;
    private static final int D = 2;

    private final String entityName;
    private final String whereClause;
    private final Map<String, Object> params;

    public QBond(Class<?> domainClass, String whereClause, Map<String, Object> params) {
        this(JpaUtils.getEntityName(domainClass), whereClause, params);
    }

    public QBond(String entityName, String whereClause, Map<String, Object> params) {
        Assert.notBlank(entityName, "entity name must not be null");
        this.entityName = entityName;
        this.whereClause = formatWhereClause(whereClause);
        if (params != null && !params.isEmpty()) {
            this.params = Collections.unmodifiableMap(params);
        } else {
            this.params = Collections.emptyMap();
        }
    }

    /**
     * 传入的query条件语句加上where语句
     * 
     * @param query
     *            查询条件
     */
    private String formatWhereClause(String query) {
        if (StringUtils.isBlank(query))
            return "";
        String lowerWhereClause = query.toLowerCase().trim();
        if (lowerWhereClause.startsWith("where ")) {
            return query.trim();
        }
        return "where " + query.trim();
    }

    public String getEntityName() {
        return entityName;
    }

    /**
     * 查询的HQL
     */
    public String getQuery() {
        return getQuery(Q);
    }

    /**
     * 统计的HQL
     */
    public String getDeleteQuery() {
        return getQuery(D);
    }

    /**
     * 删除的HQL
     */
    public String getCountQuery() {
        return getQuery(C);
    }

    /**
     * 是否有查询条件
     */
    public boolean hasWhereClause() {
        return StringUtils.isNotBlank(whereClause);
    }

    /**
     * 查询参数对应的值
     * 
     * @param key
     *            查询参数名称
     * @return 查询参数值
     */
    public Object getValue(String key) {
        return params.get(key);
    }

    /**
     * 查询的参数条件keys
     */
    public Iterator<String> paramKeys() {
        return params.keySet().iterator();
    }

    /**
     * 所有参数值
     * 
     * @return 所有参数值
     */
    public Collection<Object> getValues() {
        return params.values();
    }

    /**
     * 查询参数Map
     * 
     * @return 参数Map
     */
    public Map<String, Object> getParams() {
        return params;
    }

    private String getQuery(int type) {
        String template = type == Q ? SELECT_QUERY_STRING : type == C ? COUNT_QUERY_STRING : DELETE_QUERY_STRING;
        StringBuilder buf = new StringBuilder(String.format(template, entityName));
        if (hasWhereClause()) {
            buf.append(" ").append(this.whereClause);
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("{\n");
        buf.append("  query string -> ").append(getQuery(Q)).append("\n")
           .append("  params -> {").append("\n");
        Iterator<Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            buf.append("    ").append(entry.getKey()).append(":").append(entry.getValue());
            if (it.hasNext()) {
                buf.append(",\n");
            }
        }
        buf.append("\n  }\n}");
        return buf.toString();
    }

}
