package org.sketcher.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class PreviewView extends View {
	private Paint mPaint = new Paint();
	private Paint mOldPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	public PreviewView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mBorderPaint.setColor(Color.GRAY);
		mBorderPaint.setStrokeWidth(1);
		mBorderPaint.setStyle(Style.STROKE);
	}

	public void setPaint(Paint paint) {
		mPaint = paint;
		mOldPaint = new Paint(paint);
		invalidate();
	}

	public void setColor(int color) {
		mPaint.setColor(color);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();

		canvas.drawRect(0, 0, width / 2, height, mOldPaint);
		canvas.drawRect(width / 2, 0, width, height, mPaint);
		canvas.drawRect(0, 0, width - 1, height - 1, mBorderPaint);
	}
}
