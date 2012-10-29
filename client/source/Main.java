import com.flexmet.global.Config;
import com.flexmet.global.Log;

public class Main
{
	public static NetworkReceivedEventListener networkListener;
	public static NetworkThread networkThread;	

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
		
		//Init connection to the server:
		networkThread = new NetworkThread();

		//Register Listener:
		networkListener = new NetworkReceivedEventListener();
		networkThread.addReceiveListener(networkListener);

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
		Test testObject = new Test();
		NetworkReceivedEventListener listener = new NetworkReceivedEventListener();
		testObject.addEventListener(listener);
		testObject.fireEvent();
	}
}
