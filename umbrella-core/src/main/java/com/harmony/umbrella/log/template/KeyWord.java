package com.harmony.umbrella.log.template;

import com.harmony.umbrella.log.annotation.Logging.Scope;

/**
 * 日志相关的关键字(一般用于日志拦截器)
 * 
 * @author wuxii@foxmail.com
 */
public interface KeyWord {

    /**
     * 关键字的别名
     * 
     * @return 关键字的别名
     */
    String[] alias();

    /**
     * 关键字的默认作用域
     * 
     * @return 关键字的默认作用域
     */
    Scope scope();

    /**
     * 找寻context对应的关键字对象
     * 
     * @param context
     *            日志上下文
     * @return 关键字对应的对象
     */
    Object resolve(LoggingContext context);

}
