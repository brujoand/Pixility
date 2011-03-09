package com.grimmvarg.android.pixility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;

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
import org.apache.http.util.ByteArrayBuffer;

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
	private String source;
	private boolean gifAllowed;
	private final String filePath = "/data/data/com.grimmvarg.android.pixility/image.jpg";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String imageURL = "";
		Intent callerIntent = getIntent();
		source = callerIntent.getStringExtra("com.grimmvarg.android.pixility.pixility.source");
		gifAllowed = callerIntent.getBooleanExtra("com.grimmvarg.android.pixility.pixility.gifAllowed", false);
		
		if(source.equals("Fukung")){
			imageURL = fetchFukung();
		}
		else if(source.equals("Fatpita")){
			imageURL = fetchFatpita();
		}
		else if(source.equals("MoonBuggy")){
			imageURL = fetchMoonBuggy();
		}
		else if(source.equals("LolRandom")){
			imageURL = fetchLolRandom();
		}
		else if(source.equals("FunCage")){
			imageURL = fetchFunCage();
		}
		
		Intent data = new Intent();
		
		if(!imageURL.equals("") ){//&& downloadFromUrl(imageURL, filePath)){
			data.putExtra("imageURL", imageURL);
//			data.putExtra("filePath", filePath);
			setResult(RESULT_OK, data);				
		}else {
			setResult(RESULT_CANCELED);
		}
		
		finish();
	}
	
	private String fetchFatpita() {
		String pageURL = "http://fatpita.net";
		String image = "";
		try {
			image = fetchImageUrlByRedirect(pageURL).split("=")[1];			
		} catch (Exception e) {
			return "";
		}
		return "http://fatpita.net/images/image%20("+ image + ").jpg";
	}

	private String fetchFukung() {
		String pageURL = "http://fukung.net";
		String newBase = "http://media.fukung.net/images/";
		String oldBase = "http://fukung.net/v/";
		String image = fetchImageUrlByRedirect(pageURL);
		return (pageURL + image).replace(oldBase, newBase);
	}
	
	private String fetchMoonBuggy(){
		String pageURL = "http://img.moonbuggy.org";
		String urlPattern = "http://img2.moonbuggy.org/imgstore/";
		String image = fetchImageUrlByParsing(pageURL, urlPattern);
		for (String field : image.split("\"")) {
			if(field.contains("http://")){
				return field;
			}
		}
		return "";
	}
	
	private String fetchLolRandom(){
		String pageURL = "http://www.lolrandom.com";
		String urlPattern = "/imageSize.asp?Image=/images/funny/";
		String image = fetchImageUrlByParsing(pageURL, urlPattern);
		for (String field : image.split("'")) {
			if(field.contains(urlPattern)){
				try{
					return pageURL + field.split("=")[1];
				}
				catch (Exception e) {
					return "";
				}
			}
		}
		return "";
	}
	
	private String fetchFunCage(){
		String pageURL = "http://www.funcage.com";
		String urlPattern = "/photos/";
		String image = fetchImageUrlByParsing(pageURL, urlPattern);
		for (String field : image.split("\"")) {
			if(field.contains(urlPattern)){
				if(field.contains("http://")){
					return field;
				}
				return pageURL + field;
			}
		}
		return "";
	}


	private String fetchImageUrlByRedirect(String pageURL) {
		String result = "";
		try {
			httpClient = new DefaultHttpClient();
			localContext = new BasicHttpContext();
			httpGet = new HttpGet(pageURL);
			response = httpClient.execute(httpGet, localContext);
			HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);

			result =  req.getURI().toString();

		} catch (ClientProtocolException e) {
			Log.v("com.grimmvarg.android.pixility.pixility.WebFetcher.ClientProtocoll----------------------------------", e.toString());
			return(fetchImageUrlByRedirect(pageURL));
		} catch (IllegalStateException e) {
			Log.v("com.grimmvarg.android.pixility.pixility.WebFetcher.IllegalState", e.toString());
		} catch (IOException e) {
			Log.v("com.grimmvarg.android.pixility.pixility.WebFetcher.IO", e.toString());
		}
		
		if(result.contains(".gif") && !gifAllowed){
			return this.fetchImageUrlByRedirect(pageURL);
		}
		
		return result;
	}
	
	private String fetchImageUrlByParsing(String pageURL, String urlPattern) {
		String result = "";
		try {
			httpClient = new DefaultHttpClient();
			localContext = new BasicHttpContext();
			httpGet = new HttpGet(pageURL);
			response = httpClient.execute(httpGet, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = null;
            while ((line = reader.readLine()) != null) {
            	if (line.contains(urlPattern)) {
					result = line;
				}
            }

		} catch (ClientProtocolException e) {
			Log.v("com.grimmvarg.android.pixility.pixility.WebFetcher.ClientProtocoll----------------------------------", e.toString());
			return(fetchImageUrlByRedirect(pageURL));
		} catch (IllegalStateException e) {
			Log.v("com.grimmvarg.android.pixility.pixility.WebFetcher.IllegalState", e.toString());
		} catch (IOException e) {
			Log.v("com.grimmvarg.android.pixility.pixility.WebFetcher.IO", e.toString());
		}
		
		if(result.contains(".gif") && !gifAllowed){
			return this.fetchImageUrlByRedirect(pageURL);
		}
		
		return result;
	}
	
	public boolean downloadFromUrl(String imageURL, String fileName) {

		try {
			File file = new File(fileName);
			URL url = new URL(imageURL);
			long startTime = System.currentTimeMillis();
			
			/* Open a connection to that URL. */

			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(75);
			int current = 0;

			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();

		} catch (IOException e) {
			Log.v("ImageManager", "Error: " + e);
			return false;
		}
		
		return true;
	}
}
