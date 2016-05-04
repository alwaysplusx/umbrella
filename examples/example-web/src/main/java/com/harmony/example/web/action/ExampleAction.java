package com.harmony.example.web.action;

import com.harmony.umbrella.log.annotation.Logging;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author wuxii@foxmail.com
 */
public class ExampleAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String name;

    @Override
    @Logging(module = "Example", action = "执行execute方法", message = "对示例action进行调用，注入参数为{target.name}, 返回结果为{result}. going")
    public String execute() throws Exception {
        return super.execute();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
