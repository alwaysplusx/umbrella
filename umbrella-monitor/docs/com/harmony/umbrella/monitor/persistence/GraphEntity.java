package com.harmony.umbrella.monitor.persistence;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.harmony.umbrella.data.domain.Model;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "UMBRELLA_GRAPH")
@NamedQueries({
    @NamedQuery(name = "GraphEntity.findAll", query = "select o from GraphEntity o"), 
    @NamedQuery(name = "GraphEntity.findById", query = "select o from GraphEntity o where o.id =:id"),
    @NamedQuery(name = "GraphEntity.findByResource", query = "select o from GraphEntity o where o.resource =:resource")
})
public class GraphEntity extends Model<Long> {

    private static final long serialVersionUID = 2874692604975168947L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resource;
    private String graphType;
    private String argument;
    private String result;
    private String errorMessage;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar requestTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar responseTime;
    // 扩展属性
    protected String module;
    protected String operator;
    protected String level;

    public GraphEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Calendar getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Calendar requestTime) {
        this.requestTime = requestTime;
    }

    public Calendar getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Calendar responseTime) {
        this.responseTime = responseTime;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isException() {
        return this.errorMessage != null;
    }

}
