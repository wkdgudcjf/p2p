package com.ssm.datatransmission;

import org.json.JSONObject;


/**
 * interface for Packet Managing
 * 
 * @author armin
 *
 */
interface PacketManager {
	
	void initJsonObject();
	
	int getResolutionWidth();
	int getResolutionHeight();
	void setResolutionWidth(int width);
	void setResolutionHeight(int height);
	
	void setFps(int fps);
	int getFps();
	
	int getDataQueueSize();
	
	void makeConnection();
	boolean isConnected();
	
	
	void putDataInQueue(JSONObject data);
	JSONObject getDataInQueue();
	
}
