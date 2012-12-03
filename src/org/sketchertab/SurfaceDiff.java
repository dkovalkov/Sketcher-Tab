package org.sketchertab;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

/**
 * Two surfaces difference
 */

public final class SurfaceDiff {
    private static final String TAG = "SurfaceDiff";

    private final static int MINIMUM_SAVINGS_PERCENT = 10;
    private static final boolean DEBUG_DIFF = true;

    private boolean[] bitmask;
    private Rect bounds;
    private int[] pixels;

    private SurfaceDiff(boolean[] bitmask, Rect bounds, int[] pixels) {
        this.bitmask = bitmask;
        this.bounds = bounds;
        this.pixels = pixels;
    }

    public static native void findBounds(Bitmap original, Bitmap updatedSurf, int originalWidth, int originalHeight, int[] bounds);

    public static SurfaceDiff Create(Bitmap original, Bitmap updatedSurf) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        long startDiff;
        if (DEBUG_DIFF) {
            Log.i(TAG, String.format("Original surface size: %dx%d", originalWidth, originalHeight));
            startDiff = System.currentTimeMillis();
        }

        // STEP 1 - Find the bounds of the changed pixels.

        Rect diffBounds = new Rect(originalWidth + 1, originalHeight + 1, -1, -1);
        Rect myBounds = new Rect(originalWidth + 1, originalHeight + 1, -1, -1);

        int[] nativeBounds = new int[4];
        findBounds(original, updatedSurf, originalWidth, originalHeight, nativeBounds);
        Log.i(TAG, String.format("native bounds: %d %d %d %d", nativeBounds[0], nativeBounds[1], nativeBounds[2], nativeBounds[3]));

//        todo: need jni optimization
        for (int row = 0; row < originalHeight; row += 1) {
            boolean isChangeInRow = false;

            int[] originalRow = new int[originalWidth];
            original.getPixels(originalRow, 0, originalWidth, 0, row, originalWidth, 1);

            int[] updatedRow = new int[originalWidth];
            updatedSurf.getPixels(updatedRow, 0, originalWidth, 0, row, originalWidth, 1);

            for (int i = 0; i < originalWidth; i += 1) {
                if (originalRow[i] != updatedRow[i]) {
                    isChangeInRow = true;
                    myBounds.left = Math.min(myBounds.left, i);
                    myBounds.right = Math.max(myBounds.right, i);
                }
            }

            if (isChangeInRow) {
                myBounds.top = Math.min(myBounds.top, row);
                myBounds.bottom = Math.max(myBounds.bottom, row);
            }
        }

        diffBounds.union(myBounds);

        if (DEBUG_DIFF)
            Log.i(TAG, String.format("Truncated surface size: %dx%d", diffBounds.width(), diffBounds.height()));

        // STEP 2 - Create a bitarray of whether each pixel in the bounds has changed, and count
        // how many changed pixels we need to store.

        boolean[] bitmask = new boolean[(diffBounds.width() + 1) * (diffBounds.height() + 1)];
        int index = 0;
        int numChanged = 0;

        for (int y = diffBounds.top; y <= diffBounds.bottom; y += 1) {
            for (int x = diffBounds.left; x <= diffBounds.right; x += 1) {
                boolean changed = original.getPixel(x, y) != updatedSurf.getPixel(x, y);
                bitmask[index] = changed;
                index += 1;
                if (changed)
                    numChanged += 1;
            }
        }

        int savings = (int) (100 - (float) numChanged / (float) (originalWidth * originalHeight) * 100);

        if (DEBUG_DIFF)
            Log.i(TAG, String.format("Compressed bitmask: %d/%d = %d%%", numChanged, originalHeight * originalWidth, 100 - savings));

        if (savings < MINIMUM_SAVINGS_PERCENT)
            return null;

        // Store the old pixels.
        int[] pixels = new int[numChanged];
        int maskIndex = 0;
        int pixelIndex = 0;

        for (int y = diffBounds.top; y <= diffBounds.bottom; y += 1) {
            for (int x = diffBounds.left; x <= diffBounds.right; x += 1) {
                if (bitmask[maskIndex]) {
                    pixels[pixelIndex] = original.getPixel(x, y);
                    pixelIndex += 1;
                }
                maskIndex += 1;
            }
        }

        if (DEBUG_DIFF)
            Log.i(TAG, String.format("SurfaceDiff time: %d", System.currentTimeMillis() - startDiff));

        return new SurfaceDiff(bitmask, diffBounds, pixels);
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
}
