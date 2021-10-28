package com.ssm.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
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

import com.ssm.androidcontroller.MainActivity;
import com.ssm.androidcontroller.R;
import com.ssm.client.SocketClientActivity;
import com.ssm.natives.NativeCall;
import com.ssm.util.AppUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Files;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class SocketServerActivity extends Activity {

	static final String shortenerUrl = "http://211.189.19.56:8000/myapp/shortener";

	TextView tvServerCode;
	Button tv_server_discon;

	ImageButton send;
	View decorView;

	public static final int ServerPort = 9000;
	public static String ServerIP = "";

	// Frametime automatic regulator switch
	boolean frameTimeRegulator = false;

	ServerSocket serverSocket;
	Socket socket = null;

	DataOutputStream dos;
	OutputStream os;

	DataInputStream dis;
	InputStream is;

	String getData = "";

	ImageView serverBg;
	Bitmap viewBitmap;

	int fileSize = 0;

	// get native object
	NativeCall nc = new NativeCall();

	// setting value
	int vDefinition = 0, vFrameTime = 1000;

	// transfer time check
	long sendTime = 0, recvTime = 0, spendTime = 0;

	// thread list
	ArrayList<Thread> tList = new ArrayList<Thread>();
	Thread t, t1;
	AsyncTask at = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);

		tvServerCode = (TextView) findViewById(R.id.tv_server_code);
		tv_server_discon = (Button) findViewById(R.id.tv_server_discon);
		send = (ImageButton) findViewById(R.id.btn_server_send_code_mms);
		serverBg = (ImageView) findViewById(R.id.iv_server_bg);

		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					if (addr.getHostAddress().startsWith("192")) {
						ServerIP = addr.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}

		// code generate

		Runnable shortenerRunnable = new codeShortener();
		Thread shortenerThread = new Thread(shortenerRunnable);
		shortenerThread.start();

		tv_server_discon.setVisibility(View.INVISIBLE);

		// open socket
		Runnable r = new socketServer();

		t = new Thread(r);
		tList.add(t);
		t.start();

		// start Image transfer
		Runnable r1 = new imgSender();

		t1 = new Thread(r1);
		tList.add(t1);
		t1.start();

	}

	protected void onResume() {
		super.onResume();

	}

	// get short code with real code
	private class codeShortener implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				HttpClient hc = new DefaultHttpClient();

				HttpGet get = new HttpGet(shortenerUrl + "/make/?code="
						+ ServerIP);

				HttpResponse resGet = hc.execute(get);
				HttpEntity resEntityGet = resGet.getEntity();

				ServerIP = EntityUtils.toString(resEntityGet);
				mHandler.sendEmptyMessage(3);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class imgSender implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (dos == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while (!Thread.currentThread().isInterrupted()) {
				if (dos == null)
					break;
				at = new sendScreen().execute(1);
				try {
					Thread.sleep(vFrameTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private class socketServer implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				serverSocket = new ServerSocket(ServerPort);

				// 연결 대기
				Log.d("socket", "waiting...");
				socket = serverSocket.accept();
				Log.d("socket", "connected");

				// 서버는 자신의 데이터를 client 에 전송한다.

				os = socket.getOutputStream();
				is = socket.getInputStream();

				dis = new DataInputStream(is);

				dos = new DataOutputStream(os);

				dos.writeUTF("You connected on Android server");

				// get setting values
				vDefinition = dis.readInt();
				int setFrameValue = dis.readInt();
				if (setFrameValue == -1) {
					// generate auto set
					frameTimeRegulator = true;
					vFrameTime = 333;
				} else {
					vFrameTime = setFrameValue / setFrameValue;
				}

				mHandler.sendEmptyMessage(4);

				// send acceptance of setting valueto client

				// open native control
				nc.open();

				while (!Thread.currentThread().isInterrupted()) {
					// get input value
					getData = dis.readUTF();
					Log.d("socket", "received : " + getData);

					// admit input
					mHandler.sendEmptyMessage(2);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// socket open

	}

	int firstDownX, firstDownY, secondDownX, secondDownY;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(),
						"img size : " + fileSize, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				serverBg.setScaleType(ScaleType.FIT_XY);
				serverBg.setImageBitmap((Bitmap) msg.obj);
				break;
			case 2:
				// 받은 좌포 출력
				/*
				 * Toast.makeText(getBaseContext(), "data : " + getData,
				 * Toast.LENGTH_SHORT).show();
				 */

				String[] dataGrp = getData.split(",");

				int x = Integer.parseInt(dataGrp[0]);
				int y = Integer.parseInt(dataGrp[1]);
				String eventType = dataGrp[2];

				if (eventType.equals("down")) {
					nc.sendEvent(3, 0, x);
					nc.sendEvent(3, 1, y);
					nc.sendEvent(0, 0, 0);

					nc.sendEvent(1, 330, 1);
					nc.sendEvent(0, 0, 0);
				} else if (eventType.equals("up")) {
					nc.sendEvent(3, 0, x);
					nc.sendEvent(3, 1, y);
					nc.sendEvent(0, 0, 0);

					nc.sendEvent(1, 330, 0);
					nc.sendEvent(0, 0, 0);

				} else if (eventType.equals("move")) {
					nc.sendEvent(3, 0, x);
					nc.sendEvent(3, 1, y);
					nc.sendEvent(0, 0, 0);
				}
				
				
				// zoom in/ out test
				/*
				 * 
				 * if (eventType.equals("down2")) { secondDownX = x; secondDownY
				 * = y;
				 * 
				 * nc.sendEvent(3, 47, 1); // ABS_MT_SLOT nc.sendEvent(3, 53,
				 * x); // ABS_MT_POSITION_X nc.sendEvent(3, 54, y); //
				 * ABS_MT_POSITION_Y nc.sendEvent(3, 57, 1); //
				 * ABS_MT_TRACKING_ID nc.sendEvent(0, 0, 0); } else if
				 * (eventType.equals("up1")) { nc.sendEvent(3, 47, 0);
				 * nc.sendEvent(3, 57, -1); nc.sendEvent(0, 0, 0); } else if
				 * (eventType.equals("up2")) { nc.sendEvent(3, 47, 0);
				 * nc.sendEvent(3, 57, -1); nc.sendEvent(0, 0, 0); } else if
				 * (eventType.equals("down")) { firstDownX = x; firstDownY = y;
				 * 
				 * nc.sendEvent(3, 47, 0); // ABS_MT_SLOT nc.sendEvent(3, 53,
				 * x); // ABS_MT_POSITION_X nc.sendEvent(3, 54, y); //
				 * ABS_MT_POSITION_Y nc.sendEvent(3, 57, 0); //
				 * ABS_MT_TRACKING_ID nc.sendEvent(1, 330, 1); nc.sendEvent(0,
				 * 0, 0);
				 * 
				 * } else if (eventType.equals("up")) {
				 * 
				 * nc.sendEvent(3, 47, 1); nc.sendEvent(3, 57, -1);
				 * nc.sendEvent(1, 330, 0); nc.sendEvent(0, 0, 0);
				 * 
				 * 
				 * nc.sendEvent(3, 47, 0); nc.sendEvent(3, 57, -1);
				 * nc.sendEvent(0, 0, 0);
				 * 
				 * } else if (eventType.equals("move")) {
				 * 
				 * int gap1 = firstDownX - x; int gap2 = firstDownY - y; int
				 * gap3 = secondDownX - x; int gap4 = secondDownY - y;
				 * 
				 * if ((double) Math.sqrt(gap1 * gap1 + gap2 * gap2) > (double)
				 * Math .sqrt(gap3 * gap3 + gap4 * gap4)) { nc.sendEvent(3, 47,
				 * 1); nc.sendEvent(3, 53, x); nc.sendEvent(3, 54, y);
				 * nc.sendEvent(0, 0, 0);
				 * 
				 * secondDownX = x; secondDownY = y; } else { nc.sendEvent(3,
				 * 47, 0); nc.sendEvent(3, 53, x); nc.sendEvent(3, 54, y);
				 * nc.sendEvent(0, 0, 0); firstDownX = x; firstDownY = y; }
				 * 
				 * }
				 */

				break;
			case 3:
				tvServerCode.setText(ServerIP);
				break;
			case 4:
				Toast.makeText(getApplicationContext(),
						vDefinition + " / " + vFrameTime, Toast.LENGTH_SHORT)
						.show();
				break;

			}

		}
	};

	// 자신의 화면을 만들어서 전송
	private class sendScreen extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			// 터치가 끝난 화면의 결과를 던진다
			if (dos != null) {
				sendTime = System.currentTimeMillis(); // 전송 시작시간

				byte[] buff = new byte[1000000]; // default

				int imgSize = nc.getByteFrame(buff, vDefinition);
				try {
					if (imgSize >= 0) {
						dos.writeInt(imgSize);
					} else {
						dos.writeInt(-1);
					}
					dos.flush();

					dos.write(buff, 0, imgSize);
					dos.flush();

				} catch (IOException e) {
					// TODO Auto-generated catch block

					Log.d("aaa", "33333333333333333333");
					e.printStackTrace();
				}
			}
			return 1;
		}

		protected void onPostExecute(Integer retVal) {

			// Frame Speed Modifie
			recvTime = System.currentTimeMillis();

			spendTime = recvTime - sendTime;

			Log.d("spend time : ", spendTime + "ms // frame Time : "
					+ vFrameTime);
			// calculate time

			if (vFrameTime > 100 && vFrameTime <= 1000 && frameTimeRegulator) { // 설정 범주 이내일 경우
				if (vFrameTime < recvTime - sendTime) // slower than before
														// (decrease frame )
					vFrameTime += 20;
				else { //
					vFrameTime -= 20;
				}
				
				if(vFrameTime <= 200){
					if(vDefinition < 70)
						vDefinition += 5;
				}else if(vFrameTime > 400){
					vDefinition = 20;
				}
				
			} else { // set default; < 프레임 설정이 정상범주를 벗어날 경우 초기화한다.
				vFrameTime = 333;
			}
			Log.d("======== "," [  quality : "+ vDefinition + " // frame Time : "
					+ vFrameTime + " ] ");
			
		}
	}

	@Override
	public void onBackPressed() {
		try {
			if (!at.isCancelled())
				at.cancel(true);
			for (Thread tItem : tList)
				tItem.interrupt();

			if (socket != null) {

				socket.close();
				dos.flush();
				Log.d("back pressed",
						"socket closed --------------------------");
				dos.close();
				os.close();
			}

			serverSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			t1.interrupt();
			finish();

			System.exit(0);
		}

	};

	// Home button control
	@Override
	protected void onUserLeaveHint() {
		// do nothing . just send the location point
		Toast.makeText(getApplicationContext(), "do nothing",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (socket != null) {
				socket.close();
				Log.d("back pressed",
						"socket closed --------------------------");
				nc.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
