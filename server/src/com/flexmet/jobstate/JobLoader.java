package com.flexmet.jobstate;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
		String line = "";
		try {
			File jobFile = prepareOutputHandle();
			Unmarshaller dataUnmarshaller = prepareUnmarshaller();
			//TODO validation
			jobs = (JobListWrapper)dataUnmarshaller.unmarshal(jobFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobs;
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
	 * Write the jobs currently in memory to the disk
	 */
	public void writeJobsToDisk(){
		try {
			Marshaller dataMarshaller = prepareMarshaller();
			File jobFile = prepareOutputHandle();
			dataMarshaller.marshal(jobs, jobFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	/**
	 * Prepare the data marshaller and set marshalling formats
	 * @return
	 * @throws JAXBException
	 */
	private Marshaller prepareMarshaller() throws JAXBException{
		JAXBContext contextObj = JAXBContext.newInstance(serializeableClass);
		Marshaller dataMarshaller = contextObj.createMarshaller();
		dataMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return dataMarshaller;
	}
	
	/**
	 * Prepare the data unmarshaller
	 * @return
	 * @throws JAXBException
	 */
	private Unmarshaller prepareUnmarshaller() throws JAXBException{
		JAXBContext contextObj = JAXBContext.newInstance(serializeableClass);
		Unmarshaller dataUnmarshaller = contextObj.createUnmarshaller();
		return dataUnmarshaller;
	}
}
