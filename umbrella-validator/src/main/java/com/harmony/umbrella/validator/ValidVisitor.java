package com.harmony.umbrella.validator;

/**
 * 验证对象验证过滤
 * 
 * @author wuxii@foxmail.com
 */
public interface ValidVisitor {

    /**
     * 检验对象
     * 
     * @param obj
     *            验证对象
     * @return 如果有违规信息返回违规描述，否则返回null or 空字符串
     */
    String valid(Object obj);

}
