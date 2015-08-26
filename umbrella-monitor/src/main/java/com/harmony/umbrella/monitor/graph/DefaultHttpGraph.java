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

import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.monitor.HttpMonitor.HttpGraph;

/**
 * 基于http请求监控的结果视图
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultHttpGraph extends AbstractGraph implements HttpGraph {

    private final Map<String, Object> result = new HashMap<String, Object>();
    protected String httpMethod;
    protected String remoteAddr;
    protected String localAddr;
    protected String queryString;
    protected int status;

    public DefaultHttpGraph(String resource) {
        super(resource);
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String getLocalAddr() {
        return localAddr;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public Map<String, Object> getResult() {
        return result;
    }

    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n")//
                .append("  url:").append(identifier).append("\n")//
                .append("  remoteAddr:").append(remoteAddr).append("\n")//
                .append("  httpMethod:").append(httpMethod).append("\n")//
                .append("  requestTime:").append(ndf.format(requestTime)).append("\n")//
                .append("  use:").append(use()).append("\n")//
                .append("  status:").append(status).append("\n")//
                .append("  arguments:").append(getJsonArguments()).append("\n")//
                .append("  result:").append(getJsonResult()).append("\n")//
                .append("  exception:").append(isException()).append("\n");
        if (isException()) {
            buffer.append("  exceptionMessage:").append(exception).append("\n");
        }
        return buffer.append("}").toString();
    }

}
