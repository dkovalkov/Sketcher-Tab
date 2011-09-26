package org.sketchertab;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends Dialog {
	public AboutDialog(Context context) {
		super(context);

		setTitle(R.string.about_title);
		setContentView(R.layout.about);

		Button closeButton = (Button) findViewById(R.id.button_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
