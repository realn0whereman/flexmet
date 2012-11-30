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

#transfer files to machines
foreach(@servers,@collectors,@clients){
	$command = "rsync -avz *.jar $_:~/.";
	print $command."\n";
	print `$command`;
	$command = "rsync -avz *.conf $_:~/.";
	print $command."\n";
	print `$command`;
	$command = "rsync -avz *.txt $_:~/.";
	print $command."\n";
	print `$command`;
}


#take appropriate action for server
print "Starting servers\n";
foreach(@servers){
	$command = "ssh $_ \"java -jar server.jar &> server.out \" &";
	print $command."\n";
	system($command);
}
sleep(10);
print "Starting collectors\n";
foreach(@collectors){
	$command = "ssh $_ \"flume-ng agent --conf /etc/flume-ng/conf/ -f ~/flume.conf -Dflume.root.logger=DEBUG,console -n $_ &> collector.out\" &";
	print $command."\n";
	system($command);
}
print "Starting clients\n";
foreach(@clients){
	$command = "ssh $_ \"java -jar flexmet-client.jar &> client.out \" &";
	print $command."\n";
	system($command);
}
