package com.flexmet.global;

import java.io.IOException;
import java.io.StringReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class JSONObject
{
	public String name;
	private HashMap<String, String> data;

	public JSONObject()
	{
		name = "";
		data = new HashMap<String, String>();
	}

	public static JSONObject parseJSON(String json)
	{
		if(json == null)
		{
			System.out.println("JSONObject.parseJSON : Cannot parse a null string");
			return null;
		}

		JSONObject answer = new JSONObject();
		StringReader reader = new StringReader(json);

		StringBuilder slate = new StringBuilder();		
		char temp = ' ';

		answer.name = parseName(reader);
		
		boolean parsingKey = true;
		String key = "";
		String value = "";

		try
		{
			while(reader.ready())
			{
				temp = (char)reader.read();

				if(Character.isWhitespace(temp))
				{
					continue;
				}
				else if(temp == '"')
				{
					if(parsingKey)
					{
						key = parseString(reader);
					}
					else
					{
						value = parseString(reader);
					}
				}
				else if(temp == ':')
				{
					parsingKey = false;
				}
				else if(temp == ',')
				{
					if(value.isEmpty())
					{
						value = slate.toString();
						slate.replace(0, slate.length(), "");
					}

					answer.put(key, value);
					parsingKey = true;

					key = "";
					value = "";
				}
				else if(temp == '}')
				{
					if(!key.isEmpty() && (!value.isEmpty() || !(slate.length() == 0)))
					{
						if(value.isEmpty())
						{
							value = slate.toString();
						}

						answer.put(key, value);
					}

					return answer;
				}
				else
				{
					slate.append(temp);
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("An IO Exception has occured while trying to parse the JSON String. Message: " + ioe.getMessage());
		}

		return null;
	}

	private static String parseString(StringReader reader)
	{
		StringBuilder slate = new StringBuilder();
		char temp = ' ';

		try
		{
			while(reader.ready())
			{
				temp = (char)reader.read();

				if(temp == '\\')
				{
					slate.append(reader.read());
				}
				else if(temp == '"')
				{
					return slate.toString();
				}
				else
				{
					slate.append(temp);
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("An IO Exception had occured while trying to parse the JSON String. Message: " + ioe.getMessage());
			return "";
		}

		return "";
	}

	private static String parseName(StringReader reader)
	{
		StringBuilder slate = new StringBuilder();
	    char temp = ' ';

		try
		{
			while(reader.ready())
			{
				temp = (char)reader.read();

				if(Character.isWhitespace(temp))
				{
					continue;
				}
				else if(temp == '{')
				{
					return slate.toString();
				}
				else
				{
					slate.append(temp);
				}	
			}
		}
		catch(IOException ioe)
		{
			System.out.println("An IO Excpetion has occured while trying to read the name. Message: " + ioe.getMessage());
		}

		return "";
	}

	/**
	 *  Puts a key/value pair in the object.
	 */
	public void put(String key, String value)
	{
		if(key == null || value == null)
		{
			return;
		}
		else
		{
			data.put(key, value);
		}
	}

	public String getString(String key)	
	{
		if(key == null)
		{
			return null;
		}
		else
		{
			String temp = data.get(key);
				
			if(temp == null)
			{			
				System.out.println("JSONObject.getString : Error value for key: \"" + key + "\" does not exist.");
				return temp;
			}
			else
			{
				return temp;
			}
		}
	}
	
	public byte getByte(String key)
	{
		if(key == null)
		{
			return 0;
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getByte : Error value for key: \"" + key + "\" does not exist.");
				return 0;
			}

			try
			{
				return Byte.parseByte(temp);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("JSONObject.getByte : Error could not convert \"" + temp + "\" to byte.");
				return 0;
			}
		}
	}

	public short getShort(String key)
	{
		if(key == null)
		{
			return 0;
		}
		else
		{
			String temp = data.get(key);
			
			if(temp == null)
			{
				System.out.println("JSONObject.getShort : Error value for key: \"" + key + "\" does not exist.");
				return 0;
			}

			try
			{
				return Short.parseShort(temp);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("JSONObject.getShort : Error could not convert \"" + temp + "\" to short.");
				return 0;
			}
		}
	}

	public int getInt(String key)
	{
		if(key == null)
		{
			return 0;
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getInt : Error value for key: \"" + key + "\" does not exist.");
				return 0;
			}

			try
			{
				return Integer.parseInt(temp);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("JSONObject.getInt : Error could not convert \"" + temp + "\" to int.");
				return 0;
			}
		}
	}

	public long getLong(String key)
	{
		if(key == null)
		{
			return 0;
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getLong : Error value for key: \"" + key + "\" does not exist.");
				return 0;
			}

			try
			{
				return Long.parseLong(temp);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("JSONObject.getLong : Error could not convert \"" + temp + "\" to long");
				return 0;
			}
		}
	}

	public float getFloat(String key)
	{
		if(key == null)
		{
			return 0;
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getFloat : Error value for key: \"" + key + "\" does not exist.");
				return 0.0f;
			}

			try
			{
				return Float.parseFloat(temp);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("JSONObject.getFloat : Error could not convert \"" + temp + "\" to float.");
				return 0.0f;
			}
		}
	}

	public double getDouble(String key)
	{
		if(key == null)
		{
			return 0;
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getDouble : Error value for key: \"" + key + "\" does not exist.");
				return 0;
			}

			try
			{
				return Double.parseDouble(temp);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("JSONObject.getDouble : Error could not convert \"" + temp + "\" to double.");
				return 0;
			}
		}
	}

	public char getChar(String key)
	{
		if(key == null)
		{
			return ' ';
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getChar : Error value for key: \"" + key + "\" does not exist.");
				return 0;
			}
			else if(temp.isEmpty())
			{
				System.out.println("JSONObject.getChar:  Error value for key: \"" + key + "\" is empty string");
				return ' ';
			}
			else if(temp.length() == 1)
			{
				return temp.charAt(0);
			}
			else
			{
				System.out.println("JSONObject.getChar : String has more then one character, returning the first.");
				return temp.charAt(0);
			}			
		}
	}

	public boolean getBoolean(String key)
	{
		if(key == null)
		{
			return false;
		}
		else
		{
			String temp = data.get(key);

			if(temp == null)
			{
				System.out.println("JSONObject.getBoolean : Error value for key: \"" + key + "\" does not exist.");
				return false;
			}
			else if(temp.equals("true"))
			{
				return true;
			}
			else if(temp.equals("false"))
			{	
				return false;
			}
			else
			{
				System.out.println("JSONObject.getBoolean : Key: \"" + key + "\" can't be converted to boolean so returning false");
				return false;
			}
		}
	}

	public String getJSONString()
	{
		StringBuilder slate = new StringBuilder("");
		
		slate.append(this.name + "\n{\n");
		
		Set<String> keySet = data.keySet();
		Collection<String> valueSet = data.values();

		Iterator<String> keyIterator = keySet.iterator();
		Iterator<String> valueIterator = valueSet.iterator();

		boolean first = true;

		while(keyIterator.hasNext() && valueIterator.hasNext())
		{
			if(first)
			{
				slate.append("\"" + keyIterator.next() + "\" : \"" + valueIterator.next() + "\"");
				first = false;
			}
			else
			{
				slate.append(",\n");
				slate.append("\"" + keyIterator.next() + "\" : \"" + valueIterator.next() + "\"");
			}
		}
		
		slate.append("\n}");

		return slate.toString();
	}
}
