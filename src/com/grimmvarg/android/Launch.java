package com.grimmvarg.android;

import java.util.ArrayList;
import java.util.EventObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.RenderPriority;
import android.widget.Toast;

public class Launch extends Activity {
	WebView webView;
	Context context;
	String imageURL;
	ProgressDialog progressDialog;
	ArrayList<String> urlArray;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		 if (savedInstanceState != null){
		      ((WebView)findViewById(R.id.webView)).restoreState(savedInstanceState);
		 }
		 
		 setUpWebView();
		 //setUpWebSources();
	}

	private void setUpWebSources() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		urlArray = new ArrayList<String>();
		
		if(settings.getBoolean("Fukung", false)){
			
		}
		if(settings.getBoolean("Fatpina", false)){
			
		}
		if(settings.getBoolean("XKCD", false)){
			
		}
		
	}

	private void setUpWebView() {
		webView = (WebView) findViewById(R.id.webView);
		webView.setBackgroundColor(0);
        webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
        });
        webView.setMinimumWidth(getWallpaperDesiredMinimumWidth());
        WebView.enablePlatformNotifications();
        WebSettings webSettings = webView.getSettings();
        webSettings.setPluginsEnabled(false);
        webSettings.setRenderPriority(RenderPriority.LOW);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setCacheMode(webView.DRAWING_CACHE_QUALITY_AUTO;);
	}

	private void setImage(String url) {
		webView.loadUrl(url);
	}
	
	private void fetchRandomImage(String pageURL, String oldBase, String newBase){
		progressDialog = ProgressDialog.show(Launch.this, "", "Fetching image..", true);
		Intent nextIntent = new Intent(Intent.ACTION_VIEW);
		nextIntent.setClassName(this, WebFetcher.class.getName());
		nextIntent.putExtra("com.grimmvarg.android.pixility.pageURL", pageURL);
		nextIntent.putExtra("com.grimmvarg.android.pixility.newBase", newBase);
		nextIntent.putExtra("com.grimmvarg.android.pixility.oldBase", oldBase);
		startActivityForResult(nextIntent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			setImage(data.getExtras().getString("imageURL"));
		}else {
			showMessage("Sry, I failed :( - Try again :D");
		}
		progressDialog.dismiss();
	}
	
	@Override
	 protected void onSaveInstanceState(Bundle outState) {
	      webView.saveState(outState);
	   }

	
	public void randomFukung(View view) {
		 fetchRandomImage("http://fukung.net", "http://fukung.net/v/", "http://media.fukung.net/images/");		
	}
	
//	public void randomFatpina(View view) {
//		fetchRandomImage("http://fatpita.net", "http://fatpita.net/images");
//	}
//	
//	public void randomXKCD(View view) {
//		fetchRandomImage("http://dynamic.xkcd.com/random/mobile_comic", "http://imgs.xkcd.com/comics");
//	}
	
	public void showMessage(String message) {
		Toast toast = Toast.makeText(Launch.this, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.settings_menuitem:
	        	startActivity(new Intent(this, Settings.class));
	        	break;
	        case R.id.about_menuitem:
	        	startActivity(new Intent(this, About.class));
	        	break;
	    }
	    return true;
	}

	
}