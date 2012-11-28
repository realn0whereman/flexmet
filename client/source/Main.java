import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;

import org.quartz.CronExpression;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;


import com.flexmet.global.Config;
import com.flexmet.global.Log;
import com.flexmet.global.Job;
import com.flexmet.global.MetricEvent;
import com.flexmet.global.JSONObject;


public class Main
{
	public static Scheduler scheduler;

	public static String hostName;
	
	private static int jobGroupNumber;

	public static void main(String[] args)
	{
		init();
		
		NetworkDaemon nDaemon = new NetworkDaemon();
		nDaemon.start();

		try
		{
			hostName = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException uhe)
		{
			die("Unable to get the hose name. Exception Message: " + uhe.getMessage());
		}
		
		ArrayList<Job> startingJobs = NetworkDaemon.getJobs();
		
		resetJobList(startingJobs);

//			event.setData(Integer.toString(i));
//			NetworkDaemon.sendFlumeEvent(event);
//		}
		test();
	}

	public static void init()
	{
		Config.init();

		Log.init(Config.convertSettingToInt("log", "default_level"));	

		System.out.println("Trying to initialize the scheduler");	
		//Init Scheduler
		try
		{
			StdSchedulerFactory factory = new StdSchedulerFactory();
			scheduler = factory.getScheduler();
			scheduler.start();
		}
		catch(SchedulerException se)
		{
			die("There was a problem getting the scheduler.  Exception message is:\n" + se.getMessage());		
		}

		hostName = "";
	}

	public static void uninit()
	{
		Log.uninit();

		Config.uninit();

		//System.exit(0);
	}

	public static void die(String message)
	{
		//Log.writeFatal(message);

		System.out.println("Fatal Error: " + message);

		System.exit(1);
	}	

	public static boolean resetJobList(ArrayList<Job> newJobs)
	{	
		if(newJobs == null)
		{
			System.out.println("Can't schedule a null job list");
			return false;
		}

	
		try
		{
			if(scheduler != null)
			{
				scheduler.shutdown(false);
			}			
			StdSchedulerFactory factory = new StdSchedulerFactory();
			scheduler = factory.getScheduler();
			scheduler.start();
		}
		catch(SchedulerException se)
		{
			die("Exception was thrown while trying to reset the job list. Exception message: " + se.getMessage());
			return false;
		}

		boolean success = true;

		for(int index = 0; index < newJobs.size(); index++)
		{
			if(scheduleNewJob(newJobs.get(index)))
			{
			}
			else
			{
				//This job didn't schedule.. Now What?
				success = false;
			}
		}

		return success;
	}

	public static boolean scheduleNewJob(Job job)
	{
		JobDetailImpl jobDetail = new JobDetailImpl("newJob", jobGroupNumber + "", ClientJob.class);
		jobDetail.getJobDataMap().put("command", job.getCommand());
		jobDetail.getJobDataMap().put("metricName", job.getMetricName());
		CronTriggerImpl trigger = new CronTriggerImpl("cronTrigger", jobGroupNumber + "");
		jobGroupNumber++;

		try
		{
			CronExpression cronTime = new CronExpression(job.getCronData());
			trigger.setCronExpression(cronTime);
			scheduler.scheduleJob(jobDetail, trigger);
		}
		catch(ParseException pe)
		{
			Log.writeError("Main.scheduleNewJob : Failure while trying to schedule Job. A Parse Exception was thrown for the following cron string: \"" + job.getCronData() + "\".  The Exception message:");
			Log.writeError(pe.getMessage());
			return false; 	
		}
		catch(SchedulerException se)
		{
			Log.writeError("Main.scheduleNewJob : Failure while trying to schedule Job. A SchedulerException has occured. ExceptionMessageBelow:");
			Log.writeError(se.getMessage());
			return false;
		}

		return true;
	}

	public static void test()
	{
		System.out.println(hostName);

		/*Job blah = new Job("ps", "0/5 * * * * ?");
			
		Job kitIsARetart = new Job("sensors -f", "0/8 * * * * ?");	

		scheduleNewJob(blah);
		System.out.println("Scheduling Second Job");
		scheduleNewJob(kitIsARetart);
		System.out.println("Finished Scheduling jobs");
		MetricEvent sampleEvent = new MetricEvent("filesNames Galore", "BADASS.com", "BADASS RANKING:", 9001);
		System.out.println(sampleEvent.getJSONString());
		System.out.println("Copying.......................................................................");
		MetricEvent copy = MetricEvent.getFromJSON(sampleEvent.getJSONString());
		System.out.println(copy.getJSONString());*/

		//JSONObject json = new JSONObject("Bacon");
		//System.out.println(json.getByte("hi"));
	}
}
