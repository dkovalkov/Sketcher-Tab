package org.sketchertab;

import android.graphics.Canvas;
import org.sketchertab.style.StylesFactory;

import java.util.Map;

public interface Style {
	public void strokeStart(float x, float y);

	public void stroke(Canvas c, float x, float y);

	public void draw(Canvas c);

	public void setColor(int color);

    public void setOpacity(int opacity);

    public void setStrokeWidth(float width);

	public void saveState(Map<StylesFactory.BrushType, Object> state);

	public void restoreState(Map<StylesFactory.BrushType, Object> state);
}
