package com.example.mixzone2.classificationPage;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.mixzone2.R;
import com.example.mixzone2.UriAPI;
import com.example.mixzone2.search.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class fragmentClassificationPage extends Fragment {
	private View view ;
	private String[][] Cla;
	private boolean first_in = true;
	private ListView c1_listview;
	private ListView c2_listview;
	
	private List<String> c1 ;
	private List<String> c2 ;
	
	private int click_at = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_classification, container, false);
		
		c1_listview = (ListView)view.findViewById(R.id.c1_listview);
		c2_listview = (ListView)view.findViewById(R.id.c2_listview);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.image_search);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), search.class);
				startActivity(intent);
			}
		});
		
		return view;
	}
	
	private void getClassThread(){
		Thread thread = new Thread(new getClass());
		thread.start();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (first_in) {
			first_in = false;
			return;
		}
		if (!hidden) {
			getClassThread();
		}
	}
	
	private class getClass implements Runnable{
		@Override
		public void run() {
			try {
				URL url = new URL(UriAPI.showClass);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("POST");
				conn.setReadTimeout(5000);
				conn.setConnectTimeout(5000);
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
					}
					JSONObject object = new JSONObject(jString);
					Cla = new String[object.length()][20];
					for (int i = 0; i < object.length(); i++) {
						JSONArray object2 = object.getJSONArray(i+"");
						Cla[i][0] = object2.getString(0);
						for (int j = 1; j < object2.length(); j++) {
							Cla[i][j] = object2.getString(j);
						}
					}
					mHandler.sendEmptyMessage(0);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setClassItem(){
		c1 = new ArrayList<String>();
		c2 = new ArrayList<String>();
		for (int i = 0; i < Cla.length; i++) {
			c1.add(Cla[i][0]);
		}
		for (int i = 1; i < 20; i++) {
			if (Cla[click_at][i] == null) {
				break;
			}
			c2.add(Cla[click_at][i]);
		}
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,c1);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,c2);
		c1_listview.setAdapter(adapter1);
		c1_listview.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				click_at = position;
				setClassItem();
			}
		});
		c2_listview.setAdapter(adapter2);
		c2_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getContext(),search.class);
				intent.putExtra("search_code", "class_b");
				intent.putExtra("class", Cla[click_at][position+1]);
				startActivity(intent);
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				setClassItem();
				break;
			case 1:
				Toast.makeText(getContext(), "获取分类失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
}
