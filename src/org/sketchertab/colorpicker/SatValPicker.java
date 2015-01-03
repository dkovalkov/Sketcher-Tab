package org.sketchertab.colorpicker;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
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
		float denst = getResources().getDisplayMetrics().density;
		mTrackerPaint.setStrokeWidth(denst);
		mTrackerPaint.setColor(Color.WHITE);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		applyChanges(w, h);
	}

	private void applyChanges(int w, int h) {
		Shader valGradient = new LinearGradient(0, 0, 0, h, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);

		int rgb = Color.HSVToColor(new float[]{mHue, 1, 1});
		Shader satGradient = new LinearGradient(0, 0, w, 0, Color.WHITE, rgb, Shader.TileMode.CLAMP);

		ComposeShader shader = new ComposeShader(valGradient, satGradient, PorterDuff.Mode.MULTIPLY);

		mGradient.setShader(shader);
		invalidate();
	}

    public void setColor(int color) {
        float[] hsv = Utils.color2HSV(color);
		mHue = hsv[0];

        mColor.setColor(color);
        applyChanges(getWidth(), getHeight());

    }

	public void setHue(int color) {
		float[] hsv = Utils.color2HSV(color);
		mHue = hsv[0];

        int curColor = mColor.getColor();
        float[] curHsv = Utils.color2HSV(curColor);
        curHsv[0] = mHue;

        mColor.setColor(Color.HSVToColor(curHsv));

		applyChanges(getWidth(), getHeight());
	}

    public int getColor() {
        return mColor.getColor();
    }

	public void setOnColorChangedListener(Picker.OnColorChangedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPaint(mGradient);

		float[] hsv = Utils.color2HSV(mColor.getColor());

		float x = hsv[1] * getWidth();
		float y = (1 - hsv[2]) * getHeight();
		float denst = getResources().getDisplayMetrics().density;
		float pickerRadius = 4 * denst;
		canvas.drawCircle(x, y, pickerRadius, mTrackerPaint);
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

			mListener.colorChanged(mColor.getColor());
			invalidate();
			return true;
		}

		return false;
	}
}
