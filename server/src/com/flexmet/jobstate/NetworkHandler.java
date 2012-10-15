package com.flexmet.jobstate;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.flexmet.containers.Job;
import com.flexmet.containers.JobListWrapper;
import com.flexmet.containers.NetworkEventType;

import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.AsciiLinePacketReader;

/**
 * Class that the NetworkControLDaemon utilizes and calls to handle network events
 * @author phillip
 *
 */
public class NetworkHandler {

	/**
	 * exposed handler interface to multiplex between network events and the
	 * required actions
	 * @param event type of event
	 * @param socket network socket handling event
	 */
	public void handle(NetworkEventType event,NIOSocket socket){
		switch(event){
			case TESTEVENT:
				testHandle(socket);
				break;
			case GETJOBS:
				getJobs(socket);
				break;
			default:
				break;
		}
	}
	
	/**
	 * the GETJOBS NetworkEventType handler function
	 * @param socket
	 */
	private void getJobs(NIOSocket socket){
		ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputBytes);
		JobListWrapper jobs = JobLoader.getJobs();
		try {
			for(Job j:jobs.getJobList()){
				outStream.writeUTF(j.toString());
			}
			outStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		socket.write(outputBytes.toByteArray());
		socket.closeAfterWrite();
		
	}
	
	private void testHandle(NIOSocket socket){
		System.out.println("In Default Handler Here");
		ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputBytes);
		String response = "blah";
		try {
			for(char c:response.toCharArray()){
				outStream.writeChar(c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		socket.write(outputBytes.toByteArray());
		socket.closeAfterWrite();
	}
}
