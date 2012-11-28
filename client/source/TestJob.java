import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.flexmet.global.Log;

public class TestJob implements Job
{
	public void execute(JobExecutionContext args)
	{
		JobDataMap map = args.getJobDetail().getJobDataMap();
		System.out.println(map.get("test"));	
	}
}
