package com.ssm.handler;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Environment;

import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.PlayRTCFactory;
import com.sktelecom.playrtc.config.PlayRTCSettings;
import com.sktelecom.playrtc.connector.servicehelper.PlayRTCServiceHelperListener;
import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.observer.PlayRTCObserver;

/**
 * PlayRTC �������̽��� ������ Class.<br>
 * <b>Method</b>
 * <pre>
 * 1. public PlayRTC getPlayRTC()
 *    PlayRTC �������̽� ��ü�� ��ȯ�Ѵ�.
 * 2. public void setConfiguration()
 *    PlayRTC Communication ���񽺸� ���� PlayRTC config ���� 
 * 3. public void createChannel(String uid)
 *    P2P ������ �����ϱ� ���� PlayRTC ä�� ���񽺿� ä���� �����ϰ� ��������.
 * 4. public void connectChannel(String channelId, String uid)
 *    P2P ������ �����ϱ� ���� �����Ǿ� �ִ� ä�ο� �����Ѵ�. 
 * 5. public void disconnectChannel(String peerId)
 *    ������ ä�ο��� �����Ѵ�.
 * 6. public void deleteChannel()
 *    ������ ä���� �����Ų��.
 * 7. public void resume()
 *    Activity onResume
 * 8. public void pause()
 *    Activity onPause
 * 9. public String getChannelId()
 *    ������ �ִ� ä���� ���̵� ��ȯ�Ѵ�.
 * 10. public String getPeerId()
 *    ������ �ִ� ä�ο��� �ο� ���� ������� ���̵� ��ȯ�Ѵ�. 
 * 11. public void getChannelList(PlayRTCServiceHelperListener listener)
 *    ä�� ���񽺿� �����Ǿ� �ִ� ä���� ����� ��ȸ�Ͽ� ��ȯ�Ѵ�. 
 * </pre>
 */
public class SSMRTCHandler {
	
	private Activity activity = null;
	// PlayRTC �������̽� ��ü 
	private PlayRTC playRTC = null;
	
	/**
	 * ������
	 * @param activity MainActivity
	 * @param serviceUrl String, PlayRTC SDK �� ����ϱ� ���� Service Server Url
	 * @param oberver PlayRTCObserver, PlayRTC�� �̺�Ʈ�� ó���ϱ� ���� ������ ���� ��ü 
	 * @throws RequiredParameterMissingException  SERVICE_URL�� PlayRTCObserver������ü�� �����ڿ� �����ؾ� �Ѵ�.
	 * @throws UnsupportedPlatformVersionException Android SDK 11 �̻� �����Ѵ�.
	 */
	public SSMRTCHandler(Activity activity, String serviceUrl, PlayRTCObserver oberver) 
					throws UnsupportedPlatformVersionException, RequiredParameterMissingException {

		this.activity = activity;
		// PlayRTC ���� ��ü�� �����ϰ� PlayRTC�� ���� �޴´�.
		this.playRTC = PlayRTCFactory.newInstance(serviceUrl, oberver);
	}
	
	/**
	 * PlayRTC �������̽� ��ü�� ��ȯ�Ѵ�.
	 * @return PlayRTC
	 */
	public PlayRTC getPlayRTC() {
		return playRTC;
	}
	
