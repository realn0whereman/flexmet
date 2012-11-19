package com.flexmet.global;

import com.flexmet.global.Log;

import java.util.Scanner;
/**
 * Container class to hold metric data to send to hbase
 * @author phillip
 *
 */
public class MetricEvent
{
	private String data;
	private String hostname;
	private String metricName;
	private long timestamp;
	
	public MetricEvent()
	{
		this.data = "";
		this.hostname = "";
		this.metricName = "";
		this.timestamp = -1;
	}
	
	public MetricEvent(String data, String hostname, String metricName, long timestamp)
	{
		this.data = data;
		this.hostname = hostname;
		this.metricName =  metricName;
		this.timestamp = timestamp;
	}

	public MetricEvent(String JSONString)
	{	
		System.out.println("Trying to parse this string:");
		//System.out.println(JSONString);
	}

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getJSONString()
	{
		//Create the holder and set the name
		JSONObject json = new JSONObject();
		json.name = "MetricEvent";

		//Add all of the data of this object to the holder
		json.put("data", this.data);
		json.put("hostname", this.hostname);
		json.put("metricName", this.metricName);
		json.put("timestamp", this.timestamp + "");

		//Get the string representation
		return json.getJSONString();
	}
	
	public static MetricEvent getFromJSON(String json)
	{
		//Use static method to create an object by parsing the given string:
		JSONObject data = JSONObject.parseJSON(json);

		MetricEvent event = new MetricEvent();
		
		//Pull out the data from the object
		event.setData(data.getString("data"));
		event.setHostname(data.getString("hostname"));
		event.setMetricName(data.getString("metricName"));
		event.setTimestamp(data.getLong("timestamp"));
		
		return event;
	}
}
