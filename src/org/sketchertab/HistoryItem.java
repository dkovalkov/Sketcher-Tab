package org.sketchertab;

import android.graphics.Bitmap;
import org.sketchertab.style.StylesFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * History element
 */
public final class HistoryItem {
    private SurfaceDiff surfaceDiff;
    private Bitmap oldSurface;
    private Map<StylesFactory.BrushType, Object> oldBrushData = new HashMap<StylesFactory.BrushType, Object>();
    private HistoryItemState state = HistoryItemState.UNDO;

    public HistoryItem(Bitmap oldSurface, Map<StylesFactory.BrushType, Object> oldBrushData) {
        surfaceDiff = SurfaceDiff.Create(oldSurface, Sketcher.getInstance().getSurface().getBitmap());

        if (null == surfaceDiff)
            this.oldSurface = oldSurface;

        this.oldBrushData = oldBrushData;
    }

    public void undo() {
        swap();
    }

    public void redo() {
        swap();
    }

    private void swap() {
        Bitmap surf = Sketcher.getInstance().getSurface().getBitmap();
        Map<StylesFactory.BrushType, Object> brushData = new HashMap<StylesFactory.BrushType, Object>();
        StylesFactory.saveState(brushData);

        if (null != surfaceDiff) {
            surfaceDiff.applyAndSwap(surf);
        } else {
            Sketcher.getInstance().getSurface().setBitmap(oldSurface);
            oldSurface = surf;
        }

        StylesFactory.restoreState(oldBrushData);
        oldBrushData = brushData;

        Sketcher.getInstance().getSurface().invalidate();
    }

    public HistoryItemState getState() {
        return state;
    }

    public void setState(HistoryItemState state) {
        this.state = state;
    }

    public static enum HistoryItemState {
        UNDO,
        REDO
    }
}
