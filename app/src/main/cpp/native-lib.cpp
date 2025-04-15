#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <android/bitmap.h>
#include <opencv2/imgproc/types_c.h>

using namespace cv;

#define LOG_TAG "NativeCreditCard"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Converts YUV to RGB
Mat convertYUVtoRGB(jbyte *data, int width, int height) {
    Mat yuv(height + height / 2, width, CV_8UC1, reinterpret_cast<uchar *>(data));
    Mat rgb;
    cvtColor(yuv, rgb, COLOR_YUV2RGB_NV21);
    return rgb;
}

// Crops the image to ROI (Region of Interest)
Mat cropToROI(const Mat &image, int width, int height) {
    int roiWidth = width * 0.6;
    int roiHeight = height * 0.4;
    int roiX = (width - roiWidth) / 2;
    int roiY = (height - roiHeight) / 2;
    Rect roiRect(roiX, roiY, roiWidth, roiHeight);
    return image(roiRect);
}

// Preprocesses the image: grayscale + blur + edge detection
Mat preprocessForEdgeDetection(const Mat &roi) {
    Mat gray, blurred, edges;
    cvtColor(roi, gray, COLOR_RGB2GRAY);
    GaussianBlur(gray, blurred, Size(5, 5), 0);
    Canny(blurred, edges, 50, 150);
    return edges;
}

// Finds contours and checks for credit card-like rectangles
bool detectCreditCardShape(const Mat &edges) {
    std::vector<std::vector<Point>> contours;
    findContours(edges, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    for (const auto &contour: contours) {
        std::vector<Point> approx;
        approxPolyDP(contour, approx, 0.02 * arcLength(contour, true), true);

        if (approx.size() == 4) {
            Rect rect = boundingRect(approx);
            float aspectRatio = static_cast<float>(rect.width) / rect.height;

            if (aspectRatio > 1.5 && aspectRatio < 1.7) {
                LOGD("Credit Card Detected!");
                return true;
            }
        }
    }

    return false;
}

// JNI bridge function
extern "C" JNIEXPORT jboolean JNICALL
Java_com_haghpanah_scanner_MainActivity_checkIfPictureContainsCreditCard(
        JNIEnv *env, jobject thiz,
        jbyteArray imageData,
        jint width, jint height) {

    jbyte *data = env->GetByteArrayElements(imageData, nullptr);
    if (data == nullptr) return JNI_FALSE;

    Mat rgb = convertYUVtoRGB(data, width, height);
    Mat roi = cropToROI(rgb, width, height);
    Mat edges = preprocessForEdgeDetection(roi);
    bool isCard = detectCreditCardShape(edges);

    env->ReleaseByteArrayElements(imageData, data, 0);
    return isCard ? JNI_TRUE : JNI_FALSE;
}

jobject convertMatToBitmap(JNIEnv *env, Mat &srcMat) {
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmap = env->GetStaticMethodID(
            bitmapCls,
            "createBitmap",
            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jclass configCls = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID argb8888Fld = env->GetStaticFieldID(configCls, "ARGB_8888",
                                                 "Landroid/graphics/Bitmap$Config;");
    jobject argb8888Obj = env->GetStaticObjectField(configCls, argb8888Fld);

    jobject bitmap = env->CallStaticObjectMethod(
            bitmapCls,
            createBitmap,
            srcMat.cols,
            srcMat.rows,
            argb8888Obj);

    void *bitmapPixels;
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);
    AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels);

    // Ensure the format is CV_8UC4 for RGBA before copying
    Mat rgba;
    if (srcMat.channels() == 1) {
        cvtColor(srcMat, rgba, COLOR_GRAY2RGBA);
    } else if (srcMat.channels() == 3) {
        cvtColor(srcMat, rgba, COLOR_RGB2RGBA); // Or BGR2RGBA depending on source
    } else if (srcMat.channels() == 4) {
        rgba = srcMat;
    } else {
        AndroidBitmap_unlockPixels(env, bitmap);
        return nullptr;
    }

    Mat dst(info.height, info.width, CV_8UC4, bitmapPixels);
    rgba.copyTo(dst);

    AndroidBitmap_unlockPixels(env, bitmap);
    return bitmap;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_haghpanah_scanner_MainActivity_preprocessImage(
        JNIEnv *env,
        jobject thiz,
        jbyteArray imageData,
        jint width,
        jint height) {

    jbyte *data = env->GetByteArrayElements(imageData, nullptr);
    if (data == nullptr) return nullptr;

    Mat rgb = convertYUVtoRGB(data, width, height);
    Mat roi = cropToROI(rgb, width, height);
    Mat edges = preprocessForEdgeDetection(roi);

    return convertMatToBitmap(env, edges);
}