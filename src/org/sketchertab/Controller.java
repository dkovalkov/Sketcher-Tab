package org.sketchertab;

import org.sketchertab.style.StylesFactory;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class Controller implements View.OnTouchListener {
	private Style style;
	private final Canvas mCanvas;
	private boolean toDraw = false;
	private Paint mColor = new Paint();

	public Controller(Canvas canvas) {
		clear();
		mCanvas = canvas;
	}

	public void draw() {
		if (toDraw) {
			style.draw(mCanvas);
		}
	}

	public void setStyle(Style style) {
		toDraw = false;
		style.setColor(mColor.getColor());
        style.setOpacity(mColor.getAlpha());
        style.setStrokeWidth(mColor.getStrokeWidth());
		this.style = style;
	}

    public Style getStyle() {
        return style;
    }

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			toDraw = true;
			style.strokeStart(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			style.stroke(mCanvas, event.getX(), event.getY());
			break;
		}
		return true;
	}

	public void clear() {
		toDraw = false;
		StylesFactory.clearCache();
		setStyle(StylesFactory.getCurrentStyle());
	}

	public void setPaintColor(Paint color) {
		mColor.setColor(color.getColor());
		style.setColor(color.getColor());
	}

	public Paint getPaintColor() {
		return mColor;
	}

    public void setOpacity(int opacity) {
        mColor.setAlpha(opacity);
        style.setOpacity(opacity);
    }

    public void setStrokeWidth(float width) {
        mColor.setStrokeWidth(width);
        style.setStrokeWidth(width);
    }
}
