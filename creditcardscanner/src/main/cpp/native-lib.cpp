#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <android/bitmap.h>
#include <opencv2/imgproc/types_c.h>

using namespace cv;

#define LOG_TAG "NativeCreditCard"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

int threshold1 = 85;
int threshold2 = 125;

cv::Mat preprocessForEdgeDetection(const cv::Mat &image) {
    cv::Mat blurred, edges;
    cv::GaussianBlur(image, blurred, cv::Size(7, 7), 0);
    cv::Canny(blurred, edges, threshold1, threshold2);

    return edges;
}

std::vector<Point> findContours(const Mat &edges, const Mat &image) {
    std::vector<std::vector<Point>> contours;
    findContours(edges, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    for (size_t i = 0; i < contours.size(); i++) {
        std::vector<Point> approx;
        approxPolyDP(contours[i], approx, 0.05 * arcLength(contours[i], true), true);

        if (approx.size() == 4
            && isContourConvex(approx)
            && contourArea(contours[i]) > 800
                ) {

            Rect rect = boundingRect(approx);
            float aspectRatio = static_cast<float>(rect.width) / (float) rect.height;

            if (aspectRatio > 1.3 && aspectRatio < 1.7) {

                cv::drawContours(image, contours, i, cv::Scalar(0, 255, 0), 2);
                cv::rectangle(image, rect, cv::Scalar(255, 0, 0), 2);
                return approx;
            }
        }
    }

    return {};
}

jobject convertMatToBitmap(JNIEnv *env, Mat &srcMat) {
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmap = env->GetStaticMethodID(
            bitmapCls,
            "createBitmap",
            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jclass configCls = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID argb8888Fld = env->GetStaticFieldID(
            configCls,
            "ARGB_8888",
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

    Mat dst((int) info.height, (int) info.width, CV_8UC4, bitmapPixels);
    rgba.copyTo(dst);

    AndroidBitmap_unlockPixels(env, bitmap);
    return bitmap;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_haghpanah_creditcardscanner_data_imagerecognizer_ImageRecognizerImpl_isImageContainsCreditCard(
        JNIEnv *env,
        jobject thiz,
        jint width,
        jint height,
        jobject yBuffer,
        jint yRowStride) {

    auto *yData = static_cast<uint8_t *>(env->GetDirectBufferAddress(yBuffer));
    cv::Mat image(height, width, CV_8UC1);
    for (int i = 0; i < height; ++i) {
        memcpy(image.ptr(i), yData + i * yRowStride, width);
    }
    rotate(image, image, cv::ROTATE_90_CLOCKWISE);

    Mat edges = preprocessForEdgeDetection(image);
    std::vector<Point> approx = findContours(edges, image);

    return !approx.empty() ? JNI_TRUE : JNI_FALSE;

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_haghpanah_creditcardscanner_data_imagerecognizer_ImageRecognizerImpl_getPreprocessedImage(
        JNIEnv *env,
        jobject,
        jint width,
        jint height,
        jobject yBuffer,
        jint yRowStride) {

    auto *yData = static_cast<uint8_t *>(env->GetDirectBufferAddress(yBuffer));
    cv::Mat image(height, width, CV_8UC1);
    for (int i = 0; i < height; ++i) {
        memcpy(image.ptr(i), yData + i * yRowStride, width);
    }
    rotate(image, image, cv::ROTATE_90_CLOCKWISE);

    Mat edges = preprocessForEdgeDetection(image);
    std::vector<Point> approx = findContours(edges, image);

    if (!approx.empty()) {
        LOGD("Foundddddeeeeddd");
        LOGD("%s", std::to_string(approx.data()->x).c_str());
        LOGD("%s", std::to_string(approx.data()->y).c_str());

        cv::Mat mask = cv::Mat::zeros(edges.size(), CV_8UC1);

        std::vector<std::vector<cv::Point>> fillCont;
        fillCont.push_back(approx);
        cv::fillPoly(mask, fillCont, cv::Scalar(255));

        image.copyTo(edges, mask);

        return convertMatToBitmap(env, edges);
    }
    return nullptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_haghpanah_creditcardscanner_ui_CreditCardScannerActivity_setThreshhold(
        JNIEnv *env,
        jobject thiz,
        jint first,
        jint second) {
    threshold1 = first;
    threshold2 = second;
    return;
}
