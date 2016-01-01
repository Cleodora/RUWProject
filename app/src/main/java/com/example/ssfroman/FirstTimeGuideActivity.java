package com.example.ssfroman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;


public class FirstTimeGuideActivity extends Activity {

	final String helpText = "<!DOCTYPE html>\n" +
			"\n" +
			"<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
			"<head>\n" +
			"\t<meta charset=\"utf-8\" />\n" +
			"\t<title></title>\n" +
			"</head>\n" +
			"<body style=\"background-color:#0099cc\">\n" +
			"\t<h1>User Guide</h1>\n" +
			"\t<h2>Spam Filtering</h2>\n" +
			"\t<p>To add spam SMS in to the spam folder tap the \"Specify spam SMS\" option. The application will take you to your inbox. Select the SMS and mark as spam.</p>\n" +
			"<p>Psst: The more SMS you add in the spam folder, better the result. If you have a problem with a specific number use our \"Block number\" feature<p>\n" +
			"\t<h2>Block number</h2>\n" +
			"\t<p>To add a number to your block list, tap the \"Block number\" option. You can specify the number manually or from your contact list.</p>\n" +
			"</body>\n" +
			"</html>";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_first_time_guide);


		WebView webView = (WebView) findViewById(R.id.webView);
		Button button = (Button) findViewById(R.id.button);

		webView.loadData(helpText, "text/html", "UTF-8");

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
