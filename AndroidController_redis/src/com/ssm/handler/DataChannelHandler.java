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
 * PlayRTCData를 이용하여 데이터 통신 기능을 구현한 클래스  <br>
 * 데이터 수신 및 상태/오류 수신을 위해 PlayRTCDataObserver인터페이스를 구현 <br><br>
 * <b>Method</b>
 * <pre>
 * 1. public void setDataChannel(PlayRTCData data)
 *    PlayRTCObserver의 onAddDataStream에서 PlayRTCData를 받아 전달 받음.
 * 2. public void sendText()
 *    텍스트 데이터를 상대방에게 전송
 * 3. public void sendBinary()
 *    이미지 등의 Binary 데이터를 상대방에게 전송
 * 4. public void sendFile()
 *   파일을 읽어 상대방에게 전송 
 * </pre>
 * <b>PlayRTCDataObserver Interface</b>
 * <pre>
 * 1. public void onProgress(final PlayRTCData obj, final String peerId, final String peerUid, final long recvSize, final PlayRTCDataHeader header) 
 *    데이터 수신 진행 정보 
 * 2. public void onMessage(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataHeader header, final byte[] data)
 *    데이터 수신
 * 3. public void onStateChange(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataStatus state)
 *    상태 변경 발생
 * 4. public void onError(final PlayRTCData obj, final String peerId, final String peerUid, final long id, final PlayRTCDataCode code, final String desc)
 *    오류 발생
 * </pre>
 *
 */
public class DataChannelHandler  implements PlayRTCDataObserver{
	private static final String LOG_TAG = "DATA-HANDLER";
	
	private Activity activity = null;
	/* 로그 출력 TextBox */
	private SlideLogView logView = null;
	
	/* P2P 데이터 통신을 위한 PlayRTCData객체  */
	private PlayRTCData dataChannel = null;
	
	//파일 전송 InputStream
	private InputStream dataIs = null;
	
	/**
	 * 생성자 
	 * @param activity MainActivity
	 * @param logView 
	 * @param logView SlideLogView, 로그 출력 TextBox
	 */
	public DataChannelHandler(Activity activity, SlideLogView logView) {
		this.activity = activity;
		this.logView = logView;
	}
	
	/**
	 * PlayRTCObserver의 onAddDataStream에서 PlayRTCData를 받아 전달 받음 
	 * @param data PlayRTCData
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onAddDataStream(com.sktelecom.playrtc.PlayRTC, String, String, PlayRTCData)
	 */
	public void setDataChannel(PlayRTCData data) {
		dataChannel = data;
		// 데이터 수신 및 상태/오류 수신을 위해 PlayRTCDataObserver 구현체를 전달 
		dataChannel.setEventObserver((PlayRTCDataObserver)this);
	}
	
	
	/**
	 * 텍스트 데이터를 상대방에게 전송 하며 글자 처리는 Javascript와의 통신을 위해 Unicode code point (2 byte)로 처리한 후 10240Byte 기준으로 분할 하여 전송
	 */
	public void sendText(final String sendData)
	{
		if(dataChannel != null && dataChannel.getStatus() == PlayRTCDataStatus.Open)
		{
			dataChannel.sendText(sendData, new PlayRTCSendDataObserver(){
				@Override
				public void onSuccess(PlayRTCData obj, String peerId, String peerUid, long id, long size) {
					//Log.d(LOG_TAG,  "sendText onSuccess "+peerUid+" "+id+"["+size+"]");
					//AppUtil.showToast(activity, "메시지 보내기 완료"+sendData);
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendText onSuccess["+id+"] "+size +" bytes");
				}

				@Override
				public void onError(PlayRTCData obj, String peerId, String peerUid, long id, PlayRTCDataCode code, String desc) {
					//AppUtil.showToast(activity, "메시지 보내기 실패"+sendData);
					//Log.d(LOG_TAG,  "sendText onError "+peerUid+" "+id+"["+code+"] "+desc);
					logView.appendLog(">>Data-Channel["+ peerId+ "] sendText onError["+id+"] ["+code +"] "+desc);
				}
			});
		}
	}
	
