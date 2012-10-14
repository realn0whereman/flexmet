public class Main
{
	public static void init()
	{
		Config.init();

		Log.init(Config.convertSettingToInt("log", "default_level"));
	}

	public static void main(String[] args)
	{
		init();
		
		run();
		//test();

		uninit();
	}

	public static void run()
	{

	}
	
	public static void uninit()
	{
		Log.uninit();

		Config.uninit();
	}

	public static void die(String message)
	{
		//Log.writeFatal(message);

		System.out.println("Fatal Error: " + message);

		System.exit(1);
	}

	public static void test()
	{

	}
}                     
