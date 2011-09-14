package org.sketcher.colorpicker;

import android.graphics.Paint;

public interface Picker {
	public interface OnColorChangedListener {
		void colorChanged(Paint paint);
	}

	void setOnColorChangedListener(OnColorChangedListener listener);

	void setColor(int color);
}
