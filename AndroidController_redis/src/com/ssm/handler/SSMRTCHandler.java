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
 * PlayRTC 인터페이스를 구현한 Class.<br>
 * <b>Method</b>
 * <pre>
 * 1. public PlayRTC getPlayRTC()
 *    PlayRTC 인터페이스 개체를 반환한다.
 * 2. public void setConfiguration()
 *    PlayRTC Communication 서비스를 위한 PlayRTC config 설정 
 * 3. public void createChannel(String uid)
 *    P2P 연결을 수립하기 위해 PlayRTC 채널 서비스에 채널을 생성하고 입장힌다.
 * 4. public void connectChannel(String channelId, String uid)
 *    P2P 연결을 수립하기 위해 생성되어 있는 채널에 입장한다. 
 * 5. public void disconnectChannel(String peerId)
 *    입장한 채널에서 퇴장한다.
 * 6. public void deleteChannel()
 *    입장한 채널을 종료시킨다.
 * 7. public void resume()
 *    Activity onResume
 * 8. public void pause()
 *    Activity onPause
 * 9. public String getChannelId()
 *    입장해 있는 채널의 아이디를 반환한다.
 * 10. public String getPeerId()
 *    입장해 있는 채널에서 부여 받은 사용자의 아이디를 반환한다. 
 * 11. public void getChannelList(PlayRTCServiceHelperListener listener)
 *    채널 서비스에 생성되어 있는 채널의 목록을 조회하여 반환한다. 
 * </pre>
 */
public class SSMRTCHandler {
	
	private Activity activity = null;
	// PlayRTC 인터페이스 개체 
	private PlayRTC playRTC = null;
	
	/**
	 * 생성자
	 * @param activity MainActivity
	 * @param serviceUrl String, PlayRTC SDK 가 사용하기 위한 Service Server Url
	 * @param oberver PlayRTCObserver, PlayRTC의 이벤트를 처리하기 위한 리스너 구현 객체 
	 * @throws RequiredParameterMissingException  SERVICE_URL과 PlayRTCObserver구현개체를 생성자에 전달해야 한다.
	 * @throws UnsupportedPlatformVersionException Android SDK 11 이상만 지원한다.
	 */
	public SSMRTCHandler(Activity activity, String serviceUrl, PlayRTCObserver oberver) 
					throws UnsupportedPlatformVersionException, RequiredParameterMissingException {

		this.activity = activity;
		// PlayRTC 구현 개체를 생성하고 PlayRTC를 전달 받는다.
		this.playRTC = PlayRTCFactory.newInstance(serviceUrl, oberver);
	}
	
	/**
	 * PlayRTC 인터페이스 개체를 반환한다.
	 * @return PlayRTC
	 */
	public PlayRTC getPlayRTC() {
		return playRTC;
	}
	
	/**
	 * PlayRTC Communication 서비스를 위한 PlayRTC config 설정 
	 */
	public void setConfiguration() {
		PlayRTCSettings settings = playRTC.getSettings();		
		
		/* Android Application Context를 필요로 한다. 
		 * 지정하지 않으면 RequiredConfigMissingException 발생 
		 */
		settings.android.setContext(activity.getApplicationContext());
		
		/* 사용할 카메라를 지정  "front", "back" 카메라 지정 */
		settings.setCamera("front");
		 

		// 영상 스트림 전송 여부를 지정, 기본적으로 영상 스트림 수신은 기본 적용 됨   
		settings.setVideoEnable(false); /* 영상 전송 사용 */
		
		// 음성 스트림 전송 여부를 지정, 기본적으로 음성 스트림 수신은 기본 적용 됨   
		settings.setAudioEnable(false);   /* 음성 전송 사용 */
		
		// 데이터 스트림 통신을 사용할 지 여부, false 시 데이터 통신은 불가  
		settings.setDataEnable(true);    /* P2P 데이터 교환을 위한 DataChannel 사용 여부 */
		
		/*  채널에 입장하여 상대방과의 연결 승인 과정을 지정, true면 먼저 입장한 상대방의 수락이 있어야 연결 수립 진행  */ 
		settings.channel.setRing(false); 
		
		/* SDK Console 로그 레벨 지정 */
		settings.log.console.setLevel(PlayRTCSettings.DEBUG);
		
		/* SDK 파일 로깅을 위한 로그 파일 경로, 파일 로깅을 사용하지 않는다면 Pass */ 
        File logPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + activity.getPackageName() + "/files/log");  
        
        
		/* 파일 로그를 남기려면 로그파일 폴더 지정 . [PATH]/yyyyMMdd.log , 10일간 보존 */
		settings.log.file.setLogPath(logPath.getAbsolutePath());
		/* SDK 파일 로그 레벨 지정 */
		settings.log.file.setLevel(PlayRTCSettings.DEBUG);
		

        /* SDK 서버 로깅을 위한 로그 임시 캐시 경로, 전송 실패 시 이 경로에 파일 저장을 한다.  */
        File cachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/" + activity.getPackageName() + "/files/cache");  
        
        
		/* 서버 로그 전송 실패 시 임시 로그 저장 DB 파일 폴더   */
		settings.log.setCachePath(cachePath.getAbsolutePath());
		
