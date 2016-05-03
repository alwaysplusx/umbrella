package com.harmony.umbrella.example.ws;

import com.harmony.umbrella.ws.Key;

/**
 * @author wuxii@foxmail.com
 */
public class User {

    @Key(name = "用户名称")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
