package com.harmony.modules.jaxws.bean;
public interface BeanLoader {

    String SINGLETON = "singleton";

    String PROTOTYPE = "prototype";

    <T> T loadBean(Class<T> beanClass);

    <T> T loadBean(Class<T> beanClass, String scope);

}
