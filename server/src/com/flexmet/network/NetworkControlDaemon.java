package com.flexmet.network;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import com.flexmet.network.NetworkHandlers.HudFastPathServiceHandler;
import com.flexmet.network.NetworkHandlers.HudPIQLServiceHandler;
import com.flexmet.network.NetworkHandlers.ThriftServiceHandler;
import com.flexmet.thrift.FastPathEvent;
import com.flexmet.thrift.FastPathService;
import com.flexmet.thrift.HudFastPathEvent;
import com.flexmet.thrift.HudFastPathService;
import com.flexmet.thrift.HudPIQLQuery;
import com.flexmet.thrift.HudPIQLResponse;
import com.flexmet.thrift.HudPIQLService;
import com.flexmet.thrift.ThriftEvent;
import com.flexmet.thrift.ThriftJobList;
import com.flexmet.thrift.ThriftService;

/**
 * Thread to handle network connections on the out of band control ports.
 * @author phillip
 *
 */
public class NetworkControlDaemon extends Thread implements Runnable {
	
	
	public void run(){
		//sendFastPath("ThisIsATestOfTheNationalEmergencyBroadcastSystem");
		//Launch a thread for joblist requests from clients
		new Thread(){
			public void run() {
				listenForJobListRequests();
			}
		}.start();
		
		//Launch a thread for fastpath requests from the hud
		new Thread(){
			public void run() {
				listenForHudFastPathRequests();
			}
		}.start();
		
		//Launch a thread for handling PIQL requests
		new Thread(){
			public void run() {
				listenForPIQLRequests();
			}
		}.start();
	}
	
	/**
	 * Listen for job list requests from clients
	 */
	public void listenForJobListRequests(){
		//Create and register the callback handler
		ThriftServiceHandler handler = new ThriftServiceHandler();
		ThriftService.Processor<ThriftServiceHandler> tp = new ThriftService.Processor<ThriftServiceHandler>(handler);
		
		TServerSocket serverSocket = null;
		try {
			//Create a socket to listen a the specified port, create a server with the processor handler wrapper,
			//and begin to listen for connection.
			serverSocket = new TServerSocket(ThriftService.commPort);
			TServer.Args serverArgs = new TServer.Args(serverSocket).processor(tp);
			TSimpleServer server = new TSimpleServer(serverArgs);
			server.serve();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}

	} 
	
	/**
	 * Listen for fast path requests from the HUD
	 */
	public void listenForHudFastPathRequests(){
		//Create and register the callback handler
		HudFastPathServiceHandler handler = new HudFastPathServiceHandler();
		HudFastPathService.Processor<HudFastPathServiceHandler> tp = new HudFastPathService.Processor<HudFastPathServiceHandler>(handler);
		TServerSocket serverSocket = null;
		
		try {
			//Create a socket to listen a the specified port, create a server with the processor handler wrapper,
			//and begin to listen for connection.
			serverSocket = new TServerSocket(HudFastPathService.hudFastPathPort);
			TServer.Args serverArgs = new TServer.Args(serverSocket).processor(tp);
			TSimpleServer server = new TSimpleServer(serverArgs);
			server.serve();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}
	}
	
	/**
	 * Listen for PIQL requests from the HUD
	 */
	public void listenForPIQLRequests(){
		//Create and register the callback handler
		HudPIQLServiceHandler handler = new HudPIQLServiceHandler();
		HudPIQLService.Processor<HudPIQLServiceHandler> tp = new HudPIQLService.Processor<HudPIQLServiceHandler>(handler);
		TServerSocket serverSocket = null;
		
		try {
			//Create a socket to listen a the specified port, create a server with the processor handler wrapper,
			//and begin to listen for connection.
			serverSocket = new TServerSocket(HudPIQLService.hudPIQLPort);
			TServer.Args serverArgs = new TServer.Args(serverSocket).processor(tp);
			TSimpleServer server = new TSimpleServer(serverArgs);
			server.serve();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}
	}
	
	/**
	 * TODO INCOMPLETE
	 * @param command FastPath command to send to clients
	 */
	public ArrayList<ThriftEvent> sendFastPath(String command){
		FastPathEvent fpEvent = new FastPathEvent();
		List<String> hostList = getClients();
		ArrayList<ThriftEvent> responses = new ArrayList<ThriftEvent>();
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
			responses.add(response);
			System.out.println(response.data);
			fpSocket.close();
		}
		return responses;
	}
	
	public ArrayList<String> getClients(){
		ArrayList<String> clients = new ArrayList<String>();
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader("flexmet.conf"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		try {
			while((line = inputStream.readLine()) != null){
				if(line.contains("client")){
					clients.add(line.substring(0,line.indexOf(":")));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clients;
	}
	
	
}
