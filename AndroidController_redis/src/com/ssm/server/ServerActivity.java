package com.ssm.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.ssm.androidcontroller.R;
import com.ssm.androidcontroller.SlideLogView;
import com.ssm.datatransmission.PacketReceiver;
import com.ssm.datatransmission.PacketSender;
import com.ssm.handler.DataChannelHandler;
import com.ssm.handler.SSMRTCHandler;
import com.ssm.handler.SSMRTCObserverImpl;
import com.ssm.natives.NativeCall;

/**
 * 자신의 화면을 전송하며, 상대가 보내는 제어에 따라서 동작을 수행하는 액티비티
 * 
 * @author armin
 */

public class ServerActivity extends Activity {

	private static final String LOG_TAG = "Server_activity";
	TextView tvSrvTitle, tvSrvCode;
	ImageButton btnSendCode, btnShareCode;
	private int i = 0;
	ByteBuffer frameBuffer;
	// private SSMService mService; // 연결 타입 서비스
	// private boolean mBound = false; // 서비스 연결 여부
	// 서비스로 빼서 바인더로 하자. 그래야 편하다. 서비스는 죽지않은 서비스로 하자 그리고
	Button btndiscon;
	String code;
	private static final String SERVICE_URL = "http://211.189.20.7:5400";
	private boolean isCloesActivity = false;
	private SSMRTCHandler playrtc = null;
	private DataChannelHandler dataHandler = null;
	private SSMRTCObserverImpl playrtcObserver = null;

	private SlideLogView logView = null;

	PacketSender pSender;
	PacketReceiver pReceiver;

	int resWidth, resHeight;
	int viewFps = 1; // view 갱신 주기 ( fps )
	HyeongInputHandler hih;
	int arr[] = { R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4,
			R.drawable.c5, R.drawable.c6, R.drawable.c7, R.drawable.c8,
			R.drawable.c9, R.drawable.c10 };
	private Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		ActionBar myActionBar = getActionBar();
		myActionBar.setTitle("Server");
		// hih = new HyeongInputHandler(this);
		// set fonts
		Typeface tFace = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");
		Typeface tFaceBold = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Bold.ttf");

		tvSrvTitle = (TextView) findViewById(R.id.tv_server_title);
		tvSrvCode = (TextView) findViewById(R.id.tv_server_code);
		btnSendCode = (ImageButton) findViewById(R.id.btn_server_send_code_mms);
		btnShareCode = (ImageButton) findViewById(R.id.btn_server_send_code_share);
		btndiscon = (Button) findViewById(R.id.tv_server_discon);
		btndiscon.setVisibility(View.INVISIBLE);
		tvSrvTitle.setTypeface(tFace);
		tvSrvCode.setTypeface(tFaceBold);
		logView = (SlideLogView) findViewById(R.id.logview);

