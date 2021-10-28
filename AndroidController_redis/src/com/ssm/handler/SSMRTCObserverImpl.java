package com.ssm.handler;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.ssm.util.AppUtil;
import com.ssm.androidcontroller.SlideLogView;
import com.ssm.client.ClientActivity;
import com.ssm.server.ServerActivity;
import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.PlayRTC.PlayRTCCode;
import com.sktelecom.playrtc.PlayRTC.PlayRTCStatus;
import com.sktelecom.playrtc.observer.PlayRTCObserver;
import com.sktelecom.playrtc.stream.PlayRTCData;
import com.sktelecom.playrtc.stream.PlayRTCMedia;
/**
 * PlayRTC 객체의 이벤트를 전달 받기 위한 PlayRTCObserver의 구현 개체 클래스  <br>
 * PlayRTC 객체 생성 시 PlayRTCObserver 구현 객체를 전달해야한다.<br>
 * <br>
 * <b>PlayRTCObserver Interface</b><br>
 * - void onConnectChannel(PlayRTC obj, String channelId)<br>
 * - void onRing(PlayRTC obj, String peerId)<br>
 * - void onReject(PlayRTC obj, String peerId)<br>
 * - void onAccept(PlayRTC obj, String peerId)<br>
 * - void onCommand(PlayRTC obj, String peerId, String data)<br>
 * - void onAddLocalStream(PlayRTC obj, PlayRTCMedia media)<br>
 * - void onAddRemoteStream(PlayRTC obj, String peerId, PlayRTCMedia media)<br>
 * - void onAddDataStream(PlayRTC obj, String peerId, PlayRTCData data)<br>
 * - void onDisconnect(PlayRTC obj, )<br>
 * - void onOtherDisconnect(PlayRTC obj, String peerId)<br>
 * - void onDispose()<br>
 * - void onStateChange(PlayRTC obj, String peerId, PlayRTCStatus status, String desc)<br>
 * - void onError(PlayRTC obj, String peerUid, PlayRTCStatus status, PlayRTCCode code, String desc)<br>
 */
public class SSMRTCObserverImpl extends PlayRTCObserver{
	
	private static final String LOG_TAG = "PlayRTCObserver";
			
	private Activity activity = null;
	//private ChannelPopupView channelPopup = null;
	private SlideLogView logView = null;
	//private VideoGroupView videoGroup = null;
	private DataChannelHandler dataHandler = null;
	
	private String otherPeerId = "";
	
	/**
	 * PlayRTC 인스턴스 전역변수 
	 */
	private PlayRTC playRTC = null;
		
	/**
	 * 생성자 
	 * @param activity MainActivity
	 * @param videoGroup VideoGroupView, 영상 출력 뷰(PlayRTCVideoView)의 부모 뷰 
	 * @param channelPopup ChannelPopupView, 채널 생성 및 채널 목록 조회 및 채널 선택 화면 UI Popup
	 * @param logView SlideLogView, 로그 출력을 위한 View
	 * @see com.playrtc.sample.view.VideoGroupView
	 * @see com.playrtc.sample.view.ChannelPopupView
	 * @see com.playrtc.sample.view.SlideLogView
	 */
//	public SSMRTCObserverImpl(Activity activity, VideoGroupView videoGroup, ChannelPopupView channelPopup,  SlideLogView logView) {
//		this.activity = activity;
//		this.videoGroup = videoGroup;
//		this.channelPopup = channelPopup;
//		this.logView = logView;
//	}
	public SSMRTCObserverImpl(Activity activity,SlideLogView logView)
	{
		this.activity = activity;
		this.logView = logView;
	}
	
	/**
	 * PlayRTC와 DataChannelHandler를 전달 받는다. 
	 * @param playRTC PlayRTC
	 * @param dataHandler DataChannelHandler
	 * @see com.ssm.handler.playrtc.sample.handler.DataChannelHandler
	 */
	public void setHandlers(PlayRTC playRTC,  DataChannelHandler dataHandler) {
		this.playRTC = playRTC;
		this.dataHandler = dataHandler;
	}
	
	public String getOtherPeerId() {
		return otherPeerId;
	}
	
	/**
	 * 채널을 새로 생성하면 채널 아이디를 전달한다.
	 * @param obj PlayRTC
	 * @param channelId String, 새로 생성한 채널 아이디
	 * @param reson String, 채널 입장 구분<br>
	 * <pre>
	 * - createChannel을 호출하여 입장한 경우 "create"
	 * - connectChannel을 호출하여 입장한 경우 "connect"
	 * </pre>
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#createChannel(String)
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#connectChannel(String, String)
	 */
	@Override
	public void onConnectChannel(final PlayRTC obj, final String channelId, final String reson) {
		if(reson.equals("create")) {
			//channelPopup.setChannelId(channelId);
			if(activity instanceof ServerActivity)
			{
				AppUtil.showToast(activity, "서버 연결 완료");
				((ServerActivity)activity).setChannelId(channelId);
			}
		}
		else
		{
			if(activity instanceof ClientActivity)
			{
				AppUtil.showToast(activity, "클라이언트 연결 완료");
			}
		}
	}
	
