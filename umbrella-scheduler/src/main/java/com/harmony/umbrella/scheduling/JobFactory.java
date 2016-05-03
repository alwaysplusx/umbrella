package com.harmony.umbrella.scheduling;

import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface JobFactory {

	/**
	 * 根据job的名称获取对应的job类
	 * 
	 * @param jobName
	 *            job名称
	 * @return job class
	 */
	Class<? extends Job> getJobClass(String jobName);

	/**
	 * 根据job的名称获取job的触发规则
	 * 
	 * @param jobName
	 *            job名称
	 * @return job触发贵族
	 */
	Trigger getJobTrigger(String jobName);

	/**
	 * 根据job的名称获取对应的job实例
	 * 
	 * @param jobName
	 *            job名称
	 * @return job instance
	 */
	Job getJob(String jobName);

	/**
	 * 获得全部的job的名称
	 * 
	 * @return
	 */
	Set<String> getAllJobNames();

}
