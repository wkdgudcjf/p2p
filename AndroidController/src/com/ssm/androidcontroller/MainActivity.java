package com.ssm.androidcontroller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssm.client.SocketClientActivity;
import com.ssm.natives.NativeCall;
import com.ssm.server.SocketServerActivity;
import com.ssm.settings.SettingsActivity;

public class MainActivity extends Activity implements OnClickListener {

	Button btnConnect, btnCreateServer;
	public static EditText etConnCode;
	TextView tvConnTitle, tvTitle;
	Typeface tFace;

	int titleHeight;

	Thread tt;
	//Jedis jedis;

	ScrollView ivBg;
	ImageView ivTest;

	int cnt = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();

		// set fonts
		tFace = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		// actionBar.hide();
		// actionBar.setDisplayHomeAsUpEnabled(true);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		btnConnect = (Button) findViewById(R.id.btn_connect);
		btnCreateServer = (Button) findViewById(R.id.btn_create_server);
		etConnCode = (EditText) findViewById(R.id.et_conn_code);
		tvConnTitle = (TextView) findViewById(R.id.tv_conn_title);
		tvTitle = (TextView) findViewById(R.id.tv_main_title);

		ivTest = (ImageView) findViewById(R.id.ivTestBg);
		ivBg = (ScrollView) findViewById(R.id.bg_main);

		// set fonts
		tvConnTitle.setTypeface(tFace);
		btnConnect.setTypeface(tFace);
		btnCreateServer.setTypeface(tFace);
		tvTitle.setTypeface(tFace);
		etConnCode.setTypeface(tFace);

		btnConnect.setOnClickListener(this);
		btnCreateServer.setOnClickListener(this);

		tvConnTitle.setVisibility(View.GONE);

		//jedis = new Jedis("211.189.19.55", 8080);

		Runnable r = new dbTest();
		tt = new Thread(r);

		tt.start();

		etConnCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				etConnCode.setText("");
			}
		});

	}

	int imgSize = 0;

	private class upFile extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			NativeCall nc = new NativeCall();
			byte[] buff = new byte[10000000];
			imgSize = nc.getByteFrame(buff, 5);

			//jedis.rpush("img".getBytes(), buff);

			//byte[] resImg = jedis.lpop("img".getBytes());
			//if (resImg.length != 0) {
			//	Bitmap screenFromSrv = BitmapFactory.decodeByteArray(resImg, 0,
			//			imgSize);
			//	return screenFromSrv;
			//} else
				return null;
		}

		protected void onPostExecute(Bitmap ret) {
			if (ret != null) {
				cnt++;
				ivTest.setImageBitmap(ret);
				Toast.makeText(getApplicationContext(), "refresh : " + cnt,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class sendVal extends AsyncTask<Float, Void, Void> {

		@Override
		protected Void doInBackground(Float... params) {
			// jedis.set("move",Float.toString(params[0]));

			//jedis.rpush("mv", Float.toString(params[0]));

			return null;
		}

		protected void onPostExecute() {

		}
	}

	public class dbTest implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//jedis.set("foo", "bar");
			//String value = jedis.get("foo");
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_search:
			Intent goToHelp = new Intent(this.getApplicationContext(),
					HelpActivity.class);
			startActivity(goToHelp);
			break;
		case R.id.action_settings:
			Intent goToSetting = new Intent(this.getApplicationContext(),
					SettingsActivity.class);
			startActivity(goToSetting);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_create_server:

			/*
			 * Intent goToSrv = new Intent(getApplicationContext(),
			 * ServerActivity.class);
			 */
			Intent goToSrv = new Intent(getApplicationContext(),
					SocketServerActivity.class);
			startActivity(goToSrv);

			break;
		case R.id.btn_connect:

			/*
			 * Intent goToViewer = new Intent(getApplicationContext(),
			 * ClientActivity.class);
			 */

			Intent goToViewer = new Intent(getApplicationContext(),
					SocketClientActivity.class);
			String userCode = etConnCode.getText().toString();

			if (userCode.contains(" ")) { // ���� Ȥ��
																	// 6�ڸ��� �ƴ�
																	// ���
				Toast toast = Toast.makeText(getApplicationContext(),
						"코드를 입력하세요.",
						Toast.LENGTH_SHORT);
				toast.show();

			} else {
				goToViewer.putExtra("code", etConnCode.getText().toString());
				startActivity(goToViewer);
			}

			break;
		}
	}

	public boolean onKeyDown(int keycode, KeyEvent event) {
		switch (keycode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:

			// Toast.makeText(this, "KeyDown" + value,
			// Toast.LENGTH_SHORT).show();

			break;

		case KeyEvent.KEYCODE_VOLUME_UP:
			Toast.makeText(this, "KeyUp", Toast.LENGTH_SHORT).show();
			break;
		}
		return true;
	}

}
