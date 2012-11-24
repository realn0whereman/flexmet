import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerException;

import com.flexmet.global.Config;
import com.flexmet.global.Log;
import com.flexmet.global.MetricEvent;
import com.flexmet.global.JSONObject;


public class Main
{
	public static Scheduler scheduler;

	public static void main(String[] args)
	{
		init();
		NetworkDaemon nDaemon = new NetworkDaemon();
		nDaemon.start();
			
//		Sample Code:
//		*Getting Jobs
//		NetworkDaemon.getJobs();
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

		uninit();
	}

	public static void init()
	{
		Config.init();

		Log.init(Config.convertSettingToInt("log", "default_level"));	

		//Init Scheduler
		//SchedulerFactory factory = new SchedulerFactory();
		//scheduler = factory.getScheduler();
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

	public static void test()
	{
		MetricEvent sampleEvent = new MetricEvent("filesNames Galore", "BADASS.com", "BADASS RANKING:", 9001);
		System.out.println(sampleEvent.getJSONString());
		System.out.println("Copying.......................................................................");
		MetricEvent copy = MetricEvent.getFromJSON(sampleEvent.getJSONString());
		System.out.println(copy.getJSONString());

		//JSONObject json = new JSONObject("Bacon");
		//System.out.println(json.getByte("hi"));
	}
}
