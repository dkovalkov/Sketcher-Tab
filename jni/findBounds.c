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

void Java_org_sketchertab_SurfaceDiff_findBounds(JNIEnv *env, jobject obj, jobject original, jobject updatedSurf, int32_t originalWidth, int32_t originalHeight, jobject diffResult) {
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

    LOGI("return bitmask");
    fld = (*env)->GetFieldID(env, cls, "bitmask", "[B"); 
    (*env)->SetObjectField(env, diffResult, fld, bitmask);

    LOGI("return pixels");
    fld = (*env)->GetFieldID(env, cls, "pixels", "[I"); 
    (*env)->SetObjectField(env, diffResult, fld, pixels);


    free(bitmask);
    free(pixels);
}
