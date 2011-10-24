package org.sketchertab;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	public AboutDialog(Context context) {
		super(context);

		setTitle(R.string.about_title);
		setContentView(R.layout.about);

        TextView aboutTextView = (TextView) findViewById(R.id.about_text_view);
        aboutTextView.setText(Html.fromHtml(context.getResources().getString(R.string.about_text)));
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());

		Button closeButton = (Button) findViewById(R.id.button_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
