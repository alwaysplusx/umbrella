package com.harmony.console.log.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harmony.umbrella.data.domain.BaseEntity;
import com.harmony.umbrella.log.Level.StandardLevel;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "UMBRELLA_CONSOLE_LOG")
public class LogEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String module;
    @Column(name = "actionText")
    private String action;
    @Column(name = "keyText")
    private String key;
    private String message;
    private String result;
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date responseTime;
    private String throwableMessage;
    @Column(name = "levelText")
    private StandardLevel level;
    private String operatorName;
    private String operatorId;
    private String operatorHost;
    private String stackLocation;
    private String threadName;
    private String context;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return super.isNew();
    }

    public String getModule() {
        return module;
    }

    public String getAction() {
        return action;
    }

    public Object getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public String getThrowableMessage() {
        return throwableMessage;
    }

    public StandardLevel getLevel() {
        return level;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public Object getOperatorId() {
        return operatorId;
    }

    public String getOperatorHost() {
        return operatorHost;
    }

    public String getStackLocation() {
        return stackLocation;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getContext() {
        return context;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public void setThrowableMessage(String throwableMessage) {
        this.throwableMessage = throwableMessage;
    }

    public void setLevel(StandardLevel level) {
        this.level = level;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public void setOperatorHost(String operatorHost) {
        this.operatorHost = operatorHost;
    }

    public void setStackLocation(String stackLocation) {
        this.stackLocation = stackLocation;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
