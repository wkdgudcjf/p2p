package com.ssm.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
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
import com.ssm.natives.NativeCall;
import com.ssm.util.AppUtil;

/**
 * �������� ���� ���� ȭ���� �����ְ� ��� ������ ��Ƽ��Ƽ
 * 
 * @author armin
 *
 */
public class SocketClientActivity extends Activity implements OnTouchListener {

	final String shortenerUrl = "http://211.189.19.55:8000/myapp/shortener";

	// ���⼭ ��ư ����.
	ProgressBar progressCircle;

	TextView tvSrvWaiting;
	// viewChangeThread vThread;
	ImageView ivRemoteView; // ���� ȭ���� �����ִ� ��

	PacketReceiver pReceiver;
	PacketSender pSender;

	Context mContext;

	Thread t;

	Drawable d[] = new Drawable[10];

	boolean connected = false; // ��� ���� ����
	private boolean isGo = false;
	int imgIdx = 0;

	int resWidth, resHeight; // resolution value
	int viewFps = 0; // view ���� �ֱ� ( fps )

	String code;
	private static final String SERVICE_URL = "http://211.189.20.7:5400";
	private boolean isCloesActivity = false;
	private SSMRTCHandler playrtc = null;
	private DataChannelHandler dataHandler = null;
	private SSMRTCObserverImpl playrtcObserver = null;

	private SlideLogView logView = null;

	String getData = "";
	String tempIp = "";
	String serverIp = "";

	private int currentApiVersion;

	DataOutputStream dos = null;
	DataInputStream dis = null;

	Socket sc = null;

	ArrayList<Thread> tList = new ArrayList<Thread>();

	// setting value
	String vDefinition = null;
	int vFrame = 0;
	boolean autoFrameSetter = false;

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

		Typeface tFaceBold = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Bold.ttf");
		ivRemoteView = (ImageView) findViewById(R.id.iv_remote_view);
		tvSrvWaiting = (TextView) findViewById(R.id.tv_viewer_waiting);
		tvSrvWaiting.setTypeface(tFaceBold);

		progressCircle = (ProgressBar) findViewById(R.id.progresscircle);

		// clear all views for remote view

		// tvSrvWaiting.setVisibility(View.INVISIBLE);

		// hide notification bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// hide notification bar
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		// get server Ip address
		Intent intent = getIntent();
		tempIp = intent.getExtras().getString("code").toString();
		serverIp = "192.168."+tempIp;
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		vDefinition = sharedPref
				.getString("pref_set_quality", "Low Definition");

		String prefSetFps = sharedPref.getString("pref_set_fps", "3");

		if (prefSetFps.contains("Auto")) {
			vFrame = 333; // auto mode
			autoFrameSetter = true;
		} else
			vFrame = Integer.parseInt(prefSetFps);

		Toast.makeText(this, vDefinition + " / " + vFrame, Toast.LENGTH_SHORT)
				.show();

		// start functions

		//Runnable rGetRealCode = new getRealCode();
		//Thread tGetRealCode = new Thread(rGetRealCode);
		//tGetRealCode.start();
		//tList.add(tGetRealCode);

		// Socket client
		Runnable r = new socketClient();
		Thread t = new Thread(r);
		tList.add(t);

		t.start();

		// �� �ϴ� �׺���̼� ������. >>>> ������ ������ �������� ��� ����
		currentApiVersion = android.os.Build.VERSION.SDK_INT;

		final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // �ϴ� �׺���̼�
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

		// This work only for android 4.4+
		if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

			getWindow().getDecorView().setSystemUiVisibility(flags);

			// Code below is to handle presses of Volume up or Volume down.
			// Without this, after pressing volume buttons, the navigation bar
			// will
			// show up and won't hide
			final View decorView = getWindow().getDecorView();
			decorView
					.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

