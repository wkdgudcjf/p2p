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
 * PlayRTC ��ü�� �̺�Ʈ�� ���� �ޱ� ���� PlayRTCObserver�� ���� ��ü Ŭ����  <br>
 * PlayRTC ��ü ���� �� PlayRTCObserver ���� ��ü�� �����ؾ��Ѵ�.<br>
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
	 * PlayRTC �ν��Ͻ� �������� 
	 */
	private PlayRTC playRTC = null;
		
	/**
	 * ������ 
	 * @param activity MainActivity
	 * @param videoGroup VideoGroupView, ���� ��� ��(PlayRTCVideoView)�� �θ� �� 
	 * @param channelPopup ChannelPopupView, ä�� ���� �� ä�� ��� ��ȸ �� ä�� ���� ȭ�� UI Popup
	 * @param logView SlideLogView, �α� ����� ���� View
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
	 * PlayRTC�� DataChannelHandler�� ���� �޴´�. 
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
	 * ä���� ���� �����ϸ� ä�� ���̵� �����Ѵ�.
	 * @param obj PlayRTC
	 * @param channelId String, ���� ������ ä�� ���̵�
	 * @param reson String, ä�� ���� ����<br>
	 * <pre>
	 * - createChannel�� ȣ���Ͽ� ������ ��� "create"
	 * - connectChannel�� ȣ���Ͽ� ������ ��� "connect"
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
				AppUtil.showToast(activity, "���� ���� �Ϸ�");
				((ServerActivity)activity).setChannelId(channelId);
			}
		}
		else
		{
			if(activity instanceof ClientActivity)
			{
				AppUtil.showToast(activity, "Ŭ���̾�Ʈ ���� �Ϸ�");
			}
		}
	}
	
	/**
	 * PlayRTCSettings Channel.setRing(true) ���� �� ���߿� ä�ο� ������ ����� ������
	 * ���� ���� �ǻ縦 ����� 
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#setConfiguration()
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#createChannel(String)
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#connectChannel(String, String)
	 */
	//ring �� �ȵɵ�?
	@Override
	public void onRing(final PlayRTC obj, final String peerId, final String peerUid) {
		
		otherPeerId = peerId;
		//logView.appendLog(">>["+peerId+"] onRing....");
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
    	alert.setTitle("SSM RTC");
		alert.setMessage(peerUid + "�� ������ ��û�߽��ϴ�.");
		
		alert.setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Log.d(LOG_TAG, "onRing ["+peerId+"] accept....");
			//	AppUtil.showToast(activity, "["+peerId+"] accept....");
				logView.appendLog(">>["+peerId+"] accept....");
				
				playRTC.accept(peerId);
			}
		});
		alert.setNegativeButton("�ź�", new DialogInterface.OnClickListener() {
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
	 * �������� ���� ���� �ź� �ǻ縦 ���� ��.
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#setConfiguration()
	 */
	@Override
	public void onReject(final PlayRTC obj, final String peerId, final String peerUid) {
		//AppUtil.showToast(activity, "["+peerId+"] onReject....");
		logView.appendLog(">>["+peerId+"] onReject....");
	}

	/**
	 * �������� ���� ���� ���� �ǻ縦 ���� ��.
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#setConfiguration()
	 */
	@Override
	public void onAccept(final PlayRTC obj, final String peerId, final String peerUid) {
	//	AppUtil.showToast(activity, "["+peerId+"] onAccept....");
		logView.appendLog(">>["+peerId+"] onAccept....");
	}

	/**
	 * �������κ��� User Defined Command�� ���� ó���� ���� �˾Ƽ� 
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @param data String, ������ ������ ������ ���ڿ� 
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
					
					alert.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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
	 * �ڽ��� PlayRTCMedia ���� <br>
	 * PlayRTCMedia��ü�� ���� ������ ���� ����� ���� PlayRTCVideView�� ������ �������̽��� �����ؾ� �Ѵ�. 
	 * @param obj PlayRTC
	 * @param media PlayRTCMedia, �̵�� ó���� ���� �������̽� ��ü 
	 */
	@Override
	public void onAddLocalStream(final PlayRTC obj, final PlayRTCMedia media) {
		
		Log.d(LOG_TAG, "onMedia onAddLocalStream==============");
		logView.appendLog(">> onLocalStream...");
		
		// PlayRTCVideView ��ü�� ��ȸ�Ѵ�.
		//PlayRTCVideoView locaView = videoGroup.getLocalVideoView();
		//���� ����� ���� PlayRTCVideView�� ������ �������̽��� �����ؾ� �Ѵ�. 
		//media.setVideoRenderer(locaView.getVideoRenderer());
		
		//ChannelPopupView�� ȭ�鿡�� �����.
		//channelPopup.hide();
		
		// ȭ�� ��� 
		//locaView.show(200);
		
	}
	
	/**
	 * P2P�� �����Ǿ� ������ PlayRTCMedia ���� <br>
	 * PlayRTCMedia��ü�� ���� ������ ���� ����� ���� PlayRTCVideView�� ������ �������̽��� �����ؾ� �Ѵ�. 
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @param media PlayRTCMedia, �̵�� ó���� ���� �������̽� ��ü 
	 */
	@Override
	public void onAddRemoteStream(final PlayRTC obj, final String peerId, final String peerUid, final PlayRTCMedia media) {
		
		otherPeerId = peerId;
		Log.d(LOG_TAG, "onMedia onAddRemoteStream==============");
		logView.appendLog(">> onRemoteStream["+peerId+"]...");
		
		// PlayRTCVideView ��ü�� ��ȸ�Ѵ�.
		//PlayRTCVideoView remoteView = videoGroup.getRemoteVideoView();
		//���� ����� ���� PlayRTCVideView�� ������ �������̽��� �����ؾ� �Ѵ�. 
		//media.setVideoRenderer(remoteView.getVideoRenderer());
	}
	
	/**
	 * PlayRTCData(DataChannel) ����
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @param data PlayRTCData, ������ ��� ����� �����ϴ� ��ü�� �������̽�  
	 * @see com.ssm.handler.playrtc.sample.handler.DataChannelHandler
	 */
	@Override
	public void onAddDataStream(final PlayRTC obj, final String peerId, final String peerUid, final PlayRTCData data) {
		otherPeerId = peerId;
		logView.appendLog(">> onDataStream["+peerId+"]...");
		//DataChannelHandler�� �����Ѵ�. 
		dataHandler.setDataChannel(data);
	}

	/**
	 * ä������ �� ȣ�� <br>
	 * @param obj PlayRTC
	 * @param reson String, ä�� ���� ���� ����
	 * <pre>
	 * - �ڽ��� disconnectChannel�� ȣ���Ͽ� ���� �� ��� "disconnect"
	 * - �ڽ� �Ǵ� ������  deleteChannel�� ȣ���Ͽ� ������ ��� "delete"
	 * </pre>
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#disconnectChannel(String)
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#deleteChannel()
	 * @see com.playrtc.sample.MainActivity#setCloesActivity(boolean)
	 * @see com.playrtc.sample.MainActivity#onBackPressed()
	 */
	@Override
	public void onDisconnectChannel(final PlayRTC obj, final String reson) {
		if(reson.equals("disconnect")){
			//AppUtil.showToast(activity, "������ ����Ǿ����ϴ�.");
			logView.appendLog(">>PlayRTC ä�ο��� �����Ͽ����ϴ�....");
		}
		else {
			//AppUtil.showToast(activity, "������ ����Ǿ����ϴ�.");
			logView.appendLog(">>PlayRTC ä���� ����Ǿ����ϴ�....");
		}
	
		// MainActivity�� ���� ó���� ���� isCloesActivity�� true�� �����Ѵ�.
		if(activity instanceof ClientActivity)
		{
			((ClientActivity)activity).setCloesActivity(true);
		}
		activity.onBackPressed();
	}
	

	/**
	 * ������ ä�� ���� �� ȣ�� <br>
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @see com.ssm.handler.SSMRTCHandler.sample.handler.PlayRTCHandler#disconnectChannel(String)
	 * @see com.playrtc.sample.MainActivity#setCloesActivity(boolean)
	 * @see com.playrtc.sample.MainActivity#onBackPressed()
	 */
	@Override
	public void onOtherDisconnectChannel(final PlayRTC obj, final String peerId, final String peerUid) {
		//AppUtil.showToast(activity, "["+peerId+"]�� ������ �����Ͽ����ϴ�.");
		logView.appendLog("["+peerId+"]�� ä�ο��� �����Ͽ����ϴ�....");
		// �� �̺�Ʈ�� �̿��� ä�� ����� �������� ���� 
	}

	/**
	 * PlayRTC�� ���� ���� �̺�Ʈ ó��
	 * @param obj PlayRTC
	 * @param peerId String, ä�ο��� �ο����� ������� Peer ���̵� 
	 * @param peerUid String, ������� application���� ����ϴ� ���̵�� ä�� ���� �� ������ ��, ������ ""
	 * @param status PlayRTCStatus, ���� ���� enum
	 * @param desc String, Description 
	 */
	@Override
	public void onStateChange(final PlayRTC obj, String peerId, final String peerUid, PlayRTCStatus status, String desc) {
		otherPeerId = peerId;
	//	AppUtil.showToast(activity, peerId+"  Status["+ status+ "]...");
		logView.appendLog(">>"+peerId+"  onStatusChange["+ status+ "]...");
	}


	/**
	 * PlayRTC�� ���� �߻� �̺�Ʈ ó��
	 * @param obj PlayRTC
	 * @param status PlayRTCStatus, ���� ���� enum
	 * @param code PlayRTCCode, ���� ���� enum
	 * @param desc String, Description 
	 */
	@Override
	public void onError(final PlayRTC obj, PlayRTCStatus status, PlayRTCCode code, String desc) {
	//	AppUtil.showToast(activity, "Error["+ code + "] Status["+ status+ "] "+desc);
		logView.appendLog(">> onError["+ code + "] Status["+ status+ "] "+desc);
	}
}
