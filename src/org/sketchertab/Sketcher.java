package org.sketchertab;

import android.view.*;
import android.widget.ImageButton;
import android.widget.SeekBar;
import org.sketchertab.colorpicker.Picker;
import org.sketchertab.colorpicker.PickerDialog;
import org.sketchertab.style.StylesFactory;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View.OnClickListener;

public class Sketcher extends Activity {
	private static final short GROUP_BRUSHES = 0x1000;
	private static final String PREFS_NAME = "preferences";
    private static final float MAX_STROKE_WIDTH = 4;

	private Surface surface;
	private final FileHelper fileHelper = new FileHelper(this);
    private View selectedBrushButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		surface = (Surface) findViewById(R.id.surface);

        brushButtonOnClick(R.id.brush_sketchy, StylesFactory.SKETCHY);
        brushButtonOnClick(R.id.brush_shaded, StylesFactory.SHADED);
        brushButtonOnClick(R.id.brush_chrome, StylesFactory.CHROME);
        brushButtonOnClick(R.id.brush_fur, StylesFactory.FUR);
        brushButtonOnClick(R.id.brush_web, StylesFactory.WEB);

        SeekBar opacityBar = (SeekBar) findViewById(R.id.brush_opacity_bar);
        SeekBar sizeBar = (SeekBar) findViewById(R.id.brush_size_bar);

        opacityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                surface.getStyle().setOpacity(i);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                surface.getStyle().setStrokeWidth((float) i / 100 * MAX_STROKE_WIDTH);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
	}

    private void brushButtonOnClick(int buttonRes, final int brushStyle) {
        ImageButton button = (ImageButton) findViewById(buttonRes);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (null != selectedBrushButton) {
                    selectedBrushButton.setSelected(false);
                }
                selectedBrushButton = view;
                view.setSelected(true);
                getSurface().setStyle(StylesFactory.getStyle(brushStyle));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

//	@Override
//	protected void onPause() {
//		super.onPause();
//
//		if (fileHelper.isSaved) {
//			return;
//		}
//		// wrapped to a new thread since it can be killed due to time limits for
//		// #onPause() method
//		new Thread() {
//			@Override
//			public void run() {
//				fileHelper.saveBitmap();
//			}
//		}.run();
//	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		fileHelper.isSaved = false;
//		getSurface().setInitialBitmap(fileHelper.getSavedBitmap());
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.menu_clear:
                getSurface().clearBitmap();
                return true;
            case R.id.menu_save:
                fileHelper.saveToSD();
                return true;
            case R.id.menu_send:
                fileHelper.share();
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            case R.id.menu_color:
                new PickerDialog(this, new Picker.OnColorChangedListener() {
                    public void colorChanged(Paint color) {
                        getSurface().setPaintColor(color);
                    }
                }, getSurface().getPaintColor()).show();
                return true;
            case R.id.menu_undo:
                getSurface().undo();
                return true;

            default:
                return false;
        }
	}

	private void showAboutDialog() {
		Dialog dialog = new AboutDialog(this);
		dialog.show();
	}

	Surface getSurface() {
		return surface;
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch(keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			getSurface().undo();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
