package com.example.mixzone2.search;




import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.mixzone2.R;
import com.example.mixzone2.UriAPI;
import com.example.mixzone2.memberCenter.editUserAddress;
import com.example.mixzone2.product.productActivity;

import android.R.layout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class search extends Activity {
	private Intent intent;
	private String search_code;
	private String cla;
	private String data;
	private ListView search_listview;
	private String[] From = new String[]{"product_name","product_price"};
	private int[] To = new int[]{R.id.search_text_name,R.id.search_text_price}; 
	private SimpleAdapter adapter;
	private List<Map<String, Object>> datalist;
	
	private EditText edit_search;
	private Button btn_search;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_search);
		edit_search = (EditText) findViewById(R.id.edit_search);
		search_code = "";
		cla = "";
		data = "";
		intent = this.getIntent();
		search_code = intent.getStringExtra("search_code");
		cla = intent.getStringExtra("class");
		if (search_code != null) {
			edit_search.setText(cla);
			data = "search_code="+search_code+"&class="+cla;
			Thread thread = new Thread(new getSearch());
			thread.start();
		}
		
		btn_search = (Button)findViewById(R.id.btn_search);
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				data = edit_search.getText().toString();
				if (!data.equals("")) {
					data = "item="+data;
					Thread thread = new Thread(new getSearch());
					thread.start();
				}else {
					Toast.makeText(search.this, "ËÑË÷ÄÚÈÝ²»ÄÜÎª¿Õ", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		ImageView imageView = (ImageView)findViewById(R.id.image_back);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		search_listview = (ListView) findViewById(R.id.search_listview);
		
	}
	
	private class getSearch implements Runnable{
		@Override
		public void run() {
			try {
				URL url = new URL(UriAPI.search);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("POST");
				conn.setReadTimeout(5000);
				conn.setConnectTimeout(5000);
				
				OutputStream os = conn.getOutputStream();
				os.write(data.getBytes("UTF-8"));
				os.flush();
				
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
					if (jString.equals("NO")) {
						mHandler.sendEmptyMessage(1);
					}else {
						JSONObject object = new JSONObject(jString);
						datalist = new ArrayList<Map<String,Object>>();
						for (int i = 0; i < object.length(); i++) {
							JSONArray array = object.getJSONArray("product"+i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("product_id", array.get(0));
							map.put("product_name", array.get(1));
							map.put("product_price", "£¤"+array.get(2));
							datalist.add(map);
						}
						mHandler.sendEmptyMessage(0);
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setSearchList(){
		adapter = new SimpleAdapter(search.this, datalist, R.layout.page_search_item, From, To);
		search_listview.setAdapter(adapter);
		search_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(search.this,productActivity.class);
				intent.putExtra("product_id", datalist.get(position).get("product_id").toString());
				startActivity(intent);
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				setSearchList();
				break;
			case 1:
				Toast.makeText(search.this, "ËÑË÷Ê§°Ü£¬ÇëÖØÊÔ", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	

}
