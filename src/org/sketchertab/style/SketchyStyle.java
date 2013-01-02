package org.sketchertab.style;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Map;

class SketchyStyle extends StyleBrush {
    private float prevX;
    private float prevY;
    private float density;

    private ArrayList<PointF> points = new ArrayList<PointF>();

    {
        paint.setAntiAlias(true);
    }

    SketchyStyle(float density) {
        this.density = density;
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

            float maxLength = 4000 * density;
            if (length < maxLength && Math.random() > (length / maxLength / 2)) {
                float ddx = dx * 0.2F;
                float ddy = dy * 0.2F;
                c.drawLine(current.x + ddx, current.y + ddy, point.x - ddx, point.y - ddy, paint);
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
        state.put(StylesFactory.BrushType.SKETCHY, points);
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Map<StylesFactory.BrushType, Object> state) {
        this.points.clear();
        ArrayList<PointF> points = (ArrayList<PointF>) state
                .get(StylesFactory.BrushType.SKETCHY);
        this.points.addAll(points);
    }
}
