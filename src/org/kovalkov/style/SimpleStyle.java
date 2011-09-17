package org.sketcher.style;

import java.util.HashMap;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class SimpleStyle implements Style {
	private float prevX;
	private float prevY;

	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1.5F);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		c.drawLine(prevX, prevY, x, y, paint);

		prevX = x;
		prevY = y;
	}

	@Override
	public void strokeStart(float x, float y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void draw(Canvas c) {
	}

	@Override
	public void setColor(int color) {
		paint.setColor(color);
		paint.setAlpha(100);
	}

	@Override
	public void saveState(HashMap<Integer, Object> state) {
	}

	@Override
	public void restoreState(HashMap<Integer, Object> state) {
	}
}
