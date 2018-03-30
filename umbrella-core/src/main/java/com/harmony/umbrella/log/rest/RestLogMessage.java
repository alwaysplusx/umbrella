package com.harmony.umbrella.log.rest;

import java.io.Serializable;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class RestLogMessage implements Serializable {

    private static final long serialVersionUID = -8825551673574382941L;

    private String appName;
    private String id;
    private String module;
    private String action;

    private String key;

    private String message;
    private String throwable;
    private String level;

    private String username;
    private Object userId;
    private String userHost;

    private String result;

    private Long requestTime;
    private Long responseTime;

    private String location;
    private String thread;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThrowable() {
        return throwable;
    }

    public void setThrowable(String throwable) {
        this.throwable = throwable;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserHost() {
        return userHost;
    }

    public void setUserHost(String userHost) {
        this.userHost = userHost;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    @Override
    public String toString() {
        return "{appName: " + appName + ", id: " + id + ", action: " + action + ", username: " + username + "}";
    }

    public static RestLogMessage convert(LogInfo info) {
        return convert(ContextHelper.getApplicationName(), info);
    }

    public static RestLogMessage convert(String appName, LogInfo info) {
        RestLogMessage o = new RestLogMessage();
        o.action = info.getAction();
        o.appName = appName;
        o.id = info.getMessageId();
        o.key = info.getKey() != null ? info.getKey().toString() : null;
        o.level = info.getLevel() != null ? info.getLevel().name() : null;
        o.location = info.getLocation();
        o.message = info.getMessage();
        o.module = info.getModule();
        o.requestTime = info.getRequestTime() != null ? info.getRequestTime().getTime() : -1;
        o.responseTime = info.getResponseTime() != null ? info.getResponseTime().getTime() : -1;
        o.result = info.getResult() != null ? info.getResult().toString() : null;
        o.thread = info.getThread();
        o.throwable = StringUtils.getExceptionStackTrace(info.getThrowable());
        o.userHost = info.getHost();
        o.userId = info.getUserId();
        o.username = info.getUsername();
        return o;
    }

}
