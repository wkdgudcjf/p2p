#include "com_example_screencaptest_NativeCall.h"

#include <errno.h>
#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>
#include <string.h>

#include <linux/fb.h>
#include <sys/ioctl.h>
#include <sys/mman.h>

#include <binder/ProcessState.h>

#include <gui/SurfaceComposerClient.h>
#include <gui/ISurfaceComposer.h>

#include <ui/PixelFormat.h>

#include <SkImageEncoder.h>
#include <SkBitmap.h>
#include <SkData.h>
#include <SkStream.h>

#include <android/log.h>


using namespace android;

static uint32_t DEFAULT_DISPLAY_ID = ISurfaceComposer::eDisplayIdMain;


static SkBitmap::Config flinger2skia(PixelFormat f)
{
    switch (f) {
        case PIXEL_FORMAT_RGB_565:
            return SkBitmap::kRGB_565_Config;
        default:
            return SkBitmap::kARGB_8888_Config;
    }
}

static status_t vinfoToPixelFormat(const fb_var_screeninfo& vinfo,
        uint32_t* bytespp, uint32_t* f)
{

    switch (vinfo.bits_per_pixel) {
        case 16:
            *f = PIXEL_FORMAT_RGB_565;
            *bytespp = 2;
            break;
        case 24:
            *f = PIXEL_FORMAT_RGB_888;
            *bytespp = 3;
            break;
        case 32:
            // TODO: do better decoding of vinfo here
            *f = PIXEL_FORMAT_RGBX_8888;
            *bytespp = 4;
            break;
        default:
            return BAD_VALUE;
    }
    return NO_ERROR;
}



JNIEXPORT jint JNICALL Java_com_example_screencaptestapp_NativeCall_getByteFrame(JNIEnv *env, jobject obj, jbyteArray jByte)
{
	char test[2][100] = {"screencap","test.png"};
	
	char** argv;
	int argc = 2;
	int bufSize = 0 ;
		
    ProcessState::self()->startThreadPool();
    char inputPath[80] = "/sdcard/";

    const char* pname = strcat(inputPath,"");
//    printf("%s\n",pname);

    bool png = false;
    int32_t displayId = DEFAULT_DISPLAY_ID;
    int c;
    
    png = true;
    argc -= optind;
    argv += optind;

/*
    int fd = -1;
    
    if (argc == 0) {
        fd = dup(STDOUT_FILENO);
    } else if (argc == 1) {
        const char* fn = strcat(inputPath,"screencap.jpg");
        
		// file create
	

        fd = open(fn, O_WRONLY | O_CREAT | O_TRUNC, 0664);

        if (fd == -1) {
            fprintf(stderr, "Error opening file: %s (%s)\n", fn, strerror(errno));
            return 1;
        }
        const int len = strlen(fn);
        if (len >= 4 && 0 == strcmp(fn+len-4, ".jpg")) {
            png = true;
        }
    }
     
  */  

    void const* mapbase = MAP_FAILED;
    ssize_t mapsize = -1;

    void const* base = 0;
    uint32_t w, s, h, f;
    size_t size = 0;

    ScreenshotClient screenshot;
    sp<IBinder> display = SurfaceComposerClient::getBuiltInDisplay(displayId);
    
    if (display != NULL && screenshot.update(display) == NO_ERROR) {
    //  이 부분을 Thread 로 떨궈야하나?
    
        base = screenshot.getPixels();
        
        w = screenshot.getWidth();
        h = screenshot.getHeight();
        s = screenshot.getStride();
        f = screenshot.getFormat();
        size = screenshot.getSize();
        // __android_log_print(ANDROID_LOG_DEBUG, "debug", "get ComposerClient\n");
        
    } else {
    	__android_log_print(ANDROID_LOG_DEBUG, "debug", "get from Framebuffer devices \n");
    	/*
        const char* fbpath = "/dev/graphics/fb0";
        int fb = open(fbpath, O_RDONLY);
        if (fb >= 0) {
            struct fb_v(*env).SetByteArrayRegion(jByte,0,streamData->size(),(jbyte*)streamData->data());ar_screeninfo vinfo;
            if (ioctl(fb, FBIOGET_VSCREENINFO, &vinfo) == 0) {
                uint32_t bytespp;
                if (vinfoToPixelFormat(vinfo, &bytespp, &f) == NO_ERROR) {
                    size_t offset = (vinfo.xoffset + vinfo.yoffset*vinfo.xres) * bytespp;
                    w = vinfo.xres;
                    h = vinfo.yres;
                    s = vinfo.xres;
                    size = w*h*bytespp;
                    mapsize = offset + size;
                    mapbase = mmap(0, mapsize, PROT_READ, MAP_PRIVATE, fb, 0);
                    if (mapbase != MAP_FAILED) {
                        base = (void const *)((char const *)mapbase + offset);
                    }
                }
            }
            close(fb);
        }
        */
    }

	__android_log_print(ANDROID_LOG_DEBUG, "debug", "gogo\n");
	
	
    if (base) {
        if (png) { // png의 경우
        
        // width : w
        // height : h
        // format : f
        // stride : s ( 폭 )
        // base : pixel
        
        
            SkBitmap b;
            b.setConfig(flinger2skia(f), w, h, s*bytesPerPixel(f));
            b.setPixels((void*)base);
            SkDynamicMemoryWStream stream;
            
            /*
            SkImageEncoder::EncodeStream(&stream, b,
                    SkImageEncoder::kPNG_Type, SkImageEncoder::kDefaultQuality);
		     */
		     
		     
		     //quality 0 
		     SkImageEncoder::EncodeStream(&stream, b, SkImageEncoder::kJPEG_Type, 80);
		     
            SkData* streamData = stream.copyToData();

   	   		 // file discripter of a png file.
   	   		 
            //write(fd, streamData->data(), streamData->size());
            bufSize = streamData->size();
            
			 (*env).SetByteArrayRegion(jByte,0,streamData->size(),(jbyte*)streamData->data());
			 
            streamData->unref();

          // 결론. PNG 압축이 존~나 오래걸린다!
            
	   		// __android_log_print(ANDROID_LOG_DEBUG, "debug", "png is created!\n");
		
		// png 생성 되니, fd 가 아닌 byte로 출력해줄 것
		
		
        } else {// png가 아닐 경우  byte 그대로
        	/*
            write(fd, &w, 4); // 가로
            write(fd, &h, 4); // 세로
            write(fd, &f, 4); // 포맷
            size_t Bpp = bytesPerPixel(f); // format 별 size 를 얻음
            
            for (size_t y=0 ; y<h ; y++) { // w * h 를 쌓는다.
                write(fd, base, w*Bpp);
                base = (void *)((char *)base + s*Bpp);
            }
            */
            
        }
    }
    // close(fd);
    /*
    if (mapbase != MAP_FAILED) {
        munmap((void *)mapbase, mapsize);
    }
	*/


    return bufSize;
}
