package org.sketchertab.style;

import java.util.HashMap;

import org.sketchertab.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class RibbonStyle extends StyleBrush {
	private Painter[] painters = new Painter[50];

	private float x;
	private float y;

	private class Painter {
		private static final int SCREEN_WIDTH = 480;
		private static final int SCREEN_HEIGHT = 600;

		float dx = SCREEN_WIDTH / 2;
		float dy = SCREEN_HEIGHT / 2;
		float ax = 0;
		float ay = 0;
		float div = 0.1F;
		float ease = (float) (Math.random() * 0.2 + 0.6);
	}

	{
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);

		for (int i = 0; i < 50; i++) {
			painters[i] = new Painter();
		}
	}

	public void draw(Canvas c) {
		float startX;
		float startY;
		for (int i = 0; i < painters.length; i++) {
			startX = painters[i].dx;
			startY = painters[i].dy;
			painters[i].dx -= painters[i].ax = (painters[i].ax + (painters[i].dx - x)
					* painters[i].div)
					* painters[i].ease;
			painters[i].dy -= painters[i].ay = (painters[i].ay + (painters[i].dy - y)
					* painters[i].div)
					* painters[i].ease;
			c.drawLine(startX, startY, painters[i].dx, painters[i].dy, paint);
		}
	}

	public void stroke(Canvas c, float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void strokeStart(float x, float y) {
		this.x = x;
		this.y = y;

		for (int i = 0, max = painters.length; i < max; i++) {
			Painter painter = painters[i];
			painter.dx = x;
			painter.dy = y;
		}
	}

	public void setColor(int color) {
		paint.setColor(color);
	}

	@Override
	public void saveState(HashMap<Integer, Object> state) {
	}

	@Override
	public void restoreState(HashMap<Integer, Object> state) {
	}
}
