import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.ParseException;

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
<<<<<<< HEAD
=======
			
>>>>>>> 761f4321fdb2d6f5a7a74c89c4d997ec01ba5f85
//		Sample Code:
//		*Getting Jobs
		ArrayList<Job> startingJobs = NetworkDaemon.getJobs();
		
		for(int index = 0; index < startingJobs.size(); index++)
		{
			scheduleNewJob(startingJobs.get(index));
		}		
	
//		*Sending metrics
//		MetricEvent event = new MetricEvent();
//		event.setMetricName("CPU Temp");
//		event.setHostname("endpoint agent");
//		event.setTimestamp(System.currentTimeMillis());
//		int i = 100;
//		while(i-->0){
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

	public static boolean scheduleNewJob(Job job)
	{
		JobDetailImpl jobDetail = new JobDetailImpl("newJob", jobGroupNumber + "", ClientJob.class);
		jobDetail.getJobDataMap().put("command", job.getCommand());
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
		Job blah = new Job("ps", "0/5 * * * * ?");
			
		Job kitIsARetart = new Job("sensors -f", "0/8 * * * * ?");	

		scheduleNewJob(blah);
		System.out.println("Scheduling Second Job");
		scheduleNewJob(kitIsARetart);
		System.out.println("Finished Scheduling jobs");
		/*MetricEvent sampleEvent = new MetricEvent("filesNames Galore", "BADASS.com", "BADASS RANKING:", 9001);
		System.out.println(sampleEvent.getJSONString());
		System.out.println("Copying.......................................................................");
		MetricEvent copy = MetricEvent.getFromJSON(sampleEvent.getJSONString());
		System.out.println(copy.getJSONString());*/

		//JSONObject json = new JSONObject("Bacon");
		//System.out.println(json.getByte("hi"));
	}
}
