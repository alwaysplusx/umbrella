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
package com.harmony.umbrella.scheduling.ext;

import java.io.Serializable;

import javax.persistence.*;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "UMBRELLA_SCHEDULING_JOB")
@NamedQueries({ 
	@NamedQuery(name = "JobEntity.findAll", query = "select o from JobEntity o"),
	@NamedQuery(name = "JobEntity.findAllJobName", query = "select o.jobName from JobEntity o"),
	@NamedQuery(name = "JobEntity.findByJobName", query = "select o from JobEntity o where o.jobName=:jobName") 
})
public class JobEntity implements Serializable {

	private static final long serialVersionUID = 6827492927148470876L;
	@Id
	private String jobName;
	private String jobClassName;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobEntity other = (JobEntity) obj;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		return true;
	}

}
