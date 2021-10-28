package com.ssm.datatransmission;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Screen 과 Input n Output 을 관리하는 클래스
 *  생성되는 데이터를 web RTC를 통해 전송한다.
 * @author armin
 */
public class PacketReceiver implements PacketManager {

	JSONObject jsonPacketObj; 
	Queue dataQueue;
	
	boolean connOnWebRTC = false;
	
	int resWidht, resHeight;
	int fps;
	
	public PacketReceiver(){
		dataQueue = new LinkedList<JSONObject>();
	}

	// webRTC를 통해 packet을 전송받는다.
	void receivePacket(String jsonPacket){
		try {
			jsonPacketObj = new JSONObject(jsonPacket);

			// 전송받은 패킷의 데이터 타입을 읽는다.
			if(jsonPacketObj.get("dataType").equals("display")){
				// screen인 경우 ( 화면을 queue 에 입력해서 buffering )
				
				resWidht = jsonPacketObj.getInt("width"); 
				resHeight = jsonPacketObj.getInt("height");
				dataQueue.add(jsonPacketObj.get("imgByte"));
				
				
			}else{ 
				// control인 경우 ( queue 에 입력하지 않고 바로 제어 )
				
				/*
				 *  김 형 part
				 */
				
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void initJsonObject() {
		// TODO Auto-generated method stub
		jsonPacketObj = new JSONObject();
	}

	@Override
	public int getResolutionWidth() {
		// TODO Auto-generated method stub
		return resWidht;
	}

	@Override
	public int getResolutionHeight() {
		// TODO Auto-generated method stub
		return resHeight;
	}

	@Override
	public void putDataInQueue(JSONObject data) { // queue에 데이터를 넣는다
		// TODO Auto-generated method stub
		dataQueue.add(data);
	}

	@Override
	public JSONObject getDataInQueue() { // dequeue data from queue
		// TODO Auto-generated method stub
		return (JSONObject) dataQueue.poll();
	}
	
	// 전송받은 해상도를 receiver에 설정
	@Override
	public void setResolutionWidth(int width) {
		// TODO Auto-generated method stub
		this.resWidht = width;
	}

	@Override
	public void setResolutionHeight(int height) {
		// TODO Auto-generated method stub
		this.resHeight = height;
	}

	@Override
	public int getDataQueueSize() {
		// TODO Auto-generated method stub
		return dataQueue.size();
	}

	@Override
	public void setFps(int fps) {
		// TODO Auto-generated method stub
		this.fps = fps;
	}

	@Override
	public int getFps() {
		// TODO Auto-generated method stub
		return fps;
	}

	@Override
	public void makeConnection() {
		// TODO Auto-generated method stub
		connOnWebRTC = true;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return connOnWebRTC;
	}


	
}
