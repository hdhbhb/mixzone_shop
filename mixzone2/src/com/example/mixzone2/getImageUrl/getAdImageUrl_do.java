package com.example.mixzone2.getImageUrl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.example.mixzone2.UriAPI;

import android.widget.Toast;

public class getAdImageUrl_do {
	private String[][] jStrings;

	public boolean getAdImageUrl_success(){
		try {
			URL url = new URL(UriAPI.getAdImageUri);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
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
				jStrings = new String[obj.length()][2];
				
				for (int i = 0; i < obj.length(); i++) {
					JSONObject obj2 = new JSONObject(obj.getJSONObject("product"+i).toString());
					jStrings[i][0] = obj2.getString("url");
					jStrings[i][1] = obj2.getString("product_id");
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
