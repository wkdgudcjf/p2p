package com.ssm.settings;

import com.ssm.androidcontroller.R;
import com.ssm.androidcontroller.HelpActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_setting);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Settings");

	//	Preference prefOrg = (Preference) findPreference("pref_org");
	//	Preference prefTeam = (Preference) findPreference("pref_team");

	//	prefOrg.setOnPreferenceClickListener(this);
	//	prefTeam.setOnPreferenceClickListener(this);
		
		ListPreference lp1 = (ListPreference) findPreference("pref_set_fps");
		ListPreference lp2 = (ListPreference) findPreference("pref_set_quality");
		
		lp1.setOnPreferenceChangeListener(this);
		lp2.setOnPreferenceChangeListener(this);
		
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
			// ���� ���� ��Ƽ��Ƽ�̹Ƿ� �������� �ʴ´�.
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
	public boolean onPreferenceChange(Preference pref, Object setVal) {
		// TODO Auto-generated method stub
		
		// ������ ���� ������.
		Toast.makeText(getApplication(),setVal.toString(), Toast.LENGTH_SHORT).show();

		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub

		switch (preference.getKey()) {
		case "pref_team":
			//Intent goToTeamActivity = new Intent(getApplicationContext(),
			//		TeamInfoActivity.class);
			//startActivity(goToTeamActivity);
			break;
		case "pref_org":
			//Intent i = new Intent(getApplicationContext(),
			//		OrganizationActivity.class);
			//startActivity(i);
			break;
		}
		return false;
	}
}
