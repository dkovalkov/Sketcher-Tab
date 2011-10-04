package org.sketchertab.style;

import java.util.ArrayList;
import java.util.HashMap;

import org.sketchertab.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class ShadedStyle extends StyleBrush {
	private ArrayList<PointF> points = new ArrayList<PointF>();

	{
		paint.setAntiAlias(true);
	}

	public void stroke(Canvas c, float x, float y) {
		PointF current = new PointF(x, y);
		points.add(current);

		float dx;
		float dy;
		int length;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);

			dx = point.x - current.x;
			dy = point.y - current.y;

			length = (int) (dx * dx + dy * dy);

			if (length < 1000) {
				paint.setAlpha((int) ((1 - (length / 1000)) * opacity * 0.1));
				c.drawLine(current.x, current.y, point.x, point.y, paint);
			}
		}
	}

	public void strokeStart(float x, float y) {
	}

	public void draw(Canvas c) {
	}



	public void saveState(HashMap<Integer, Object> state) {
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.addAll(this.points);
		state.put(StylesFactory.SHADED, points);
	}

	@SuppressWarnings("unchecked")
	public void restoreState(HashMap<Integer, Object> state) {
		this.points.clear();
		ArrayList<PointF> points = (ArrayList<PointF>) state
				.get(StylesFactory.SHADED);
		this.points.addAll(points);
	}
}
