#!/usr/bin/perl -w
#This program creates the configuration for client->collector mappings and flume config

#check input args
if ($#ARGV != 0 ) {
	print "usage: ./generateConfig.pl <name of config file>\n";
	exit;
}
$file=$ARGV[0];

#read flexmet configuration
print "Reading $file\n";
open(FILE,$file);
while(<FILE>){
    $_ =~ /(\w*):(\w*)/;
    if($2 eq "server"){
	push(@servers,$1);
    }
    if($2 eq "collector"){
	push(@collectors,$1);
    }
    if($2 eq "client"){
	push(@clients,$1);
    }
}

#generate the flume config

##generate client-collector mappings
print "Generating client<->collector mappings\n";
$clientCollectorMappings = "";
$collectorIterator = 0;
foreach(@clients){
	$clientCollectorMappings .= $_.":".$collectors[($collectorIterator++)%@collectors];
	$clientCollectorMappings .= "\n";
}

##generate collector flume config string
print "Generating collector config\n";
$collectorString = "";
$serverIterator = 0;
foreach(@collectors){
	$collectorString .= getCollectorString($_,$servers[($serverIterator++)%@servers]);
	$collectorString .= "\n";
}

##generate server flume config string
print "Generating server config\n";
$serverString = "";
foreach(@servers){
	$serverString .= getServerString($_);
	$serverString .= "\n";
}

#write flume.conf file
print "Writing flume.conf\n";
open (FLUME, '>flume.conf');
print FLUME $serverString.$collectorString;
close(FLUME);

#write collectors.conf
print "Writing collectors.conf\n";
open (COLLECTOR,'>collectors.conf');
printf COLLECTOR $clientCollectorMappings;
close(COLLECTOR);

sub getCollectorString{
	$host = $_[0];
	$server = $_[1];
	$configString = <<END;
$host.sources = src2
$host.sinks = sink2
$host.channels = ch2
$host.sources.src2.type = avro
$host.sources.src2.port = 26812
$host.sources.src2.bind = localhost
$host.sources.src2.channels = ch2
$host.sinks.sink2.type = avro
$host.sinks.sink2.channel = ch2
$host.sinks.sink2.hostname = $server
$host.sinks.sink2.port = 26816
$host.channels.ch2.type=memory
END
	return $configString;
}

sub getServerString{
	$host = $_[0];
	$configString = <<END;
$host.sources = src1
$host.sinks = sink1
$host.channels = ch1
$host.sources.src1.type = avro
$host.sources.src1.port = 26816
$host.sources.src1.bind = localhost
$host.sources.src1.channels = ch1
$host.sinks.sink1.type = org.apache.flume.sink.hbase.HBaseSink
$host.sinks.sink1.channel = ch1
$host.sinks.sink1.table = test3
$host.sinks.sink1.columnFamily = foo
$host.sinks.sink1.column = tcol
$host.sinks.sink1.serializer = org.apache.flume.sink.hbase.SimpleHbaseEventSerializer
$host.sinks.sink1.serializer.payloadColumn = pcol
$host.sinks.sink1.serializer.incrementColumn = icol
$host.channels.ch1.type=memory
END
	return $configString;
	
}


