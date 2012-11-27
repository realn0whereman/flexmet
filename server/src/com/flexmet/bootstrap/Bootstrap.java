package com.flexmet.bootstrap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.flexmet.jobstate.JobLoader;
import com.flexmet.network.NetworkControlDaemon;
/**
 * This class restores the state of scheduled jobs, opens network port for out of band communication
 * checks if flume is running, and if not starts it.
 * @author phillip
 *
 */
public class Bootstrap {
	public static JobLoader loader;
	public static NetworkControlDaemon networkServer;
	private static String hostname;
	static{
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			if(hostname.contains(".")){
				hostname = hostname.substring(0,hostname.indexOf("."));
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final String runAgent = "flume-ng agent --conf /etc/flume-ng/conf/ -f ./flume.conf -Dflume.root.logger=DEBUG,console -n "+hostname;
	
	public Bootstrap(){
		
		loader = new JobLoader();
		networkServer = new NetworkControlDaemon();
	}
	
	/**
	 * Main entry point into the initialization procedures. It calls the methods to load jobs from disk, open
	 * the network port, and start flume if it isn't running
	 */
	public void initialize(){
		loader.loadJobsFromDisk();
		
		networkServer.start();
		boolean isRunning = isFlumeRunning();
		if(!isRunning){
			System.out.println("Flume isn't running, starting flume1");
			try {
				startFlume();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Flume IS running");
		}
	}
	
	
	
	/**
	 * Checks to see if flume is running. Uses jps and grep to filter java processes. Double checks that correct 
	 * namespace is running
	 * @return
	 */
	private boolean isFlumeRunning(){
		boolean isRunning = false;
		String namespace = "org.apache.flume.node.Application";
		try {
			Process processSearch = Runtime.getRuntime().exec("jps -l");
			String filtered;
			BufferedReader input = new BufferedReader(new InputStreamReader(processSearch.getInputStream()));
			while ((filtered = input.readLine()) != null){
		        if(filtered.length() != 0 && filtered.contains(namespace)){
					isRunning = true;
				}
		    }
			input.close();
		} catch (IOException e) {
			//TODO handle properly
			e.printStackTrace();
		} 
		return isRunning;
	}
	
	/**
	 * Starts running flume with the parameters specified in runAgent
	 * @throws IOException
	 */
	private void startFlume() throws IOException{
		Process processSearch = Runtime.getRuntime().exec(runAgent);
		//TODO DEBUG
		String line = "";
		BufferedReader error = new BufferedReader(new InputStreamReader(processSearch.getErrorStream()));
		BufferedReader input = new BufferedReader(new InputStreamReader(processSearch.getInputStream()));
		while((line = error.readLine()) != null){
			System.out.println(line);
		}
		while ((line = input.readLine()) != null){
			System.out.println(line);
		}
		error.close();
		input.close();
	}
}
