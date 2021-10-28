package com.ssm.androidcontroller;

import com.ssm.androidcontroller.R;

import android.content.Context;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * �α׺� ����� ���� TextView Ȯ�� Ŭ���� 
 * @author ds3grk
 *
 */
public class SlideLogView extends TextView{

	public SlideLogView(Context context) {
		super(context);
		this.setMovementMethod(new ScrollingMovementMethod());
	}

	public SlideLogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setMovementMethod(new ScrollingMovementMethod());
	}
	
	public SlideLogView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setMovementMethod(new ScrollingMovementMethod());
	}
	
	/**
	 * �α� �並 ȭ�鿡 �����ش�. 
	 */
	public void show() {
		Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.log_show);
		animation.setAnimationListener(new Animation.AnimationListener(){
			
			@Override
			public void onAnimationEnd(Animation anim) {
				
			}

			@Override
			public void onAnimationRepeat(Animation anim) {
		
			}

			@Override
			public void onAnimationStart(Animation anim) {
				SlideLogView.this.setVisibility(View.VISIBLE);
			}
			
		});
		this.startAnimation(animation);
		SlideLogView.this.bringToFront();
		
	}
	/**
	 * �α� �並 ȭ�鿡�� �����. 
	 */
	public void hide() {
		Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.log_hide);
		animation.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationEnd(Animation anim) {
				SlideLogView.this.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation anim) {
				
			}

			@Override
			public void onAnimationStart(Animation anim) {
				
			}
			
		});
		this.startAnimation(animation);
	}
	
	/**
	 * �α� ��� ������ ���� �����.
	 */
	public void clear() {
		this.post(new Runnable(){
		   public void run()
		   {
			   SlideLogView.this.setText("");
		   }
	   });
	}
	
	/**
	 * �α� ������ �ǳ��� �߰��Ѵ�.
	 * 
	 * @param msg String, �α� �޼��� 
	 */
	public void appendLog(final String msg) {
	   this.post(new Runnable(){
		   public void run()
		   {
			   SlideLogView.this.append(msg+"\n");
			   // ��Ʈ�� �ڵ��̵� 
			   final Layout layout = SlideLogView.this.getLayout();
		        if(layout != null){
		            int scrollDelta = layout.getLineBottom(SlideLogView.this.getLineCount() - 1) 
		                - SlideLogView.this.getScrollY() - SlideLogView.this.getHeight();
		            if(scrollDelta > 0)
		            	SlideLogView.this.scrollBy(0, scrollDelta);
		        }
		   }
	   });
		    
	}
}