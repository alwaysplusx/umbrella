/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.scheduling.jpa;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.JobFactory;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.scheduling.ext.JobEntity;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class JpaJobFactory implements JobFactory {

    private final EntityManager em;
    private BeanFactory beanFactory = new SimpleBeanFactory();

    public JpaJobFactory(EntityManager em) {
        Assert.notNull(em, "entity manager must not be null");
        this.em = em;
    }

    @SuppressWarnings("unchecked")
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
