package org.sketchertab;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.sketchertab.style.StylesFactory;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class DrawController implements View.OnTouchListener {
    public static final int INIT_BG_COLOR = Color.rgb(255, 250, 232);
    public static final int DEFAULT_OPACITY = 50;
    public static final float DEFAULT_WIDTH = 1;
    public static final int DEFAULT_COLOR = Color.argb(DEFAULT_OPACITY, 0, 0, 0);

    private Style style;
    private final Canvas mCanvas;
    private boolean toDraw = false;
    private Paint mColor = new Paint();
    private Paint bgColor = new Paint();
    private IntBuffer undoSurfaceBuffer;
    private Map<StylesFactory.BrushType, Object> brushData;

    public DrawController(Canvas canvas) {
        setStyle(StylesFactory.getCurrentStyle());
        mCanvas = canvas;
        bgColor.setColor(INIT_BG_COLOR);
        setOpacity(DEFAULT_OPACITY);
        setStrokeWidth(DEFAULT_WIDTH);
        setPaintColor(DEFAULT_COLOR);
    }

    public void draw() {
        if (toDraw)
            style.draw(mCanvas);
    }

    public void setStyle(Style style) {
        toDraw = false;
        style.setColor(mColor.getColor());
        style.setOpacity(mColor.getAlpha());
        style.setStrokeWidth(mColor.getStrokeWidth());
        this.style = style;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    undoSurfaceBuffer = Sketcher.getInstance().getSurface().copyPixelsToBuffer(undoSurfaceBuffer);
                } catch (OutOfMemoryError error) {
                    error.printStackTrace();
                    DocumentHistory.getInstance().clear();
                    undoSurfaceBuffer = Sketcher.getInstance().getSurface().copyPixelsToBuffer(undoSurfaceBuffer);
                }
                brushData = new HashMap<StylesFactory.BrushType, Object>();
                StylesFactory.saveState(brushData);
                toDraw = true;
                style.strokeStart(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                style.stroke(mCanvas, event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                HistoryItem item = new HistoryItem(undoSurfaceBuffer, brushData);
                DocumentHistory.getInstance().pushNewItem(item);
                Sketcher.getInstance().invalidateOptionsMenu();
                break;
        }
        return true;
    }

    public IntBuffer getUndoSurfaceBuffer() {
        return undoSurfaceBuffer;
    }

    public void setUndoSurfaceBuffer(IntBuffer buffer) {
        undoSurfaceBuffer = buffer;
    }

    public void clear() {
        toDraw = false;
        StylesFactory.clearCache();
        setStyle(StylesFactory.getCurrentStyle());

        brushData = new HashMap<StylesFactory.BrushType, Object>();
        StylesFactory.saveState(brushData);
        HistoryItem item = new HistoryItem(undoSurfaceBuffer, brushData);
        DocumentHistory.getInstance().pushNewItem(item);
    }

    public void setPaintColor(int color) {
        mColor.setColor(color);
        style.setColor(color);
    }

    public int getPaintColor() {
        return mColor.getColor();
    }

    public void setBackgroundColor(int color) {
        bgColor.setColor(color);
    }

    public int getBackgroundColor() {
        return bgColor.getColor();
    }

    public void setOpacity(int opacity) {
        mColor.setAlpha(opacity);
        style.setOpacity(opacity);
    }

    public int getOpacity() {
        return mColor.getAlpha();
    }

    public void setStrokeWidth(float width) {
        mColor.setStrokeWidth(width);
        style.setStrokeWidth(width);
    }

    public float getStrokeWidth() {
        return mColor.getStrokeWidth();
    }
}
