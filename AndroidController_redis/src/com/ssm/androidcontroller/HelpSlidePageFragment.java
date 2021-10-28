package com.ssm.androidcontroller;

import com.ssm.androidcontroller.R;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class HelpSlidePageFragment extends Fragment {

	private Typeface typeface = null;

	TextView tvHelpTitle, tvHelpContent, tvSlideLead, tvBlackCover1,
			tvBlackCover2;
	String title, content, slideLead;
	ImageView ivHelpBg;

	int pageIdx;

	public HelpSlidePageFragment(int pageIdx) {
		this.pageIdx = pageIdx;
		this.slideLead = "< Get help        Help others >";
	}

	public HelpSlidePageFragment(int pageIdx, String title, String content,
			String slideLead) {
		// TODO Auto-generated constructor stub
		this.pageIdx = pageIdx;
		this.title = title;
		this.content = content;
		this.slideLead = slideLead;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_help_slide_page, container, false);

		// set fonts
		Typeface tfRoboto = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");
		Typeface tfRobotoBold = Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/RobotoCondensed-Bold.ttf");

		tvHelpTitle = (TextView) rootView.findViewById(R.id.tv_help_title);
		tvHelpContent = (TextView) rootView.findViewById(R.id.tv_help_content);
		tvSlideLead = (TextView) rootView.findViewById(R.id.tv_help_slide_lead);
		tvBlackCover1 = (TextView) rootView
				.findViewById(R.id.tv_bg_blackcover1);
		tvBlackCover2 = (TextView) rootView
				.findViewById(R.id.tv_bg_blackcover2);

		ivHelpBg = (ImageView) rootView.findViewById(R.id.iv_help_top_image);

		tvHelpTitle.setTypeface(tfRobotoBold);
		tvHelpContent.setTypeface(tfRoboto);
		tvSlideLead.setTypeface(tfRobotoBold);

		// setImageBackground
		if (pageIdx <= 1 || pageIdx >= 5) {
			tvBlackCover1.setAlpha(0);
			tvBlackCover2.setAlpha(0);
			ivHelpBg.setScaleType(ScaleType.FIT_XY);
		} else {
			tvBlackCover1.setAlpha(1);
			tvBlackCover2.setAlpha(1);
			ivHelpBg.setScaleType(ScaleType.FIT_CENTER);
		}

		switch (pageIdx) {
		case 0:
			title = "";
			content = "";
			slideLead = "";
			ivHelpBg.setImageResource(R.drawable.img_help2);
			break;
		case 1:
			title = "";
			content = "";
			slideLead = "slide to read more <";
			ivHelpBg.setImageResource(R.drawable.img_help1);
			break;
		case 2:
			title = "Ask your friends for help";
			content = "You can get help with your code";
			slideLead = "slide to read more <";
			break;
		case 3:
			break;
		case 4:
			title = "Get code from others";
			content = "You can help them with the code\nThey need your help to use device";
			slideLead = "> slide to read more";
			break;
		case 5:
			title = "";
			content = "";
			slideLead = "> slide to read more";
			ivHelpBg.setImageResource(R.drawable.img_help3);
			break;
		case 6:
			title = "";
			content = "";
			slideLead = "";
			ivHelpBg.setImageResource(R.drawable.img_help4);
			break;
		}

		if (title != null)
			tvHelpTitle.setText(title);
		if (content != null)
			tvHelpContent.setText(content);
		if (slideLead != null)
			tvSlideLead.setText(slideLead);

		return rootView;
	}

}

/*
 * 
 * switch(arg0){ // case 0: title = "#"; content = ""; break; case 1: pageIdx =
 * 1; title = "Ask your friends for help"; content =
 * "You can get help with your code"; slideLead = "slide to read more <"; break;
 * case 2: pageIdx = 2; break; case 3: pageIdx = 3; title =
 * "Get code from others"; content =
 * "You can help them with the code\nThey need your help to use device"; break;
 * case 4: pageIdx = 4; title = "#"; content = ""; break; } if(title == null &&
 * content == null) return new HelpSlidePageFragment(pageIdx); else return new
 * HelpSlidePageFragment(pageIdx,title,content,slideLead);
 */
