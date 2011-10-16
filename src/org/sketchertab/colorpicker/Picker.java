package org.sketchertab.colorpicker;

public interface Picker {
	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	void setOnColorChangedListener(OnColorChangedListener listener);

	void setColor(int color);
}
