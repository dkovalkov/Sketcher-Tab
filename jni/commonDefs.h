#ifndef _COMMONDEFS_H
#define _COMMONDEFS_H

#include <android/bitmap.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>

#define LOG_TAG "libsurfaceDiff"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define max(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a > _b ? _a : _b; })
#define min(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a < _b ? _a : _b; })

#endif /* _COMMONDEFS_H */
