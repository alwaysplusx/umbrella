package com.harmony.umbrella.util;

/**
 * @author wuxii@foxmail.com、
 */
public interface Converter<IN, OUT> {

    OUT convert(IN t);

}
