/**
 * Create two surfaces diffirence structure
 *
 */
#include <android/bitmap.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include "findBounds.h"

#define DEBUG_DIFF 1
#define MINIMUM_SAVINGS_PERCENT (10)

void Java_org_sketchertab_SurfaceDiff_findBounds(JNIEnv *env, jobject obj, jobject original, jobject updatedSurf, int32_t originalWidth, int32_t originalHeight, jbooleanArray bitmaskArr, jintArray bounds, jintArray pixelsArr) {
    AndroidBitmapInfo  originalInfo;
    uint32_t          *originalPixels;
    AndroidBitmapInfo  updatedInfo;
    uint32_t          *updatedPixels;
    int                ret;

    // STEP 1 - Find the bounds of the changed pixels.
    //
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

    int boundWidth = myBoundRight - myBoundLeft;
    int boundHeight = myBoundBottom - myBoundTop;

    if (DEBUG_DIFF)
        LOGI("Truncated surface size: %dx%d", boundWidth, boundHeight);
    
    // STEP 2 - Create a bitarray of whether each pixel in the bounds has changed, and count
    // how many changed pixels we need to store.

    char* bitmask = malloc((boundWidth + 1) * (boundHeight + 1));
    int32_t numChanged = 0;
    char* bitmaskPtr = bitmask;

    for (int y = myBoundTop; y <= myBoundBottom; y += 1) {
        originalPtr = originalPixels + (y - 1) * originalWidth + myBoundLeft;
        updatedPtr = updatedPixels + (y - 1) * originalWidth + myBoundLeft;

        for (int x = myBoundLeft; x <= myBoundRight; x += 1) {
            char changed = *originalPtr != *updatedPtr;
            *bitmaskPtr = changed;
            originalPtr += 1;
            updatedPtr += 1;
            bitmaskPtr += 1;
            if (changed)
                numChanged += 1;
        }
    }

    AndroidBitmap_unlockPixels(env, updatedSurf);

    int savings = (int) (100 - (float) numChanged / (float) (originalWidth * originalHeight) * 100);

    if (DEBUG_DIFF)
        LOGI("Compressed bitmask: %d/%d = %d%%", numChanged, originalHeight * originalWidth, 100 - savings);

    if (savings < MINIMUM_SAVINGS_PERCENT)
        return;

    // Store the old pixels.
    originalPtr = originalPixels;
    int32_t* pixels = malloc(numChanged * sizeof(int32_t));
    int32_t* pixelsPtr = pixels;
    bitmaskPtr = bitmask;

    for (int y = myBoundTop; y <= myBoundBottom; y += 1) {
        originalPtr = originalPixels + (y - 1) * originalWidth + myBoundLeft;
        for (int x = myBoundLeft; x <= myBoundRight; x += 1) {
            if (*bitmaskPtr) {
                *pixelsPtr = *originalPtr;
                pixelsPtr += 1;
            }
            bitmaskPtr += 1;
            originalPtr += 1;
        }
    }


    AndroidBitmap_unlockPixels(env, original);

    jint* cArray;
    cArray = (*env)->GetIntArrayElements(env, bounds, NULL);

    if (NULL == cArray)
        return;

    cArray[0] = myBoundLeft;
    cArray[1] = myBoundRight;
    cArray[2] = myBoundTop;
    cArray[3] = myBoundBottom;

    (*env)->ReleaseIntArrayElements(env, bounds, cArray, 0);

    // Fill pixels array
    //jintArray pixelsArray = (*env)->NewIntArray(env, numChanged);
    
    LOGI("setIntArray");
    (*env)->SetIntArrayRegion(env, pixelsArr, 0, numChanged, pixels);
    LOGI("pixelsArr length %d", (*env)->GetArrayLength(env, pixelsArr));
    //for (int i = 0; i < numChanged; i += 1)
    //    pixelsArr[i] = pixelsArray[i];

    //cArray = (*env)->GetIntArrayElements(env, pixelsArr, NULL);

    //if (NULL == cArray)
    //    return;
    // add elements
    //(*env)->ReleaseIntArrayElements(env, pixelsArr, cArray, 0);

    //(*env)->SetBooleanArrayRegion(env, bitmaskArr, 0, boundWidth * boundHeight, (jboolean*) bitmask);
    
    //jchar* byteArray;
    //byteArray = (*env)->GetCharArrayElements(env, bitmaskArr, NULL);

    //if (NULL == byteArray)
    //    return;
    //(*env)->ReleaseCharArrayElements(env, bitmaskArr, byteArray, 0);
    free(bitmask);
    free(pixels);
}

