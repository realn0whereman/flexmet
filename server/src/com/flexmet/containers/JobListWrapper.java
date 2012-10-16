package com.flexmet.containers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.flexmet.global.Job;
/**
 * Wrapper class for an arraylist of jobs. This allows easy serialization of jobs
 * @author phillip
 *
 */
@XmlRootElement
public class JobListWrapper {
	private static final long serialVersionUID = 1L;
	private ArrayList<Job> jobList;
	
	public JobListWrapper(){
		jobList = new ArrayList<Job>();
	}
	
	public ArrayList<Job> getJobList() {
		return jobList;
	}
	public void setJobList(ArrayList<Job> jobList) {
		this.jobList = jobList;
	}
	public void addJob(Job j){
		jobList.add(j);
	}
	
	
}
