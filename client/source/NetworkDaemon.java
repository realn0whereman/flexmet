import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.flexmet.global.Log;
import com.flexmet.global.Job;
import com.flexmet.global.MetricEvent;

import com.flexmet.thrift.FastPathEvent;
import com.flexmet.thrift.FastPathService;
import com.flexmet.thrift.ThriftEvent;
import com.flexmet.thrift.ThriftJobList;
import com.flexmet.thrift.ThriftService;

/**
 * Class that is responsible for network communication
 * @author phillip
 *
 */
public class NetworkDaemon extends Thread implements Runnable {
	 
	private final static String avroClientCommand = "flume-ng avro-client --host "+ NetworkDaemon.getAssociatedCollector("collectors.conf") +" --port " + ThriftService.clientFlumePort +" --conf /etc/flume-ng/conf/";
	private static BufferedWriter out = null;
	private static String masterServer = "factor076";
	private static final int bufferCap = 50;
	/**
	 * The method run when the thread starts. Starts listening for pastpath commands issued from the server
	 */
	public void run(){
		
		//start flume client 
		if(!isFlumeAvroClientRunning()){
			out = startFlumeAvroClient();
		}
		
			//Initialize the handler, and processor
			FastPathHandler handler = new FastPathHandler();
			FastPathService.Processor<FastPathHandler> tp = new FastPathService.Processor<FastPathHandler>(handler);
			TServerSocket fpServerSocket;
			try {
				//connect to the socket and start listening for requests.
				fpServerSocket = new TServerSocket(FastPathService.fastPathPort);
				TSimpleServer server = new TSimpleServer(new Args(fpServerSocket).processor(tp));
				server.serve();
			} catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	/**
	 * Fetches the current list of jobs that need to be processed from the server
	 * @return a list of jobs
	 */
	public static ArrayList<Job> getJobs(){
		TSocket t = new TSocket(masterServer, ThriftService.commPort);
		try {
			t.open();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TBinaryProtocol tbp = new TBinaryProtocol(t);
		ThriftService.Client client = new ThriftService.Client(tbp);
		ThriftJobList thriftJobList = null;
		try {
			thriftJobList = client.getJobs();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String jsonString = thriftJobList.getJobList();
		ArrayList<Job> jobList = parseJobList(jsonString);
		
		return jobList;
	}
	private static ArrayList<Job> parseJobList(String jsonString) {
		ArrayList<Job> jobList = new ArrayList<Job>();
		
		BufferedReader stringReader = new BufferedReader(new StringReader(jsonString));
		String line = "", currentJob = "";
		
		try {
			while((line = stringReader.readLine()) != null){
				currentJob += line + "\n";
				if(line.equals("}")){
					jobList.add(Job.getFromJSON(currentJob));
					currentJob = "";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobList;
	}

	/**
	 * Add a metric event to the flume buffer. If the buffer is over a certain size, flush it to flume.
	 * @param event 
	 */
	public static void sendFlumeEvent(MetricEvent event){
		System.out.println("Sending:"+event.getMetricName());
		if(!isFlumeAvroClientRunning()){
			System.out.println("Starting Flume Avro Client");
			out = startFlumeAvroClient();
		}
		
		try {
			out.write(event.getJSONString().replaceAll("\n", "")+"\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Start the flume avro client and return a buffer writer for it.
	 * @return a writer for the STDIN handle to the flume avro client process
	 */
	private static BufferedWriter startFlumeAvroClient() {
		System.out.println(avroClientCommand);
		BufferedWriter output = null;
		try {
			Process processSearch = Runtime.getRuntime().exec(avroClientCommand);
			output = new BufferedWriter( new OutputStreamWriter(processSearch.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
		
	}
	
	/**
	 * Check to see if the flume avro client is running
	 * @return
	 */
	private static boolean isFlumeAvroClientRunning(){
		boolean isRunning = false;
		try {
			//ps a | grep ... | grep -v grep  -- this lists all processes, search for the flume avro client, 
			//and removes "grep" from the possible running processes
			Process processSearch = Runtime.getRuntime().exec("jps -l");
			String filtered;
			BufferedReader input = new BufferedReader(new InputStreamReader(processSearch.getInputStream()));
			while ((filtered = input.readLine()) != null){
		        if(filtered.contains("org.apache.flume.client.avro.AvroCLIClient")){
					isRunning = true;
					break;
				}
		    }
			input.close();
		} catch (IOException e) {
			//TODO handle properly
			e.printStackTrace();
		} 
		return isRunning;
	}
	
	public static String getAssociatedCollector(String filename){
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			if(hostname.contains(".")){
				hostname = hostname.substring(0,hostname.indexOf("."));
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(new File(filename)));
			while((line = reader.readLine()) != null){
				if(line.contains(hostname)){
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line.substring(line.indexOf(":")+1,line.length());
	}
	
	
	/**
	 * Handler that reacts to fastpath events
	 * @author phillip
	 *
	 */
	class FastPathHandler implements FastPathService.Iface{
		/**
		 * This is the method that will be called by thrift when the server issues a fastpath command.
		 */
		public ThriftEvent sendFastPathCommand(FastPathEvent event) throws TException {
			String command = event.getCommand();
			ThriftEvent response = new ThriftEvent();
			response.setHostname(Main.hostName);
			response.setTimestamp(System.currentTimeMillis());

			Runtime runtime = null;
			Process process = null; 

			try
			{
				runtime = Runtime.getRuntime();
				process = runtime.exec(command);
			}
			catch(IOException ioe)
			{
				response.setData("Error. Exception Message: " + ioe.getMessage());
				return response;
			}
	
			try
			{
				process.waitFor();
			}
			catch(InterruptedException ie)
			{
				Log.writeError("The job has thrown an interrupted exception while trying to run. Message below:");
				Log.writeError(ie.getMessage());
				response.setData("Error. Exception Message: " + ie.getMessage());
				return response;
			}
			catch(NullPointerException npe)
			{
				System.out.println("Command: \"" + command + "\" is not valid.");
				response.setData("Command: \"" + command + "\" is not valid.");
				return response;
			}

			BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = "";
			String temp = "";	

			try
			{
				temp = outputReader.readLine();
			}
			catch(IOException ioe)	
			{
				Log.writeError("There was an IO exception while trying to read the command's output. Message Below:");
				Log.writeError(ioe.getMessage());
				response.setData("Error. Exception Message: " + ioe.getMessage());
				return response;
			}	

			while(temp != null)	
			{

				output = output.concat(temp);			
					
				try
				{
					temp = outputReader.readLine();
				}
				catch(IOException ioe)
				{
					Log.writeError("There was an IO exception while trying to read the command's output. Message Below:");
					Log.writeError(ioe.getMessage());
					response.setData("Error. Exception Message: " + ioe.getMessage());
					return response;
				}
			}
			
			response.setData(output);

			return response;
		}
	}
	
	
	
}
