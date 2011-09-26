package org.sketchertab.colorpicker;

import org.sketchertab.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PickerDialog extends Dialog {
	private Picker.OnColorChangedListener mListener;
	private final Paint mPaint;

	public PickerDialog(Context context, Picker.OnColorChangedListener listener, Paint initialPaint) {
		super(context);

		mListener = listener;
		mPaint = new Paint(initialPaint);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.pick_a_color);
		setContentView(R.layout.color_picker);

		final PreviewView previewView = (PreviewView) findViewById(R.id.preview_new);
		previewView.setPaint(mPaint);

		final Picker satValPicker = (Picker) findViewById(R.id.satval_picker);
        final Picker alphaPicker = (Picker) findViewById(R.id.alpha_picker);

		Picker.OnColorChangedListener satValLstr = new Picker.OnColorChangedListener() {
			public void colorChanged(Paint paint) {
                alphaPicker.setColor(paint.getColor());
				previewView.setColor(paint.getColor());
				mPaint.setColor(paint.getColor());
			}
		};
		satValPicker.setOnColorChangedListener(satValLstr);
		satValPicker.setColor(mPaint.getColor());

		Picker huePicker = (Picker) findViewById(R.id.hue_picker);
		Picker.OnColorChangedListener hueLstr = new Picker.OnColorChangedListener() {
			public void colorChanged(Paint paint) {
				satValPicker.setColor(paint.getColor());
				previewView.setColor(paint.getColor());
				mPaint.setColor(paint.getColor());
			}
		};
		huePicker.setOnColorChangedListener(hueLstr);
		huePicker.setColor(mPaint.getColor());


		Button acceptButton = (Button) findViewById(R.id.picker_button_accept);
		acceptButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mListener.colorChanged(mPaint);
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