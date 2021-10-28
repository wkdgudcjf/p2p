package com.ssm.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.sktelecom.playrtc.observer.PlayRTCDataObserver;
import com.sktelecom.playrtc.observer.PlayRTCSendDataObserver;
import com.sktelecom.playrtc.stream.PlayRTCData;
import com.sktelecom.playrtc.stream.PlayRTCData.PlayRTCDataCode;
import com.sktelecom.playrtc.stream.PlayRTCData.PlayRTCDataStatus;
import com.sktelecom.playrtc.stream.PlayRTCDataHeader;
import com.ssm.androidcontroller.SlideLogView;
import com.ssm.client.ClientActivity;
import com.ssm.server.ServerActivity;
import com.ssm.util.AppUtil;

public class DataChannelHandler  implements PlayRTCDataObserver{
	private static final String LOG_TAG = "DATA-HANDLER";
	
	private Activity activity = null;
	private SlideLogView logView = null;
	
	private PlayRTCData dataChannel = null;
	
	//���� ���� InputStream
	private InputStream dataIs = null;
	
	public DataChannelHandler(Activity activity, SlideLogView logView) {
		this.activity = activity;
		this.logView = logView;
	}
	
	public void setDataChannel(PlayRTCData data) {
		dataChannel = data;
		dataChannel.setEventObserver((PlayRTCDataObserver)this);
	}
	
	public void sendText(final String sendData)
	{
		if(dataChannel != null && dataChannel.getStatus() == PlayRTCDataStatus.Open)
		{
			dataChannel.sendText(sendData, new PlayRTCSendDataObserver(){
				@Override
				public void onSuccess(PlayRTCData obj, String peerId, String peerUid, long id, long size) {
				
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendText onSuccess["+id+"] "+size +" bytes");
				}

				@Override
				public void onError(PlayRTCData obj, String peerId, String peerUid, long id, PlayRTCDataCode code, String desc) {
				
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendText onError["+id+"] ["+code +"] "+desc);
				}
			});
		}
	}
	
	public void sendBinary(byte[] sendData)
	{
		if(dataChannel != null && dataChannel.getStatus() == PlayRTCDataStatus.Open)
		{
			dataChannel.sendByte(sendData, null, new PlayRTCSendDataObserver(){
				@Override
				public void onSuccess(PlayRTCData obj, String peerId, String peerUid, long id, long size) {
					Log.d(LOG_TAG,  "sendBinary onSuccess "+peerUid+" "+id+"["+size+"]");
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendBinary onSuccess["+id+"] "+size +" bytes");
				}

				@Override
				public void onError(PlayRTCData obj, String peerId, String peerUid, long id, PlayRTCDataCode code, String desc) {
					Log.d(LOG_TAG,  "sendBinary onError "+peerUid+" "+id+"["+code+"] "+desc);
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendBinary onError["+id+"] ["+code +"] "+desc);
				}
			});
		}
	}
	
	public void sendFile()
	{
		if(dataChannel != null && dataChannel.getStatus() == PlayRTCDataStatus.Open)
		{
			final String fileName = "main.html";
			dataIs = null;
			try {
				dataIs = activity.getAssets().open(fileName);
				dataChannel.sendFile(dataIs, fileName, new PlayRTCSendDataObserver(){

					@Override
					public void onSuccess(PlayRTCData obj, String peerId, String peerUid, long id, long size) {
						Log.d(LOG_TAG,  "sendFile onSuccess "+peerUid+" "+id+"["+size+"]");
						logView.appendLog(">>Data-Channel["+ peerId+ "] sendFile["+fileName+"] onSuccess["+id+"] "+size +" bytes");
						closeInputStream();
					}

					@Override
					public void onError(PlayRTCData obj, String peerId, String peerUid, long id, PlayRTCDataCode code, String desc) {
						Log.d(LOG_TAG,  "sendFile onError "+peerUid+" "+id+"["+code+"] "+desc);
						logView.appendLog(">>Data-Channel["+ peerId+ "] sendFile["+fileName+"] onError["+id+"] ["+code +"] "+desc);
						closeInputStream();
					}
					
				});

			} catch (IOException e) {
				e.printStackTrace();
				closeInputStream();
			}
		}
	}
		
