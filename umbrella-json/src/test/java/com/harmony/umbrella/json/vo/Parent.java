package com.harmony.umbrella.json.vo;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author wuxii@foxmail.com
 */
public class Parent {

    private Long parentId;
    private String parentName;

    private Collection<Child> childs;

    public Parent() {
    }

    public Parent(Long parentId, String parentName) {
        this.parentId = parentId;
        this.parentName = parentName;
    }

    public Parent(Long parentId, String parentName, Child... child) {
        this.parentId = parentId;
        this.parentName = parentName;
        this.childs.addAll(Arrays.asList(child));
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Collection<Child> getChilds() {
        // throw new IllegalArgumentException();
        return childs;
    }

    public void setChilds(Collection<Child> childs) {
        this.childs = childs;
    }

}
