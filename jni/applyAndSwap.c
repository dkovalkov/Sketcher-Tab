/**
 * Apply surface difference structure to bitmap
 */

#include "commonDefs.h"

void Java_org_sketchertab_SurfaceDiff_applyAndSwap(JNIEnv *env, jobject obj, jobject dest, jint boundTop, jint boundBottom, jint boundLeft, jint boundRight, jbooleanArray bitmask, jintArray pixels) {

    AndroidBitmapInfo  info;
    uint32_t*          destPixels;
    int                ret;

    if ((ret = AndroidBitmap_getInfo(env, dest, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Destination bitmap format is not RGBA_8888!");
        return;
    }

    AndroidBitmap_lockPixels(env, dest, (void*)&destPixels);

    jint* pixelsPtr = (*env)->GetIntArrayElements(env, pixels, 0);
    jboolean* bitmaskPtr = (*env)->GetBooleanArrayElements(env, bitmask, 0);

    uint32_t* destPixelsPtr;

    for (int y = boundTop; y <= boundBottom; y += 1) {
        destPixelsPtr = destPixels + y * info.width + boundLeft;

        for (int x = boundLeft; x <= boundRight; x += 1) {
            if (*bitmaskPtr) {
                int swapPixel = *destPixelsPtr;
                *destPixelsPtr = *pixelsPtr;
                *pixelsPtr = swapPixel;
                pixelsPtr += 1;
            }
            destPixelsPtr += 1;
            bitmaskPtr += 1;
        }
    }

    (*env)->ReleaseIntArrayElements(env, pixels, pixelsPtr, 0);
    (*env)->ReleaseBooleanArrayElements(env, bitmask, bitmaskPtr, 0);

    AndroidBitmap_unlockPixels(env, dest);
}
