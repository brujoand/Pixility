package com.grimmvarg.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WebFetcher extends Activity{
	HttpClient httpClient;
	HttpContext localContext;
	HttpGet httpGet;
	HttpResponse response;
	String result = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent daddy = getIntent();
		String pageURL = daddy.getStringExtra("com.grimmvarg.android.pixility.pageURL");
		String urlPattern = daddy.getStringExtra("com.grimmvarg.android.pixility.urlPattern");
		
		String imageURL = fetchWebImage(pageURL, urlPattern);
		Intent data = new Intent();
		
		if(!result.equals("")){
			data.putExtra("imageURL", imageURL);
			setResult(RESULT_OK, data);
		}else {
			setResult(RESULT_CANCELED);
		}
		
		finish();
	}
	
	public String fetchWebImage(String url, String urlPattern) {
		try {
			httpClient = new DefaultHttpClient();
			// Xkcd breaks if useragent is set
			if(!url.contains("xkcd")){
				httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 1.1; en-gb; dream) AppleWebKit/525.10+ (KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2 – G1 Phone");
			}
			localContext = new BasicHttpContext();
			httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet, localContext);
			result = "";
			
			BufferedReader reader = new BufferedReader(
				    new InputStreamReader(
				      response.getEntity().getContent()
				    )
				  );
			String line = null;
			while ((line = reader.readLine()) != null){
				if(line.contains(urlPattern)){
					Log.v("-------------", line);
					for (String linePart : line.split("\"")) {
						if(linePart.startsWith(urlPattern)){
							result = linePart;
						}
					}
				}
			}
		} catch (ClientProtocolException e) {
			Log.v("com.grimmvarg.android.pixility.WebFetcher.ClientProtocoll", e.toString());
		} catch (IllegalStateException e) {
			Log.v("com.grimmvarg.android.pixility.WebFetcher.IllegalState", e.toString());
		} catch (IOException e) {
			Log.v("com.grimmvarg.android.pixility.WebFetcher.IO", e.toString());
		}
		Log.v("com.grimmvarg.android.pixility.WebFetcher.Result", result);
		
		if(result.contains(".gif")){
			return this.fetchWebImage(url, urlPattern);
		}
		
		return result;
	}
}
