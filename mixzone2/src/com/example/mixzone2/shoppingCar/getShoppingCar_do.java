package com.example.mixzone2.shoppingCar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.example.mixzone2.UriAPI;

public class getShoppingCar_do {
	private String username;
	public getShoppingCar_do(String username) {
		this.username = username;
	}
	
	public String[][] getShoppingCar(){
		try {
			URL url = new URL(UriAPI.getFromCarUri);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
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
				byte buffer[] = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					baos.write(buffer,0,len);
				}
				is.close();
				baos.close();
				String string = baos.toString();
				if (string.equals("NO_SHOPPING_CAR")) {
					return null;
				}
				JSONObject obj1 = new JSONObject(string);
				String[][] strings = new String[obj1.length()][6];
				for (int i = 0; i < obj1.length(); i++) {
					JSONObject obj2 = new JSONObject();
					obj2 = obj1.getJSONObject("shoppingcar"+i);
					strings[i][0] = UriAPI.mainUri+obj2.getString("product_img");
					strings[i][1] = obj2.getString("product_name");
					strings[i][2] = obj2.getString("product_size");
					strings[i][3] = obj2.getString("product_price");
					strings[i][4] = obj2.getString("product_count");
					strings[i][5] = obj2.getString("product_id");
				}
				return strings;
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
