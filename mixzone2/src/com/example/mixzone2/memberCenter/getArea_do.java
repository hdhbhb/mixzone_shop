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

public class getArea_do {
	private String Arearequest;
	private String RequestArea;
	private String father;
	private String[] aStrings =null;
	public getArea_do(String Arearequest,String RequestArea,String father) {
		this.Arearequest = Arearequest;
		this.RequestArea = RequestArea;
		this.father = father;
	}
	
	
	public String[] returnArea(){
		try {
			if (RequestArea == null) {
				RequestArea = "a";
			}
			if (father == null) {
				father = "a";
			}
			URL url = new URL(UriAPI.getArea);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			String data = "AreaRequest="+URLEncoder.encode(Arearequest,"UTF-8")+"&RequestArea="+URLEncoder.encode(RequestArea,"UTF-8")+"&father="+URLEncoder.encode(father,"UTF-8");
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
				JSONObject obj = new JSONObject(baos.toString());
				JSONArray jsonArray = obj.getJSONArray("area");
				aStrings = new String[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					aStrings[i] = (String) jsonArray.get(i);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return aStrings;
	}
	

}