	/**
	 * PlayRTC Communication ���񽺸� ���� PlayRTC config ���� 
	 */
	public void setConfiguration() {
		PlayRTCSettings settings = playRTC.getSettings();		
		
		/* Android Application Context�� �ʿ�� �Ѵ�. 
		 * �������� ������ RequiredConfigMissingException �߻� 
		 */
		settings.android.setContext(activity.getApplicationContext());
		
		/* ����� ī�޶� ����  "front", "back" ī�޶� ���� */
		settings.setCamera("front");
		 

		// ���� ��Ʈ�� ���� ���θ� ����, �⺻������ ���� ��Ʈ�� ������ �⺻ ���� ��   
		settings.setVideoEnable(false); /* ���� ���� ��� */
		
		// ���� ��Ʈ�� ���� ���θ� ����, �⺻������ ���� ��Ʈ�� ������ �⺻ ���� ��   
		settings.setAudioEnable(false);   /* ���� ���� ��� */
		
		// ������ ��Ʈ�� ����� ����� �� ����, false �� ������ ����� �Ұ�  
		settings.setDataEnable(true);    /* P2P ������ ��ȯ�� ���� DataChannel ��� ���� */
		
		/*  ä�ο� �����Ͽ� ������� ���� ���� ������ ����, true�� ���� ������ ������ ������ �־�� ���� ���� ����  */ 
		settings.channel.setRing(false); 
		
		/* SDK Console �α� ���� ���� */
		settings.log.console.setLevel(PlayRTCSettings.DEBUG);
		
		/* SDK ���� �α��� ���� �α� ���� ���, ���� �α��� ������� �ʴ´ٸ� Pass */ 
        File logPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + activity.getPackageName() + "/files/log");  
        
        
		/* ���� �α׸� ������� �α����� ���� ���� . [PATH]/yyyyMMdd.log , 10�ϰ� ���� */
		settings.log.file.setLogPath(logPath.getAbsolutePath());
		/* SDK ���� �α� ���� ���� */
		settings.log.file.setLevel(PlayRTCSettings.DEBUG);
		

        /* SDK ���� �α��� ���� �α� �ӽ� ĳ�� ���, ���� ���� �� �� ��ο� ���� ������ �Ѵ�.  */
        File cachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/" + activity.getPackageName() + "/files/cache");  
        
        
		/* ���� �α� ���� ���� �� �ӽ� �α� ���� DB ���� ����   */
		settings.log.setCachePath(cachePath.getAbsolutePath());
		
