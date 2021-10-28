/*package com.ssm.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;

import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.ssm.androidcontroller.MainActivity;
import com.ssm.datatransmission.PacketReceiver;
import com.ssm.datatransmission.PacketSender;
import com.ssm.handler.SSMRTCHandler;
import com.ssm.handler.SSMRTCObserverImpl;
import com.ssm.handler.ServerDataChannelHandler;
import com.ssm.handler.ServerSSMRTCHandler;
import com.ssm.handler.ServerSSMRTCObserverImpl;
import com.ssm.natives.NativeCall;

public class SSMService extends Service{

	static String tag = "SSMService";
	Context context;
	private static final String SERVICE_URL = "http://211.168.206.1:5400";
	private ServerSSMRTCHandler playrtc = null;
	private ServerDataChannelHandler dataHandler = null;
	private ServerSSMRTCObserverImpl playrtcObserver = null;

	PacketSender pSender;
	PacketReceiver pReceiver;
	
	int resWidth, resHeight;
	int viewFps = 1; // view 갱신 주기 ( fps )
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		Log.i(tag, "onStartCommand(4)");
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	private final IBinder mBinder = new LocalBinder();    // 컴포넌트에 반환되는 IBinder

	// 컴포넌트에 반환해줄 IBinder를 위한 클래스
	public class LocalBinder extends Binder{
		SSMService getService()
		{
			return SSMService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent){
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(tag, "OnCreate(2)");

		dataHandler = new ServerDataChannelHandler(this);
		
		// PlatRTCObserver 구현 개체 
		playrtcObserver = createSSMRTCObserver();
		//PlayRTC 구현 개체 
		playrtc = createSSMRTCHandler(playrtcObserver);
		// 서비스 설정 
		playrtc.setConfiguration();
				
		playrtcObserver.setHandlers(playrtc.getPlayRTC(), dataHandler);
		//String channelName = txtCrChannelName.getText().toString();
		//String userId = txtCrUserId.getText().toString();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		// 현재 기기의 해상도를 세팅
		resWidth = size.x;
		resHeight = size.y;
		
		
		pSender = new PacketSender();
		pSender.setResolutionHeight(resHeight);
		pSender.setResolutionWidth(resWidth);
		
		viewFps = pSender.getFps(); // server 는 viewFps 에 맞춰서 전송한다.
		startInputServer();
		try {
			playrtc.createChannel("server");
		} catch (RequiredConfigMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*//**
	 * PlayRTCHandler 개체를 생성한다.<br>
	 * @return PlayRTCHandler
	 * @see com.playrtc.sample.handler.PlayRTCHandler
	 *//*
	private ServerSSMRTCHandler createSSMRTCHandler(ServerSSMRTCObserverImpl observer) {
		ServerSSMRTCHandler handler = null;
		try 
		{
			handler = new ServerSSMRTCHandler(this, SERVICE_URL, observer);
		} 
		catch (UnsupportedPlatformVersionException e) 
		{
			// 현재 SDK는 Android SDK 11 이상만 지원한다.
			e.printStackTrace();
		} 
		catch (RequiredParameterMissingException e) 
		{
			// SERVICE_URL과 PlayRTCObserver구현개체를 생성자에 전달해야 한다.
			e.printStackTrace();
		}
		
		return handler;
	}
	private ServerSSMRTCObserverImpl createSSMRTCObserver() 
	{
		ServerSSMRTCObserverImpl observer = new ServerSSMRTCObserverImpl(this);
		return observer;
	}
	private void startInputServer() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				
					Log.i(tag, "startInputServer(4)");
					
					//server = new ServerSocket(6155);
					//client = server.accept(); //?꾧뎔媛?client)媛??묒냽?댁빞 ?? 
					
					//inputEventHandler = new HyeongInputHandler(HyeongService.this, client);
					inputEventHandler = new HyeongInputHandler(SSMService.this);
					
					inputEventHandler.start(); //?닿쾶end?섏빞 ?꾨옒濡쒓렇媛??곕벏
					
					Log.i(tag, "inputEventHandler end");
					if(MainActivity.handler!=null)MainActivity.handler.sendEmptyMessage(UIHandler.SERVICE_CLOSE);
					stopSelf();
			}
		}.start();
	}

	public void onDestroy() {
		Log.d("SSMService", "onDestroy()");
			
		super.onDestroy();
	}

	// 자신의 화면을 만들어서 전송 
	private class makeScreenOnDevice extends AsyncTask<Integer, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			
			byte bArr[] = new byte[1000000];
			NativeCall nc = new NativeCall();
				
			int imgSize = nc.getFrame(bArr);
				
			JSONObject imgJsonObj = new JSONObject();
				
			try {
				imgJsonObj.put("dataType", "display");
				imgJsonObj.put("width", resWidth);
				imgJsonObj.put("height", resHeight);
				imgJsonObj.put("imgByte", bArr); // 화면을 Object로 담는다.
					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			pSender.putDataInQueue(imgJsonObj); // 큐에 데이터를 삽입
				
			return null;
		}
	}
}
*/