package org.sketchertab.style;

import java.util.HashMap;

import org.sketchertab.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

class SquaresStyle implements Style {
	private float prevX;
	private float prevY;

	private Paint paint = new Paint();
	private Paint mBackgroundPaint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
	}

	{
		mBackgroundPaint.setColor(Color.WHITE);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
	}

	private static final Path PATH = new Path();

	private static final float ALPHA = 1.57079633F;
	private static final float COS_ALPHA = (float) Math.cos(ALPHA);
	private static final float SIN_ALPHA = (float) Math.sin(ALPHA);

	@Override
	public void stroke(Canvas c, float x, float y) {
		float dx = x - prevX;
		float dy = y - prevY;

		float ax = COS_ALPHA * dx - SIN_ALPHA * dy;
		float ay = SIN_ALPHA * dx + COS_ALPHA * dy;

		PATH.reset();
		PATH.moveTo(prevX - ax, prevY - ay);
		PATH.lineTo(prevX + ax, prevY + ay);
		PATH.lineTo(x + ax, y + ay);
		PATH.lineTo(x - ax, y - ay);
		PATH.close();

		c.drawPath(PATH, mBackgroundPaint);
		c.drawPath(PATH, paint);

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
