package org.sketchertab.style;

import java.util.HashMap;

import org.sketchertab.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class GridStyle implements Style {
	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(25);
		paint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		float gridx = Math.round(x / 100) * 100;
		float gridy = Math.round(y / 100) * 100;

		float dx = (gridx - x) * 10;
		float dy = (gridy - y) * 10;

		for (int i = 0; i < 50; i++) {
			c.drawLine(x + (float) Math.random() * dx,
					y + (float) Math.random() * dy, gridx, gridy, paint);
		}
	}

	@Override
	public void strokeStart(float x, float y) {
	}

	@Override
	public void draw(Canvas c) {
	}

	@Override
	public void setColor(int color) {
		paint.setColor(color);
		paint.setAlpha(25);
	}

	@Override
	public void saveState(HashMap<Integer, Object> state) {
	}

	@Override
	public void restoreState(HashMap<Integer, Object> state) {
	}
}
