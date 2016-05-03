package com.harmony.umbrella.scheduling.persistence;

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
