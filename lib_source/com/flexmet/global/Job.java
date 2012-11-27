package com.flexmet.global;

import java.io.Serializable;

/**
 * Container class to hold the details of a scheduled job. Contains the cron string
 * as well as the command to run
 * @author phillip
 *
 */
public class Job implements Serializable {
	private static final long serialVersionUID = 1L;
	private String command;
	private String cronData;
	private String metricName;
	
	public Job(String command, String cronData)
	{
		this.command = command;
		this.cronData = cronData;
	}

	@Override
	public String toString() {
		return "Job [command=" + command + ", cronData=" + cronData + ", metricName=" + metricName + "]";
	}
	
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getCronData() {
		return cronData;
	}
	public void setCronData(String cronData) {
		this.cronData = cronData;
	}


	public String getMetricName() {
		return metricName;
	}


	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	
	
	
}
