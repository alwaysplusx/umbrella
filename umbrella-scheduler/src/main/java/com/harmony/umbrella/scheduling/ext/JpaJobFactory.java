package com.harmony.umbrella.scheduling.ext;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.util.Assert;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.JobFactory;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.scheduling.persistence.JobEntity;

/**
 * @author wuxii@foxmail.com
 */
public class JpaJobFactory implements JobFactory {

    private final EntityManager em;
    private BeanFactory beanFactory = SimpleBeanFactory.INSTANCE;

    public JpaJobFactory(EntityManager em) {
        Assert.notNull(em, "entity manager must not be null");
        this.em = em;
    }

    @Override
    public Class<? extends Job> getJobClass(String jobName) {
        TypedQuery<JobEntity> query = em.createNamedQuery("JobEntity.findByJobName", JobEntity.class);
        JobEntity entity = query.setParameter("jobName", jobName).getSingleResult();
        if (entity != null) {
            String jobClassName = entity.getJobClassName();
            try {
                Class<?> clazz = Class.forName(jobClassName);
                if (Job.class.isAssignableFrom(clazz) && !clazz.isInterface() && Modifier.isPublic(clazz.getModifiers())) {
                    return (Class<? extends Job>) clazz;
                } else {
                    throw new IllegalArgumentException(jobClassName + "class not " + Job.class.getName() + " sub class");
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("job class not find");
            }
        }
        return null;
    }

    @Override
    public Trigger getJobTrigger(String jobName) {
        TypedQuery<Trigger> query = em.createNamedQuery("TriggerEntity.findByTriggerCode", Trigger.class);
        query.setParameter("triggerCode", jobName);
        return query.getSingleResult();
    }

    @Override
    public Job getJob(String jobName) {
        Class<? extends Job> jobClass = getJobClass(jobName);
        if (jobClass != null) {
            return beanFactory.getBean(jobClass);
        }
        return null;
    }

    @Override
    public Set<String> getAllJobNames() {
        Set<String> result = new HashSet<String>();
        result.addAll(em.createNamedQuery("JobEntity.findAllJobName", String.class).getResultList());
        return result;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "bean loader must not be null");
        this.beanFactory = beanFactory;
    }

}
