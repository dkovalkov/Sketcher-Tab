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

	jboolean isCopyPixels;
    jint* pixelsPtr = (*env)->GetIntArrayElements(env, pixels, &isCopyPixels);
	jint* pixelsElements = pixelsPtr;
	
	jboolean isCopyBimask;
	jboolean* bitmaskPtr = (*env)->GetBooleanArrayElements(env, bitmask, &isCopyBimask);
	jboolean* bitmaskElements = bitmaskPtr;

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

	if (JNI_TRUE == isCopyPixels)
    	(*env)->ReleaseIntArrayElements(env, pixels, pixelsElements, 0);

	if (JNI_TRUE == isCopyBimask)
		(*env)->ReleaseBooleanArrayElements(env, bitmask, bitmaskElements, 0);

    AndroidBitmap_unlockPixels(env, dest);
}
