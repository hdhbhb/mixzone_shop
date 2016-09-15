package com.example.mixzone2.memberCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RecoverySystem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class editUserAddress extends Activity{
	private ListView address_lisview;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> all_address;
	private String[] From = new String[]{"name","phone","address"};
	private int[] To = new int[]{R.id.text_edit_address_name,R.id.text_address_phone,R.id.text_edit_address_address};
	private Button btn_add_address;
	private ImageView back;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_address_page);
		
		address_lisview = (ListView)findViewById(R.id.address_listview);
		btn_add_address = (Button)findViewById(R.id.btn_add_address);
		btn_add_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(editUserAddress.this,addUserAddress.class);
				startActivity(intent);
			}
		});
		back = (ImageView)findViewById(R.id.image_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		SharedPreferences preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		username = preferences.getString(ShareString.Username, "");
		
		Thread thread = new Thread(new getAddressRun());
		thread.start();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Thread thread = new Thread(new getAddressRun());
		thread.start();
		super.onResume();
	}
	
	private class getAddressRun implements Runnable{
		@Override
		public void run() {
			getAddress gAddress = new getAddress(username);
			String[][] strings = gAddress.returnAdd(); 
			all_address = new ArrayList<Map<String,Object>>();
			if (strings != null) {
				for (int i = 0; i < strings.length; i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("address_id", strings[i][0]);
					map.put("address", strings[i][1]);
					map.put("name", strings[i][2]);
					map.put("phone", strings[i][3]);
					all_address.add(map);
				}
				mHandler.sendEmptyMessage(0);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				adapter = new SimpleAdapter(editUserAddress.this, all_address, R.layout.edit_address_item, From, To);
				address_lisview.setAdapter(adapter);
				break;

			default:
				break;
			}
		};
	};
}
