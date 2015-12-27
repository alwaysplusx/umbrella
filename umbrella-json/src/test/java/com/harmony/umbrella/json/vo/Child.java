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
