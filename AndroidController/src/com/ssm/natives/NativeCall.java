package com.ssm.natives;

import android.util.Log;

public class NativeCall {
	
	static {
		System.loadLibrary("my_lib");
	}
	
	public NativeCall(){
		Log.i(tag, "InputHandler(Context context)");
		Log.i(tag, "Width : " + String.valueOf(displayWidth));
		Log.i(tag, "Height : " + String.valueOf(displayHeight));
	}
	/**
	 * 
	 * @param buff 
	 * @return buffer size
	 */
	public native int getByteFrame( byte[] buff , int quality);
	
	
	private static String tag = "InputHandler";
	private boolean isDeviceOpened = false;
	
	private int displayWidth;
	private int displayHeight;
	
	/**
	 * uinput's touch input will be mapped into 4096*4096 matrix,<br/>
	 * where center coordinate is (0,0) and each axis ranges from -2047 to 2048.<br/>
	 * We should re-map original coordinate to send event properly.
	 */
	private static final int DIMENSION = 4096;
	private static final int HALF_DIMENSION =2048;
	

	public boolean open(){
		Log.i(tag, "open()");
		//isDeviceOpened = openInputDevice(displayWidth, displayHeight);
		isDeviceOpened = openInputDevice(1080, 1920);
		return isDeviceOpened;
	}
	
	public void close(){
		closeInputDevice();
		isDeviceOpened = false;
	}
	
	private native boolean openInputDevice(final int scrWidth, final int scrHeight);
	
	private native void closeInputDevice();

	public native void sendEvent(int type, int code, int value);

}



