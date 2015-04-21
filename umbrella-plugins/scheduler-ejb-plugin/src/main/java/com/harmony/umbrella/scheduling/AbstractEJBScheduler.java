/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.scheduling;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;

import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import com.harmony.umbrella.scheduling.AbstractScheduler;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.Scheduler;
import com.harmony.umbrella.scheduling.SchedulerException;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.Exceptions;

/**
 * 基于EJB定时任务的抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractEJBScheduler extends AbstractScheduler<AbstractEJBScheduler.EJBJobInfo> {

	/**
	 * 获取JavaEE环境中的定时服务
	 * 
	 * @return
	 */
	protected abstract TimerService getTimerService();

	/**
	 * 指定的JavaEE定时任务的入口，一般给该方法添加{@linkplain javax.ejb.Timeout}注释
	 * 
	 * @param timer
	 */
	protected abstract void monitorTask(Timer timer);

	@Override
	protected void init() throws SchedulerException {
		Set<String> jobNames = getJobFactory().getAllJobNames();
		if (!jobNames.isEmpty()) {
			for (String jobName : jobNames) {
				Class<? extends Job> jobClass = getJobFactory().getJobClass(jobName);
				if (jobClass != null) {
					jobInfoMap.put(jobName, new EJBJobInfo(jobName, jobClass));
				} else {
					log.error("job {} can't find related job class", jobName);
				}
			}
		} else {
			log.warn("scheduler not job find");
		}
	}

	@Override
	protected void doStart(String jobName, EJBJobInfo jobInfo) {
		Trigger jobTrigger = getJobFactory().getJobTrigger(jobName);
		Assert.notNull(jobTrigger, "can't find job " + jobName + "'s trigger");
		ScheduleExpression expression = toScheduleExpression(jobInfo.trigger = jobTrigger);
		jobInfo.timer = getTimerService().createCalendarTimer(expression, jobInfo);
		jobInfo.startTime = Calendar.getInstance();
	}

	@Override
	protected void doStop(String jobName, EJBJobInfo jobInfo) {
		jobInfo.timer.cancel();
	}

	@Override
	protected void doPause(String jobName, EJBJobInfo jobInfo) {
		jobInfo.pausedTimes++;
	}

	protected void handle(Timer timer) {
		EJBJobInfo jobInfo = (EJBJobInfo) timer.getInfo();
		final String jobName = jobInfo.jobName;
		try {
			if (hasJob(jobName) && isStarted(jobName)) {
				Job job = getJobFactory().getJob(jobName);
				jobInfo.lastExecuteStartTime = Calendar.getInstance();
				job.process(jobInfo);
				jobInfo.lastExecuteFinishTime = Calendar.getInstance();
				jobInfo.executeTimes++;
			}
		} catch (Exception e) {
			log.error("", e);
			jobInfo.lastExceptionMessage = Exceptions.getErrorMessageWithNestedException(e);
			jobInfo.lastExceptionTime = Calendar.getInstance();
			jobInfo.execeptionTimes++;
		}
	}

	protected ScheduleExpression toScheduleExpression(Trigger trigger) {
		return new ScheduleExpression().year(trigger.getYears()).month(trigger.getMonths()).dayOfMonth(trigger.getDayOfMonth()).dayOfWeek(trigger.getDayOfWeek()).hour(trigger.getHours())
				.minute(trigger.getMinutes()).second(trigger.getSeconds());
	}

	protected class EJBJobInfo extends TimerConfig implements Scheduler.JobInfo, Serializable {

		private static final long serialVersionUID = -1853696933095642375L;
		protected Timer timer;
		protected final String jobName;
		protected final Class<? extends Job> jobClass;
		protected final Calendar regiestTime;
		protected Trigger trigger;
		protected Status status = Status.READY;
		protected Calendar startTime;
		protected Calendar lastExecuteStartTime;
		protected Calendar lastExecuteFinishTime;
		protected Calendar lastExceptionTime;
		protected int pausedTimes;
		protected int executeTimes;
		protected int execeptionTimes;
		protected String lastExceptionMessage;

		public EJBJobInfo(String jobName, Class<? extends Job> jobClass) {
			this.jobName = jobName;
			this.jobClass = jobClass;
			this.regiestTime = Calendar.getInstance();
		}

		@Override
		public String getJobName() {
			return jobName;
		}

		@Override
		public void setInfo(Serializable i) {
			super.setInfo(this);
		}

		@Override
		public Serializable getInfo() {
			return this;
		}

		public int getSuccessTimes() {
			return executeTimes - execeptionTimes;
		}

		@Override
		public Calendar getRegisterTime() {
			return regiestTime;
		}

		@Override
		public Calendar getStartTime() {
			return startTime;
		}

		@Override
		public Calendar getLastExecuteStartTime() {
			return lastExecuteStartTime;
		}

		@Override
		public Calendar getLastExecuteFinishTime() {
			return lastExecuteFinishTime;
		}

		@Override
		public Calendar getLastExceptionTime() {
			return lastExceptionTime;
		}

		@Override
		public int getPauseTimes() {
			return pausedTimes;
		}

		@Override
		public float getAverageInterval() {
			if (lastExecuteStartTime != null && startTime != null && executeTimes != 0) {
				return (lastExecuteStartTime.getTimeInMillis() - startTime.getTimeInMillis()) / executeTimes;
			}
			return -1;
		}

		@Override
		public int getExecuteTimes() {
			return executeTimes;
		}

		@Override
		public int getExceptionTimes() {
			return execeptionTimes;
		}

		@Override
		public Class<? extends Job> getJobClass() {
			return jobClass;
		}

		@Override
		public Trigger getJobTrigger() {
			return trigger;
		}

		@Override
		public Object getTimer() {
			return timer;
		}

		@Override
		public Status getJobStatus() {
			return status;
		}

		@Override
		public void setJobStatus(Status status) {
			this.status = status;
		}

		@Override
		public String getLastExceptionMessage() {
			return lastExceptionMessage;
		}

		@Override
		public void dump() {

		}

		@Override
		public String toString() {
			return "{\"jobName\":\"" + jobName + "\", \"jobClass\":\"" + jobClass + "\", \"trigger\":\"" + trigger + "\", \"status\":\"" + status + "\"}";
		}

	}

}
