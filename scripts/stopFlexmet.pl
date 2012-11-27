#!/usr/bin/perl -w
#This program stops the entire flexmet framework including client jars, server jars, and collector flume "forwarderer"

#check input args
if ($#ARGV != 0 ) {
	print "usage: ./stoplexmet.pl <name of config file>\n";
	exit;
}
$file=$ARGV[0];

print `./generateConfig.pl $file`;
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


foreach(@servers){
	#kill flume
	$command = "ssh $_ \"jps -l | grep org.apache.flume.node | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
	
	#kill server jar
	$command = "ssh $_ \"jps -l | grep server.jar | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
}

foreach(@collectors){
	#kill flume
	$command = "ssh $_ \"jps -l | grep org.apache.flume.node | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
}

foreach(@clients){
	#kill flume
	$command = "ssh $_ \"jps -l | grep org.apache.flume.node | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
	
	#kill client
	$command = "ssh $_ \"jps -l | grep flexmet-client.jar | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
}
