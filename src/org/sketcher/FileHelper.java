package org.sketcher;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class FileHelper {
	private static final String FILENAME_PATTERN = "sketch_%04d.png";

	private final Sketcher context;
	boolean isSaved = false;

	FileHelper(Sketcher context) {
		this.context = context;
	}

	private File getSDDir() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/sketcher/";

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

//	Bitmap getSavedBitmap() {
//		if (!isStorageAvailable()) {
//			return null;
//		}
//
//		File lastFile = getLastFile(getSDDir());
//		if (lastFile == null) {
//			return null;
//		}
//
//		Bitmap savedBitmap = null;
//		try {
//			FileInputStream fis = new FileInputStream(lastFile);
//			savedBitmap = BitmapFactory.decodeStream(fis);
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		}
//		return savedBitmap;
//	}

//	File getLastFile(File dir) {
//		int suffix = 1;
//
//		File newFile = null;
//		File file = null;
//		do {
//			file = newFile;
//			newFile = new File(dir, String.format(FILENAME_PATTERN, suffix));
//			suffix++;
//		} while (newFile.exists());
//
//		return file;
//	}

	private File getUniqueFilePath(File dir) {

//		while (new File(dir, String.format(FILENAME_PATTERN, suffix)).exists()) {
//			suffix++;
//		}

        File[] sketchList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                if (s.matches("^sketch_(\\d\\d\\d\\d)\\.png$"))
                    return true;
               return false;
            }
        });
        Pattern p = Pattern.compile("^sketch_(\\d\\d\\d\\d)\\.png$");
        int lastNum = 0;

        for (int i = 0; i < sketchList.length; i += 1) {
            Matcher m = p.matcher(sketchList[i].getName());
            if (m.find()) {
                int fileNum = Integer.valueOf(m.group(1));
                if (fileNum > lastNum) {
                    lastNum = fileNum;
                }
            }
        }

        lastNum += 1;
		return new File(dir, String.format(FILENAME_PATTERN, lastNum));
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
			Toast.makeText(context, R.string.sd_card_is_not_available,
					Toast.LENGTH_SHORT).show();
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

	File saveBitmap() {
		File newFile = getUniqueFilePath(getSDDir());
		saveBitmap(newFile);
		notifyMediaScanner(newFile);
		return newFile;
	}

	private void notifyMediaScanner(File file) {
		Uri uri = Uri.fromFile(file);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				uri));
	}

	private class SaveTask extends AsyncTask<Void, Void, File> {
		private ProgressDialog dialog = ProgressDialog.show(context, "",
				context.getString(R.string.saving_to_sd_please_wait), true);

		protected File doInBackground(Void... none) {
			context.getSurface().getDrawThread().pauseDrawing();
			return saveBitmap();
		}

		protected void onPostExecute(File file) {
			dialog.hide();

			String absolutePath = file.getAbsolutePath();
//			String sdPath = Environment.getExternalStorageDirectory()
//					.getAbsolutePath();
//			String beautifiedPath = absolutePath.replace(sdPath, "SD:/");
			
			Toast.makeText(context, context.getString(R.string.successfully_saved_to, absolutePath),
					Toast.LENGTH_LONG).show();

			context.getSurface().getDrawThread().resumeDrawing();
		}
	}

}