	@Override
	public void onProgress(final PlayRTCData obj, final String peerId, final String peerUid, final long recvSize, final PlayRTCDataHeader header) {
		// ������ ������ Ȯ���� �� �ֽ��ϴ�.
		String dataType = header.isBinary()?"binary":"text";
		//Log.d(LOG_TAG,  "onProgress "+peerUid+" "+header.getId()+" recv "+dataType+"["+recvSize+"] ");
		//logView.appendLog(">>Data-Channel["+ peerId+ "] onProgress["+header.getId()+"] ["+dataType+"] ["+recvSize +"]");
	}
	
	@Override
	public void onMessage(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataHeader header, final byte[] data) {
		Log.d(LOG_TAG,  "PlayRTCDataEvent onMessage peerId["+peerId+"] peerUid["+peerUid+"]");
		if(header.getType() == PlayRTCDataHeader.DATA_TYPE_TEXT)
		{
				String recvText = new String(data);
			AppUtil.showToast(activity, "������ �Ӵ�"+recvText);
			if(recvText.equals("start"))
			{
				((ServerActivity)activity).startImage();
			}
			else
			{
				JSONObject revobj = null;
				try
				{
					revobj = new JSONObject(recvText);
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try
				{
					if(((String)revobj.get("dataType")).equals("control"))
					{
						((ServerActivity)activity).inputMessage(revobj);
					}
					else
					{
						((ClientActivity)activity).saveImage(revobj);
					}
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//AppUtil.showToast(activity, "��ǥ�� : "+data);
			//Log.d(LOG_TAG, "Text["+recvText+"]");
			logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage["+header.getSize()+"]");
		}
		else {
			String filaNmae = header.getFileName();
			if(TextUtils.isEmpty(filaNmae))
			{
				Message msg = Message.obtain();
				msg.obj = data;
				msg.what = 1;
				((ClientActivity)activity).mHandler.sendMessage(msg);
				Log.d(LOG_TAG, "Binary["+header.getSize()+"]");
				logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage Binary["+header.getSize()+"]");
				Bitmap viewBitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				Message msg = Message.obtain();				
				msg.obj = viewBitmap;
				msg.what = 1;
				((ClientActivity)activity).mHandler.sendMessage(msg);
			}
			else {
				Log.d(LOG_TAG, "File["+filaNmae+"]");
				
				File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
	                    "/Android/data/" + activity.getPackageName() + "/files/"+filaNmae);  
				Log.d(LOG_TAG, "FilePath["+f.getAbsolutePath()+"]");
				logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage File["+f.getAbsolutePath()+"]");
				try {
					if(!f.exists()){
						f.createNewFile();
					}
					FileOutputStream dataWs = new FileOutputStream(f.getAbsolutePath(), false);
					dataWs.write(data);
					dataWs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
		@Override
	public void onStateChange(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataStatus state) {
		logView.appendLog(">>Data-Channel["+ peerUid+ "] "+state+"...");
		if(state==PlayRTCDataStatus.Open)
		{
			logView.appendLog(">>������ �³�");
			if(activity instanceof ClientActivity)
			{
				((ClientActivity) activity).startButton();
			}
		}
	}
	
	@Override
	public void onError(final PlayRTCData obj, final String peerId, final String peerUid, final long id, final PlayRTCDataCode code, final String desc) {
		//AppUtil.showToast(activity, "Data-Channel["+ peerUid+ "] onError["+code+"] "+desc);
		logView.appendLog(">>Data-Channel["+ peerUid+ "] onError["+code+"] "+desc);
	}
	
	private void closeInputStream() {
		if(dataIs != null) {
			try {
				dataIs.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dataIs = null;
	}
}
