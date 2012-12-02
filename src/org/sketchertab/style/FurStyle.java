package org.sketchertab.style;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Map;

class FurStyle extends StyleBrush {
	private float prevX;
	private float prevY;

	private ArrayList<PointF> points = new ArrayList<PointF>();

	{
		paint.setAntiAlias(true);
	}

    @Override
    public void setOpacity(int opacity) {
        super.setOpacity((int) (opacity * 0.5f));
    }

	public void stroke(Canvas c, float x, float y) {
		PointF current = new PointF(x, y);
		points.add(current);

		c.drawLine(prevX, prevY, x, y, paint);

		float dx;
		float dy;
		float length;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);
			dx = point.x - current.x;
			dy = point.y - current.y;

			length = dx * dx + dy * dy;

			if (length < 2000 && Math.random() > length / 2000) {
				float ddx = dx * 0.5F;
				float ddy = dy * 0.5F;
				c.drawLine(x + ddx, y + ddy, x - ddx, y - ddy, paint);
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

	public void saveState(Map<StylesFactory.BrushType, Object> state) {
		ArrayList<PointF> points = new ArrayList<PointF>();
		points.addAll(this.points);
		state.put(StylesFactory.BrushType.FUR, points);
	}

	@SuppressWarnings("unchecked")
	public void restoreState(Map<StylesFactory.BrushType, Object> state) {
		this.points.clear();
		ArrayList<PointF> points = (ArrayList<PointF>) state
				.get(StylesFactory.BrushType.FUR);
		this.points.addAll(points);
	}
}
