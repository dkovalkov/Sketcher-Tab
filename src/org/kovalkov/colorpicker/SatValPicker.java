package org.sketcher.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SatValPicker extends View implements Picker {
	private Picker.OnColorChangedListener mListener = null;
	private final Paint mColor = new Paint();
	private final Paint mGradient = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mTrackerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private float mHue = 0;

	public SatValPicker(Context context, AttributeSet attrs) {
		super(context, attrs);

		mGradient.setStyle(Paint.Style.STROKE);
		mGradient.setStrokeWidth(0);

		mTrackerPaint.setStyle(Style.STROKE);
		mTrackerPaint.setStrokeWidth(1);
		mTrackerPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		applyChages(w, h);
	}

	private void applyChages(int w, int h) {
		Shader valGradient = new LinearGradient(0, 0, 0, h, Color.WHITE,
				Color.BLACK, Shader.TileMode.CLAMP);

		int rgb = Color.HSVToColor(new float[] { mHue, 1f, 1f });
		Shader satGradient = new LinearGradient(0, 0, w, 0, Color.WHITE, rgb,
				Shader.TileMode.CLAMP);

		ComposeShader shader = new ComposeShader(valGradient, satGradient,
				PorterDuff.Mode.MULTIPLY);

		mGradient.setShader(shader);
		invalidate();
	}

	@Override
	public void setColor(int color) {
		mColor.setColor(color);

		float[] hsv = Utils.color2HSV(color);

		mHue = hsv[0];

		applyChages(getWidth(), getHeight());
	}

	@Override
	public void setOnColorChangedListener(Picker.OnColorChangedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPaint(mGradient);

		float[] hsv = Utils.color2HSV(mColor.getColor());

		float x = hsv[1] * getWidth();
		float y = (1 - hsv[2]) * getHeight();
		canvas.drawCircle(x, y, 4, mTrackerPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float sat = event.getX() / getWidth();
			float val = 1 - event.getY() / getHeight();

			mColor.setColor(Color.HSVToColor(0xFF,
					new float[] { mHue, sat, val }));

			mListener.colorChanged(mColor);
			invalidate();
			return true;
		}

		return false;
	}
}
