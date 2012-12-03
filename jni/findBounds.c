#include <android/bitmap.h>
#include <android/log.h>

#define LOG_TAG "libfindBounds"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define max(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a > _b ? _a : _b; })
#define min(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a < _b ? _a : _b; })

void Java_org_sketchertab_SurfaceDiff_findBounds(JNIEnv *env, jobject obj, jobject original, jobject updatedSurf, int32_t originalWidth, int32_t originalHeight, jintArray bounds) {
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

    uint32_t* originalPtr = originalPixels;
    uint32_t* updatedPtr = updatedPixels;
    int32_t myBoundLeft = originalWidth + 1;
    int32_t myBoundRight = -1;
    int32_t myBoundTop = originalHeight + 1;
    int32_t myBoundBottom = -1;

    for (int row = 0; row < originalHeight; row += 1) {
        int isChangeInRow = 0;

        for (int i = 0; i < originalWidth; i += 1) {
            if (*originalPtr != *updatedPtr) {
                isChangeInRow = 1;
                myBoundLeft = min(myBoundLeft, i);
                myBoundRight = max(myBoundRight, i);
            }
            originalPtr += 1;
            updatedPtr += 1;
        }

        if (isChangeInRow) {
            myBoundTop = min(myBoundTop, row);
            myBoundBottom = max(myBoundBottom, row);
        }
    }

    AndroidBitmap_unlockPixels(env, original);
    AndroidBitmap_unlockPixels(env, updatedSurf);

    jint *cArray;
    cArray = (*env)->GetIntArrayElements(env, bounds, NULL);

    if (NULL == cArray)
        return;

    cArray[0] = myBoundLeft;
    cArray[1] = myBoundRight;
    cArray[2] = myBoundTop;
    cArray[3] = myBoundBottom;

    (*env)->ReleaseIntArrayElements(env, bounds, cArray, 0);
}

