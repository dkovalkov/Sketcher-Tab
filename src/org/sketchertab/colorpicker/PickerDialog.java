package org.sketchertab.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.sketchertab.R;

public class PickerDialog extends Dialog {
	private Picker.OnColorChangedListener mListener;
	private final Paint mPaint;
    private final int alpha;

	public PickerDialog(Context context, Picker.OnColorChangedListener listener, int initialColor) {
		super(context);

		mListener = listener;
		mPaint = new Paint();
        mPaint.setColor(initialColor);
        alpha = mPaint.getAlpha();
        mPaint.setAlpha(255);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.pick_a_color);
		setContentView(R.layout.color_picker);

		final PreviewView previewView = (PreviewView) findViewById(R.id.preview_new);
		previewView.setPaint(mPaint);

		final SatValPicker satValPicker = (SatValPicker) findViewById(R.id.satval_picker);

		Picker.OnColorChangedListener satValLstr = new Picker.OnColorChangedListener() {
			public void colorChanged(int color) {
				previewView.setColor(color);
				mPaint.setColor(color);
			}
		};
		satValPicker.setOnColorChangedListener(satValLstr);
		satValPicker.setColor(mPaint.getColor());

		Picker huePicker = (Picker) findViewById(R.id.hue_picker);
		Picker.OnColorChangedListener hueLstr = new Picker.OnColorChangedListener() {
			public void colorChanged(int color) {
				satValPicker.setHue(color);
				previewView.setColor(satValPicker.getColor());
				mPaint.setColor(satValPicker.getColor());
			}
		};
		huePicker.setOnColorChangedListener(hueLstr);
		huePicker.setColor(mPaint.getColor());

		Button acceptButton = (Button) findViewById(R.id.picker_button_accept);
		acceptButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                mPaint.setAlpha(alpha);
				mListener.colorChanged(mPaint.getColor());
				dismiss();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.picker_button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}