package com.harmony.umbrella.web.method.support;

import java.util.List;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.query.JpaQueryBuilder;

public class QueryParameter {

    protected String composition;
    protected String operator;
    protected String name;
    protected Object value;

    protected List<QueryParameter> params;

    public void apply(JpaQueryBuilder<?> builder) {
        // TODO 设置查询属性
        builder.start(getCompositionType());
        if (params != null && !params.isEmpty()) {
            for (QueryParameter param : params) {
                param.apply(builder);
            }
        }
    }

    protected CompositionType getCompositionType() {
        return composition == null ? CompositionType.AND : CompositionType.forName(composition.toLowerCase());
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<QueryParameter> getParams() {
        return params;
    }

    public void setParams(List<QueryParameter> params) {
        this.params = params;
    }

}