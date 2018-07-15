package com.harmony.umbrella.web.method.support;

import java.util.List;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.JpaQueryBuilder;

/**
 * web 查询参数一体化处理
 * 
 * @author wuxii@foxmail.com
 */
public final class QueryParameter {

    protected String name;
    protected Object value;
    protected String operator;
    protected String composition;

    protected List<QueryParameter> params;

    private Operator operatorType;
    private CompositionType compositionType;

    public QueryParameter() {
    }

    public QueryParameter(String name, Object value) {
        this(name, value, Operator.EQUAL);
    }

    public QueryParameter(String name, Object value, Operator operator) {
        this.name = name;
        this.value = value;
        this.operator = operator.qualifiedName();
    }

    public void apply(JpaQueryBuilder<?> builder) {
        if (CompositionType.OR.equals(getCompositionType())) {
            builder.or();
        } else {
            builder.and();
        }
        builder.addCondition(name, value, getOperatorType());
        if (params != null && !params.isEmpty()) {
            builder.begin();
            for (QueryParameter param : params) {
                param.apply(builder);
            }
            builder.end();
        }
    }

    protected Operator getOperatorType() {
        if (operatorType == null) {
            if (operator == null) {
                operatorType = Operator.EQUAL;
            } else {
                int ordinal = -1;
                try {
                    ordinal = Integer.parseInt(operator);
                } catch (NumberFormatException e) {
                }
                for (Operator op : Operator.values()) {
                    if (op.name().equalsIgnoreCase(operator) //
                            || op.qualifiedName().equalsIgnoreCase(operator) //
                            || op.qualifiedName().replace("_", "").equalsIgnoreCase(operator)//
                            || op.symbol().equals(operator)//
                            || op.ordinal() == ordinal) {
                        operatorType = op;
                        break;
                    }
                }
                if (operatorType == null) {
                    throw new IllegalArgumentException("illegal query operator name " + operator);
                }
            }
        }
        return operatorType;
    }

    protected CompositionType getCompositionType() {
        if (compositionType == null) {
            if (composition == null) {
                compositionType = CompositionType.AND;
            } else {
                int ordinal = -1;
                try {
                    ordinal = Integer.parseInt(composition);
                } catch (NumberFormatException e) {
                }
                for (CompositionType c : CompositionType.values()) {
                    if (c.name().equalsIgnoreCase(composition) //
                            || c.qualifiedName().equalsIgnoreCase(composition)//
                            || c.ordinal() == ordinal) {
                        compositionType = c;
                        break;
                    }
                }
                if (compositionType == null) {
                    throw new IllegalArgumentException("illegal query composition " + composition);
                }
            }
        }
        return compositionType;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
        this.compositionType = null;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
        this.operatorType = null;
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