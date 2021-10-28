package com.ssm.settings;

import com.ssm.androidcontroller.R;
import com.ssm.androidcontroller.R.layout;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TeamInfoActivity extends Activity {

	TextView tvTeamInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_info);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Team");

		Typeface tFaceBold = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Bold.ttf");
		
		tvTeamInfo = (TextView)findViewById(R.id.tv_team_info);
		tvTeamInfo.setTypeface(tFaceBold);
		
	}
}
