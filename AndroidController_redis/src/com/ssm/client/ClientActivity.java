package com.ssm.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ssm.androidcontroller.R;
import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.ssm.androidcontroller.SlideLogView;
import com.ssm.datatransmission.PacketReceiver;
import com.ssm.datatransmission.PacketSender;
import com.ssm.handler.DataChannelHandler;
import com.ssm.handler.SSMRTCHandler;
import com.ssm.handler.SSMRTCObserverImpl;
import com.ssm.util.AppUtil;

/**
 * 상대방으로 부터 받은 화면을 보여주고 제어를 보내는 액티비티
 * 
 * @author armin
 *
 */

public class ClientActivity extends Activity implements OnTouchListener {
	// 여기서 버튼 막자.
	ProgressBar progressCircle;

	TextView tvSrvWaiting;
	// viewChangeThread vThread;
	ImageView ivRemoteView; // 상대방 화면을 보여주는 뷰

	PacketReceiver pReceiver;
	PacketSender pSender;

	Context mContext;

	Thread t;

	Drawable d[] = new Drawable[10];

	boolean connected = false; // 통신 연결 여부
	private boolean isGo = false;
	int imgIdx = 0;

	int resWidth, resHeight; // resolution value
	int viewFps = 0; // view 갱신 주기 ( fps )

	String code;
	private static final String SERVICE_URL = "http://211.189.20.7:5400";
	private boolean isCloesActivity = false;
	private SSMRTCHandler playrtc = null;
	private DataChannelHandler dataHandler = null;
	private SSMRTCObserverImpl playrtcObserver = null;

	private SlideLogView logView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View vw = new View(this);
		vw.setOnTouchListener(this);
		setContentView(R.layout.activity_remoteviewer);

		mContext = this;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		resWidth = size.x;
		resHeight = size.y;

		// set receiver and sender
		pReceiver = new PacketReceiver();
		pSender = new PacketSender(dataHandler);

		Typeface tFaceBold = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Bold.ttf");
		ivRemoteView = (ImageView) findViewById(R.id.iv_remote_view);
		tvSrvWaiting = (TextView) findViewById(R.id.tv_viewer_waiting);
		tvSrvWaiting.setTypeface(tFaceBold);

		// hide notification bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// hide notification bar
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
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
		// 형철
		Intent intent = getIntent();
		code = intent.getExtras().getString("code").toString();

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
		try {
			playrtc.connectChannel(code, "client");
		} catch (RequiredConfigMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
		// hide notification bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/*
		 * decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
		 * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		 */

		// progressbar ...
		progressCircle = (ProgressBar) findViewById(R.id.progresscircle);

		// callTaskThread callTaskRunnable = new callTaskThread();
		// t = new Thread(callTaskRunnable);
		// t.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	/**
	 * setViewTask 를 주기적으로 호출하는 스레드 setviewTast 를 설정된 주기에 따라 호출한다.
	 * 
	 * @author armin
	 */
	private class callTaskThread implements Runnable {
		int sleepTime = 1000; // default delay time(ms)

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {

				new setViewTask().execute(1);
				
				try {
					switch (viewFps) {
					case 0:
						sleepTime = 2000;
						break;
					case 1:
						sleepTime = 1000;
						break;
					case 2:
						sleepTime = 700;
						break;
					case 3:
						sleepTime = 330;
						break;
					case 5:
						sleepTime = 200;
						break;
					}
					Thread.sleep(sleepTime); // 설정된 FPS 에 따라서 스레드를 호출
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	// image Change task
	private class setViewTask extends AsyncTask<Integer, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			// 상대의 화면을 계속해서 전송받는다.
			if (pReceiver.getDataQueueSize() > 0) { // 리시버 큐에 데이터가 존재하는 경우.
				JSONObject Data = pReceiver.getDataInQueue();

				try {

					String dataType = Data.getString("dataType");
					int servResolutionWidth = Data.getInt("width");
					int servResolutionHeight = Data.getInt("height");
					String tempString = Data.getString("imgByte");
					byte[] bArr = tempString.getBytes();
					
					byte[] imageByteArr = null;
					
					try {
						ByteArrayInputStream bais = new ByteArrayInputStream(bArr);
						
						GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
						
						gzipInputStream.read(imageByteArr);
						
						gzipInputStream.close();
						bais.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					// get Bitmap from byte array
					Bitmap viewBitmap = BitmapFactory.decodeByteArray(imageByteArr, 0,
							bArr.length);

					Message msg = Message.obtain();

					msg.what = 1;
					msg.obj = viewBitmap;
					mHandler.sendMessage(msg);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else { // queue empty

			}

			return null;
		}

	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				progressCircle.setVisibility(View.INVISIBLE);
				tvSrvWaiting.setVisibility(View.INVISIBLE);
				dataHandler.sendText("start");
				break;
			case 1:
				ivRemoteView.setScaleType(ScaleType.FIT_XY);
				ivRemoteView.setImageBitmap((Bitmap) msg.obj);
			}
		}
	};

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

	public void setCloesActivity(boolean isCloesActivity) {
		this.isCloesActivity = isCloesActivity;
	}

	/*
	 * isCloesActivity가 false이면 앱 종료 의사를 묻는 다이얼로그를 출력하고 true이면
	 * super.onBackPressed()를 호출하여 앱을 종료하도록 한다.
	 */
	@Override
	public void onBackPressed() {
		String userPid = playrtc.getPeerId();
		if (TextUtils.isEmpty(userPid) == false) {
			isCloesActivity = false;
			playrtc.deleteChannel();
		}
		super.onBackPressed();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		JSONObject obj = new JSONObject();
		if (!isGo) {
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			try {
				obj.put("x", x);
				obj.put("y", y);
				obj.put("dataType", "control");
				obj.put("event", "down");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataHandler.sendText(obj.toString());
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			try {
				obj.put("x", x);
				obj.put("y", y);
				obj.put("dataType", "control");
				obj.put("event", "up");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataHandler.sendText(obj.toString());
			return true;
		} else {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			try {
				obj.put("x", x);
				obj.put("y", y);
				obj.put("dataType", "control");
				obj.put("event", "move");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataHandler.sendText(obj.toString());
			return true;
		}
	}

	public void startButton() {
		// TODO Auto-generated method stub
		isGo = true;
		mHandler.sendEmptyMessage(0);
	}

	public void saveImage(JSONObject obj) {
		pReceiver.putDataInQueue(obj);
	}
}
