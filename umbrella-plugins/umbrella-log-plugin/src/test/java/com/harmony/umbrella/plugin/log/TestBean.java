package com.harmony.umbrella.plugin.log;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Stateless;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Logging.Expression;
import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.log.annotation.Module;
import com.harmony.umbrella.plugin.log.expression.ValueStack;

/**
 * @author wuxii@foxmail.com
 */
@Stateless
@Module("日志模块")
public class TestBean implements Serializable {

    private static final long serialVersionUID = -7775533769520721695L;

    private String foo;
    private int bar;

    public TestBean() {
    }

    public TestBean(String foo, int bar) {
        this.foo = foo;
        this.bar = bar;
    }

    // 常用
    @Logging(action = "TestBean#ping", message = "输入参数为:foo=[{0.foo}, bar={0.bar}], 返回结果为:{result}")
    public String ping(TestBean testBean) {
        return "success";
    }

    // 备用
    @Logging(//
            action = "TestBean#test", //
            message = "通过表达式注解来创建模版:foo[in]={0}, foo[out]={1}", //
            expressions = { //
                    @Expression(value = "args[0].foo", scope = Scope.IN), //
                    @Expression(value = "args[0].foo", scope = Scope.OUT) //
            }, //
            keyExpression = @Expression("args[0].bar")//
    )
    public String test(TestBean testBean) {
        testBean.foo = "other";
        return "test";
    }

    // 非常用
    @Logging(action = "TestBean#sayHi", message = "通过内部设置来创建表达式:内部设值=[{INTERNAL_NAME}]. [{args[0]}]向管理员[{INTERNAL_ADMIN}]问好")
    public String sayHi(String name) {
        Map<String, Object> inContext = ValueStack.peek().getInContext();
        inContext.put("INTERNAL_NAME", "内部测试值");
        inContext.put("INTERNAL_ADMIN", "david");
        return "Hi " + name;
    }

    public int getBar() {
        return bar;
    }

    public String getFoo() {
        return foo;
    }
}
