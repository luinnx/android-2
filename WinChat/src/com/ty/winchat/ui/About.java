package com.ty.winchat.ui;

import com.ty.winchat.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class About extends Activity{
	WebView webAboutGodTalk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		webAboutGodTalk=(WebView)findViewById(R.id.webAbout);
		webAboutGodTalk.loadUrl("file:///android_asset/about.html");
	}
}