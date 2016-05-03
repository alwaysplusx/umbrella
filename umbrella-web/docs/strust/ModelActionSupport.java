package com.harmony.umbrella.web.front.strust;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.harmony.umbrella.biz.Business;
import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.web.render.WebRender;
import com.harmony.umbrella.web.util.FrontUtils;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ModelActionSupport<T extends Model<ID>, ID extends Serializable> extends ActionSupport implements ServletRequestAware,
        ServletResponseAware {

    private static final long serialVersionUID = 941939963000360885L;

    public static final String DEFAULT_FILTER_SPLIT = "_";

    public static final String DEFAULT_FILTER_PREFIX = "filter";

    private static final String MESSAGE = "message";
    private static final String MODEL = "model";

    protected static final Map<Class<?>, Set<String>> EXCULDE_PROPERTIES = new HashMap<Class<?>, Set<String>>();

    static {
        Set<String> names = new HashSet<String>();
        names.add("new");
        EXCULDE_PROPERTIES.put(Model.class, names);
    }

    protected final Map<Class<?>, Set<String>> excludeProperties = new HashMap<Class<?>, Set<String>>();

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected T model;
    protected ID id;
    protected ID[] ids;

    protected int pageNo;
    protected int pageSize;

    protected boolean resultWithModel;

    private boolean wrapper;

    private static final Map<String, String> emptyProperties = Collections.<String, String> emptyMap();

    public ModelActionSupport() {
    }

    public String preview() {
        return "preview";
    }

    public String show() {
        return "show";
    }

    public String save() {
        model = getBusiness().save(model);
        return success();
    }

    public String update() {
        model = getBusiness().update(model);
        return success();
    }

    public String view() {
        model = getBusiness().findOne(id);
        if (wrapper) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("model", model);
            return toJson(result);
        }
        return toJson(model);
    }

    public String delete() {
        if (ids != null && ids.length > 0) {
            getBusiness().deleteByIds(Arrays.asList(ids));
        } else {
            getBusiness().deleteById(id);
        }
        return success();
    }

    public String list() {
        // getBusinessController().findList(bond);
        return "";
    }

    public String page() {
        return "";
    }

    public Bond filterRequest() {
        return filterRequest(DEFAULT_FILTER_PREFIX);
    }

    // f_l_name = wuxii f_o_l_name
    public Bond filterRequest(String prefix) {
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith(prefix)) {

            }
        }
        return null;
    }

    protected abstract Business<T, ID> getBusiness();

    protected boolean renderJson(Object obj) {
        return renderJson(obj, emptyProperties);
    }

    protected boolean renderJson(Object obj, Map<String, String> props) {
        WebRender render = new WebRender(props);
        try {
            render.renderJson(FrontUtils.toJson(obj), response);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected boolean renderFile(File file) {
        return renderFile(file, emptyProperties);
    }

    protected boolean renderFile(File file, Map<String, String> props) {
        WebRender render = new WebRender(props);
        try {
            render.renderFile(file, response);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected boolean render(String text) {
        return render(text, emptyProperties);
    }

    protected boolean render(String text, Map<String, String> props) {
        WebRender render = new WebRender(props);
        try {
            render.render(text, response.getWriter());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected String toJson(Object object) {
        String[] excludes = getExcludeProperties(object.getClass());
        return FrontUtils.toJson(object, excludes);
    }

    public String[] getExcludeProperties(Class<?> clazz) {
        Set<String> result = new HashSet<String>();
        Iterator<Class<?>> iterator = excludeProperties.keySet().iterator();
        while (iterator.hasNext()) {
            Class<?> keyClass = iterator.next();
            if (keyClass.isAssignableFrom(clazz)) {
                result.addAll(excludeProperties.get(keyClass));
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public void addExcludeProperties(Class<?> clazz, String... names) {
        Set<String> excludes = excludeProperties.get(clazz);
        if (excludes == null) {
            excludes = new HashSet<String>();
        }
        Collections.addAll(excludes, names);
        excludeProperties.put(clazz, excludes);
    }

    public void removeExcludeProperties(Class<?> clazz) {
        excludeProperties.remove(clazz);
    }

    protected final String success(String message) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        result.put(MESSAGE, message);
        if (resultWithModel) {
            result.put(MODEL, model);
        }
        return FrontUtils.toJson(result);
    }

    protected final String success() {
        return "";
    }

    protected final String warn(String message) {
        return "";
    }

    protected final String warn(Exception exception) {
        return "";
    }

    protected final String error(String message) {
        return "";
    }

    protected final String error(Exception exception) {
        return "";
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

}