	/**
	 * PlayRTCSettings Channel.setRing(true) 설정 시 나중에 채널에 입장한 사용자 측에서
	 * 연결 수락 의사를 물어옴 
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#setConfiguration()
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#createChannel(String)
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#connectChannel(String, String)
	 */
	//ring 은 안될듯?
	@Override
	public void onRing(final PlayRTC obj, final String peerId, final String peerUid) {
		
		otherPeerId = peerId;
		//logView.appendLog(">>["+peerId+"] onRing....");
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
    	alert.setTitle("SSM RTC");
		alert.setMessage(peerUid + "이 연결을 요청했습니다.");
		
		alert.setPositiveButton("연결", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Log.d(LOG_TAG, "onRing ["+peerId+"] accept....");
			//	AppUtil.showToast(activity, "["+peerId+"] accept....");
				logView.appendLog(">>["+peerId+"] accept....");
				
				playRTC.accept(peerId);
			}
		});
		alert.setNegativeButton("거부", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();
            	Log.d(LOG_TAG, "onRing ["+peerId+"] reject....");
            	//AppUtil.showToast(activity, "["+peerId+"] reject....");
            	logView.appendLog(">>["+peerId+"] reject....");
            	playRTC.reject(peerId);
            }
        });
		alert.show();
		
	}

	/**
	 * 상대방으로 부터 연결 거부 의사를 수신 함.
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#setConfiguration()
	 */
	@Override
	public void onReject(final PlayRTC obj, final String peerId, final String peerUid) {
		//AppUtil.showToast(activity, "["+peerId+"] onReject....");
		logView.appendLog(">>["+peerId+"] onReject....");
	}

	/**
	 * 상대방으로 부터 연결 수락 의사를 수신 함.
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#setConfiguration()
	 */
	@Override
	public void onAccept(final PlayRTC obj, final String peerId, final String peerUid) {
	//	AppUtil.showToast(activity, "["+peerId+"] onAccept....");
		logView.appendLog(">>["+peerId+"] onAccept....");
	}

	/**
	 * 상대방으로부터 User Defined Command를 받은 처리는 각지 알아서 
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @param data String, 상대방이 전송한 데이터 문자열 
	 */
	@Override
	public void onUserCommand(final PlayRTC obj, final String peerId, final String peerUid, final String data) {
		otherPeerId = peerId;
		//AppUtil.showToast(activity, "["+peerId+"] onCommand....");
		logView.appendLog(">>["+peerId+"] onCommand["+data+"]");
		try {
			JSONObject userData = new JSONObject(data);
			if(userData.has("command") && userData.has("data")) {
				String command = userData.getString("command");
				String dataString = userData.getString("data");
				if(command.equals("alert")) {
					AlertDialog.Builder alert = new AlertDialog.Builder(activity);
			    	alert.setTitle("SSMRTC- User Data");
					alert.setMessage(dataString);
					
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					alert.show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 자신의 PlayRTCMedia 전달 <br>
	 * PlayRTCMedia객체를 전달 받으면 영상 출력을 위해 PlayRTCVideView의 렌더러 인터페이스를 전달해야 한다. 
	 * @param obj PlayRTC
	 * @param media PlayRTCMedia, 미디어 처리를 위한 인터페이스 개체 
	 */
	@Override
	public void onAddLocalStream(final PlayRTC obj, final PlayRTCMedia media) {
		
		Log.d(LOG_TAG, "onMedia onAddLocalStream==============");
		logView.appendLog(">> onLocalStream...");
		
		// PlayRTCVideView 개체를 조회한다.
		//PlayRTCVideoView locaView = videoGroup.getLocalVideoView();
		//영상 출력을 위해 PlayRTCVideView의 렌더러 인터페이스를 전달해야 한다. 
		//media.setVideoRenderer(locaView.getVideoRenderer());
		
		//ChannelPopupView를 화면에서 숨긴다.
		//channelPopup.hide();
		
		// 화면 출력 
		//locaView.show(200);
		
	}
	
	/**
	 * P2P가 수립되어 상대방의 PlayRTCMedia 전달 <br>
	 * PlayRTCMedia객체를 전달 받으면 영상 출력을 위해 PlayRTCVideView의 렌더러 인터페이스를 전달해야 한다. 
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @param media PlayRTCMedia, 미디어 처리를 위한 인터페이스 개체 
	 */
	@Override
	public void onAddRemoteStream(final PlayRTC obj, final String peerId, final String peerUid, final PlayRTCMedia media) {
		
		otherPeerId = peerId;
		Log.d(LOG_TAG, "onMedia onAddRemoteStream==============");
		logView.appendLog(">> onRemoteStream["+peerId+"]...");
		
		// PlayRTCVideView 개체를 조회한다.
		//PlayRTCVideoView remoteView = videoGroup.getRemoteVideoView();
		//영상 출력을 위해 PlayRTCVideView의 렌더러 인터페이스를 전달해야 한다. 
		//media.setVideoRenderer(remoteView.getVideoRenderer());
	}
	
	/**
	 * PlayRTCData(DataChannel) 전달
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @param data PlayRTCData, 데이터 통신 기능을 제공하는 개체의 인터페이스  
	 * @see com.ssm.handler.playrtc.sample.handler.DataChannelHandler
	 */
	@Override
	public void onAddDataStream(final PlayRTC obj, final String peerId, final String peerUid, final PlayRTCData data) {
		otherPeerId = peerId;
		logView.appendLog(">> onDataStream["+peerId+"]...");
		//DataChannelHandler에 전달한다. 
		dataHandler.setDataChannel(data);
	}

	/**
	 * 채널퇴장 시 호출 <br>
	 * @param obj PlayRTC
	 * @param reson String, 채널 퇴장 유형 정의
	 * <pre>
	 * - 자신이 disconnectChannel을 호출하여 퇴장 한 경우 "disconnect"
	 * - 자신 또는 상대방이  deleteChannel을 호출하여 퇴장한 경우 "delete"
	 * </pre>
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#disconnectChannel(String)
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#deleteChannel()
	 * @see com.playrtc.sample.MainActivity#setCloesActivity(boolean)
	 * @see com.playrtc.sample.MainActivity#onBackPressed()
	 */
	@Override
	public void onDisconnectChannel(final PlayRTC obj, final String reson) {
		if(reson.equals("disconnect")){
			//AppUtil.showToast(activity, "원격이 종료되었습니다.");
			logView.appendLog(">>PlayRTC 채널에서 퇴장하였습니다....");
		}
		else {
			//AppUtil.showToast(activity, "원격이 종료되었습니다.");
			logView.appendLog(">>PlayRTC 채널이 종료되었습니다....");
		}
	
		// MainActivity의 종료 처리를 위해 isCloesActivity를 true로 설정한다.
		if(activity instanceof ClientActivity)
		{
			((ClientActivity)activity).setCloesActivity(true);
		}
		activity.onBackPressed();
	}
	

	/**
	 * 상대방의 채널 퇴장 시 호출 <br>
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#disconnectChannel(String)
	 * @see com.playrtc.sample.MainActivity#setCloesActivity(boolean)
	 * @see com.playrtc.sample.MainActivity#onBackPressed()
	 */
	@Override
	public void onOtherDisconnectChannel(final PlayRTC obj, final String peerId, final String peerUid) {
		//AppUtil.showToast(activity, "["+peerId+"]가 원격을 종료하였습니다.");
		logView.appendLog("["+peerId+"]가 채널에서 퇴장하였습니다....");
		// 이 이벤트를 이용한 채널 종료는 구현하지 않음 
	}

	/**
	 * PlayRTC의 상태 변경 이벤트 처리
	 * @param obj PlayRTC
	 * @param peerId String, 채널에서 부여받은 사용자의 Peer 아이디 
	 * @param peerUid String, 사용자의 application에서 사용하는 아이디로 채널 입장 시 전달한 값, 없으면 ""
	 * @param status PlayRTCStatus, 상태 정의 enum
	 * @param desc String, Description 
	 */
	@Override
	public void onStateChange(final PlayRTC obj, String peerId, final String peerUid, PlayRTCStatus status, String desc) {
		otherPeerId = peerId;
	//	AppUtil.showToast(activity, peerId+"  Status["+ status+ "]...");
		logView.appendLog(">>"+peerId+"  onStatusChange["+ status+ "]...");
	}


	/**
	 * PlayRTC의 오류 발생 이벤트 처리
	 * @param obj PlayRTC
	 * @param status PlayRTCStatus, 상태 정의 enum
	 * @param code PlayRTCCode, 오류 정의 enum
	 * @param desc String, Description 
	 */
	@Override
	public void onError(final PlayRTC obj, PlayRTCStatus status, PlayRTCCode code, String desc) {
	//	AppUtil.showToast(activity, "Error["+ code + "] Status["+ status+ "] "+desc);
		logView.appendLog(">> onError["+ code + "] Status["+ status+ "] "+desc);
	}
}
