import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

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
	private final static String avroClientCommand = "flume-ng avro-client --host localhost --port " + ThriftService.clientFlumePort +" --conf /etc/flume-ng/conf/";
	private static LinkedList<String> buffer = new LinkedList<String>();
	private static BufferedWriter out = null;
	private static String masterServer = "localhost";
	private static final int bufferCap = 50;
	/**
	 * The method run when the thread starts. Starts listening for pastpath commands issued from the server
	 */
	public void run(){
		
		//start flume client 
		if(!isFlumeAvroClientRunning()){
			startFlumeAvroClient();
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
		ArrayList<Job> jobList = new ArrayList<Job>();
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
		System.out.println(thriftJobList.getJobList()); //TODO parse this
		return jobList;
	}
	/**
	 * Add a metric event to the flume buffer. If the buffer is over a certain size, flush it to flume.
	 * @param event 
	 */
	public static void sendFlumeEvent(MetricEvent event){
		System.out.println("Sending:"+event.getData());
		StringBuilder sb = new StringBuilder();
		
		//TODO Jsonify this
		sb.append(event.getTimestamp() + event.getMetricName() + event.getHostname() + event.getData());
		buffer.add(sb.toString());
		if(buffer.size() > bufferCap){
			processFlumeBuffer(); //TODO buffer also needs to be flushed on a certain timeout
			buffer.clear();
		}
	}
	
	/**
	 * Checks to see if there is a valid handle for the flume avro client. If not, execute the process and get the handle. Then
	 * proceed to write everything in the buffer to the flume avro client.
	 */
	private static void processFlumeBuffer(){
		if(!isFlumeAvroClientRunning()){
			System.out.println("Starting Flume Avro Client");
			out = startFlumeAvroClient();
		}
		try {
			for(String s:buffer){
				out.write(s);
				out.newLine();
			}
			out.flush();
			out.close();
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
		BufferedWriter out = null;
		try {
			Process processSearch = Runtime.getRuntime().exec(avroClientCommand);
			out = new BufferedWriter( new OutputStreamWriter(processSearch.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
		
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
			Process processSearch = Runtime.getRuntime().exec("ps a | grep "+avroClientCommand+" | grep -v grep");
			String filtered;
			BufferedReader input = new BufferedReader(new InputStreamReader(processSearch.getInputStream()));
			while ((filtered = input.readLine()) != null){
		        if(filtered.length() != 0 && filtered.contains(avroClientCommand)){
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
			System.out.println(event.getCommand());
			//TODO /*Run command*/
			ThriftEvent response = new ThriftEvent();
			response.setData(event.getCommand()+"3");
			response.setHostname("localhost");
			response.setTimestamp(System.currentTimeMillis());
			return response;
		}
	}
	
}
