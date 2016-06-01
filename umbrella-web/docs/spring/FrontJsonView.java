package com.harmony.umbrella.web.front.spring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.spring.JsonView;
import com.harmony.umbrella.web.util.FrontUtils;

/**
 * @author wuxii@foxmail.com
 */
public class FrontJsonView extends JsonView {

    private boolean ignoreModel = true;

    private Object object;

    private Set<String> excludeNames = new HashSet<String>();

    public FrontJsonView() {
        super();
    }

    public FrontJsonView(Object object) {
        this(object, new String[0]);
    }

    public FrontJsonView(Object object, String... excludeNames) {
        super();
        this.object = object;
        Collections.addAll(this.excludeNames, excludeNames);
    }

    @Override
    protected String toJsonString(Map<String, Object> model, SerializerFeature[] serializerFeature) {
        Object object = filterObject(model);
        return FrontUtils.toJson(object, serializerFeature, excludeNames.toArray(new String[excludeNames.size()]));
    }

    private Object filterObject(Map<String, Object> model) {
        if (ignoreModel) {
            return object;
        }

        if (object != null) {
            for (Object value : model.values()) {
                if (object == value || object.equals(value)) {
                    return model;
                }
            }
            model.put(getBeanName(), object);
        }

        return model;
    }

    public void addExcludeName(String excludeName) {
        this.excludeNames.add(excludeName);
    }

    public void removeExcludeName(String excludeName) {
        this.excludeNames.remove(excludeName);
    }

    public void clearExcludeNames() {
        this.excludeNames.clear();
    }

    public boolean isIgnoreModel() {
        return ignoreModel;
    }

    public void setIgnoreModel(boolean ignoreModel) {
        this.ignoreModel = ignoreModel;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
