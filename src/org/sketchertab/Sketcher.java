package org.sketchertab;

import android.app.ActionBar;
import android.graphics.Color;
import android.view.*;
import android.widget.ImageButton;
import android.widget.SeekBar;
import org.sketchertab.colorpicker.Picker;
import org.sketchertab.colorpicker.PickerDialog;
import org.sketchertab.style.StyleBrush;
import org.sketchertab.style.StylesFactory;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.util.Log;

import java.io.File;

public class Sketcher extends Activity {
	public static final String PREFS_NAME = "preferences";
    private static final float MAX_STROKE_WIDTH = 4;
    private static final float MAX_OPACITY = 255;
    private static final String TEMP_FILE_NAME = "current_pic.png";

	private Surface surface;
	private final FileHelper fileHelper = new FileHelper(this);
    private View selectedBrushButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.action_bar));

		setContentView(R.layout.main);
		surface = (Surface) findViewById(R.id.surface);

        brushButtonOnClick(R.id.brush_sketchy, StylesFactory.SKETCHY);
        brushButtonOnClick(R.id.brush_shaded, StylesFactory.SHADED);
        brushButtonOnClick(R.id.brush_chrome, StylesFactory.CHROME);
        brushButtonOnClick(R.id.brush_fur, StylesFactory.FUR);
        brushButtonOnClick(R.id.brush_web, StylesFactory.WEB);

        SeekBar opacityBar = (SeekBar) findViewById(R.id.brush_opacity_bar);
        opacityBar.setProgress((int) (surface.getBrushProperties().opacity / MAX_OPACITY * 100));

        SeekBar sizeBar = (SeekBar) findViewById(R.id.brush_size_bar);
        sizeBar.setProgress((int) (surface.getBrushProperties().width / MAX_STROKE_WIDTH * 100));

        opacityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                surface.setOpacity((int) (i * MAX_OPACITY / 100));
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                surface.setStrokeWidth((float) i / 100 * MAX_STROKE_WIDTH);
            }

            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onStopTrackingTouch(SeekBar seekBar) { }
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
                StyleBrush styleBrush = StylesFactory.getStyle(brushStyle);
                getSurface().setStyle(styleBrush);
            }
        });
    }

    public void switchToolbars() {
        View brushToolbar = findViewById(R.id.brush_toolbar);
        View brushProperties = findViewById(R.id.brush_property);
        ActionBar actionBar = getActionBar();

        if (actionBar.isShowing()) {
            actionBar.hide();
            brushToolbar.setVisibility(View.GONE);
            brushProperties.setVisibility(View.GONE);
        } else {
            actionBar.show();
            brushToolbar.setVisibility(View.VISIBLE);
            brushProperties.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

	@Override
	protected void onPause() {
		super.onPause();

		if (fileHelper.isSaved) {
			return;
		}
		// wrapped to a new thread since it can be killed due to time limits for
		// #onPause() method
		new Thread() {
			@Override
			public void run() {
                String tempFileName = getExternalFilesDir(null) + File.separator + TEMP_FILE_NAME;
				fileHelper.saveBitmap(tempFileName);
			}
		}.run();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fileHelper.isSaved = false;
        String tempFileName = getExternalFilesDir(null) + File.separator + TEMP_FILE_NAME;
		getSurface().setInitialBitmap(fileHelper.getSavedBitmap(tempFileName));
	}

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
//                        surface.getBrushProperties().color = color.getColor();
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

}
