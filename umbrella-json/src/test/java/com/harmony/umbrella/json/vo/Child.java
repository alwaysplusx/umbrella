package com.harmony.umbrella.json.vo;

import java.util.Calendar;

/**
 * @author wuxii@foxmail.com
 */
public class Child {

    private Long childId;
    private String childName;
    private Parent parent;
    private Calendar createdTime;

    public Child() {
    }

    public Child(Long childId, String childName, Parent parent) {
        this.childId = childId;
        this.childName = childName;
        this.parent = parent;
        this.createdTime = Calendar.getInstance();
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Parent getParent() {
        // throw new IllegalArgumentException();
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Calendar getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Calendar createdTime) {
        this.createdTime = createdTime;
    }

}
