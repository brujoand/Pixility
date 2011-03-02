package com.grimmvarg.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Toast;

public class Launch extends Activity {
	WebView webView;
	Context context;
	String imageURL;
	ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		 if (savedInstanceState != null){
		      ((WebView)findViewById(R.id.webView)).restoreState(savedInstanceState);
		 }

		
		webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
        });
        webView.setMinimumWidth(getWallpaperDesiredMinimumWidth());
        WebView.enablePlatformNotifications();
        webView.getSettings().setJavaScriptEnabled(true);

	}

	private void setImage(String url) {
		webView.loadUrl(url);
	}
	
	private void fetchRandomImage(String pageURL, String urlPattern){
		progressDialog = ProgressDialog.show(Launch.this, "", "Fetching image..", true);
		Intent nextIntent = new Intent(Intent.ACTION_VIEW);
		nextIntent.setClassName(this, WebFetcher.class.getName());
		nextIntent.putExtra("com.grimmvarg.android.pixility.pageURL", pageURL);
		nextIntent.putExtra("com.grimmvarg.android.pixility.urlPattern", urlPattern);
		startActivityForResult(nextIntent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			setImage(data.getExtras().getString("imageURL"));
		}else {
			showMessage("Sorry, I failed :(");
		}
		progressDialog.dismiss();
	}
	
	@Override
	 protected void onSaveInstanceState(Bundle outState) {
	      webView.saveState(outState);
	   }

	
	public void randomFukung(View view) {
		 fetchRandomImage("http://fukung.net/random", "http://media.fukung.net/images/");		
	}
	
	public void randomFatpina(View view) {
		fetchRandomImage("http://fatpita.net/random", "http://fatpita.net/images/");
	}
	
	public void randomXKCD(View view) {
		fetchRandomImage("http://dynamic.xkcd.com/random/mobile_comic/", "http://imgs.xkcd.com/comics/");
	}
	
	public void showMessage(String message) {
		CharSequence text = message;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(Launch.this, text, duration);
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