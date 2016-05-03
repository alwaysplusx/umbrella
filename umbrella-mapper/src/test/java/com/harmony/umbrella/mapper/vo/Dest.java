package com.harmony.umbrella.mapper.vo;

import java.util.Date;

/**
 * @author wuxii@foxmail.com
 */
public class Dest {

    private String name;
    // @Mapping("createTime")
    private Date modifyTime;

    public Dest() {
    }

    public Dest(String name, Date modifyTime) {
        this.name = name;
        this.modifyTime = modifyTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\", \"modifyTime\":\"" + modifyTime + "\"}";
    }
}
