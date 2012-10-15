package com.flexmet.containers;

/**
 * Enum for defining the type of network event reaction a network handler should take
 * @author phillip
 *
 */
public enum NetworkEventType {
	GETJOBS (10),
	TESTEVENT(20),
	DEFAULT(99);
	
	private final int type;
	NetworkEventType(int type){
		this.type = type;
	}
	
	/**
	 * Maps a string input from the network to an event type
	 * @param networkInput input string from the network
	 * @return associated enum type
	 */
	public static NetworkEventType resolveType(String networkInput){
		NetworkEventType event;
		if(eventEquals(networkInput,"GETJOBS")){
			event = GETJOBS;
		} else {
			event = DEFAULT;
		}
		return event;
	}
	
	/**
	 * Utility function to compare strings. This exists in order to allow case insensitive comparisons
	 * as well as trimming any additional newline characters.
	 * @param inputString
	 * @param eventString
	 * @return
	 */
	private static boolean eventEquals(String inputString, String eventString){
		return inputString.trim().toUpperCase().equals(eventString);
	}
	
}
