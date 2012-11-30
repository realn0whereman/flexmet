package com.flexmet.network;

import java.util.ArrayList;

import org.apache.thrift.TException;

import com.flexmet.jobstate.JobLoader;
import com.flexmet.thrift.HudFastPathEvent;
import com.flexmet.thrift.HudFastPathService;
import com.flexmet.thrift.HudPIQLQuery;
import com.flexmet.thrift.HudPIQLResponse;
import com.flexmet.thrift.HudPIQLService;
import com.flexmet.thrift.ThriftEvent;
import com.flexmet.thrift.ThriftJobList;
import com.flexmet.thrift.ThriftService;

/**
 * Class that the NetworkControLDaemon utilizes and calls to handle network events
 * @author phillip
 *
 */
public class NetworkHandlers {
	//The handler for handling job list requests
	static class ThriftServiceHandler implements ThriftService.Iface{
	
		@Override
		public ThriftJobList getJobs() throws TException {
			ThriftJobList jobList = new ThriftJobList();
			 String jobListString = JobLoader.getJobs().toString(); 
			jobList.setJobList(jobListString); //TODO JSONifiy this
			return jobList;
		}
	
		@Override
		public void send(ThriftEvent event) throws TException {
			//Unimplemented, this is handled by flume instead
			
		}
	
		
	}
	
	//The handler for fastPath requests from the HUD
	static class HudFastPathServiceHandler implements HudFastPathService.Iface {
		private static NetworkControlDaemon ncd = new NetworkControlDaemon();
		@Override
		public HudFastPathEvent executeFastPath(HudFastPathEvent event){
			HudFastPathEvent totalResponse = new HudFastPathEvent();
			totalResponse.setFpData("");
			ArrayList<ThriftEvent> responses = ncd.sendFastPath(event.getFpData());
			for(ThriftEvent response:responses){
				totalResponse.setFpData(totalResponse.getFpData() + response.data);
			}
			return totalResponse;
		}
	
		
		
	}
	
	//The handler for PIQL requests from the HUD
	static class HudPIQLServiceHandler implements HudPIQLService.Iface{
	
		@Override
		public HudPIQLResponse executePIQLQuery(HudPIQLQuery piqlEvent) throws TException {
			System.out.println("PIQL Query:"+piqlEvent.getQuery());
			HudPIQLResponse response = new HudPIQLResponse();
			response.setResponse(piqlEvent.getQuery()+"resp");
			return response;
		}
	
	}
}