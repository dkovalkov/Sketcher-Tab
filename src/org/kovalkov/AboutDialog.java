package org.sketcher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends Dialog {
	private static final String MARKET_URL = "market://details?id=org.ru.kovalkov.kovalkov.pro";

	public AboutDialog(Context context) {
		super(context);

		setTitle(R.string.about_title);
		setContentView(R.layout.about);

		Button buyButton = (Button) findViewById(R.id.about_buy);
		buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.VIEW", Uri
						.parse(MARKET_URL));
				getContext().startActivity(intent);
			}
		});

		Button closeButton = (Button) findViewById(R.id.button_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
