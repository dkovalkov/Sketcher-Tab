package org.sketchertab.style;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Map;

class CirclesStyle extends StyleBrush {
	private float prevX;
	private float prevY;

	{
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
	}

	public void stroke(Canvas c, float x, float y) {
		float dx = x - prevX;
		float dy = y - prevY;
		
		int dxy = (int) (Math.sqrt(dx * dx + dy * dy) * 2);

		int gridx = (int) (Math.floor(x / 70) * 70 + 35);
		int gridy = (int) (Math.floor(y / 70) * 70 + 35);

		int rand = (int) (Math.floor(Math.random() * 9) + 1);
		int radius = dxy / rand;

		for (int i = 0; i < rand; i++) {
			c.drawCircle(gridx, gridy, (rand - i) * radius, paint);
		}

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
