package com.flexmet.jobstate;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOService;
import naga.NIOSocket;
import naga.ServerSocketObserverAdapter;
import naga.SocketObserverAdapter;

import com.flexmet.global.NetworkEventType;

/**
 * Thread to handle network connections on the out of band control port.
 * @author phillip
 *
 */
public class NetworkControlDaemon extends Thread implements Runnable {
	private static NetworkHandler handler;
	private final int port = 1234;
	
	public NetworkControlDaemon(){
		handler = new NetworkHandler(); // create the network handler
	}

	public void run() {
		NIOService service; 
		try {
			service = new NIOService(); // create a server
			NIOServerSocket serverSocket = service.openServerSocket(port); // and get a socket from it on a port
			serverSocket.listen(new ServerSocketObserverAdapter(){ //define the listener
				public void newConnection(NIOSocket nioSocket){ // and what the listener does on a new connection
					nioSocket.listen(new SocketObserverAdapter(){ // and what the listener does with a fresh socket
						public void packetReceived(NIOSocket socket, byte[] packet){ // and receipt of a packet
							DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet));
							StringBuilder sb = new StringBuilder("");
							try {
								while(stream.available() > 0){ //while the packet still has bytes, append them to a string
									sb.append((char)stream.readByte());
								}
								NetworkEventType event = NetworkEventType.resolveType(sb.toString()); // get the event that resolves from that string
								handler.handle(event, socket); //call the proper function to handle the event
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								NetworkControlDaemon.closeQuietly(stream);
								socket.closeAfterWrite(); // close after any writes by the handler.
							}
                        }
					});
				}
				
			});
			
			serverSocket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
			while(true){ //read from the network forever
				service.selectBlocking();
				Thread.yield();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//TODO handle already taken sockets
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Utility method to avoid try catches within try catches and enable cleaner looking cleanup code.
	 * @param stream
	 */
	private static void closeQuietly(DataInputStream stream){
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
