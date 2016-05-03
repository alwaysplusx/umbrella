package com.harmony.umbrella.mapper.vo;

import java.util.Date;

/**
 * @author wuxii@foxmail.com
 */
public class Source {

    private String name;
    private Date createTime;

    public Source() {
    }

    public Source(String name, Date createTime) {
        this.name = name;
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\", \"createTime\":\"" + createTime + "\"}";
    }

}
