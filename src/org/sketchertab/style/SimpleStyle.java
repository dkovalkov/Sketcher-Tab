package org.sketchertab.style;

import android.graphics.Canvas;

import java.util.Map;

class SimpleStyle extends StyleBrush {
	private float prevX;
	private float prevY;

	{
		paint.setAntiAlias(true);
	}

	public void stroke(Canvas c, float x, float y) {
		c.drawLine(prevX, prevY, x, y, paint);
		prevX = x;
		prevY = y;
	}

	public void strokeStart(float x, float y) {
		prevX = x;
		prevY = y;
	}

	public void draw(Canvas c) {
	}

	public void saveState(Map<StylesFactory.BrushType, Object> state) {
	}

	public void restoreState(Map<StylesFactory.BrushType, Object> state) {
	}
}
