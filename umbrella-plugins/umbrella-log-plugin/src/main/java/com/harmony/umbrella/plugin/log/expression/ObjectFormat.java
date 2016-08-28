package com.harmony.umbrella.plugin.log.expression;

/**
 * 内容格式化工具
 * 
 * @author wuxii@foxmail.com
 */
public interface ObjectFormat {

    /**
     * 内容格式化
     * 
     * @param val
     *            格式化的值
     * @return 格式化后的文本
     */
    String format(Object val);

}
