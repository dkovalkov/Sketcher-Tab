package org.sketchertab.style;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Map;

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
		float length;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);

			dx = point.x - current.x;
			dy = point.y - current.y;

			length = dx * dx + dy * dy;

			if (length < 1000) {
				int targetOpacity = (int) ((1 - (length / 1000)) * opacity);
				paint.setAlpha(targetOpacity);
				c.drawLine(current.x, current.y, point.x, point.y, paint);
			}
		}
	}

	public void strokeStart(float x, float y) {
	}

	public void draw(Canvas c) {
	}



	public void saveState(Map<StylesFactory.BrushType, Object> state) {
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.addAll(this.points);
		state.put(StylesFactory.BrushType.SHADED, points);
	}

	@SuppressWarnings("unchecked")
	public void restoreState(Map<StylesFactory.BrushType, Object> state) {
		this.points.clear();
		ArrayList<PointF> points = (ArrayList<PointF>) state
				.get(StylesFactory.BrushType.SHADED);
		this.points.addAll(points);
	}
}
