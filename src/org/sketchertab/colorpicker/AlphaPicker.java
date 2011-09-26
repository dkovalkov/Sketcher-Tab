package org.sketchertab.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AlphaPicker extends View implements Picker {
	private static final float PICKER_RADIUS = 4;

	private Picker.OnColorChangedListener mListener = null;
	private final Paint mColor = new Paint();
	private float[] mHsv = new float[3];
	private final Paint mGradient = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mTrackerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public AlphaPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGradient.setStyle(Paint.Style.STROKE);

		mTrackerPaint.setStrokeWidth(1);
		mTrackerPaint.setColor(Color.WHITE);
		mTrackerPaint.setStyle(Style.STROKE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		applyChages(w, h);
	}

	public void setColor(int color) {
		mColor.setColor(color);
		mHsv = Utils.color2HSV(color);
		applyChages(getWidth(), getHeight());
	}

	private void applyChages(int w, int h) {
		int color = Color.HSVToColor(255, mHsv);
		int acolor = Color.HSVToColor(0, mHsv);

		Shader alphaShader = new LinearGradient(0, 0, w, 0, color, acolor,
				TileMode.CLAMP);

		mGradient.setShader(alphaShader);
		mGradient.setStrokeWidth(h);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float x = Math.max(0, Math.min(getWidth(), event.getX()));
			int alpha = (int) ((1f - x / getWidth()) * 255f);
			mColor.setColor(Color.HSVToColor(alpha, mHsv));

			mListener.colorChanged(mColor);
			invalidate();
			return true;
		}

		return false;
	}

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(0, PICKER_RADIUS, getWidth(), getHeight()
				- PICKER_RADIUS, mGradient);

		float x = (1f - mColor.getAlpha() / 255f) * getWidth();

		RectF rect = new RectF();
		rect.left = x - PICKER_RADIUS;
		rect.right = x + PICKER_RADIUS;
		rect.top = 0;
		rect.bottom = getHeight();

		canvas.drawRoundRect(rect, PICKER_RADIUS, PICKER_RADIUS, mTrackerPaint);
	}
}
