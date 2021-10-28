#include "include/InputHandler.h"
#include "include/Input.h"
#include <unistd.h>

#define EV_ABS 0x03
#define ABS_MT_TRACKING_ID 0x39
#define EV_KEY 0x01
#define BTN_TOUCH 0x14a
#define DOWN 0x01
#define ABS_MT_POSITION_X 0x00
#define ABS_MT_POSITION_Y 0x01
#define SWIPE_GAP 200
#define SWIPE_COUNT 2
#define DISPLAY_VERTICAL 0
#define DISPLAY_HORIZONTAL 1
extern int inputFd;


JNIEXPORT jboolean JNICALL Java_com_ssm_server_InputHandler_openInputDevice(JNIEnv* env, jobject thiz, jint scrWidth, jint scrHeight){
  return (jboolean)openInput(scrWidth-1, scrHeight-1);
}

JNIEXPORT void JNICALL Java_com_ssm_server_InputHandler_closeInputDevice(JNIEnv* env, jobject thiz){
  closeInput();
}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_keyDown(JNIEnv *env, jobject thiz, jint keyCode){
	sendNativeEvent(inputFd, EV_KEY, keyCode, 1);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_keyUp(JNIEnv *env, jobject thiz, jint keyCode){
	sendNativeEvent(inputFd, EV_KEY, keyCode, 0);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_keyStroke(JNIEnv *env, jobject thiz, jint keyCode){
	Java_org_secmem232_phonetop_android_natives_InputHandler_keyDown(env, thiz, keyCode);
	Java_org_secmem232_phonetop_android_natives_InputHandler_keyUp(env, thiz, keyCode);
}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_touchDown(JNIEnv *env, jobject thiz){

	sendNativeEvent(inputFd, EV_KEY, BTN_TOUCH, 1);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);

}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_touchUp(JNIEnv *env, jobject thiz){
	sendNativeEvent(inputFd, EV_KEY, BTN_TOUCH, 0);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_touchSetPtr(JNIEnv *env, jobject thiz, jint x, jint y){
	sendNativeEvent(inputFd, EV_ABS, ABS_X, x);
	sendNativeEvent(inputFd, EV_ABS, ABS_Y, y);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

JNIEXPORT void JNICALL Java_com_ssm_server_InputHandler_sendEvent(JNIEnv *env, jobject thiz, jint type, jint code, jint value){
  sendNativeEvent(inputFd, type, code, value);
}

JNIEXPORT void JNICALL Java_org_secmem232_phonetop_android_natives_InputHandler_sendEventByLow(JNIEnv *env, jobject thiz, jbyteArray){
}