		/* 로그뷰 토글 버튼 이벤트 처리 */
		((Button) this.findViewById(R.id.btn_log))
				.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						if (logView.isShown() == false) {
							logView.show();
							((Button) v).setText("로그닫기");
						} else {
							logView.hide();
							((Button) v).setText("로그보기");
						}
					}
				});
		dataHandler = new DataChannelHandler(this, logView);
		// PlatRTCObserver 구현 개체
		playrtcObserver = createSSMRTCObserver();
		// PlayRTC 구현 개체
		playrtc = createSSMRTCHandler(playrtcObserver);
		// 서비스 설정
		playrtc.setConfiguration();

		playrtcObserver.setHandlers(playrtc.getPlayRTC(), dataHandler);

		// String channelName = txtCrChannelName.getText().toString();
		// String userId = txtCrUserId.getText().toString();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		// 현재 기기의 해상도를 세팅
		resWidth = size.x;
		resHeight = size.y;

		pSender = new PacketSender(dataHandler);
		pSender.setResolutionHeight(resHeight);
		pSender.setResolutionWidth(resWidth);

		viewFps = pSender.getFps(); // server 는 viewFps 에 맞춰서 전송한다.
		// startInputServer();
		try {
			playrtc.createChannel("server");
		} catch (RequiredConfigMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btnSendCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mmsIntent = new Intent(Intent.ACTION_VIEW);
				String smsBody = "Remote code : [ "
						+ tvSrvCode.getText().toString() + " ]";
				mmsIntent.putExtra("sms_body", smsBody); // 보낼 문자
				mmsIntent.setType("vnd.android-dir/mms-sms");
				startActivity(mmsIntent);
			}
		});

		btnShareCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"This's my remote code");
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						tvSrvCode.getText().toString()
								+ "\nPlease connect here and help me :)");

				startActivity(shareIntent);
			}
		});
		btndiscon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String userPid = playrtc.getPeerId();
				if (TextUtils.isEmpty(userPid) == false) {
					isCloesActivity = false;
					playrtc.deleteChannel();
				}
			}
		});
		// Intent intent = new Intent(this, SSMService.class);
		// bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private class callTaskThread implements Runnable {
		int sleepTime = 200; // default delay time(ms)

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				new makeScreenOnDevice().execute(1);
				i++;
				if (i == 10) {
					i = 0;
				}
				try {
					switch (viewFps) {
					case 1:
						sleepTime = 1000;
						break;
					case 3:
						sleepTime = 330;
						break;
					case 5:
						sleepTime = 200;
						break;
					}
					Thread.sleep(2000); // 설정된 FPS 에 따라서 스레드를 호출
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void startImage() {
		btnShareCode.setVisibility(View.INVISIBLE);
		btnSendCode.setVisibility(View.INVISIBLE);
		tvSrvCode.setVisibility(View.INVISIBLE);
		btndiscon.setVisibility(View.VISIBLE);
		callTaskThread callTaskRunnable = new callTaskThread();
		t = new Thread(callTaskRunnable);
		t.start();

	}

	// ServiceConnection 인터페이스를 구현하는 객체를 생성한다.
	/*
	 * private ServiceConnection mConnection = new ServiceConnection(){
	 * 
	 * @Override public void onServiceConnected(ComponentName className, IBinder
	 * service){ LocalBinder binder = (LocalBinder) service; mService =
	 * binder.getService(); mBound = true; }
	 * 
	 * @Override public void onServiceDisconnected(ComponentName arg0){ mBound =
	 * false; } };
	 */
	public void setChannelId(final String channelId) {
		tvSrvCode.post(new Runnable() {
			public void run() {
				tvSrvCode.setText(channelId);
			}
		});
	}

	/**
	 * PlayRTCHandler 개체를 생성한다.<br>
	 * 
	 * @return PlayRTCHandler
	 * @see com.playrtc.sample.handler.PlayRTCHandler
	 */
	private SSMRTCHandler createSSMRTCHandler(SSMRTCObserverImpl observer) {
		SSMRTCHandler handler = null;
		try {
			handler = new SSMRTCHandler(this, SERVICE_URL, observer);
		} catch (UnsupportedPlatformVersionException e) {
			// 현재 SDK는 Android SDK 11 이상만 지원한다.
			e.printStackTrace();
		} catch (RequiredParameterMissingException e) {
			// SERVICE_URL과 PlayRTCObserver구현개체를 생성자에 전달해야 한다.
			e.printStackTrace();
		}

		return handler;
	}

	private SSMRTCObserverImpl createSSMRTCObserver() {
		SSMRTCObserverImpl observer = new SSMRTCObserverImpl(this, logView);
		return observer;
	}

	// 자신의 화면을 만들어서 전송
	private class makeScreenOnDevice extends AsyncTask<Integer, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Log.i("time", "time aaa1");
			byte bArr[] = new byte[1000000];
			// Bitmap bit = BitmapFactory.decodeResource(getResources(),
			// arr[i]);
			//
			// ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// bit.compress(CompressFormat.JPEG , 80 , stream);
			// bArr = stream.toByteArray();
			//
			// getgetResources().getDrawable(R.drawable.c1);
			NativeCall nc = new NativeCall();

			// captured image size
			int imgSize = nc.getByteFrame(bArr, 0);

			byte[] capImgByteArr = new byte[imgSize];
			
			byte[] compressedData = null;
			int compressedSize = 0;
			
			// capImgByteArr 로 capture 된 크기만큼 byte 를 복사
			System.arraycopy(bArr, 0, capImgByteArr, 0, imgSize);

			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream(
						imgSize);
				GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);
				
				zipStream.write(capImgByteArr);
				
				byteStream.close();
				zipStream.close();
				
				compressedData = byteStream.toByteArray();
				compressedSize = compressedData.length;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * JSONObject imgJsonObj = new JSONObject();
			 * 
			 * try { imgJsonObj.put("dataType", "display");
			 * imgJsonObj.put("width", resWidth); imgJsonObj.put("height",
			 * resHeight); imgJsonObj.put("imgByte", bArr.toString()); // 화면을
			 * Object로 담는다.
			 * 
			 * } catch (JSONException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			// pSender.putDataInQueue(imgJsonObj); // 큐에 데이터를 삽입
			// pSender.sendPacket();
			// Log.i("time", "time aaa2"+bArr.length);
			
			dataHandler.sendBinary(compressedData);

			return null;
		}
	}

	public void inputMessage(JSONObject revobj) {
		String type = null;
		int x = 0;
		int y = 0;
		try {
			type = revobj.getString("event");
			x = revobj.getInt("x"); 
			y = revobj.getInt("y");
		} catch (Exception e) {

		}
		hih.buttonEvents(x, y, type);
		
		
	}
}
