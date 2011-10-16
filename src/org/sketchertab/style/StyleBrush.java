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
