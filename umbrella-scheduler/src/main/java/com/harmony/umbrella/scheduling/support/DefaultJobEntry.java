package com.harmony.umbrella.scheduling.support;

import com.harmony.umbrella.scheduling.JobEntry;

public class DefaultJobEntry implements JobEntry {

    private final String jobName;
    private final String jobClassName;

    public DefaultJobEntry(String jobName, String jobClassName) {
        this.jobName = jobName;
        this.jobClassName = jobClassName;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public String getJobClassName() {
        return jobClassName;
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
        DefaultJobEntry other = (DefaultJobEntry) obj;
        if (jobName == null) {
            if (other.jobName != null)
                return false;
        } else if (!jobName.equals(other.jobName))
            return false;
        return true;
    }

}