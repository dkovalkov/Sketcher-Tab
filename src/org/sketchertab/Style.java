package org.sketchertab;

import java.util.HashMap;

import android.graphics.Canvas;
import org.sketchertab.style.StyleBrush;

public interface Style {
	public void strokeStart(float x, float y);

	public void stroke(Canvas c, float x, float y);

	public void draw(Canvas c);

	public void setColor(int color);

    public void setOpacity(int opacity);

    public void setStrokeWidth(float width);

	public void saveState(HashMap<Integer, Object> state);

	public void restoreState(HashMap<Integer, Object> state);
}
