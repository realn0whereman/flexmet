public class Main
{
	public static void main(String[] args)
	{
		init();

		uninit();
	}

	public static void init()
	{
		Config.init();

		Log.init(Config.convertSettingToInt("log", "default_level"));	
	}

	public static void uninit()
	{
		Log.uninit();

		Config.uninit();

		System.exit(0);
	}

	public static void die(String message)
	{
		//Log.writeFatal(message);

		System.out.println("Fatal Error: " + message);

		System.exit(1);
	}
}
