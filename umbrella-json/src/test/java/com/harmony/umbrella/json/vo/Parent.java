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

import java.util.Arrays;
import java.util.Collection;

import com.harmony.umbrella.data.domain.Model;

/**
 * @author wuxii@foxmail.com
 */
public class Parent extends Model<Long> {

    private static final long serialVersionUID = -5683031004471290566L;

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

    @Override
    public Long getId() {
        return getParentId();
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
