package org.sketchertab;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutDialog extends Dialog {
    public AboutDialog(Context context) {
        super(context);

        setTitle(R.string.about_title);
        setContentView(R.layout.about);

        TextView aboutTextView = (TextView) findViewById(R.id.about_text_view);
        aboutTextView.setText(Html.fromHtml(context.getResources().getString(R.string.about_text)));
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
