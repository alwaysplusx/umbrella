package com.harmony.umbrella.scheduling.support;

import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Set;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.SimpleBeanFactory;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.JobFactory;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PropertiesFileJobFactory implements JobFactory {

    public static final String jobPropertiesFileLocation = "META-INF/scheduler/jobs.properties";

    public static final String triggerPropertiesFileLocation = "META-INF/scheduler/triggers.properties";

    private final String jobFileLocation;
    private final String triggerFileLocation;
    private BeanFactory beanFactory = new SimpleBeanFactory();

    public PropertiesFileJobFactory() {
        this(jobPropertiesFileLocation, triggerPropertiesFileLocation);
    }

    public PropertiesFileJobFactory(String jobFileLocation, String triggerFileLocation) {
        this.jobFileLocation = jobFileLocation;
        this.triggerFileLocation = triggerFileLocation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Job> getJobClass(String jobName) {
        try {
            Properties props = PropertiesUtils.loadPropertiesSilently(jobFileLocation);
            String jobClassName = props.getProperty(jobName);
            if (jobClassName != null) {
                Class<?> clazz = Class.forName(jobClassName);
                if (Job.class.isAssignableFrom(clazz) && !clazz.isInterface() && Modifier.isPublic(clazz.getModifiers())) {
                    return (Class<? extends Job>) clazz;
                }
            } else {
                throw new IllegalArgumentException(jobClassName + "class not " + Job.class.getName() + " sub class");
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("job class not find");
        }
        return null;
    }

    @Override
    public Trigger getJobTrigger(String jobName) {
        Properties props = PropertiesUtils.loadPropertiesSilently(triggerFileLocation);
        String expression = props.getProperty(jobName);
        if (expression != null) {
            return new ExpressionTrigger(expression);
        }
        return null;
    }

    @Override
    public Job getJob(String jobName) {
        Class<? extends Job> jobClass = getJobClass(jobName);
        if (jobClass != null) {
            return beanFactory.getBean(jobClass);
        }
        return null;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "bean loader must not be null");
        this.beanFactory = beanFactory;
    }

    @Override
    public Set<String> getAllJobNames() {
        Properties props = PropertiesUtils.loadPropertiesSilently(jobFileLocation);
        return props.stringPropertyNames();
    }
}
