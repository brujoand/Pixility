package com.grimmvarg.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WebFetcher extends Activity{
	private HttpClient httpClient;
	private HttpContext localContext;
	private HttpGet httpGet;
	private HttpResponse response;
	private String result = "";
	private String pageURL;
	private String newBase;
	private String oldBase;
	private boolean gifAllowed;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent callerIntent = getIntent();
		pageURL = callerIntent.getStringExtra("com.grimmvarg.android.pixility.pageURL");
		newBase = callerIntent.getStringExtra("com.grimmvarg.android.pixility.newBase");
		oldBase = callerIntent.getStringExtra("com.grimmvarg.android.pixility.oldBase");
		gifAllowed = callerIntent.getBooleanExtra("com.grimmvarg.android.pixility.gifAllowed", false);
		
		String imageURL = fetchWebImage();
		Intent data = new Intent();
		
		if(!result.equals("")){
			data.putExtra("imageURL", imageURL);
			setResult(RESULT_OK, data);
		}else {
			setResult(RESULT_CANCELED);
		}
		
		finish();
	}
	
	private String fetchWebImage() {
		try {
			httpClient = new DefaultHttpClient();
			localContext = new BasicHttpContext();
			httpGet = new HttpGet(pageURL);
			response = httpClient.execute(httpGet, localContext);
			HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);

			result = pageURL + req.getURI().toString();
			result = result.replace(oldBase, newBase);
			
			Log.v("----------------------------------", result);

		} catch (ClientProtocolException e) {
			Log.v("com.grimmvarg.android.pixility.WebFetcher.ClientProtocoll----------------------------------", e.toString());
			return(fetchWebImage());
		} catch (IllegalStateException e) {
			Log.v("com.grimmvarg.android.pixility.WebFetcher.IllegalState", e.toString());
		} catch (IOException e) {
			Log.v("com.grimmvarg.android.pixility.WebFetcher.IO", e.toString());
		}
		
		if(result.contains(".gif") && !gifAllowed){
			return this.fetchWebImage();
		}
		
		return result;
	}
}
