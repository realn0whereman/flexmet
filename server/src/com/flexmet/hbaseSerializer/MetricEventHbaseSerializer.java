package com.flexmet.hbaseSerializer;

import java.util.LinkedList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase.HbaseEventSerializer;
import org.apache.flume.sink.hbase.SimpleRowKeyGenerator;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;

import com.flexmet.global.MetricEvent;
import com.google.common.base.Charsets;

/**
 * This class is what flume uses to serialize metric events into hbase
 * @author phillip
 *
 */
public class MetricEventHbaseSerializer implements HbaseEventSerializer {
	  private String rowPrefix;
	  private byte[] incrementRow;
	  private byte[] cf;
	  private byte[] plCol;
	  private byte[] incCol;
	  private KeyType keyType;
	  private byte[] payload;
	  private MetricEvent metricEvent;

	  public MetricEventHbaseSerializer(){

	  }

	  @Override
	  public void configure(Context context) {
	    rowPrefix = context.getString("rowPrefix", "default");
	    incrementRow =
	        context.getString("incrementRow", "incRow").getBytes(Charsets.UTF_8);
	    String suffix = context.getString("suffix", "uuid");

	    String payloadColumn = context.getString("payloadColumn");
	    String incColumn = context.getString("incrementColumn");
	    if(payloadColumn != null && !payloadColumn.isEmpty()) {
	      if(suffix.equals("timestamp")){
	        keyType = KeyType.TS;
	      } else if (suffix.equals("random")) {
	        keyType = KeyType.RANDOM;
	      } else if(suffix.equals("nano")){
	        keyType = KeyType.TSNANO;
	      } else {
	        keyType = KeyType.UUID;
	      }
	      plCol = payloadColumn.getBytes(Charsets.UTF_8);
	    }
	    if(incColumn != null && !incColumn.isEmpty()) {
	      incCol = incColumn.getBytes(Charsets.UTF_8);
	    }
	  }

	  @Override
	  public void configure(ComponentConfiguration conf) {
	  }

	  @Override
	  public void initialize(Event event, byte[] cf) {
	    this.payload = event.getBody();
	    this.cf = cf;
	  }

	  @Override
	  public List<Row> getActions() throws FlumeException {
	    List<Row> actions = new LinkedList<Row>();
	    if(plCol != null){
	      byte[] rowKey;
	      try {
	        if (keyType == KeyType.TS) {
	          rowKey = SimpleRowKeyGenerator.getTimestampKey(rowPrefix);
	        } else if(keyType == KeyType.RANDOM) {
	          rowKey = SimpleRowKeyGenerator.getRandomKey(rowPrefix);
	        } else if(keyType == KeyType.TSNANO) {
	          rowKey = SimpleRowKeyGenerator.getNanoTimestampKey(rowPrefix);
	        } else {
	          rowKey = SimpleRowKeyGenerator.getUUIDKey(rowPrefix);
	        }
	        
	        metricEvent = MetricEvent.getFromJSON(new String(payload));
	        Put put = new Put(rowKey);
	        //put all of the data in metricEvent into their respective columns
	        put.add(cf, "data".getBytes(), metricEvent.getData().getBytes());
	        put.add(cf, "metricName".getBytes(),metricEvent.getMetricName().getBytes());
	        put.add(cf, "hostname".getBytes(),metricEvent.getHostname().getBytes());
	        put.add(cf, "timestamp".getBytes(),String.valueOf(metricEvent.getTimestamp()).getBytes());
	        
	        actions.add(put);
	      } catch (Exception e){
	        throw new FlumeException("Could not get row key!", e);
	      }

	    }
	    return actions;
	  }
	    @Override
	    public List<Increment> getIncrements(){
	      List<Increment> incs = new LinkedList<Increment>();
	      if(incCol != null) {
	        Increment inc = new Increment(incrementRow);
	        inc.addColumn(cf, incCol, 1);
	        incs.add(inc);
	      }
	      return incs;
	    }

	    @Override
	    public void close() {
	    }

	    public enum KeyType{
	      UUID,
	      RANDOM,
	      TS,
	      TSNANO;
	    }
	    
	    private final byte[] longToBytes(long v) {
	        byte[] writeBuffer = new byte[ 8 ];

	        writeBuffer[0] = (byte)(v >>> 56);
	        writeBuffer[1] = (byte)(v >>> 48);
	        writeBuffer[2] = (byte)(v >>> 40);
	        writeBuffer[3] = (byte)(v >>> 32);
	        writeBuffer[4] = (byte)(v >>> 24);
	        writeBuffer[5] = (byte)(v >>> 16);
	        writeBuffer[6] = (byte)(v >>>  8);
	        writeBuffer[7] = (byte)(v >>>  0);

	        return writeBuffer;
	    }
}


