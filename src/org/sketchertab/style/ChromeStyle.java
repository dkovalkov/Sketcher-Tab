package org.sketchertab.style;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.*;
import android.util.Log;

class ChromeStyle extends StyleBrush {
	private float prevX;
	private float prevY;

	private ArrayList<PointF> points = new ArrayList<PointF>();

	private Paint randPaint = new Paint();

	{
		paint.setAntiAlias(true);
		randPaint.setAntiAlias(true);
	}

    public void setStrokeWidth(float width) {
        super.setStrokeWidth(width);
        randPaint.setStrokeWidth(width);
    }

	public void stroke(Canvas c, float x, float y) {
		PointF current = new PointF(x, y);
		points.add(current);

		c.drawLine(prevX, prevY, x, y, paint);

		float dx;
		float dy;
		float length;
		int curColorRed = paint.getColor() >> 16 & 0xFF;
		int curColorGreen = paint.getColor() >> 8 & 0xFF;
		int curColorBlue = paint.getColor() & 0xFF;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);

			dx = point.x - current.x;
			dy = point.y - current.y;

			length = dx * dx + dy * dy;

			if (length < 1000) {
				
				randPaint.setARGB(opacity, (int) (Math.random() * curColorRed), (int) (Math.random() * curColorGreen),
				                        (int) (Math.random() * curColorBlue));
				float ddx = dx * 0.2F;
				float ddy = dy * 0.2F;
				// c.drawLine(current.x + ddx, current.y + ddy, point.x - ddx,	point.y - ddy, paint);
				c.drawLine(current.x + ddx, current.y + ddy, point.x - ddx,	point.y - ddy, randPaint);
			}
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
	}

	@SuppressWarnings("unchecked")
	public void restoreState(HashMap<Integer, Object> state) {
		this.points.clear();
		ArrayList<PointF> points = (ArrayList<PointF>) state
				.get(StylesFactory.CHROME);
		this.points.addAll(points);
	}

	public void saveState(HashMap<Integer, Object> state) {
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.addAll(this.points);
		state.put(StylesFactory.CHROME, points);
	}
}
