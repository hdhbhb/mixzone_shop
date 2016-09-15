package com.example.mixzone2.tools;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectHelper {
	private HttpURLConnection conn;
	public HttpConnectHelper() {
		// TODO Auto-generated constructor stub
	}
	public void connHttp(String uri){
		try {
			URL url = new URL(uri);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
