package com.flexmet.hbase;

//import javax.security.auth.login.Configuration;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;

public class HbaseDAO {

	public String dataTableName = "test3";
	public void initHbase(){ 
//		Configuration conf = HBaseConfiguration.create();
//		conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
//		Get query = new Get("poo".getBytes());
//		HTable table = null;
//		try {
//			table = new HTable(conf, dataTableName);
//			Result r = table.get(query);
//			System.out.println(r.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
	}
}