		/* ���� �α� ���� ���� �� ������ �õ� ���� �ð�, msec */
		settings.log.setRetryQueueDelays(5 * 1000);
		/* ���� �α� �� ���� ���н� �α� DB ���� �� ������ �õ� ���� �ð�, msec */
		settings.log.setRetryCacheDelays(20 * 1000);
	}
	
	/**
	 * P2P ������ �����ϱ� ���� PlayRTC ä�� ���񽺿� ä���� �����ϰ� ��������.<br>
	 * ä�ο� �����ϸ� ä�� ���񽺴� PlayRTC ���� ���� Configuration ������ ä�� ���̵� ��ȯ�ϰ�,<br>
	 * SDK�� ������ �����Ͽ� ���������� ���� ������ �Ѵ�.<br>
	 * �� �������� ȹ���� ä�� ���̵� PlayRTCObserver �������̽�(onConnectChannel : reson -> "create")�� ���� �����Ѵ�.<br>
	 * <br>
	 * 
	 * @param uid String, Application ����� ���̵�
	 * @throws RequiredConfigMissingException  Android Application Context�� �ʿ�� �Ѵ�. <br>
	 * PlayRTCSettings.android.setContext �޼ҵ�� Context�� �ݵ��� �����ؾ� �Ѵ�. 
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onConnectChannel(PlayRTC, String, String)
	 */
	public void createChannel(String uid) throws RequiredConfigMissingException{
		JSONObject parameters = new JSONObject();
		// ä�������� �����Ѵ�.
		JSONObject channel = new JSONObject();
		try {
			// ä�ο� ���� �̸��� �����Ѵ�.
			channel.put("channelName", uid+"���� ä�ι��Դϴ�.");
			parameters.put("channel", channel);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject peer = new JSONObject();
		try {
			// Application�� ����� ���̵� �����Ѵ�. 
			peer.put("uid", uid);
			parameters.put("peer", peer);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// ä���� �ű� �����ϰ� �����Ѵ�.
		// ä�� ���� �� PlayRTCObserver�� onConnectChannel�� ȣ��ȴ�.
		playRTC.createChannel(parameters);
	}
	
	/**
	 * P2P ������ �����ϱ� ���� �����Ǿ� �ִ� ä�ο� �����Ѵ�. 
	 * 
	 * @param channelId String, ������Ǿ� �ִ� ä���� ���̵� 
	 * @param uid String, NooNooN ����� ���̵� 
	 * @throws RequiredConfigMissingException  Android Application Context�� �ʿ�� �Ѵ�. <br>
	 * PlayRTCSettings.android.setContext �޼ҵ�� Context�� �ݵ��� �����ؾ� �Ѵ�. 
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onConnectChannel(PlayRTC, String, String)
	 */
	public void connectChannel(String channelId, String uid) throws RequiredConfigMissingException {
		JSONObject parameters = new JSONObject();

		JSONObject peer = new JSONObject();
		try {
			// Application�� ����� ���̵� �����Ѵ�. 
			peer.put("uid", uid);
			parameters.put("peer", peer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// ä�ο� �����Ѵ�.
		// ä�� ���� �� PlayRTCObserver�� onConnectChannel�� ȣ��ȴ�.
		playRTC.connectChannel(channelId, parameters);
	}
	
	/**
	 * ������ ä�ο��� �����Ѵ�.<br>
	 * PlayRTCObserver �������̽��� onDisconnectChannel : reson-> "disconnect"�� ȣ��ǰ�,<br> 
	 * ������ onOtherDisconnectChannel�� ȣ��ȴ�.
	 * @param peerId String, ä�ο��� �ο� ���� ä�� ������ ����� �ĺ����̵� 
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onDisconnectChannel(PlayRTC, String)
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onOtherDisconnectChannel(PlayRTC, String, String)
	 */
	public void disconnectChannel(String peerId) {
		if(playRTC != null){
			playRTC.disconnectChannel(peerId);
		}

	}
	/**
	 * ������ ä���� �����Ų��.<br>
	 * ��� �ÿ��ڿ��� PlayRTCObserver �������̽��� onDeleteChannel : reson-> "close"�� ȣ��ȴ�.
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onDisconnectChannel(PlayRTC, String)
	 */
	public void deleteChannel() {
		if(playRTC != null){
			playRTC.deleteChannel();
		}
	}
	
	
	/* Activity onResume */
	public void resume()
	{
		if(playRTC != null){
			playRTC.resume();
		}
	}
	
	
	/* Activity onPause */
	public void pause()
	{
		if(playRTC != null){
			playRTC.resume();
		}
		
	}
	
	/**
	 * ������ �ִ� ä���� ���̵� ��ȯ�Ѵ�.
	 * @return String
	 */
	public String getChannelId()
	{
		if(playRTC != null){
			return playRTC.getChannelId();
		}
		return null;
	}
	
	/**
	 * ������ �ִ� ä�ο��� �ο� ���� ������� Peer ���̵� ��ȯ�Ѵ�. 
	 * @return String
	 */
	public String getPeerId()
	{
		if(playRTC != null){
			return playRTC.getPeerId();
		}
		return null;
	}
	
	/**
	 * ���濡�� User-Defined Command��  ������ �������� �״�� �����Ѵ�.<br>
	 * @param peerId String, PlayRTC �÷��� ä�� ������ User ���̵�
	 * @param data String, ������ ���ڿ� ������ ������ ���� application���� ���� �� ���� 
	 * @see com.sktelecom.playrtc.connector.PlayRTCConnector#command
	 */ 
	public void userCommand(final String peerId, final String data) {
		if(playRTC != null){
			playRTC.userCommand(peerId, data);
		}
	}
	/**
	 * ä�� ���񽺿� �����Ǿ� �ִ� ä���� ����� ��ȸ�Ͽ� ��ȯ�Ѵ�. 
	 * @param listener PlayRTCServiceHelperListener
	 */
	public void getChannelList(PlayRTCServiceHelperListener listener) {
		playRTC.getChannelList(listener);
	}
}
