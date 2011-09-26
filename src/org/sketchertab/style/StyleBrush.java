package org.sketchertab.style;


import android.graphics.Paint;
import android.util.Log;
import org.sketchertab.Style;

/**
 * Created by IntelliJ IDEA.
 * User: denis
 * Date: 9/18/11
 * Time: 7:54 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StyleBrush implements Style {
    protected int opacity;
    protected float strokeWidth;
    protected Paint paint = new Paint();

    public void setOpacity(int opacity) {
        Log.i("opacity", String.valueOf(opacity));
        this.opacity = opacity;
        paint.setAlpha(this.opacity);
    }

    public void setStrokeWidth(float width) {
        Log.i("size", String.valueOf(width));
        strokeWidth = width;
        paint.setStrokeWidth(strokeWidth);
    }

}
