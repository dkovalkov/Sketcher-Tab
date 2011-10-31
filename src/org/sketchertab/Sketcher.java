package org.sketchertab;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import org.sketchertab.colorpicker.Picker;
import org.sketchertab.colorpicker.PickerDialog;
import org.sketchertab.style.StyleBrush;
import org.sketchertab.style.StylesFactory;

import java.io.File;
import java.util.HashMap;

public class Sketcher extends Activity {
    private static final String PREF_OPACITY = "cur_opacity";
    private static final String PREF_STYLE = "cur_style";
    private static final String PREF_COLOR = "cur_color";
    private static final String PREF_BG_COLOR = "cur_background_color";
    private static final String PREF_STROKE_WIDTH = "cur_stroke_width";
    private static final float MAX_STROKE_WIDTH = 4;
    private static final float MAX_OPACITY = 255;
    private static final String TEMP_FILE_NAME = "current_pic.png";
    public static final String PREFS_NAME = "preferences";


    private static final HashMap<Integer, Integer> StyleButtonIdMap = new HashMap<Integer, Integer>();

	private Surface surface;
    private final FileHelper fileHelper = new FileHelper(this);
    private View selectedBrushButton;
    private View backgroundPickerButton;
    private View foregroundPickerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        StyleButtonIdMap.put(StylesFactory.SKETCHY, R.id.brush_sketchy);
        StyleButtonIdMap.put(StylesFactory.SHADED, R.id.brush_shaded);
        StyleButtonIdMap.put(StylesFactory.FUR, R.id.brush_fur);
        StyleButtonIdMap.put(StylesFactory.WEB, R.id.brush_web);
		StyleButtonIdMap.put(StylesFactory.CIRCLES, R.id.brush_circles);
		StyleButtonIdMap.put(StylesFactory.RIBBON, R.id.brush_ribbon);
        StyleButtonIdMap.put(StylesFactory.SIMPLE, R.id.brush_simple);

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.action_bar));

		setContentView(R.layout.main);
		surface = (Surface) findViewById(R.id.surface);

        restoreFromPrefs();
        initButtons();
        initStyle();
        initSliders();
	}


    private void initButtons() {
        for (int styleId : StyleButtonIdMap.keySet()) {
            brushButtonOnClick(StyleButtonIdMap.get(styleId), styleId);
        }

        backgroundPickerButton = findViewById(R.id.background_picker_button);
        backgroundPickerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new PickerDialog(Sketcher.this, new Picker.OnColorChangedListener() {
                    public void colorChanged(int color) {
                        getSurface().setBackgroundColor(color);
                        backgroundPickerButton.setBackgroundColor(color);
                    }
                }, getSurface().getBackgroundColor()).show();
            }
        });

        foregroundPickerButton = findViewById(R.id.foreground_picker_button);
        foregroundPickerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new PickerDialog(Sketcher.this, new Picker.OnColorChangedListener() {
                    public void colorChanged(int color) {
                        getSurface().setPaintColor(color);
                        foregroundPickerButton.setBackgroundColor(color);
                    }
                }, getSurface().getPaintColor()).show();
            }
        });

        View menuSwitcherImage = findViewById(R.id.menu_switcher);
        menuSwitcherImage.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                switchToolbars();
            }
        });
    }

    private void initSliders() {
        SeekBar opacityBar = (SeekBar) findViewById(R.id.brush_opacity_bar);
        opacityBar.setProgress((int) (surface.getOpacity() / MAX_OPACITY * 100));

        SeekBar sizeBar = (SeekBar) findViewById(R.id.brush_size_bar);
        sizeBar.setProgress((int) (surface.getStrokeWidth() / MAX_STROKE_WIDTH * 100));

        opacityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                surface.setOpacity((int) (i * MAX_OPACITY / 100));
                foregroundPickerButton.setBackgroundColor(surface.getPaintColor());
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

    private void initStyle() {
        selectedBrushButton = findViewById(StyleButtonIdMap.get(StylesFactory.getCurrentStyleId()));
        selectedBrushButton.setSelected(true);
        backgroundPickerButton.setBackgroundColor(surface.getBackgroundColor());
        foregroundPickerButton.setBackgroundColor(surface.getPaintColor());
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

    private void restoreFromPrefs() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        surface.setOpacity(preferences.getInt(PREF_OPACITY, Controller.DEFAULT_OPACITY));
        surface.setStrokeWidth(preferences.getFloat(PREF_STROKE_WIDTH, Controller.DEFAULT_WIDTH));
        surface.setPaintColor(preferences.getInt(PREF_COLOR, Controller.DEFAULT_COLOR));
        surface.setBackgroundColor(preferences.getInt(PREF_BG_COLOR, Controller.INIT_BG_COLOR));
        surface.setStyle(StylesFactory.getStyle(preferences.getInt(PREF_STYLE, StylesFactory.DEFAULT_STYLE)));
    }

	@Override
	protected void onPause() {
		super.onPause();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(PREF_OPACITY, surface.getOpacity())
                .putFloat(PREF_STROKE_WIDTH, surface.getStrokeWidth())
                .putInt(PREF_COLOR, surface.getPaintColor())
                .putInt(PREF_BG_COLOR, surface.getBackgroundColor())
                .putInt(PREF_STYLE, StylesFactory.getCurrentStyleId()).apply();

//        if (fileHelper.isSaved) {
//			return;
//		}
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
//  Ввиду утечки памяти требуется доработка Отмены
//            case R.id.menu_undo:
//                getSurface().undo();
//                return true;
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
