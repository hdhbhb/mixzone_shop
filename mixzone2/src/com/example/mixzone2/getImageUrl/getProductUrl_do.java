package com.example.mixzone2.getImageUrl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

public class getProductUrl_do {
	private String uri;
	
	public getProductUrl_do(String uri) {
		this.uri = uri;
	}
	
	/**返回图片的地址字符串数组*/
	public String[] returnImageUrl(String product_id){
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			String data = "ProductRequest=product_img&product_id="+URLEncoder.encode(product_id,"UTF-8");
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
				String[] strings = new String[]{
						obj.getString("url0"),
						obj.getString("url1")
				};
				return strings;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] returnShow(String product_id){
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			String data = "ProductRequest=product_show&product_id="+URLEncoder.encode(product_id,"UTF-8");
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
				String[] strings = new String[]{
						obj.getString("product_name"),
						obj.getString("product_show"),
						obj.getString("product_size"),
						obj.getString("product_price_a"),
						obj.getString("product_price_b"),
				};
			return strings;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String[][] returnComment(String product_id){
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			String data = "ProductRequest=product_comment&product_id="+URLEncoder.encode(product_id,"UTF-8");
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
				if (jString.equals("NOCOMMENT")) {
					return null;
				}
				JSONObject obj = new JSONObject(jString);
				JSONObject obj2 = new JSONObject();
				String[][] strings = new String[obj.length()][3];
				for (int i = 0; i < obj.length(); i++) {
					obj2 = obj.getJSONObject("comment"+i);
					strings[i][0] = obj2.getString("user_name");
					strings[i][1] = obj2.getString("comment_time");
					strings[i][2] = obj2.getString("comment_show");
				}
				
			return strings;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
