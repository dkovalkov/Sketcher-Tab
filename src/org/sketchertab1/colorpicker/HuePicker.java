package org.sketchertab.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HuePicker extends View implements Picker {
	private static final float PICKER_RADIUS = 4;
	private static final float[] MARGIN = new float[] { 5, 0, 0, 0 };

	private Picker.OnColorChangedListener mListener = null;

	private final int[] mColors;
	private final Paint mColor = new Paint();
	private final Paint mGradient = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mTrackerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public HuePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
				0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };

		mGradient.setStyle(Paint.Style.FILL);

		mTrackerPaint.setStrokeWidth(1);
		mTrackerPaint.setColor(Color.WHITE);
		mTrackerPaint.setStyle(Style.STROKE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Shader s = new LinearGradient(0, 0, 0, h, mColors, null,
				Shader.TileMode.CLAMP);

		mGradient.setShader(s);
		mGradient.setStrokeWidth(w);
	}

	@Override
	public void setColor(int color) {
		mColor.setColor(color);
	}

	@Override
	public void setOnColorChangedListener(Picker.OnColorChangedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(PICKER_RADIUS + MARGIN[0], MARGIN[1], getWidth()
				- PICKER_RADIUS - MARGIN[2], getHeight() - MARGIN[3], mGradient);

		float[] hsv = Utils.color2HSV(mColor.getColor());
		float hue = hsv[0];
		float y = hueToOffset(hue);

		RectF rect = new RectF();
		rect.left = MARGIN[0];
		rect.top = y - PICKER_RADIUS + MARGIN[1];
		rect.right = getWidth() - MARGIN[2];
		rect.bottom = y + PICKER_RADIUS - MARGIN[3];

		canvas.drawRoundRect(rect, PICKER_RADIUS, PICKER_RADIUS, mTrackerPaint);
	}

	private float hueToOffset(float hue) {
		final float height = getHeight();

		return height - (hue * height / 360f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float y = event.getY() / getHeight();
			setColor(interpColor(mColors, y));

			mListener.colorChanged(mColor);
			return true;
		}

		return false;
	}

	private int interpColor(int colors[], float unit) {
		if (unit <= 0) {
			return colors[0];
		}
		if (unit >= 1) {
			return colors[colors.length - 1];
		}

		float p = unit * (colors.length - 1);
		int i = (int) p;
		p -= i;

		// now p is just the fractional part [0...1) and i is the index
		int c0 = colors[i];
		int c1 = colors[i + 1];
		int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		int r = ave(Color.red(c0), Color.red(c1), p);
		int g = ave(Color.green(c0), Color.green(c1), p);
		int b = ave(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}

	private int ave(int s, int d, float p) {
		return s + java.lang.Math.round(p * (d - s));
	}
}
