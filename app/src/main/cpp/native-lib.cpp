#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOG_TAG "NativeOpenCV"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

//using namespace cv;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_haghpanah_scanner_MainActivity_checkIfPictureContainsCreditCard(
        JNIEnv *env, jobject thiz,
        jbyteArray imageData,
        jint width, jint height
) {
    return JNI_FALSE;
}
