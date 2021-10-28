package com.ssm.androidcontroller;

import com.ssm.androidcontroller.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.admin.DeviceAdminReceiver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpActivity extends FragmentActivity {

	ImageView ivTopImage;
	
	private static final int NUM_PAGES = 7;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screenslide);

		ActionBar actionBar = getActionBar();
		actionBar.hide();

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(3);

	}

	class myThread extends Thread {

		public void run() {
			myHandler.sendEmptyMessage(1);
		}
	}

	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) { // 메시�?? 받는�?��
			switch (msg.what) { // 메시지 처리
			case 1:
				break;
			}
		};

	};

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 3) {
			// If the user is currently looking at the first step, allow the
			// system to handle the
			// Back button. This calls finish() on this activity and pops the
			// back stack.
			super.onBackPressed();
		} else if(mPager.getCurrentItem() > 3){
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		} else{
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);
		}
	}

	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects,
	 * in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int pageIdx) {
			// TODO Auto-generated method stub
			
			return new HelpSlidePageFragment(pageIdx);
		}
	}
}
