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
package com.harmony.umbrella.monitor.graph;

import java.util.Calendar;

import com.harmony.umbrella.monitor.Graph;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.util.Formats.NullableDateFormat;

/**
 * 监视结果基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractGraph<T> implements Graph<T> {

    protected NullableDateFormat ndf = Formats.createDateFormat(Formats.FULL_DATE_PATTERN);
    protected String identifie;
    protected Object result;
    protected Calendar requestTime = Calendar.getInstance();
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

    public void setRequestTime(Calendar requestTime) {
        this.requestTime = requestTime;
    }

    public void setResponseTime(Calendar responseTime) {
        this.responseTime = responseTime;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String getDescription() {
        return "{\"identifie\":\"" + getIdentifie() + "\", \"arguments\":\"" + getArguments() + "\", \"result\":\"" + getResult() + "\", \"use\":\"" + use()
                + "\", \"requestTime\":\"" + ndf.format(getRequestTime()) + "\", \"exception\":\"" + isException() + "\"}";
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifie == null) ? 0 : identifie.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractGraph other = (AbstractGraph) obj;
        if (identifie == null) {
            if (other.identifie != null)
                return false;
        } else if (!identifie.equals(other.identifie))
            return false;
        return true;
    }

}
