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

import com.harmony.umbrella.monitor.HttpGraph;
import com.harmony.umbrella.util.StringUtils;

/**
 * 基于http请求监控的结果视图
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultHttpGraph extends AbstractGraph implements HttpGraph {

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
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n")//
                .append("  url:").append(identifier).append("\n");//
        if (StringUtils.isNotBlank(module)) {
            buffer.append("  module:").append(module).append("\n");
        }
        if (StringUtils.isNotBlank(operator)) {
            buffer.append("  operator:").append(operator).append("\n");
        }
        if (level != null) {
            buffer.append("  level:").append(level).append("\n");
        }
        buffer.append("  remoteAddr:").append(remoteAddr).append("\n")//
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
