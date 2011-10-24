package org.sketchertab.style;

import java.util.HashMap;

import android.graphics.Canvas;

class RibbonStyle extends StyleBrush {
    private static final int LINE_NUM = 50;
	private Painter[] paintPool = new Painter[LINE_NUM];

	private float x;
	private float y;

    @Override
    public void setOpacity(int opacity) {
        super.setOpacity((int) (opacity * 0.25f));
    }


	{
		paint.setAntiAlias(true);

		for (int i = 0; i < LINE_NUM; i++) {
			paintPool[i] = new Painter();
		}
	}

	public void draw(Canvas c) {
		float startX;
		float startY;
        for (Painter painter : paintPool) {
            startX = painter.dx;
            startY = painter.dy;
            painter.dx -= painter.ax = (painter.ax + (painter.dx - x) * painter.div)
                    * painter.ease;
            painter.dy -= painter.ay = (painter.ay + (painter.dy - y) * painter.div)
                    * painter.ease;
            c.drawLine(startX, startY, painter.dx, painter.dy, paint);
        }
	}

	public void stroke(Canvas c, float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void strokeStart(float x, float y) {
		this.x = x;
		this.y = y;

		for (int i = 0, max = paintPool.length; i < max; i++) {
			paintPool[i].dx = x;
			paintPool[i].dy = y;
		}
	}

	public void saveState(HashMap<Integer, Object> state) {
	}

	public void restoreState(HashMap<Integer, Object> state) {
	}

    private class Painter {
        private int screenWidth;
        private int screenHeight;

        float dx;
        float dy;
        float ax = 0;
        float ay = 0;
        float div = 0.1F;
        float ease = (float) (Math.random() * 0.2 + 0.6);

        public Painter() {
//            Point size = new Point();
//            Display.getSize(size);
            screenWidth = 1280;
            screenHeight = 752;
            dx = screenWidth / 2;
            dy = screenHeight / 2;
        }
    }

}
