package org.sketchertab;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class FileHelper {
	private static final String FILENAME_PATTERN = "sketch_%04d.png";
    private static final String CUR_FILE_NUM = "cur_file_num";

	private final Sketcher context;
	boolean isSaved = false;

	FileHelper(Sketcher context) {
		this.context = context;
	}

	private File getSDDir() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/sketcher_tab/";

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

	Bitmap getSavedBitmap(String fileName) {
		if (!isStorageAvailable()) {
			return null;
		}

		File lastFile = new File(fileName);
		if (!lastFile.exists()) {
			return null;
		}

		Bitmap savedBitmap;

		try {
			FileInputStream fis = new FileInputStream(lastFile);
			savedBitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return savedBitmap;
	}

	private String getUniqueFilePath(File dir) {
        SharedPreferences preferences = context.getSharedPreferences(Sketcher.PREFS_NAME, Context.MODE_PRIVATE);
        int curFileNum = preferences.getInt(CUR_FILE_NUM, 0);

        int freeFileNum = findFreeFileNum(curFileNum + 1, dir);
        preferences.edit().putInt(CUR_FILE_NUM, freeFileNum).apply();

        return new File(dir, String.format(FILENAME_PATTERN, freeFileNum)).getAbsolutePath();
    }

    private int findFreeFileNum(int fileNum, File dir) {
        int result = fileNum;
        if (new File(dir, String.format(FILENAME_PATTERN, fileNum)).exists()) {
            result = findFreeFileNum(fileNum + 1, dir);
        }
        return result;
    }

	private void saveBitmap(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			Bitmap bitmap = context.getSurface().getBitmap();
			if (bitmap == null) {
				return;
			}
			bitmap.compress(CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isStorageAvailable() {
		String externalStorageState = Environment.getExternalStorageState();
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.sd_card_is_not_available, Toast.LENGTH_SHORT).show();
            return false;
        }
		return true;
	}

	void share() {
		if (!isStorageAvailable()) {
			return;
		}

		new SaveTask() {
			protected void onPostExecute(File file) {
				isSaved = true;
				Uri uri = Uri.fromFile(file);

				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("image/png");
				i.putExtra(Intent.EXTRA_STREAM, uri);
				context.startActivity(Intent.createChooser(i,
						context.getString(R.string.send_image_to)));

				super.onPostExecute(file);
			}
		}.execute();
	}

	void saveToSD() {
		if (!isStorageAvailable()) {
			return;
		}
		new SaveTask().execute();
	}

	File saveBitmap(String fileName) {
        File newFile = new File(fileName);
		saveBitmap(newFile);
		notifyMediaScanner(newFile);
		return newFile;
	}

	private void notifyMediaScanner(File file) {
		Uri uri = Uri.fromFile(file);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
	}

	private class SaveTask extends AsyncTask<Void, Void, File> {
		private ProgressDialog dialog = ProgressDialog.show(context, "",
				context.getString(R.string.saving_to_sd_please_wait), true);

		protected File doInBackground(Void... none) {
			context.getSurface().getDrawThread().pauseDrawing();
			return saveBitmap(getUniqueFilePath(getSDDir()));
		}

		protected void onPostExecute(File file) {
			dialog.hide();
			String absolutePath = file.getAbsolutePath();
			Toast.makeText(context, context.getString(R.string.successfully_saved_to, absolutePath),
					Toast.LENGTH_LONG).show();
			context.getSurface().getDrawThread().resumeDrawing();
		}
	}

}
