package org.sketchertab;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

/**
 * Two surfaces difference
 */

public final class SurfaceDiff {
    private static final String TAG = "SurfaceDiff";

    private static final boolean DEBUG_DIFF = true;

    private boolean[] bitmask;
    private Rect bounds;
    private int[] pixels;

    private SurfaceDiff(boolean[] bitmask, Rect bounds, int[] pixels) {
        this.bitmask = bitmask;
        this.bounds = bounds;
        this.pixels = pixels;
    }

    public static native void findBounds(Bitmap original, Bitmap updatedSurf, int originalWidth, int originalHeight, DiffResult result);

    public static SurfaceDiff Create(Bitmap original, Bitmap updatedSurf) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        long startDiff;
        if (DEBUG_DIFF) {
            Log.i(TAG, String.format("Original surface size: %dx%d", originalWidth, originalHeight));
            startDiff = System.currentTimeMillis();
        }

        DiffResult diffResult = new DiffResult();
        findBounds(original, updatedSurf, originalWidth, originalHeight, diffResult);

        Rect diffBounds = new Rect(diffResult.boundLeft, diffResult.boundTop, diffResult.boundRight, diffResult.boundBottom);

//        if (savings < MINIMUM_SAVINGS_PERCENT)
//            return null;

        // Store the old pixels.
//        int[] pixels = new int[numChanged];
//        int maskIndex = 0;
//        int pixelIndex = 0;
//
//        for (int y = diffBounds.top; y <= diffBounds.bottom; y += 1) {
//            for (int x = diffBounds.left; x <= diffBounds.right; x += 1) {
//                if (bitmask[maskIndex]) {
//                    pixels[pixelIndex] = original.getPixel(x, y);
//                    pixelIndex += 1;
//                }
//                maskIndex += 1;
//            }
//        }

        if (DEBUG_DIFF)
            Log.i(TAG, String.format("SurfaceDiff time: %d", System.currentTimeMillis() - startDiff));

        boolean[] bMask = new boolean[diffResult.bitmask.length];
        for (int i = 0; i < diffResult.bitmask.length; i += 1) {
            bMask[i] = diffResult.bitmask[i] == 1;
        }
        return new SurfaceDiff(bMask, diffBounds, diffResult.pixels);
    }

    public void applyAndSwap(Bitmap dest) {
        int maskIndex = 0;
        int pixelIndex = 0;

        for (int y = bounds.top; y <= bounds.bottom; y += 1) {
            for (int x = bounds.left; x <= bounds.right; x += 1) {
                if (bitmask[maskIndex]) {
                    int swapPixel = dest.getPixel(x, y);
                    dest.setPixel(x, y, pixels[pixelIndex]);
                    pixels[pixelIndex] = swapPixel;
                    pixelIndex += 1;
                }
                maskIndex += 1;
            }
        }
    }

    static {
        System.loadLibrary("findBounds");
    }

    public static class DiffResult {
        public int boundLeft;
        public int boundRight;
        public int boundTop;
        public int boundBottom;
        public byte[] bitmask;
        public int[] pixels;
    }

}
