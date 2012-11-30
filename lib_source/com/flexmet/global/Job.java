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
	
	public Job(){
		
	}
	
	public Job(String command, String cronData, String metricName)
	{
		this.command = command;
		this.cronData = cronData;
		this.metricName = metricName;
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
	
	public String getJSONString()
	{
		//Create the holder and set the name
		JSONObject json = new JSONObject();
		json.name = "Job";

		//Add all of the data of this object to the holder
		json.put("command", this.command);
		json.put("cronData", this.cronData);
		json.put("metricName", this.metricName);

		//Get the string representation
		return json.getJSONString()+"\n";
	}
	
	public static Job getFromJSON(String json)
	{
		//Use static method to create an object by parsing the given string:
		JSONObject data = JSONObject.parseJSON(json);

		Job job = new Job();
		
		//Pull out the data from the object
		job.setCommand(data.getString("command"));
		job.setCronData(data.getString("cronData"));
		job.setMetricName(data.getString("metricName"));
		
		return job;
	}
	
	
	
}
