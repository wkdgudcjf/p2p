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

/**
 * PlayRTCData�� �̿��Ͽ� ������ ��� ����� ������ Ŭ����  <br>
 * ������ ���� �� ����/���� ������ ���� PlayRTCDataObserver�������̽��� ���� <br><br>
 * <b>Method</b>
 * <pre>
 * 1. public void setDataChannel(PlayRTCData data)
 *    PlayRTCObserver�� onAddDataStream���� PlayRTCData�� �޾� ���� ����.
 * 2. public void sendText()
 *    �ؽ�Ʈ �����͸� ���濡�� ����
 * 3. public void sendBinary()
 *    �̹��� ���� Binary �����͸� ���濡�� ����
 * 4. public void sendFile()
 *   ������ �о� ���濡�� ���� 
 * </pre>
 * <b>PlayRTCDataObserver Interface</b>
 * <pre>
 * 1. public void onProgress(final PlayRTCData obj, final String peerId, final String peerUid, final long recvSize, final PlayRTCDataHeader header) 
 *    ������ ���� ���� ���� 
 * 2. public void onMessage(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataHeader header, final byte[] data)
 *    ������ ����
 * 3. public void onStateChange(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataStatus state)
 *    ���� ���� �߻�
 * 4. public void onError(final PlayRTCData obj, final String peerId, final String peerUid, final long id, final PlayRTCDataCode code, final String desc)
 *    ���� �߻�
 * </pre>
 *
 */
public class DataChannelHandler  implements PlayRTCDataObserver{
	private static final String LOG_TAG = "DATA-HANDLER";
	
	private Activity activity = null;
	/* �α� ��� TextBox */
	private SlideLogView logView = null;
	
	/* P2P ������ ����� ���� PlayRTCData��ü  */
	private PlayRTCData dataChannel = null;
	
	//���� ���� InputStream
	private InputStream dataIs = null;
	
	/**
	 * ������ 
	 * @param activity MainActivity
	 * @param logView 
	 * @param logView SlideLogView, �α� ��� TextBox
	 */
	public DataChannelHandler(Activity activity, SlideLogView logView) {
		this.activity = activity;
		this.logView = logView;
	}
	
	/**
	 * PlayRTCObserver�� onAddDataStream���� PlayRTCData�� �޾� ���� ���� 
	 * @param data PlayRTCData
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onAddDataStream(com.sktelecom.playrtc.PlayRTC, String, String, PlayRTCData)
	 */
	public void setDataChannel(PlayRTCData data) {
		dataChannel = data;
		// ������ ���� �� ����/���� ������ ���� PlayRTCDataObserver ����ü�� ���� 
		dataChannel.setEventObserver((PlayRTCDataObserver)this);
	}
	
	
	/**
	 * �ؽ�Ʈ �����͸� ���濡�� ���� �ϸ� ���� ó���� Javascript���� ����� ���� Unicode code point (2 byte)�� ó���� �� 10240Byte �������� ���� �Ͽ� ����
	 */
	public void sendText(final String sendData)
	{
		if(dataChannel != null && dataChannel.getStatus() == PlayRTCDataStatus.Open)
		{
			dataChannel.sendText(sendData, new PlayRTCSendDataObserver(){
				@Override
				public void onSuccess(PlayRTCData obj, String peerId, String peerUid, long id, long size) {
					//Log.d(LOG_TAG,  "sendText onSuccess "+peerUid+" "+id+"["+size+"]");
					//AppUtil.showToast(activity, "�޽��� ������ �Ϸ�"+sendData);
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendText onSuccess["+id+"] "+size +" bytes");
				}

				@Override
				public void onError(PlayRTCData obj, String peerId, String peerUid, long id, PlayRTCDataCode code, String desc) {
					//AppUtil.showToast(activity, "�޽��� ������ ����"+sendData);
					//Log.d(LOG_TAG,  "sendText onError "+peerUid+" "+id+"["+code+"] "+desc);
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendText onError["+id+"] ["+code +"] "+desc);
				}
			});
		}
	}
	
	/**
	 *  �̹��� ���� Binary �����͸� ���濡�� �����մϴ�. <br>
	 *  Bynary ���� �� �����Ϳ� ���� MimeType�� ���� ���� <br>
	 *  �Ʒ� �������� �ؽ�Ʈ���� Byte �迭�� �����Ͽ� �����ϰ� ������, MimeType���� null�� �����ϸ� <br>
	 *  ���ڿ� ���� Ư���� ó���� ���� �ʰ� �־� ���� ���� native�� ��쿡 �ùٸ��� �������� ���� ���� ���� <br>
	 */
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
	
	
	/**
	 *  ������ �����ϱ� ���ؼ� File ��ü�� �����Ͽ� ���� �ϰų� ���Ͽ��� InputStream�� �����Ͽ� ����<br>
	 *  �Ʒ� �������� Android Application�� asset������ �ִ� ���Ϸκ��� InputStream�� �����Ͽ� ���� <br>
	 */
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
		
