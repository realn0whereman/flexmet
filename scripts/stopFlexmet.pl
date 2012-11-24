#!/usr/bin/perl -w
#This program starts the entire flexmet framework including client jars, server jars, and collector flume "forwarderer"

#check input args
if ($#ARGV != 0 ) {
	print "usage: ./startFlexmet.pl <name of config file>\n";
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

#take appropriate action for server
foreach(@servers){
	$command = "ssh $_ \"jps -l | grep org.apache.flume.node | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
	
	$command = "ssh $_ \"jps -l | grep server.jar | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
}

foreach(@collectors){
	$command = "ssh $_ \"jps -l | grep org.apache.flume.node | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
}

foreach(@clients){
	$command = "ssh $_ \"jps -l | grep org.apache.flume.node | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
	
	$command = "ssh $_ \"jps -l | grep flexmet-client.jar | grep -v grep | awk '{print \$1}' | xargs kill\"";
	print $command."\n";
	print `$command`;
}
