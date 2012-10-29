package com.flexmet.jobstate;



import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.flexmet.thrift.FastPathEvent;
import com.flexmet.thrift.FastPathService;
import com.flexmet.thrift.ThriftEvent;
import com.flexmet.thrift.ThriftJobList;
import com.flexmet.thrift.ThriftService;

/**
 * Thread to handle network connections on the out of band control port.
 * @author phillip
 *
 */
public class NetworkControlDaemon extends Thread implements Runnable {
	
	
	public void run(){
		//sendFastPath("ThisIsATestOfTheNationalEmergencyBroadcastSystem");
		//while(true){
		listenForJobListRequests();
			//Thread.yield();
		//}
	}
	
	public void listenForJobListRequests(){
		ThriftServiceHandler handler = new ThriftServiceHandler();
		ThriftService.Processor<ThriftServiceHandler> tp = new ThriftService.Processor<ThriftServiceHandler>(handler);
		TServerSocket serverSocket;
		//TThreadPoolServer.Args serverArgs;
		
		try {
			serverSocket = new TServerSocket(ThriftService.commPort);
			TServer.Args serverArgs = new TServer.Args(serverSocket).processor(tp);
			TSimpleServer server = new TSimpleServer(serverArgs);
			//serverArgs = new TThreadPoolServer.Args(serverSocket).processor(handler);
			//TThreadPoolServer server = new TThreadPoolServer(serverArgs);
			server.serve();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	} 
	
	public void sendFastPath(String command){
		FastPathEvent fpEvent = new FastPathEvent();
		List<String> hostList = new ArrayList<String>(); //TODO fix later
		hostList.add("localhost");
		ThriftEvent response = null;
		for(String host:hostList){
			TSocket fpSocket = new TSocket(host, FastPathService.fastPathPort);
			try {
				fpSocket.open();
				TBinaryProtocol fpProtocol = new TBinaryProtocol(fpSocket);
				FastPathService.Client fpClient = new FastPathService.Client(fpProtocol);
				fpEvent.setCommand(command);
				response = fpClient.sendFastPathCommand(fpEvent);
				
			} catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(response.data);
			fpSocket.close();
		}
		
	}
	
	class ThriftServiceHandler implements ThriftService.Iface, TProcessor{

		@Override
		public ThriftJobList getJobs() throws TException {
			ThriftJobList jobList = new ThriftJobList();
			jobList.setJobList(JobLoader.getJobs().getJobList().toString()); //TODO JSONifiy this
			return jobList;
		}

		@Override
		public void send(ThriftEvent event) throws TException {
			//Unimplemented, this is handled by flume instead
			
		}

		@Override
		public boolean process(TProtocol in, TProtocol out) throws TException {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	//	private static NetworkHandler handler;
//	private final int port = 1234;
//	
//	public NetworkControlDaemon(){
//		handler = new NetworkHandler(); // create the network handler
//	}
//
//	public void run() {
//		NIOService service; 
//		try {
//			service = new NIOService(); // create a server
//			NIOServerSocket serverSocket = service.openServerSocket(port); // and get a socket from it on a port
//			serverSocket.listen(new ServerSocketObserverAdapter(){ //define the listener
//				public void newConnection(NIOSocket nioSocket){ // and what the listener does on a new connection
//					nioSocket.listen(new SocketObserverAdapter(){ // and what the listener does with a fresh socket
//						public void packetReceived(NIOSocket socket, byte[] packet){ // and receipt of a packet
//							DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet));
//							StringBuilder sb = new StringBuilder("");
//							try {
//								while(stream.available() > 0){ //while the packet still has bytes, append them to a string
//									sb.append((char)stream.readByte());
//								}
//								NetworkEventType event = NetworkEventType.resolveType(sb.toString()); // get the event that resolves from that string
//								handler.handle(event, socket); //call the proper function to handle the event
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} finally {
//								NetworkControlDaemon.closeQuietly(stream);
//								socket.closeAfterWrite(); // close after any writes by the handler.
//							}
//                        }
//					});
//				}
//				
//			});
//			
//			serverSocket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
//			while(true){ //read from the network forever
//				service.selectBlocking();
//				Thread.yield();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//TODO handle already taken sockets
//			e.printStackTrace();
//		}
//		
//	}
//	
//	/**
//	 * Utility method to avoid try catches within try catches and enable cleaner looking cleanup code.
//	 * @param stream
//	 */
//	private static void closeQuietly(DataInputStream stream){
//		try {
//			stream.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
