package com.harmony.umbrella.context.ee.jmx;

/**
 * @author wuxii@foxmail.com
 */
public class EJBContext implements EJBContextMBean {

    private final EJBContextMBean context;

    public EJBContext(EJBContextMBean context) {
        this.context = context;
    }

    @Override
    public boolean exists(String className) {
        return context.exists(className);
    }

    @Override
    public String showProperties() {
        return context.showProperties();
    }

    @Override
    public void resetProperties() {
        context.resetProperties();
    }

    @Override
    public String propertiesFileLocation() {
        return context.propertiesFileLocation();
    }

}
