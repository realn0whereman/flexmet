package com.flexmet.bootstrap;


public class EntryPoint implements Runnable {

	public static void main(String[] args){
		EntryPoint mainThread = new EntryPoint();
		//mainThread.setDaemon(true);
		mainThread.run();
	}
	
	public void run(){
		Bootstrap b = new Bootstrap();
		b.initialize();
		while (true) {  //TODO what goes here?
	        try {  
	          Thread.sleep(500);  
	        } catch (InterruptedException x) { 
	        	//TODO handle this
	        }  
		}
	}
}
