package com.flexmet.bootstrap;

public class EntryPoint extends Thread implements Runnable {

	public static void main(String[] args){
		Thread mainThread = new EntryPoint();
		mainThread.setDaemon(true);
		mainThread.run();
	}
	
	public void run(){
		Bootstrap b = new Bootstrap();
		b.initialize();
		while (true) {  
	        try {  
	          Thread.sleep(500);  
	        } catch (InterruptedException x) { 
	        	//TODO handle this
	        }  
		}
	}
}