	/** 
	 * ������ ���� ���� ���� 
	 * @param obj PlayRTCData
	 * @param peerId String, ���� ������� peer ���̵�
	 * @param peerUid String, ���� ������� ���̵�
	 * @param recvSize long, ���� ��Ŷ ������ ���� ������
	 * @param header PlayRTCDataHeader, PlayRTCDataHeader, ��Ŷ ���� 
	 */
	@Override
	public void onProgress(final PlayRTCData obj, final String peerId, final String peerUid, final long recvSize, final PlayRTCDataHeader header) {
		// ������ ������ Ȯ���� �� �ֽ��ϴ�.
		String dataType = header.isBinary()?"binary":"text";
		//Log.d(LOG_TAG,  "onProgress "+peerUid+" "+header.getId()+" recv "+dataType+"["+recvSize+"] ");
		//logView.appendLog(">>Data-Channel["+ peerId+ "] onProgress["+header.getId()+"] ["+dataType+"] ["+recvSize +"]");
	}
	
	/**
	 * ������ ���� <br>
	 * ��� ������ Ȯ���ϰ� ������ Ÿ�Կ� �°� �����͸� ó��<br>
	 * ���� ���� �����Ͱ� ���� ������ �ʿ��� ��쿡 ���� �����͸� ������ ���� �ϰ� ���Ͽ� �߰��ϴ� ������ �ʿ�
	 * @param obj PlayRTCData
	 * @param peerId String, ���� ������� peer ���̵�
	 * @param peerUid String, ���� ������� ���̵�
	 * @param header PlayRTCDataHeader, PlayRTCDataHeader, ��Ŷ ���� 
	 * @param data byte[], ������
	 */
	@Override
	public void onMessage(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataHeader header, final byte[] data) {
		Log.d(LOG_TAG,  "PlayRTCDataEvent onMessage peerId["+peerId+"] peerUid["+peerUid+"]");
		// �ؽ�Ʈ ������ ������ ��� 
		if(header.getType() == PlayRTCDataHeader.DATA_TYPE_TEXT)
		{
			// ���⼭ json ���� �����ؼ� �Ѱ���ߵȴ�.�� ���� ��ũ���̸� activity�� �ڵ鷯�� �� ������ ������ �����ϱ� ���񽺷� �����Ŵ٤� �׷��� 
			String recvText = new String(data);
			//AppUtil.showToast(activity, "������ �Ӵ�"+recvText);
			if(recvText.equals("start"))
			{
				((ServerActivity)activity).startImage();
			}
//			else
//			{
//				JSONObject revobj = null;
//				try
//				{
//					revobj = new JSONObject(recvText);
//				} catch (JSONException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try
//				{
//					if(((String)revobj.get("dataType")).equals("control"))
//					{
//						((ServerActivity)activity).inputMessage(revobj);
//					}
//					else
//					{
//						//((ClientActivity)activity).saveImage(revobj);
//					}
//				} catch (JSONException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			//AppUtil.showToast(activity, "��ǥ�� : "+data);
			// uInput ó���ؾ���.
			//Log.d(LOG_TAG, "Text["+recvText+"]");
			logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage["+header.getSize()+"]");
		}
		// ���̳ʸ� ������ ������ ��� 
		else {
			// ������� ���ϸ��� �����Ѵ�.
			String filaNmae = header.getFileName();
			// ���� ���� ���ٸ� ���̳ʸ� ������ ����.
			if(TextUtils.isEmpty(filaNmae))
			{
//				Message msg = Message.obtain();
//				msg.obj = data;
//				msg.what = 1;
//				((ClientActivity)activity).mHandler.sendMessage(msg);
//				Log.d(LOG_TAG, "Binary["+header.getSize()+"]");
				logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage Binary["+header.getSize()+"]");
				Bitmap viewBitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				Message msg = Message.obtain();				
				msg.obj = viewBitmap;
				msg.what = 1;
				((ClientActivity)activity).mHandler.sendMessage(msg);
			}
			// ���ϸ��� �ִٸ� ���� ������ ���� �� 
			else {
				Log.d(LOG_TAG, "File["+filaNmae+"]");
				
				// ���� ���� ��� 
				File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
	                    "/Android/data/" + activity.getPackageName() + "/files/"+filaNmae);  
				Log.d(LOG_TAG, "FilePath["+f.getAbsolutePath()+"]");
				logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage File["+f.getAbsolutePath()+"]");
				// ���� �����͸� ���Ϸ� �����Ѵ�. 
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
	
	
	/**
	 * ���� ���� �߻�
	 * @param obj PlayRTCData
	 * @param peerId String, ���� ������� peer ���̵�
	 * @param peerUid String, ���� ������� ���̵�
	 * @param state PlayRTCDataStatus, PlayRTCData ���� �ڵ� ���� 
	 */
	@Override
	public void onStateChange(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataStatus state) {
		//AppUtil.showToast(activity, "Data-Channel["+ peerUid+ "] "+state+"...");
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
	
	/**
	 *  ���� �߻�
	 * @param obj PlayRTCData
	 * @param peerId String, ���� ������� peer ���̵�
	 * @param peerUid String, ���� ������� ���̵�
	 * @param code PlayRTCDataCode, PlayRTCData ���� �ڵ� ���� 
	 * @param desc String, description 
	 */
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
