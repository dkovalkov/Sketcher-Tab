package org.sketchertab.style;

import java.util.HashMap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class CirclesStyle extends StyleBrush {
	private float prevX;
	private float prevY;

//	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
//		paint.setAlpha(50);
//		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
//		paint.setStrokeWidth(1);
	}

	public void stroke(Canvas c, float x, float y) {
		float dx = x - prevX;
		float dy = y - prevY;

		int dxy = (int) (Math.sqrt(dx * dx + dy * dy) * 2);

		int gridx = (int) (Math.floor(x / 50) * 50 + 25);
		int gridy = (int) (Math.floor(y / 50) * 50 + 25);

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

	public void setColor(int color) {
		paint.setColor(color);
//		paint.setAlpha(50);
	}

	public void saveState(HashMap<Integer, Object> state) {
	}

	public void restoreState(HashMap<Integer, Object> state) {
	}
}
