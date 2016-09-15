package com.example.mixzone2.memberCenter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.UriAPI;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class showBuylistActivity extends Activity {
	private String username;
	private List<Map<String, Object>> list;
	private ListView listView;
	private String[] From = new String[]{"buylist_id","all_price","buylist_status"};
	private int[] To = new int[]{R.id.search_text_price,R.id.textView2,R.id.textView3};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_buylist_page);
		
		ImageView back = (ImageView) findViewById(R.id.image_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		listView = (ListView)findViewById(R.id.buylist);
		SharedPreferences preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		username = preferences.getString(ShareString.Username, "");
		
		Thread thread = new Thread(new getBuylist());
		thread.start();
	}
	private class getBuylist implements Runnable{
		@Override
		public void run() {
			try {
				URL url = new URL(UriAPI.getBuylist);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
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
						mhHandler.sendEmptyMessage(1);
					}else {
						list = new ArrayList<Map<String,Object>>();
						JSONObject obj = new JSONObject(baos.toString());
						for (int i = 0; i < obj.length(); i++) {
							JSONObject jsonObject = new JSONObject();
							jsonObject = obj.getJSONObject("list"+i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("buylist_id", "订单号："+jsonObject.getString("buylist_id"));
							map.put("all_price", "总额：￥"+jsonObject.getString("all_price"));
							if (jsonObject.getString("buylist_status").equals("0")) {
								map.put("buylist_status","未付款" );
							}else if (jsonObject.getString("buylist_status").equals("1")) {
								map.put("buylist_status","已付款" );
							}else if (jsonObject.getString("buylist_status").equals("2")) {
								map.put("buylist_status","已收货" );
							}
							
							list.add(map);
						}
						mhHandler.sendEmptyMessage(0);
					}
					
					
				}
				
			} catch (Exception e) {
				mhHandler.sendEmptyMessage(1);
				e.printStackTrace();
			}
		}
	}
	@SuppressLint("HandlerLeak")
	Handler mhHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				SimpleAdapter adapter = new SimpleAdapter(showBuylistActivity.this, list, R.layout.show_buylist_page_item, From, To);
				listView.setAdapter(adapter);
				break;
			case 1:
				Toast.makeText(showBuylistActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
}
