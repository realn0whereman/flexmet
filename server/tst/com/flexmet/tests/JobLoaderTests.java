package com.flexmet.tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.flexmet.containers.Job;
import com.flexmet.containers.JobListWrapper;
import com.flexmet.jobstate.JobLoader;

public class JobLoaderTests {

	@Test
	public void writeJobsToDisk(){ 
		JobLoader j = new JobLoader();
		Job job1 = new Job();
		job1.setCommand("TestCommand");
		job1.setCronData("CronData");
		j.addJob(job1);
		j.addJob(job1);
		j.writeJobsToDisk();
		String dataFromFile = getDataFromFile(j);
		String expectedData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<jobListWrapper>" +
					"<jobList>" +
						"<command>TestCommand</command>" +
						"<cronData>CronData</cronData>" +
					"</jobList>" +
					"<jobList>" +
						"<command>TestCommand</command>" +
						"<cronData>CronData</cronData>" +
						"</jobList>" +
				"</jobListWrapper>";
//		System.out.println(dataFromFile);
//		System.out.println(expectedData);
		assertTrue(expectedData.equals(dataFromFile));
	}
	
	@Test
	public void readJobsFromDisk(){
		JobLoader j = new JobLoader();
		Job job1 = new Job();
		Job job2 = new Job();
		job1.setCommand("TestCommand1");
		job1.setCronData("CronData1");
		job2.setCommand("TestCommand2");
		job2.setCronData("CronData2");
		j.addJob(job1);
		j.addJob(job2);
		j.writeJobsToDisk();
		JobListWrapper list = j.loadJobsFromDisk();
		ArrayList<Job> l =  list.getJobList();
		
		//test
		assertTrue(l.get(0).getCommand().equals("TestCommand1"));
		assertTrue(l.get(0).getCronData().equals("CronData1"));
		assertTrue(l.get(1).getCommand().equals("TestCommand2"));
		assertTrue(l.get(1).getCronData().equals("CronData2"));
		
	}
	
	private String getDataFromFile(JobLoader j){
		String output = "", line = "";
		try {
			File f = j.prepareOutputHandle();
			BufferedReader reader = new BufferedReader(new FileReader(f));
			while((line = reader.readLine()) != null){
				output += line.trim();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
}
