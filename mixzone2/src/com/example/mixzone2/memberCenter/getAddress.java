package com.example.mixzone2.memberCenter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.mixzone2.UriAPI;

public class getAddress {
	private String username;
	public getAddress(String username) {
		this.username = username;
	}
	
	public String[][] returnAdd(){
		String[][] strings = null;
		try {
			URL url = new URL(UriAPI.getAddress);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			String data = "username="+URLEncoder.encode(username,"UTF-8");
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
				if (baos.equals("NO")) {
					return strings;
				}else {
					JSONObject obj1 = new JSONObject(baos.toString());
					strings = new String[obj1.length()][4];
					for (int i = 0; i < obj1.length(); i++) {
						JSONObject obj2 = new JSONObject();
						obj2 = obj1.getJSONObject("add"+i);
						strings[i][0] = obj2.getString("address_id");
						strings[i][1] = obj2.getString("address");
						strings[i][2] = obj2.getString("people");
						strings[i][3] = obj2.getString("phone");
					}
					return strings;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strings;
	}

}
