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
	int viewFps = 1; // view ���� �ֱ� ( fps )
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		Log.i(tag, "onStartCommand(4)");
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	private final IBinder mBinder = new LocalBinder();    // ������Ʈ�� ��ȯ�Ǵ� IBinder

	// ������Ʈ�� ��ȯ���� IBinder�� ���� Ŭ����
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
		
		// PlatRTCObserver ���� ��ü 
		playrtcObserver = createSSMRTCObserver();
		//PlayRTC ���� ��ü 
		playrtc = createSSMRTCHandler(playrtcObserver);
		// ���� ���� 
		playrtc.setConfiguration();
				
		playrtcObserver.setHandlers(playrtc.getPlayRTC(), dataHandler);
		//String channelName = txtCrChannelName.getText().toString();
		//String userId = txtCrUserId.getText().toString();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		// ���� ����� �ػ󵵸� ����
		resWidth = size.x;
		resHeight = size.y;
		
		
		pSender = new PacketSender();
		pSender.setResolutionHeight(resHeight);
		pSender.setResolutionWidth(resWidth);
		
		viewFps = pSender.getFps(); // server �� viewFps �� ���缭 �����Ѵ�.
		startInputServer();
		try {
			playrtc.createChannel("server");
		} catch (RequiredConfigMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*//**
	 * PlayRTCHandler ��ü�� �����Ѵ�.<br>
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
			// ���� SDK�� Android SDK 11 �̻� �����Ѵ�.
			e.printStackTrace();
		} 
		catch (RequiredParameterMissingException e) 
		{
			// SERVICE_URL�� PlayRTCObserver������ü�� �����ڿ� �����ؾ� �Ѵ�.
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
					//client = server.accept(); //?�군�?client)�??�속?�야 ?? 
					
					//inputEventHandler = new HyeongInputHandler(HyeongService.this, client);
					inputEventHandler = new HyeongInputHandler(SSMService.this);
					
					inputEventHandler.start(); //?�게end?�야 ?�래로그�??�듯
					
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

	// �ڽ��� ȭ���� ���� ���� 
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
				imgJsonObj.put("imgByte", bArr); // ȭ���� Object�� ��´�.
					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			pSender.putDataInQueue(imgJsonObj); // ť�� �����͸� ����
				
			return null;
		}
	}
}
*/