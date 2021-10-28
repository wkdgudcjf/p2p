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

public class OrganizationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organization);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Organization");

		
	}
}
