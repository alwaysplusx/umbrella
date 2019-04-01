package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface Expression {

    String getValue(Object rootObject);

    int getIndex();
    
}
