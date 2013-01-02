package org.sketchertab;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.nio.IntBuffer;

/**
 * Two surfaces difference
 */

public final class SurfaceDiff {
    private static final String TAG = "SurfaceDiff";
    private static final boolean DEBUG_DIFF = false;

    private boolean[] bitmask;
    private Rect bounds;
    private int[] pixels;

    private SurfaceDiff(boolean[] bitmask, Rect bounds, int[] pixels) {
        this.bitmask = bitmask;
        this.bounds = bounds;
        this.pixels = pixels;
    }

    private static native boolean findBounds(int[] original, Bitmap updatedSurf, DiffResult result);

    private static native boolean applyAndSwap(Bitmap dest, int boundTop, int boundBottom, int boundLeft, int boundRight, boolean[] bitmask, int[] pixels);

    public static SurfaceDiff Create(IntBuffer original, Bitmap updatedSurf) {
        long startDiff;
        if (DEBUG_DIFF) {
            Log.i(TAG, String.format("Surface size: %dx%d", updatedSurf.getWidth(), updatedSurf.getHeight()));
            startDiff = System.currentTimeMillis();
        }

        DiffResult diffResult = new DiffResult();
        if (!findBounds(original.array(), updatedSurf, diffResult))
            return null;

        if (DEBUG_DIFF)
            Log.i(TAG, String.format("SurfaceDiff time: %d", System.currentTimeMillis() - startDiff));

        Rect diffBounds = new Rect(diffResult.boundLeft, diffResult.boundTop, diffResult.boundRight, diffResult.boundBottom);

        return new SurfaceDiff(diffResult.bitmask, diffBounds, diffResult.pixels);
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
        public boolean[] bitmask;
        public int[] pixels;
    }

}
