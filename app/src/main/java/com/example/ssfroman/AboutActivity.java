package com.example.ssfroman;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.WebView;


public class AboutActivity extends ActionBarActivity {


	final String helpText = "<!DOCTYPE html>\n" +
			"\n" +
			"<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
			"<head>\n" +
			"    <meta charset=\"utf-8\" />\n" +
			"    <title></title>\n" +
			"</head>\n" +
			"<body style=\"background-color:black; color:white\">\n" +
			"\t<h1>About</h1>\n" +
			"\t<h2>Smart SMS Spammer v 1.0</h2>\n" +
			"\t<span>Tauqeer Hassan</span><br/><span>Zeeshan ul Hassan Dar</span><br/>\n" +
			"\t<span>Afnan Shahid</span>\n" +
			"\t<p></p>\n" +
			"\t<span>Supervised By: Mr. Ali Nasir</span><br />\n" +
			"\t<h3>NU FAST Islamabad</h3>\n" +
			"</body>\n" +
			"</html>";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_about);

		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadData(helpText, "text/html", "UTF-8");
	}
}
