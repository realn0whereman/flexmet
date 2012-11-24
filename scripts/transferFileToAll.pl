#!/usr/bin/perl
#transfer a file to all servers listed in the config file
#check input args
if ($#ARGV != 1 ) {
	print "usage: ./transferFileToAll.pl <name of config file> <file to push to servers> \n";
	exit;
}
$file=$ARGV[0];
$fileToPush=$ARGV[1];

open(FILE,$file);
while(<FILE>){
    $_ =~ /(\w*):(\w*)/;
    push(@servers,$1);
}
print "Sending $fileToPush\n";
foreach(@servers){
	$command = "rsync -avz $fileToPush $_:~/.";
	print $command."\n";
	print `$command`;
}