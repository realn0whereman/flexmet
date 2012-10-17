//This is structure to model a metric being sent through flume
struct ThriftEvent {
        1: i64 timestamp, // time of creation
	2: string hostname, // source of metric
	3: string metricName, // name of the metric
        2: string data //the actual metric data
}

//models a list of jobs sent back from the main server
struct ThriftJobList {
	1: string list
}

//The flexmet client implements this to interact with the server directly to get jobs
//or with flume to send metric data
service ThriftService {
	ThriftJobList getJobs(), // get jobs directly from the server
        oneway void send(1: ThriftEvent event) // send metric data
}

//structure to hold a command to execute across the FastPath flow
struct FastPathEvent {
	1: string command
}

//This service will be implementent the CLIENT SIDE, but on the flexmet server.
//The purpose is this: the server will receive a fastpath request from the HUD
//and immediately forward this request to all the agents and away responses.
service FastPathService {
	ThriftEvent sendFastPathCommand(1: FastPathEvent event)
}


