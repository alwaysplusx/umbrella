/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.web.action;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.harmony.umbrella.data.domain.Model;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ModelActionSupport<T extends Model<ID>, ID extends Serializable> extends ActionSupport implements ServletRequestAware,
        ServletResponseAware {

    private static final long serialVersionUID = 941939963000360885L;

    public static final String LIST = "list";
    public static final String NONE = "none";

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected T model;
    protected ID id;
    protected ID[] ids;

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
