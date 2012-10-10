import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

/**
 * Configuration manager. Should aways be called "config.txt"
 * @author Shane del Solar
 * @version 0.00
 */ 
public class Config
{
	//The data structure where all of the info is stored. The Trikey object is just a wrapper for HashTable inside of a HashTable
	private static Trikey options = null;
        
	/**
	 * Initialzies the Configuration manager. *WARNING STOP EXECUTION WHEN AN ERROR OCCURS*
	 * @see Main#die(String)
	 */
	public static void init()
	{ 
                int lineNumber = 0;
		File configFile = new File("config.txt");
		Scanner reader = null;

		try
		{ 
			reader = new Scanner(configFile);
		}
		catch(FileNotFoundException fnfe)
		{
			Main.die("Error: Unable to locate \"config.txt\". Looked in this directory: " + configFile.getPath() + "\"");
		}

		//Initalize Data Structure for config options
		options = new Trikey();		

		while(reader.hasNextLine())
		{
			//Count the line and use to help find errors
			lineNumber++; 
			String temp = reader.nextLine();
			temp = temp.trim();
                        
			//Ignore any empty lines or comment lines
			if(temp.isEmpty() || temp.startsWith("//") || temp.startsWith("#"))
			{	
				continue;
			}
			
			if(!temp.contains("section"))
			{
				reader.close();
				Main.die("Error while reading config file. Line: \"" + temp + "\" is out of place. Line Number: " + lineNumber); 
			}

			String[] pieces = temp.split(" ");

			if(!pieces[0].equals("section"))
			{
				reader.close();
				System.out.println("Error while reading config file. Line: \"" + temp + "\" is out of order. Line Number: " + lineNumber);
			}

			if(pieces.length != 2)
			{
				reader.close();
				System.out.println("Error while reading config file. Line: \"" + temp + "\" has " + pieces.length + " words when it should have 2. Line Number: " + lineNumber);			
			}
			
			temp = reader.nextLine();
			temp = temp.trim();
			lineNumber++;

			if(temp.equals("{"))
			{
				lineNumber = parseSection(reader , pieces[1], lineNumber); 
			}
			else
			{
				System.out.println("What's Here?: " + temp);
			}
			
		}
		reader.close();
	}

	public static String getSetting(String sectionName, String settingName)
	{
		String setting = options.getValue(sectionName, settingName);
		if(setting == null)
		{
			Main.die("Error: Failed to find section: \"" + sectionName + "\" with setting: \"" + settingName + "\".");
			return null;		
		}
		else
		{
			return setting;
		}	
	}
	
	public static boolean convertSettingToBoolean(String sectionName, String settingName)
	{
		String setting = getSetting(sectionName, settingName);
		
		if(setting.equalsIgnoreCase("true") || setting.equalsIgnoreCase("yes") || setting.equalsIgnoreCase("T"))
		{
			return true;	
		}
		else if(setting.equalsIgnoreCase("false") || setting.equalsIgnoreCase("no") || setting.equalsIgnoreCase("F"))
		{
			return false;
		}
		else
		{
			Main.die("Error: Tried to conversion to boolean value failed. Attempted to convert section \"" + sectionName + "\" setting: \"" + settingName  + "\".");
			return false;
		}
	}

	public static byte convertSettingToByte(String sectionName, String settingName)
	{
		String setting = getSetting(sectionName, settingName);
		
		try
		{
			return Byte.parseByte(setting);
		}
		catch(NumberFormatException nfe)
		{
			Main.die("Error: Conversion to byte has failed. A number format exception has occured. Section: \"" + sectionName + "\" Setting: \"" + settingName + "\".");
		}	

		return 0;	
	}

	public static char convertSettingToChar(String sectionName, String settingName)
	{
		String setting = getSetting(sectionName, settingName);
	
		return setting.charAt(0);	
	}

	public static short convertStingToShort(String sectionName, String settingName)
	{
		String setting = options.getValue(sectionName, settingName);
		
		try
		{
			return Short.parseShort(setting);
		}
		catch(NumberFormatException nfe)
		{
			Main.die("Error: Conversion to short has failed. A number format exception has occured. Section: \"" + sectionName + "\" Setting: \"" + settingName + "\".");
		}	

		return 0;		
	}

	public static int convertSettingToInt(String sectionName, String settingName)
	{
		String setting = options.getValue(sectionName, settingName);
		
		try
		{
			return Integer.parseInt(setting);
		}
		catch(NumberFormatException nfe)
		{
			Main.die("Error: Conversion to int has failed. A number format exception has occured. Section: \"" + sectionName + "\" Setting: \"" + settingName + "\".");
		}
		
		return 0;			
	}

	public static long convertSettingToLong(String sectionName, String settingName)
	{
		String setting = options.getValue(sectionName, settingName);
		
		try
		{
			return Long.parseLong(setting);
		}
		catch(NumberFormatException nfe)
		{
			Main.die("Error: Conversion to long has failed. A number format exception has occured. Section: \"" + sectionName + "\" Setting: \"" + settingName + "\".");
		}	

		return 0;		
	}

	public static float convertSettingToFloat(String sectionName, String settingName)
	{
		String setting = options.getValue(sectionName, settingName);
		
		try
		{
			return Float.parseFloat(setting);
		}
		catch(NumberFormatException nfe)
		{
			Main.die("Error: Conversion to float has failed. A number format exception has occured. Section: \"" + sectionName + "\" Setting: \"" + settingName + "\".");
		}		
		
		return 0;	
	}

	public static double convertSettingToDouble(String sectionName, String settingName)
	{
		String setting = options.getValue(sectionName, settingName);
		
		try
		{
			return Double.parseDouble(setting);
		}
		catch(NumberFormatException nfe)
		{
			Main.die("Error: Conversion to double has failed. A number format exception has occured. Section: \"" + sectionName + "\" Setting: \"" + settingName + "\".");
		}	

		return 0;		
	}

	public static void debugPrintLiveConfig(boolean toLog)
	{	
		options.debugPrintAllEntries(toLog);
	}

	public static void uninit()
	{
	}

	private static int parseSection(Scanner reader, String sectionName, int lineNumber)
	{
		String temp = reader.nextLine();
		temp = temp.trim();
		lineNumber++;
				
		options.addSection(sectionName);

		while(!temp.equals("}"))
		{
			//Ignore any empty lines or comment lines
			if(temp.isEmpty() || temp.startsWith("//") || temp.startsWith("#"))
			{	
				temp = reader.nextLine();
				temp = temp.trim();
				lineNumber++;
				continue;
			}

			String[] pieces = temp.split(" ");

			if(pieces.length != 2)
			{		
				Main.die("Config Error: line " + lineNumber + " in section \"" + sectionName + "\" has too many or too few words on it.  Line looks like this: \"" + temp + "\".");
			}

			options.addOption(sectionName, pieces[0], pieces[1]);

			temp = reader.nextLine();
			temp = temp.trim();
			lineNumber++;	
		}

		return lineNumber++;
	}
}	