		/* 서버 로그 전송 실패 시 재전송 시도 지연 시간, msec */
		settings.log.setRetryQueueDelays(5 * 1000);
		/* 서버 로그 재 전송 실패시 로그 DB 저장 후 재전송 시도 지연 시간, msec */
		settings.log.setRetryCacheDelays(20 * 1000);
	}
	
	/**
	 * P2P 연결을 수립하기 위해 PlayRTC 채널 서비스에 채널을 생성하고 입장힌다.<br>
	 * 채널에 입장하면 채널 서비스는 PlayRTC 서비스 관련 Configuration 정보와 채널 아이디를 반환하고,<br>
	 * SDK가 정보를 수신하여 내부적으로 서비스 설정을 한다.<br>
	 * 이 과정에서 획득한 채널 아이디를 PlayRTCObserver 인터페이스(onConnectChannel : reson -> "create")를 통해 전달한다.<br>
	 * <br>
	 * 
	 * @param uid String, Application 사용자 아이디
	 * @throws RequiredConfigMissingException  Android Application Context를 필요로 한다. <br>
	 * PlayRTCSettings.android.setContext 메소드로 Context를 반듯이 전달해야 한다. 
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onConnectChannel(PlayRTC, String, String)
	 */
	public void createChannel(String uid) throws RequiredConfigMissingException{
		JSONObject parameters = new JSONObject();
		// 채널정보를 정의한다.
		JSONObject channel = new JSONObject();
		try {
			// 채널에 대한 이름을 지정한다.
			channel.put("channelName", uid+"님의 채널방입니다.");
			parameters.put("channel", channel);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject peer = new JSONObject();
		try {
			// Application의 사용자 아이디를 전달한다. 
			peer.put("uid", uid);
			parameters.put("peer", peer);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 채널을 신규 생성하고 입장한다.
		// 채널 입장 시 PlayRTCObserver의 onConnectChannel가 호출된다.
		playRTC.createChannel(parameters);
	}
	
	/**
	 * P2P 연결을 수립하기 위해 생성되어 있는 채널에 입장한다. 
	 * 
	 * @param channelId String, 상생성되어 있는 채널의 아이디 
	 * @param uid String, NooNooN 사용자 아이디 
	 * @throws RequiredConfigMissingException  Android Application Context를 필요로 한다. <br>
	 * PlayRTCSettings.android.setContext 메소드로 Context를 반듯이 전달해야 한다. 
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onConnectChannel(PlayRTC, String, String)
	 */
	public void connectChannel(String channelId, String uid) throws RequiredConfigMissingException {
		JSONObject parameters = new JSONObject();

		JSONObject peer = new JSONObject();
		try {
			// Application의 사용자 아이디를 전달한다. 
			peer.put("uid", uid);
			parameters.put("peer", peer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 채널에 입장한다.
		// 채널 입장 시 PlayRTCObserver의 onConnectChannel가 호출된다.
		playRTC.connectChannel(channelId, parameters);
	}
	
	/**
	 * 입장한 채널에서 퇴장한다.<br>
	 * PlayRTCObserver 인터페이스의 onDisconnectChannel : reson-> "disconnect"가 호출되고,<br> 
	 * 상대방은 onOtherDisconnectChannel가 호출된다.
	 * @param peerId String, 채널에서 부여 받은 채널 내부의 사용자 식별아이디 
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onDisconnectChannel(PlayRTC, String)
	 * @see com.ssm.handler.SSMRTCObserverImpl.sample.handler.PlayRTCObserverImpl#onOtherDisconnectChannel(PlayRTC, String, String)
	 */
	public void disconnectChannel(String peerId) {
		if(playRTC != null){
			playRTC.disconnectChannel(peerId);
		}

	}
	/**
	 * 입장한 채널을 종료시킨다.<br>
	 * 모든 시용자에게 PlayRTCObserver 인터페이스의 onDeleteChannel : reson-> "close"가 호출된다.
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
	 * 입장해 있는 채널의 아이디를 반환한다.
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
	 * 입장해 있는 채널에서 부여 받은 사용자의 Peer 아이디를 반환한다. 
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
	 * 상대방에게 User-Defined Command를  데이터 가공없이 그대로 전달한다.<br>
	 * @param peerId String, PlayRTC 플랫폼 채널 서비스의 User 아이디
	 * @param data String, 데이터 문자열 데이터 형식은 개별 application에서 정의 한 형태 
	 * @see com.sktelecom.playrtc.connector.PlayRTCConnector#command
	 */ 
	public void userCommand(final String peerId, final String data) {
		if(playRTC != null){
			playRTC.userCommand(peerId, data);
		}
	}
	/**
	 * 채널 서비스에 생성되어 있는 채널의 목록을 조회하여 반환한다. 
	 * @param listener PlayRTCServiceHelperListener
	 */
	public void getChannelList(PlayRTCServiceHelperListener listener) {
		playRTC.getChannelList(listener);
	}
}
