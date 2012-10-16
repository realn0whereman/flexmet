package com.flexmet.global;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

/**
 *	Data Struture used to store the config information. Uses nested hash tables.
 *  @author Shane del Solar
 *  @version 1.01
 */

public class Trikey
{
	private HashMap<String, HashMap<String, String>> table;
	
	public Trikey()
	{
		this.table = new HashMap<String, HashMap<String, String>>();
	}

	public void addSection(String sectionName)
	{
		if(sectionName == null)
		{
			throw new NullPointerException();
		}
		else
		{
			table.put(sectionName, new HashMap<String, String>());
		}		
	}

	public void addOption(String sectionName, String optionName, String value)
	{
		if(sectionName == null)
		{
			throw new NullPointerException();
		}
		if(optionName == null)
		{
			throw new NullPointerException();
		}
		if(value == null)
		{
			throw new NullPointerException();
		}
		
		HashMap<String, String> temp = table.get(sectionName);
		temp.put(optionName, value);
	}

	public String getValue(String sectionName, String optionName)
	{
		if(sectionName == null)
		{
			throw new NullPointerException();
		}
		if(optionName == null)
		{
			throw new NullPointerException();
		}

		HashMap<String, String> temp = table.get(sectionName);
		
		if(temp == null)
		{
			System.err.println("Config Error : Section name \"" + sectionName + "\" does not exist.");
		}

		if(temp.get(optionName) == null)
		{
			System.err.println("Config Error : Property name \"" + optionName + "\" does not exist in section: \"" + sectionName + "\".");
		}

		return temp.get(optionName);
	}

	public void debugPrintAllEntries(boolean toLog)
	{
		Set<String> overKeySet = table.keySet();
		Iterator<String> overKeySetIterator = overKeySet.iterator();

		Collection<HashMap<String, String>> overValues = table.values();
		Iterator<HashMap<String, String>> overValuesIterator = overValues.iterator();	
		
		debugPrint("Attempting to pring the loaded configuration file:", toLog);

		while(overKeySetIterator.hasNext() && overValuesIterator.hasNext())
		{
			String currentOverKey = overKeySetIterator.next();
			HashMap<String, String> currentHashMap = overValuesIterator.next();

			//Retrieve the Under HashMap Keys and Values
			Set<String> underKeySet = currentHashMap.keySet();
			Iterator<String> underKeySetIterator = underKeySet.iterator();

			Collection<String> underValues = currentHashMap.values();
			Iterator<String> underValuesIterator = underValues.iterator();							

			debugPrint("Section Name: \"" + currentOverKey + "\"", toLog);
			debugPrint("{", toLog);
	
			while(underKeySetIterator.hasNext() && underValuesIterator.hasNext())
			{
				String key = underKeySetIterator.next();
				String value = underValuesIterator.next();

				debugPrint("\t" + key + " : " + value, toLog);				
			}

			debugPrint("}", toLog);
		}
	}

	private void debugPrint(String message, boolean toLog)
	{
		if(message == null)
		{
			System.err.println("Trikey Error: Null got pasted into debugPrint as the message.");
		}

		if(toLog)
		{

		}
		else
		{
			System.out.println(message);
		}
	}
}
