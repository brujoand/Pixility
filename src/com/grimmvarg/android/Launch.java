package com.grimmvarg.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
import android.widget.TextView;
import android.widget.Toast;

public class Launch extends Activity implements OnSharedPreferenceChangeListener {
	private WebView webView;
	private boolean allowGif = false;
	private ProgressDialog progressDialog;
	private ArrayList<String> sourcesArray;
	private String nowWatching = "";
	private String imageURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		if (savedInstanceState != null){
			((WebView)findViewById(R.id.webView)).restoreState(savedInstanceState);
		}
		
		setUpWebView();

	}
	
	@Override
	public void onResume(){
		super.onResume();
		refreshWebSources();
	}

	private void refreshWebSources() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		sourcesArray = new ArrayList<String>();
		
		if(settings.getBoolean("Fukung", false)){
			sourcesArray.add("Fukung");
		}
		if(settings.getBoolean("Fatpita", false)){
			sourcesArray.add("Fatpita");
		}
		if(settings.getBoolean("AllowGif", false)){
			allowGif = true;
		}
		if(settings.getBoolean("MoonBuggy", false)){
			sourcesArray.add("MoonBuggy");
		}
		if(settings.getBoolean("LolRandom", false)){
			sourcesArray.add("LolRandom");
		}
		if(settings.getBoolean("FunCage", false)){
			sourcesArray.add("FunCage");
		}
//		if(settings.getBoolean("LolPics", false)){
//			sourcesArray.add("LolPics");
//		}
		
		Collections.shuffle(sourcesArray);
	}
	
	private void setUpWebView() {
		webView = (WebView) findViewById(R.id.webView);
		webView.setBackgroundColor(0);
        webView.setMinimumWidth(getWallpaperDesiredMinimumWidth());
        //webView.clearCache(false);
        WebView.enablePlatformNotifications();
        WebSettings webSettings = webView.getSettings();
        webSettings.setPluginsEnabled(false);
        webSettings.setRenderPriority(RenderPriority.LOW);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
	}

	private void setImage(String url) {
		webView.loadUrl(url);
	}
	
	public void fetchRandomImage(View view){
		if(sourcesArray.isEmpty()){
			showMessage("Please select sources in settings!");
		}else {			
			progressDialog = ProgressDialog.show(Launch.this, "", "Fetching image..", true);
			Intent fetchImage = new Intent(Intent.ACTION_VIEW);
			fetchImage.setClassName(this, WebFetcher.class.getName());
			nowWatching = sourcesArray.get(0);
			fetchImage.putExtra("com.grimmvarg.android.pixility.source", nowWatching);
			fetchImage.putExtra("com.grimmvarg.android.pixility.gifAllowed", allowGif);
			Collections.shuffle(sourcesArray);
			
			startActivityForResult(fetchImage, 1);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			imageURL = data.getExtras().getString("imageURL");
			setImage(imageURL);
			((TextView)findViewById(R.id.nowShowing)).setText("Fetched from: " + nowWatching);
		}else {
			showMessage("Sry, I failed :( - Try again :D");
		}
		progressDialog.dismiss();
	}
	
	@Override
	 protected void onSaveInstanceState(Bundle outState) {
	      webView.saveState(outState);
	   }

	

	
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
	        case R.id.share_menuitem:
	        	share();
	        	break;
	    }
	    return true;
	}
	
	public void share() {
		 final Intent intent = new Intent(Intent.ACTION_SEND);

		 intent.setType("text/plain");
		 intent.putExtra(Intent.EXTRA_SUBJECT, "An image I found using Pixility app for android :)");
		 intent.putExtra(Intent.EXTRA_TEXT, imageURL);

		 startActivity(Intent.createChooser(intent, "Share"));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		refreshWebSources();
		
	}

	
}