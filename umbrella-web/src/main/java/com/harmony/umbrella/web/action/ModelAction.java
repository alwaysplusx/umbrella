package com.harmony.umbrella.web.action;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.web.render.HttpRender;
import com.harmony.umbrella.web.render.WebHttpRender;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ModelAction<T extends Model<ID>, ID extends Serializable> extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    private static final long serialVersionUID = 941939963000360885L;

    public static final String LIST = "list";
    public static final String NONE = "none";

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected HttpRender render = new WebHttpRender();

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

    protected String renderJson(Object obj) {
        return renderJson(obj, new String[0]);
    }

    protected String renderJson(Object obj, String... excludes) {
        try {
            render.renderJson(Json.toJson(obj, excludes), response);
        } catch (IOException e) {
            return ERROR;
        }
        return NONE;
    }

}
