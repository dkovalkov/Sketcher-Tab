/**
 * Create two surfaces difference structure
 * Pinta project was very helpful
 */
#include "commonDefs.h"

#define DEBUG_DIFF 0
#define MINIMUM_SAVINGS_PERCENT 10

jboolean Java_org_sketchertab_SurfaceDiff_findBounds(JNIEnv *env, jobject obj, jintArray original, jobject updatedSurf, jobject diffResult) {

    jint* originalPixels;
    AndroidBitmapInfo updatedInfo;
    uint32_t* updatedPixels;
    int ret;

    // STEP 1 - Find the bounds of the changed pixels.

    if ((ret = AndroidBitmap_getInfo(env, updatedSurf, &updatedInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    if (updatedInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Updated surface bitmap format is not RGBA_8888!");
        return JNI_FALSE;
    }

    uint32_t originalWidth = updatedInfo.width;
    uint32_t originalHeight = updatedInfo.height;
	
    AndroidBitmap_lockPixels(env, updatedSurf, (void*)&updatedPixels);
	originalPixels = (*env)->GetIntArrayElements(env, original, NULL);

    uint32_t* originalPtr = originalPixels;
    uint32_t* updatedPtr = updatedPixels;
    int32_t myBoundLeft = originalWidth + 1;
    int32_t myBoundRight = -1;
    int32_t myBoundTop = originalHeight + 1;
    int32_t myBoundBottom = -1;

    for (int row = 0; row < originalHeight; row += 1) {
        int isChangeInRow = 0;

        for (int i = 0; i < originalWidth; i += 1) {
            if (*originalPtr++ != *updatedPtr++) {
                isChangeInRow = 1;
                myBoundLeft = min(myBoundLeft, i);
                myBoundRight = max(myBoundRight, i);
            }
        }

        if (isChangeInRow) {
            myBoundTop = min(myBoundTop, row);
            myBoundBottom = max(myBoundBottom, row);
        }
    }

    int boundWidth = myBoundRight - myBoundLeft;
    int boundHeight = myBoundBottom - myBoundTop;

    if (DEBUG_DIFF)
        LOGI("Truncated surface size: %dx%d", boundWidth, boundHeight);
    
    // STEP 2 - Create a bitarray of whether each pixel in the bounds has changed, and count
    // how many changed pixels we need to store.

    uint32_t bitmaskLength = (boundWidth + 1) * (boundHeight + 1);
    char* bitmask = malloc(bitmaskLength);
    int32_t numChanged = 0;
    char* bitmaskPtr = bitmask;

    for (int y = myBoundTop; y <= myBoundBottom; y += 1) {
        originalPtr = originalPixels + y * originalWidth + myBoundLeft;
        updatedPtr = updatedPixels + y * originalWidth + myBoundLeft;

        for (int x = myBoundLeft; x <= myBoundRight; x += 1) {
            if (*bitmaskPtr++ = *originalPtr++ != *updatedPtr++)
                numChanged += 1;
        }
    }

    AndroidBitmap_unlockPixels(env, updatedSurf);

    int savings = (int) (100 - (float) numChanged / (float) (originalWidth * originalHeight) * 100);

    if (DEBUG_DIFF)
        LOGI("Compressed bitmask: %d/%d = %d%%", numChanged, originalHeight * originalWidth, 100 - savings);

    if (savings < MINIMUM_SAVINGS_PERCENT) {
        free(bitmask);
        return JNI_FALSE;
    }

    // Store the old pixels.
    int32_t* pixels = malloc(numChanged * sizeof(int32_t));
    int32_t* pixelsPtr = pixels;
    bitmaskPtr = bitmask;

    for (int y = myBoundTop; y <= myBoundBottom; y += 1) {
        originalPtr = originalPixels + y * originalWidth + myBoundLeft;
        for (int x = myBoundLeft; x <= myBoundRight; x += 1) {
            if (*bitmaskPtr)
                *pixelsPtr++ = *originalPtr;
            bitmaskPtr += 1;
            originalPtr += 1;
        }
    }

	(*env)->ReleaseIntArrayElements(env, original, originalPixels, 0);

    // Make return object
    
    jclass cls = (*env)->FindClass(env, "org/sketchertab/SurfaceDiff$DiffResult");
    jfieldID fld = (*env)->GetFieldID(env, cls, "boundLeft", "I"); 
    (*env)->SetIntField(env, diffResult, fld, myBoundLeft);
    fld = (*env)->GetFieldID(env, cls, "boundRight", "I"); 
    (*env)->SetIntField(env, diffResult, fld, myBoundRight);
    fld = (*env)->GetFieldID(env, cls, "boundTop", "I"); 
    (*env)->SetIntField(env, diffResult, fld, myBoundTop);
    fld = (*env)->GetFieldID(env, cls, "boundBottom", "I"); 
    (*env)->SetIntField(env, diffResult, fld, myBoundBottom);

    jbyteArray bitmaskArr = (*env)->NewByteArray(env, bitmaskLength);
    if (NULL == bitmaskArr) {
        free(bitmask);
        free(pixels);
        return JNI_FALSE;
    }

    (*env)->SetByteArrayRegion(env, bitmaskArr, 0, bitmaskLength, bitmask);
    fld = (*env)->GetFieldID(env, cls, "bitmask", "[B"); 
    (*env)->SetObjectField(env, diffResult, fld, bitmaskArr);
    (*env)->DeleteLocalRef(env, bitmaskArr);

    jintArray pixelsArr = (*env)->NewIntArray(env, numChanged);
    if (NULL == pixelsArr) {
        free(bitmask);
        free(pixels);
        return JNI_FALSE;
    }

    (*env)->SetIntArrayRegion(env, pixelsArr, 0, numChanged, pixels);
    fld = (*env)->GetFieldID(env, cls, "pixels", "[I"); 
    (*env)->SetObjectField(env, diffResult, fld, pixelsArr);
    (*env)->DeleteLocalRef(env, pixelsArr);

    free(bitmask);
    free(pixels);
    return JNI_TRUE;
}
