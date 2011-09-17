package org.sketcher.style;

import java.util.ArrayList;
import java.util.HashMap;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class LongfurStyle implements Style {
	private ArrayList<PointF> points = new ArrayList<PointF>();

	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(25);
		paint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		PointF current = new PointF(x, y);
		points.add(current);

		float dx = 0;
		float dy = 0;
		float rand = 0;
		float length = 0;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);

			dx = point.x - current.x;
			dy = point.y - current.y;

			rand = (float) -Math.random();
			length = dx * dx + dy * dy;

			if (length < 4000 && Math.random() > length / 4000) {
				float ddx = dx * rand;
				float ddy = dy * rand;
				c.drawLine(current.x + ddx, current.y + ddy, point.x - ddx
						+ (float) Math.random() * 2, point.y - ddy
						+ (float) Math.random() * 2, paint);
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
		paint.setAlpha(25);
	}

	@Override
	public void saveState(HashMap<Integer, Object> state) {
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.addAll(this.points);
		state.put(StylesFactory.LONGFUR, points);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(HashMap<Integer, Object> state) {
		this.points.clear();
		ArrayList<PointF> points = (ArrayList<PointF>) state
				.get(StylesFactory.LONGFUR);
		this.points.addAll(points);
	}
}
