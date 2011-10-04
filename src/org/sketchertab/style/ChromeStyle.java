package org.sketchertab.style;

import java.util.ArrayList;
import java.util.HashMap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class ChromeStyle extends StyleBrush {
	private float prevX;
	private float prevY;

	private ArrayList<PointF> points = new ArrayList<PointF>();

	private Paint randPaint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		randPaint.setAntiAlias(true);
	}

    public void setOpacity(int opacity) {
        super.setOpacity(opacity);
        randPaint.setAlpha(opacity);
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

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);

			dx = point.x - current.x;
			dy = point.y - current.y;

			length = dx * dx + dy * dy;

			if (length < 1000) {
				randPaint.setColor(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255),
                        (int) (Math.random() * 255)));
				float ddx = dx * 0.2F;
				float ddy = dy * 0.2F;
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
