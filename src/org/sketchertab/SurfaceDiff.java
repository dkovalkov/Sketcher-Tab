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

    private static native boolean findBounds(Bitmap original, Bitmap updatedSurf, DiffResult result);

    private static native boolean applyAndSwap(Bitmap dest, int boundTop, int boundBottom, int boundLeft, int boundRight, boolean[] bitmask, int[] pixels);

    public static SurfaceDiff Create(Bitmap original, Bitmap updatedSurf) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        long startDiff;
        if (DEBUG_DIFF) {
            Log.i(TAG, String.format("Original surface size: %dx%d", originalWidth, originalHeight));
            startDiff = System.currentTimeMillis();
        }

        DiffResult diffResult = new DiffResult();
        if (!findBounds(original, updatedSurf, diffResult))
            return null;

        if (DEBUG_DIFF)
            Log.i(TAG, String.format("SurfaceDiff time: %d", System.currentTimeMillis() - startDiff));

        boolean[] bMask = new boolean[diffResult.bitmask.length];
        for (int i = 0; i < diffResult.bitmask.length; i += 1) {
            bMask[i] = diffResult.bitmask[i] == 1;
        }

        Rect diffBounds = new Rect(diffResult.boundLeft, diffResult.boundTop, diffResult.boundRight, diffResult.boundBottom);

        return new SurfaceDiff(bMask, diffBounds, diffResult.pixels);
    }

    public void applyAndSwap(Bitmap destination) {
        applyAndSwap(destination, bounds.top, bounds.bottom, bounds.left, bounds.right, bitmask, pixels);
    }

    static {
        System.loadLibrary("surfaceDiff");
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
