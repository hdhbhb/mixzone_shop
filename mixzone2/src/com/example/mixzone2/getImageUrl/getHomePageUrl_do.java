package com.example.mixzone2.getImageUrl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;


public class getHomePageUrl_do {
	private String[][] jStrings;
	private String uri;
	private String call;
	public getHomePageUrl_do(String uri,String call) {
		this.uri = uri;
		this.call = call;
	}
	public boolean getAdImageUrl_success(){
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			String data = "HomePageRequest="+call;
			
			OutputStream os = conn.getOutputStream();
			os.write(data.getBytes());
			os.flush();
			os.close();
			
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					baos.write(buffer,0,len);
				}
				is.close();
				baos.close();
				String jString = baos.toString();
				JSONObject obj = new JSONObject(jString);
				jStrings = new String[obj.length()][4];
				
				for (int i = 0; i < obj.length(); i++) {
					JSONObject obj2 = new JSONObject(obj.getJSONObject("product"+i).toString());
					jStrings[i][0] = obj2.getString("url");
					jStrings[i][1] = obj2.getString("product_id");
					jStrings[i][2] = obj2.getString("product_name");
					jStrings[i][3] = obj2.getString("product_price_b");
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String[][] returnStrings(){
		return jStrings;
	}

}
