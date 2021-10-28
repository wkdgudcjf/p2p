package com.ssm.datatransmission;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Screen �� Input n Output �� �����ϴ� Ŭ����
 *  �����Ǵ� �����͸� web RTC�� ���� �����Ѵ�.
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

	// webRTC�� ���� packet�� ���۹޴´�.
	void receivePacket(String jsonPacket){
		try {
			jsonPacketObj = new JSONObject(jsonPacket);

			// ���۹��� ��Ŷ�� ������ Ÿ���� �д´�.
			if(jsonPacketObj.get("dataType").equals("display")){
				// screen�� ��� ( ȭ���� queue �� �Է��ؼ� buffering )
				
				resWidht = jsonPacketObj.getInt("width"); 
				resHeight = jsonPacketObj.getInt("height");
				dataQueue.add(jsonPacketObj.get("imgByte"));
				
				
			}else{ 
				// control�� ��� ( queue �� �Է����� �ʰ� �ٷ� ���� )
				
				/*
				 *  �� �� part
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
	public void putDataInQueue(JSONObject data) { // queue�� �����͸� �ִ´�
		// TODO Auto-generated method stub
		dataQueue.add(data);
	}

	@Override
	public JSONObject getDataInQueue() { // dequeue data from queue
		// TODO Auto-generated method stub
		return (JSONObject) dataQueue.poll();
	}
	
	// ���۹��� �ػ󵵸� receiver�� ����
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
