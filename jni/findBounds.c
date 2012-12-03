#include <android/bitmap.h>
#include <android/log.h>

#define LOG_TAG "libfindBounds"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

void findBounds(JNIEnv *env, jobject obj, jobject original, jobject updatedSurf, uint32_t originalWidth, uint32_t originalHeight, jintArray bounds) {
    AndroidBitmapInfo  originalInfo;
    uint32_t          *originalPixels;
    AndroidBitmapInfo  updatedInfo;
    uint32_t          *updatedPixels;
    int                ret;

    AndroidBitmap_getInfo(env, original, &originalInfo);
    if (originalInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Original bitmap format is not RGBA_8888!");
        return;
    }

    AndroidBitmap_getInfo(env, updatedSurf, &updatedInfo);
    if (updatedInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Updated surface bitmap format is not RGBA_8888!");
        return;
    }

    AndroidBitmap_lockPixels(env, original, (void*)&originalPixels);
    AndroidBitmap_lockPixels(env, updatedSurf, (void*)&updatedPixels);

    for (int row = 0; row < originalHeight; row += 1) {
        int isChangeInRow = 0;

//        int[] originalRow = new int[originalWidth];
//        original.getPixels(originalRow, 0, originalWidth, 0, row, originalWidth, 1);

//        int[] updatedRow = new int[originalWidth];
//        updatedSurf.getPixels(updatedRow, 0, originalWidth, 0, row, originalWidth, 1);

        for (int i = 0; i < originalWidth; i += 1) {
//            if (originalRow[i] != updatedRow[i]) {
//                isChangeInRow = true;
//                myBounds.left = Math.min(myBounds.left, i);
//                myBounds.right = Math.max(myBounds.right, i);
//            }
        }

//        if (isChangeInRow) {
//            myBounds.top = Math.min(myBounds.top, row);
//            myBounds.bottom = Math.max(myBounds.bottom, row);
//        }
    }

    AndroidBitmap_unlockPixels(env, original);
    AndroidBitmap_unlockPixels(env, updatedSurf);

    jint *cArray;
    cArray = (*env)->GetIntArrayElements(env, bounds, NULL);

    if (NULL == cArray)
        return;

    cArray[0] = 11;
    (*env)->ReleaseIntArrayElements(env, bounds, cArray, 0);
}

