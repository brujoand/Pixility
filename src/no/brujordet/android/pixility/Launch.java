package no.brujordet.android.pixility;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.TextView;
import android.widget.Toast;

public class Launch extends Activity implements OnSharedPreferenceChangeListener, OnClickListener {
	private WebView webView;
	private boolean allowGif = false;
	private ProgressDialog progressDialog;
	private ArrayList<String> sourcesArray;
	private String nowWatching = "";
	private String imageURL;
	private boolean useFullscreen;
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		useFullscreen = settings.getBoolean("Fullscreen", true);
		if (useFullscreen) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(R.layout.main);

		if (savedInstanceState != null) {
			((WebView) findViewById(R.id.webView))
					.restoreState(savedInstanceState);
		}
		
		((TextView) findViewById(R.id.nowShowing)).setOnClickListener(this);

		setUpWebView();

	}

	@Override
	public void onResume() {
		super.onResume();
		refreshWebSources();
		if (useFullscreen != settings.getBoolean("Fullscreen", true)) {
			useFullscreen = !useFullscreen;
			restart();
		}
	}

	private void refreshWebSources() {
		sourcesArray = new ArrayList<String>();

		if (settings.getBoolean("Fukung", false)) {
			sourcesArray.add("Fukung");
		}
		if (settings.getBoolean("Fatpita", false)) {
			sourcesArray.add("Fatpita");
		}
		if (settings.getBoolean("AllowGif", false)) {
			allowGif = true;
		}
		if (settings.getBoolean("MoonBuggy", false)) {
			sourcesArray.add("MoonBuggy");
		}
		if (settings.getBoolean("LolRandom", false)) {
			sourcesArray.add("LolRandom");
		}
		if (settings.getBoolean("FunCage", false)) {
			sourcesArray.add("FunCage");
		}

		Collections.shuffle(sourcesArray);

	}

	private void setUpWebView() {
		webView = (WebView) findViewById(R.id.webView);
		webView.setBackgroundColor(0);
		webView.setDrawingCacheQuality(WebView.DRAWING_CACHE_QUALITY_LOW);
		webView.setDrawingCacheEnabled(true);
		WebView.enablePlatformNotifications();
		WebSettings webSettings = webView.getSettings();
		webSettings.setPluginsEnabled(false);
		webSettings.setRenderPriority(RenderPriority.LOW);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
	}

	private void setImage(String url) {
		webView.loadUrl(url);
	}

	public void fetchRandomImage() {
		if (sourcesArray.isEmpty()) {
			showMessage("Please select sources in settings!");
		} else {
			nowWatching = sourcesArray.get(0);
			Intent fetchImage = new Intent(Intent.ACTION_VIEW);
			progressDialog = ProgressDialog.show(Launch.this, "","Fetching image from " + nowWatching, true);
			fetchImage.setClassName(this, WebFetcher.class.getName());
			fetchImage.putExtra("no.brujordet.android.pixility.pixility.source",nowWatching);
			fetchImage.putExtra("no.brujordet.android.pixility.pixility.gifAllowed",allowGif);
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
			((TextView) findViewById(R.id.nowShowing)).setText("Fetched from: " + nowWatching);
		} else {
			showMessage("Sry, I failed :( - Try again :D");
		}
		progressDialog.dismiss();
		webView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
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
		intent.putExtra(Intent.EXTRA_SUBJECT,
				"An image I found using Pixility app for android :)");
		intent.putExtra(Intent.EXTRA_TEXT, imageURL);

		startActivity(Intent.createChooser(intent, "Share"));
	}

	private void restart() {
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		refreshWebSources();

	}

	@Override
	public void onClick(View v) {
		fetchRandomImage();
		
	}

}