import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Bootstrap {

	public void initialize(){
		boolean isRunning = isFlumeRunning();
		if(!isRunning){
			System.out.println("Flume isn't running");
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
			Process processSearch = Runtime.getRuntime().exec("jps -l | grep flume");
			String filtered;
			BufferedReader input = new BufferedReader(new InputStreamReader(processSearch.getInputStream()));
			while ((filtered = input.readLine()) != null) {
		        System.out.println(filtered);
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
