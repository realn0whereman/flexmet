package com.flexmet.bootstrap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Bootstrap {

	public void initialize(){
		String runAgent = "flume-ng agent --conf ~/apache-flume-1.2.0/conf/ " +
				"-f ~/apache-flume-1.2.0/conf/flume.conf -Dflume.root.logger=DEBUG,console -n host1";
		boolean isRunning = isFlumeRunning();
		if(!isRunning){
			System.out.println("Flume isn't running, starting flume");
			try {
				Runtime.getRuntime().exec(runAgent);
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
		} catch (IOException e) {
			//TODO handle properly
			e.printStackTrace();
		} 
		return isRunning;
	}
}
