package com.grimmvarg.android;

public class ImageURL {
	private String webPageUrl;
	private String preImageUrl;
	private String postImageUrl;
	private String uniqueUrl;
	
	public void setUniqueUrl() {
		this.uniqueUrl = uniqueUrl;
	}

	public ImageURL (String webPageUrl, String preImageUrl, String postImageUrl){
		this.webPageUrl = webPageUrl;
		this.preImageUrl = preImageUrl;
		this.postImageUrl = postImageUrl;
	}
	
	public String getImageUrl(){
		return preImageUrl + uniqueUrl + postImageUrl;
	}

}