						@Override
						public void onSystemUiVisibilityChange(int visibility) {
							if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
								decorView.setSystemUiVisibility(flags);
							}
						}
					});
		}

		// on Touch
		ivRemoteView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				int x = (int) event.getRawX();
				int y = (int) event.getRawY();

				String eventType = null;

				try {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						eventType = "down";
						/*
						 * Toast.makeText(getApplicationContext(), x + " / " + y
						 * + " / " + eventType, Toast.LENGTH_SHORT).show();
						 */
						if (dos != null) {
							dos.writeUTF(x + "," + y + "," + eventType);
						} 
						break;
					case MotionEvent.ACTION_MOVE:
						eventType = "move";
						if (dos != null) {
							dos.writeUTF(x + "," + y + "," + eventType);
						}
						break;
					case MotionEvent.ACTION_UP:
						eventType = "up";
						/*
						 * Toast.makeText(getApplicationContext(), x + " / " + y
						 * + " / " + eventType, Toast.LENGTH_SHORT).show();
						 */
						if (dos != null) {
							dos.writeUTF(x + "," + y + "," + eventType);
						}
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return true;
					// e.printStackTrace();
				} finally {
				}

				return true;
			}
		});

	}

	// get Real code with short code
	private class getRealCode implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				HttpClient hc = new DefaultHttpClient();

				HttpGet get = new HttpGet(shortenerUrl + "/get/?hcode=" + code);

				HttpResponse resGet = hc.execute(get);
				HttpEntity resEntityGet = resGet.getEntity();

				serverIp = EntityUtils.toString(resEntityGet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class imgReceiver implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (!Thread.currentThread().interrupted()) {
				while (sc.isClosed()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (sc.isClosed()) {
					mHandler.sendEmptyMessage(999);
					break;
				}

				if (dis != null) {
					new socketImageGetTask().execute(1);
				} else {

				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	// image Change task
	private class socketImageGetTask extends AsyncTask<Integer, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Integer... params) {
			Bitmap screenFromSrv = null;
			try {
				int imgSize = dis.readInt();
				// Log.d("zzz", imgSize + "");

				byte[] screen = new byte[1000000];
				while (imgSize <= 0) {
					Thread.sleep(10);
				}
				if (imgSize > 0) {
					dis.readFully(screen, 0, imgSize);
					screenFromSrv = BitmapFactory.decodeByteArray(screen, 0,
							imgSize);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				for (Thread threadItem : tList) {
					threadItem.interrupt();
				}
				mHandler.sendEmptyMessage(999);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandler.sendEmptyMessage(999);
			}
			return screenFromSrv;
		}

		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				ivRemoteView.setImageBitmap(result);
				// dos.writeBoolean(true);
			} else {
				// dos.writeBoolean(false);
			} 
		}
	}

	private class socketClient implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			int port = 9000;

			try {
				while (!Thread.currentThread().interrupted()) {
					Thread.sleep(500);
					if (serverIp.length() > 0)
						break;
				}
				sc = new Socket(serverIp, port);

				InputStream is = sc.getInputStream();
				OutputStream os = sc.getOutputStream();

				// get Image through stream
				dis = new DataInputStream(is);

				// send control through stream
				dos = new DataOutputStream(os);

				getData = dis.readUTF();

				// send setting values

				// 0. remove useless view
				mHandler.sendEmptyMessage(0);
				// 1. send present preference of definition

				if (vDefinition.contains("High")) { // high quality
					dos.writeInt(80);
				} else if (vDefinition.contains("Medium")) { // medium quality
					dos.writeInt(40);
				} else {
					dos.writeInt(0);
				}

				dos.flush();
				// 2. send frame
				if (autoFrameSetter) {
					dos.writeInt(-1);
				} else {
					dos.writeInt(vFrame);
				}
				dos.flush();

				// suspend while the setting values are accepted
				// dis.readInt();

				// send controls
				mHandler.sendEmptyMessage(2);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	// image Change task
	private class setViewTask extends AsyncTask<Integer, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			// ����� ȭ���� ����ؼ� ���۹޴´�.
			if (pReceiver.getDataQueueSize() > 0) { // ���ù� ť�� �����Ͱ� �����ϴ� ���.
				JSONObject Data = pReceiver.getDataInQueue();

				try {

					String dataType = Data.getString("dataType");
					int servResolutionWidth = Data.getInt("width");
					int servResolutionHeight = Data.getInt("height");
					String tempString = Data.getString("imgByte");
					byte[] bArr = tempString.getBytes();

					byte[] imageByteArr = null;

					try {
						ByteArrayInputStream bais = new ByteArrayInputStream(
								bArr);

						GZIPInputStream gzipInputStream = new GZIPInputStream(
								bais);

						gzipInputStream.read(imageByteArr);

						gzipInputStream.close();
						bais.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// get Bitmap from byte array
					Bitmap viewBitmap = BitmapFactory.decodeByteArray(
							imageByteArr, 0, bArr.length);

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
				break;
			case 1:
				ivRemoteView.setScaleType(ScaleType.FIT_XY);
				ivRemoteView.setImageBitmap((Bitmap) msg.obj);
			case 2:
				/*
				 * Toast.makeText(getBaseContext(), "data : " + getData,
				 * Toast.LENGTH_SHORT).show();
				 */
				// get definition setting\

				// start Image receive
				Runnable r1 = new imgReceiver();

				Thread t1 = new Thread(r1);
				tList.add(t1);
				t1.start();

				break;
			case 4:
				break;
			case 999:// finish activity
				Log.i("handler", "finished");
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				break;

			}
		}
	};

	public void startButton() {
		// TODO Auto-generated method stub
		isGo = true;
	}

	public void saveImage(JSONObject obj) {
		pReceiver.putDataInQueue(obj);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
