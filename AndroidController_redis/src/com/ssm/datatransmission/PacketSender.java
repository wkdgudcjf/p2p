package com.ssm.datatransmission;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

import com.ssm.handler.DataChannelHandler;


/**
 * packet �� ���� ����.
 * data ������ View or Control value
 * @author armin
 *
 */
public class PacketSender implements PacketManager{

	JSONObject jsonPacketObj; 
	Queue dataQueue;
	
	boolean connOnWebRTC = false;
	DataChannelHandler dch;
	int resWidth, resHeight;
	int fps;
	
	public PacketSender(DataChannelHandler dch){
		dataQueue = new LinkedList<JSONObject>(); // ������ ť �ʱ�ȭ ( jsonObject )
		this.dch = dch;
	}

	public void sendPacket() { // dataQueue �� �ִ� ������ ����.
		// TODO Auto-generated method stub
		if(!dataQueue.isEmpty())
		{
			JSONObject dataObj = getDataInQueue(); // �̰� WebRTC�� �̿��� ������.
			dch.sendText(dataObj.toString());
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
		return resWidth;
	}

	@Override
	public int getResolutionHeight() {
		// TODO Auto-generated method stub
		return resHeight;
	}

	@Override
	public void putDataInQueue(JSONObject data) {
		// TODO Auto-generated method stub
		this.dataQueue.add(data);
	}

	@Override
	public JSONObject getDataInQueue() {
		// TODO Auto-generated method stub
		return (JSONObject) dataQueue.poll();
	}

	@Override
	public void setResolutionWidth(int width) {
		// TODO Auto-generated method stub
		this.resWidth = width;
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
