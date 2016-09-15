package com.example.mixzone2.memberCenter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.example.mixzone2.UriAPI;

public class addAddress_do {
	 	private String username;
	 	private String address;
	 	private String province;
	 	private String city;
	 	private String area;
	 	private String people;
	 	private String phone;
	public addAddress_do(String username,String address,String province,String city,String area,String people,String phone) {
		this.username = username;
		this.address = address ;
		this.province = province;
		this.city = city;
		this.area = area;
		this.people = people;
		this.phone = phone;
	}
	public boolean returnAdd(){
		try {
			URL url = new URL(UriAPI.addAddress);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(1000);
			conn.setConnectTimeout(1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			String data = "username="+URLEncoder.encode(username,"UTF-8")+"&address="+URLEncoder.encode(address,"UTF-8")+"&province="+URLEncoder.encode(province,"UTF-8")+"&city="+URLEncoder.encode(city,"UTF-8")+"&area="+URLEncoder.encode(area,"UTF-8")+"&people="+URLEncoder.encode(people,"UTF-8")+"&phone="+URLEncoder.encode(phone,"UTF-8");
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
				if (baos.toString().equals("YES")) {
					return true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
