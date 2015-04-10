/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.modules.monitor;

import static com.harmony.modules.utils.DateFormat.*;

import java.util.Calendar;
import java.util.Map;

import com.harmony.modules.utils.Exceptions;

/**
 * 监视结果基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public class AbstractGraph implements Graph {

    protected String identifie;
    protected Map<String, Object> arguments;
    protected Object result;
    protected Calendar requestTime;
    protected Calendar responseTime;
    protected Exception exception;

    @Override
    public String getIdentifie() {
        return identifie;
    }

    @Override
    public Calendar getRequestTime() {
        return requestTime;
    }

    @Override
    public Calendar getResponseTime() {
        return responseTime;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public boolean isException() {
        return exception != null;
    }

    @Override
    public String getExceptionMessage() {
        return isException() ? exception.getMessage() : null;
    }

    @Override
    public String getCause() {
        return isException() ? Exceptions.getRootCause(exception).getMessage() : null;
    }

    @Override
    public long use() {
        if (requestTime != null && responseTime != null) {
            return responseTime.getTimeInMillis() - requestTime.getTimeInMillis();
        }
        return -1;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setIdentifie(String identifie) {
        this.identifie = identifie;
    }

    public void setRequestTime(Calendar requestTime) {
        this.requestTime = requestTime;
    }

    public void setResponseTime(Calendar responseTime) {
        this.responseTime = responseTime;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "{\"identifie\":\"" + identifie + "\", \"arguments\":\"" + arguments + "\", \"result\":\"" + result + "\", \"requestTime\":\""
                + FULL_DATEFORMAT.format(requestTime) + "\", \"responseTime\":\"" + FULL_DATEFORMAT.format(responseTime) + "\", \"exception\":\""
                + isException() + "\"}";
    }

}
