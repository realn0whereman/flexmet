package com.flexmet.jobstate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.flexmet.containers.JobListWrapper;
import com.flexmet.global.Job;

/**
 * Class responsible for loading scheduled jobs to and from the disk
 * @author phillip
 *
 */
public class JobLoader {
	static JobListWrapper jobs;
	Class<?> serializeableClass;
	String filename = "./jobs.conf"; //name of file to serialize to/from
	/**
	 * Initialize the class variables
	 */
	public JobLoader(){
		jobs = new JobListWrapper();
		serializeableClass = jobs.getClass();
	}
	
	/**
	 * Unmarshalls the data from the disk and loads it into memory.
	 * @return returns a the class variable for convenience (mostly testing)
	 */
	public JobListWrapper loadJobsFromDisk(){
		try {
			File jobFile = prepareOutputHandle();
			jobs = parseJobsFromFile(jobFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobs;
	}
	
	private JobListWrapper parseJobsFromFile(File jobFile) {
		JobListWrapper jlw = new JobListWrapper();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(jobFile));
			String line = "";
			String jsonString = "";
			while((line = reader.readLine()) != null){
				jsonString += line + "\n";
				if(line.substring(0,1).equals("#")){ // comments
					continue;
				}
				if(line.equals("}")){ //end of metric event json block
					
					jlw.addJob(Job.getFromJSON(jsonString));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jlw;
	}

	/**
	 * Add a job to the list of scheduled jobs
	 * @param j
	 */
	public void addJob(Job j){
		jobs.addJob(j);
	}
	
	public static JobListWrapper getJobs(){
		return jobs;
	}
	
	/**
	 * Prepare the filehandle for file storage. Create the file if it doesn't exist.
	 * @return file handle to data file
	 * @throws IOException
	 */
	public File prepareOutputHandle() throws IOException{
		File jobFile = new File(filename);
		jobFile.createNewFile();
		return jobFile;
	}
	
}
