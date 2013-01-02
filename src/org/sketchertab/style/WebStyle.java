package org.sketchertab.style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Map;

class WebStyle extends StyleBrush {
    private float prevX;
    private float prevY;
    private float density;

    private ArrayList<PointF> points = new ArrayList<PointF>();

    {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
    }

    WebStyle(float density) {
        this.density = density;
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

            float maxLength = 2500 * density * density;
            if (length < maxLength && Math.random() > 0.9) {
                c.drawLine(current.x, current.y, point.x, point.y, paint);
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

    public void saveState(Map<StylesFactory.BrushType, Object> state) {
        ArrayList<PointF> points = new ArrayList<PointF>();
        points.addAll(this.points);
        state.put(StylesFactory.BrushType.WEB, points);
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Map<StylesFactory.BrushType, Object> state) {
        this.points.clear();
        ArrayList<PointF> points = (ArrayList<PointF>) state.get(StylesFactory.BrushType.WEB);
        this.points.addAll(points);
    }
}
