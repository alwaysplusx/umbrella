package com.harmony.umbrella.web.support;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.data.domain.PageImpl;
import com.harmony.umbrella.web.util.FrontUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PageTest {

    @Test
    public void test() {
        List<Parent> list = Arrays.<Parent> asList(new Parent(1l, "a"), new Parent(2l, "b"), new Parent(3l, "c"));
        PageImpl<Parent> page = new PageImpl<Parent>(list, null, 100);
        System.out.println(FrontUtils.toJson(page, "id"));
        System.out.println(FrontUtils.toJson(list, new SerializerFeature[] { SerializerFeature.PrettyFormat }, new String[] { "id", "new" }));
    }

    public static class Child extends Model<Long> {

        private static final long serialVersionUID = -3640652984873091178L;

        private Long childId;
        private String childName;
        private Parent parent;

        public Child() {
        }

        public Child(Long childId, String childName, Parent parent) {
            this.childId = childId;
            this.childName = childName;
            this.parent = parent;
            this.createdTime = Calendar.getInstance();
            this.creatorCode = "admin";
            this.creatorName = "admin";
            this.creatorId = 1l;
        }

        @Override
        public Long getId() {
            return getChildId();
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

    }

    public static class Parent extends Model<Long> {

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

}
