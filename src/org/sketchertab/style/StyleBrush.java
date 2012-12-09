package org.sketchertab.style;


import android.graphics.Paint;
import org.sketchertab.Style;

/**
 * Abstract class for brush
 */
public abstract class StyleBrush implements Style {
    protected Paint paint = new Paint();
    protected int opacity;

    public void setOpacity(int opacity) {
        this.opacity = opacity;
        paint.setAlpha(opacity);
    }

    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }
}
