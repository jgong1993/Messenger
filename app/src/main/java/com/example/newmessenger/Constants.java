package com.example.newmessenger;

public interface Constants {
	public static String ACTIVEMQ_URL = "tcp://mmenarini.ucsd.edu:61616";
	public static String ACTIVEMQ_URL_MQTT = "tcp://mmenarini.ucsd.edu:1883";
	public static String USERNAME = "student";	
	public static String PASSWORD = "cse110";	
	/* 
	 * All Topics and Queues must start with TEAM.XX. 
	 * where you should change XX with your team number.
	*/
	public static String QUEUENAME = "TEAM.25.test";
	public static String SERVER_TOPICNAME = "TEAM/25/server";
	public static String CLIENT_TOPICNAME = "TEAM/25/client";
}