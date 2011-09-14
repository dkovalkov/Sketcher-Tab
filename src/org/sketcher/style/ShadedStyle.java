package org.sketcher.style;

import java.util.ArrayList;
import java.util.HashMap;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class ShadedStyle implements Style {
	private ArrayList<PointF> points = new ArrayList<PointF>();

	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		PointF current = new PointF(x, y);
		points.add(current);

		float dx = 0;
		float dy = 0;
		int length = 0;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);

			dx = point.x - current.x;
			dy = point.y - current.y;

			length = (int) (dx * dx + dy * dy);

			if (length < 1000) {
				paint.setAlpha(((1 - (length / 1000)) * 30));
				c.drawLine(current.x, current.y, point.x, point.y, paint);
			}
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
	}

	@Override
	public void saveState(HashMap<Integer, Object> state) {
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.addAll(this.points);
		state.put(StylesFactory.SHADED, points);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(HashMap<Integer, Object> state) {
		this.points.clear();
		ArrayList<PointF> points = (ArrayList<PointF>) state
				.get(StylesFactory.SHADED);
		this.points.addAll(points);
	}
}