	/**
	 *  이미지 등의 Binary 데이터를 상대방에게 전송합니다. <br>
	 *  Bynary 전송 시 데이터에 대한 MimeType을 같이 전달 <br>
	 *  아래 예에서는 텍스트에서 Byte 배열을 추출하여 전송하고 있으며, MimeType으로 null을 전달하며 <br>
	 *  문자에 대한 특별한 처리는 하지 않고 있어 수신 측이 native인 경우에 올바르게 수신하지 못할 수도 있음 <br>
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
	 *  파일을 전송하기 위해서 File 객체를 생성하여 전달 하거나 파일에서 InputStream을 샹성하여 전달<br>
	 *  아래 예에서는 Android Application의 asset폴더에 있는 파일로부터 InputStream을 생성하여 전달 <br>
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
	 * 데이터 수신 진행 정보 
	 * @param obj PlayRTCData
	 * @param peerId String, 상대방 사용자의 peer 아이디
	 * @param peerUid String, 상대방 사용자의 아이디
	 * @param recvSize long, 수신 패킷 데이터 원형 사이즈
	 * @param header PlayRTCDataHeader, PlayRTCDataHeader, 패킷 정보 
	 */
	@Override
	public void onProgress(final PlayRTCData obj, final String peerId, final String peerUid, final long recvSize, final PlayRTCDataHeader header) {
		// 데이터 유형을 확인할 수 있습니다.
		String dataType = header.isBinary()?"binary":"text";
		//Log.d(LOG_TAG,  "onProgress "+peerUid+" "+header.getId()+" recv "+dataType+"["+recvSize+"] ");
		//logView.appendLog(">>Data-Channel["+ peerId+ "] onProgress["+header.getId()+"] ["+dataType+"] ["+recvSize +"]");
	}
	
	/**
	 * 데이터 수신 <br>
	 * 헤더 정보를 확인하고 데이터 타입에 맞게 데이터를 처리<br>
	 * 만약 수신 데이터가 파일 저장이 필요한 경우에 수신 데이터를 파일을 생성 하고 파일에 추가하는 로직이 필요
	 * @param obj PlayRTCData
	 * @param peerId String, 상대방 사용자의 peer 아이디
	 * @param peerUid String, 상대방 사용자의 아이디
	 * @param header PlayRTCDataHeader, PlayRTCDataHeader, 패킷 정보 
	 * @param data byte[], 데이터
	 */
	@Override
	public void onMessage(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataHeader header, final byte[] data) {
		Log.d(LOG_TAG,  "PlayRTCDataEvent onMessage peerId["+peerId+"] peerUid["+peerUid+"]");
		// 텍스트 데이터 수신인 경우 
		if(header.getType() == PlayRTCDataHeader.DATA_TYPE_TEXT)
		{
			// 여기서 json 으로 구분해서 넘겨줘야된다.ㅋ 만약 스크린이면 activity의 핸들러에 값 보내고 제어라면 서버니까 서비스로 뺃을거다ㅋ 그러니 
			String recvText = new String(data);
			//AppUtil.showToast(activity, "데이터 왓니"+recvText);
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
			//AppUtil.showToast(activity, "좌표값 : "+data);
			// uInput 처리해야함.
			//Log.d(LOG_TAG, "Text["+recvText+"]");
			logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage["+header.getSize()+"]");
		}
		// 바이너리 데이터 수신인 경우 
		else {
			// 헤더에서 파일명을 학인한다.
			String filaNmae = header.getFileName();
			// 파일 명이 없다면 바이너리 데이터 수신.
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
			// 파일명이 있다면 파일 데이터 수신 임 
			else {
				Log.d(LOG_TAG, "File["+filaNmae+"]");
				
				// 파일 저장 경로 
				File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
	                    "/Android/data/" + activity.getPackageName() + "/files/"+filaNmae);  
				Log.d(LOG_TAG, "FilePath["+f.getAbsolutePath()+"]");
				logView.appendLog(">>Data-Channel["+ peerId+ "] onMessage File["+f.getAbsolutePath()+"]");
				// 수신 데이터를 파일로 저장한다. 
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
	 * 상태 변경 발생
	 * @param obj PlayRTCData
	 * @param peerId String, 상대방 사용자의 peer 아이디
	 * @param peerUid String, 상대방 사용자의 아이디
	 * @param state PlayRTCDataStatus, PlayRTCData 상태 코드 정의 
	 */
	@Override
	public void onStateChange(final PlayRTCData obj, final String peerId, final String peerUid, final PlayRTCDataStatus state) {
		//AppUtil.showToast(activity, "Data-Channel["+ peerUid+ "] "+state+"...");
		logView.appendLog(">>Data-Channel["+ peerUid+ "] "+state+"...");
		if(state==PlayRTCDataStatus.Open)
		{
			logView.appendLog(">>오픈이 맞네");
			if(activity instanceof ClientActivity)
			{
				((ClientActivity) activity).startButton();
			}
		}
	}
	
	/**
	 *  오류 발생
	 * @param obj PlayRTCData
	 * @param peerId String, 상대방 사용자의 peer 아이디
	 * @param peerUid String, 상대방 사용자의 아이디
	 * @param code PlayRTCDataCode, PlayRTCData 오류 코드 정의 
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
