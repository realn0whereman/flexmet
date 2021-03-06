import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.flexmet.global.Log;
import com.flexmet.global.MetricEvent;

public class ClientJob implements Job
{
	public void execute(JobExecutionContext context)
	{
		if(context == null)
		{
			//What's the best thing to do here?
			Log.writeError("ClientJob:execute() : The run agrs were null");
			return;
		}

		JobDataMap args = context.getJobDetail().getJobDataMap();
		String command = (String)args.get("command");
		String metricName = (String)args.get("metricName");
		
		System.out.println("Command executing:"+command);
		
		//Get the runtime for the command
		Runtime runtime = null;
		Process process = null; 

		try
		{
			runtime = Runtime.getRuntime();
			process = runtime.exec(command);
		}
		catch(NullPointerException npe)	
		{
			Log.writeError("There was a null pointer exception while trying to get resources to run a job. Message below:");
			Log.writeError(npe.getMessage());
			return;
		}
		catch(IOException ioe)
		{
			Log.writeError("There was a IO exception while trying to get resources to run a job. Message below:");
			Log.writeError(ioe.getMessage());
			return;
		}

		//Wait for it to finish:
		try
		{
			process.waitFor();
		}
		catch(InterruptedException ie)
		{
			Log.writeError("The job has thrown an interrupted exception while trying to run. Message below:");
			Log.writeError(ie.getMessage());
			return;
		}
		catch(NullPointerException npe)
		{
			System.out.println("Command: \"" + command + "\" is not valid.");
			return;
		}

		BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = "";
		String temp = "";	

		try
		{
			temp = outputReader.readLine();
		}
		catch(IOException ioe)	
		{
			Log.writeError("There was an IO exception while trying to read the command's output. Message Below:");
			Log.writeError(ioe.getMessage());
			return;
		}	

		while(temp != null)	
		{

			output = output.concat(temp);			
					
			try
			{
				temp = outputReader.readLine();
			}
			catch(IOException ioe)
			{
				Log.writeError("There was an IO exception while trying to read the command's output. Message Below:");
				Log.writeError(ioe.getMessage());
				return;
			}
		}
	
		if(output.isEmpty())
		{
			Log.writeError("The output for command \"" + command + "\" was empty.");
			return;
		}
		
		//Time to give the results to Flume.
		long timeStamp = System.currentTimeMillis();
		
		//Testing:
		//System.out.println("output:\n" + output);
		//Log.writeSpecial(output);

		//Build the container Object
		MetricEvent event = new MetricEvent(output, Main.hostName, metricName, timeStamp);		

		//Send to Flume
		NetworkDaemon.sendFlumeEvent(event);
	}

}
