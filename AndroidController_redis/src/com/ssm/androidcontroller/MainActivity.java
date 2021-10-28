package com.ssm.androidcontroller;

import java.io.IOException;

import redis.clients.jedis.Jedis;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NativeActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssm.androidcontroller.R;
import com.ssm.client.ClientActivity;
import com.ssm.client.SocketClientActivity;
import com.ssm.natives.NativeCall;
import com.ssm.server.ServerActivity;
import com.ssm.server.SocketServerActivity;
import com.ssm.settings.SettingsActivity;

public class MainActivity extends Activity implements OnClickListener {

	Button btnConnect, btnCreateServer;
	public static EditText etConnCode;
	TextView tvConnTitle, tvTitle;
	Typeface tFace;

	int titleHeight;

	Thread tt;

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



		etConnCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				etConnCode.setText("");
			}
		});
		
	}

	int imgSize = 0;


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

			if (userCode.contains(" ") || userCode.length() != 6) { // 공백 혹은
																	// 6자리가 아닐
																	// 경우
				Toast toast = Toast.makeText(getApplicationContext(),
						"\t\t  잘못된 코드입니다.\n입력한 코드를 확인해 주세요.",
						Toast.LENGTH_SHORT);
				toast.show();

			} else {
				goToViewer.putExtra("code", etConnCode.getText().toString());
				startActivity(goToViewer);
			}

			break;
		}
	}


}
