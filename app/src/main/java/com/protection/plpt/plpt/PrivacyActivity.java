package com.protection.plpt.plpt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by sergii on 6/16/17.
 */

public class PrivacyActivity extends BaseActivity {

  public static void start(Context context) {
    context.startActivity(new Intent(context, PrivacyActivity.class));
  }

  private WebView mWebView = null;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.privacy_layout);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mWebView = (WebView) findViewById(R.id.privacy_web_view);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.loadUrl(getString(R.string.privacy_url));
  }
}
