package com.ssm.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class HyeongInputHandler
{

	private static String tag = "HyeongInputHandler";

	static final public byte INPUT_MOUSE_START = 1;
	static final public byte INPUT_MOUSE_SLEEP = 2;

	private Context context;
	private boolean isEnd;

	InputHandler inputHandler;
	int type;
	int code;
	int value;

	public HyeongInputHandler(Context context)
	{
		Log.i(tag, "HyeongInputHandler");
		this.context = context;
		inputHandler = new InputHandler();
		inputHandler.open(); // 이거서비스에서 호출
	}

	public void buttonEvents(int x, int y, String type)
	{
		if(type.equals("move"))
		{
			inputHandler.sendEvent(3, 0, x);
			inputHandler.sendEvent(3, 1, y);
			inputHandler.sendEvent(0, 0, 0);
		}
		else if(type.equals("down"))
		{
			inputHandler.sendEvent(3, 0, x);
			inputHandler.sendEvent(3, 1, y);
			inputHandler.sendEvent(0, 0, 0);
			inputHandler.sendEvent(1, 330, 1);
			inputHandler.sendEvent(0, 0, 0);
		}
		else
		{
			inputHandler.sendEvent(1, 330, 0);
			inputHandler.sendEvent(0, 0, 0);
		}
	}

	public void stop()
	{
		isEnd = true;
	}

}
