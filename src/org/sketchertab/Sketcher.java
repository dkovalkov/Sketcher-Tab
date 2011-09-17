package org.sketchertab;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import org.sketchertab.colorpicker.Picker;
import org.sketchertab.colorpicker.PickerDialog;
import org.sketchertab.style.StylesFactory;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View.OnClickListener;

public class Sketcher extends Activity {
	private static final short GROUP_BRUSHES = 0x1000;
	private static final short MENU_CLEAR = 0x2001;
	private static final short MENU_SAVE = 0x2002;
	private static final short MENU_SHARE = 0x2003;
	private static final short MENU_COLOR = 0x2004;
	private static final short MENU_ABOUT = 0x2005;
	private static final short MENU_UNDO = 0x2006;
	private static final String PREFS_NAME = "preferences";
	private static final String KEY_CHANGELOG_VERSION_VIEWED = "lastVersionDialogShowed";

	private Surface surface;
	private final FileHelper fileHelper = new FileHelper(this);
    private View selectedBrush;

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

		try {
			// current version
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			int versionCode = packageInfo.versionCode;

			// version where changelog has been viewed
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
			int viewedChangelogVersion = settings.getInt(
					KEY_CHANGELOG_VERSION_VIEWED, 0);

			if (viewedChangelogVersion < versionCode) {
				Editor editor = settings.edit();
				editor.putInt(KEY_CHANGELOG_VERSION_VIEWED, versionCode);
				editor.commit();
				showAboutDialog();
			}
		} catch (NameNotFoundException e) {
			Log.w("Unable to get version code. Will not show changelog", e);
		}
	}

    private void brushButtonOnClick(int buttonRes, final int brushStyle) {
        ImageButton button = (ImageButton) findViewById(buttonRes);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (null != selectedBrush) {
                    selectedBrush.setSelected(false);
                }
                selectedBrush = view;
                view.setSelected(true);
                getSurface().setStyle(StylesFactory.getStyle(brushStyle));
            }
        });
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_UNDO, 0, R.string.undo).setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(0, MENU_SAVE, 0, R.string.save).setIcon(
				android.R.drawable.ic_menu_save);
		SubMenu subMenu = menu.addSubMenu(R.string.brushes).setIcon(
				android.R.drawable.ic_menu_edit);
		menu.add(0, MENU_COLOR, 0, R.string.color).setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_ABOUT, 0, R.string.about).setIcon(
				android.R.drawable.ic_menu_info_details);

		menu.add(0, MENU_SHARE, 0, R.string.send).setIcon(
				android.R.drawable.ic_menu_send);
		menu.add(0, MENU_CLEAR, 0, R.string.clear).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);

		subMenu.add(GROUP_BRUSHES, StylesFactory.ERASER, 0, R.string.eraser);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SKETCHY, 0, R.string.sketchy);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SIMPLE, 0, R.string.simple);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SHADED, 0, R.string.shaded);
		subMenu.add(GROUP_BRUSHES, StylesFactory.CHROME, 0, R.string.chrome);
		subMenu.add(GROUP_BRUSHES, StylesFactory.FUR, 0, R.string.fur);
		subMenu.add(GROUP_BRUSHES, StylesFactory.LONGFUR, 0, R.string.longfur);
		subMenu.add(GROUP_BRUSHES, StylesFactory.WEB, 0, R.string.web);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SQUARES, 0, R.string.squares);
		subMenu.add(GROUP_BRUSHES, StylesFactory.RIBBON, 0, R.string.ribbon);
		subMenu.add(GROUP_BRUSHES, StylesFactory.CIRCLES, 0, R.string.circles);
		subMenu.add(GROUP_BRUSHES, StylesFactory.GRID, 0, R.string.grid);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == GROUP_BRUSHES) {
			getSurface().setStyle(StylesFactory.getStyle(item.getItemId()));
			return true;
		}

		switch (item.getItemId()) {
		case MENU_CLEAR:
			getSurface().clearBitmap();
			return true;
		case MENU_SAVE:
			fileHelper.saveToSD();
			return true;
		case MENU_SHARE:
			fileHelper.share();
			return true;
		case MENU_ABOUT:
			showAboutDialog();
			return true;
		case MENU_COLOR:
			new PickerDialog(this, new Picker.OnColorChangedListener() {
				public void colorChanged(Paint color) {
					getSurface().setPaintColor(color);
				}
			}, getSurface().getPaintColor()).show();
			return true;
		case MENU_UNDO:
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
