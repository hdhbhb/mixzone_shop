package com.example.mixzone2.product;

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

import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.UriAPI;
import com.example.mixzone2.LoginPage.LoginActivity;
import com.example.mixzone2.getImageUrl.getProductUrl_do;
import com.example.mixzone2.tools.CycleImages;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class productActivity extends Activity {
	private String product_id;
	private CycleImages cycleImages;
	private getProductUrl_do getProductUrl_do;
	private int[] dots = new int[]{R.id.dot1_0,R.id.dot1_1};
	private String product_name;
	private String product_show;
	private String product_size;
	private String product_price;
	private TextView pName;
	private TextView pShow;
	private TextView pSize;
	private TextView pPrice;
	private TextView commentCount;
	private TextView noComment;
	private List<Map<String, Object>> data_list;
	private String[] from = new String[]{"name","time","detail"};
	private int[] to = new int[]{R.id.text_comment_member_name,R.id.text_comment_time,R.id.text_comment_detail};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_goods);
		
		Intent intent = getIntent();
		product_id = intent.getStringExtra("product_id");

		pName = (TextView)findViewById(R.id.text_good_detail_name);
		pShow = (TextView)findViewById(R.id.text_good_detail_text1);
		pSize = (TextView)findViewById(R.id.text_good_detail_size);
		pPrice = (TextView)findViewById(R.id.text_good_detail_price);
		noComment = (TextView)findViewById(R.id.text_nocomment);
		commentCount = (TextView)findViewById(R.id.text_good_comment);
		ImageView back = (ImageView)findViewById(R.id.image_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		RelativeLayout addInCar = (RelativeLayout) findViewById(R.id.addInCar);
		addInCar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
				if (preferences.getBoolean(ShareString.isLogin, false)) {
					String username = preferences.getString(ShareString.Username, "");
					ProductaddInCar(username);
				}else{
					Intent intent = new Intent(productActivity.this,LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		
		cycleImages = new CycleImages(R.id.vp_goods, this, dots, getWindow().getDecorView(),R.drawable.system_default, false, false);
	
		getProductUrl_do = new getProductUrl_do(UriAPI.getProductUri);
		startGetImage();
		startGetShow();
		startGetComment();
	}
	
	private void ProductaddInCar(final String username){
		new Thread(){
			public void run() {
				try {
					URL url = new URL(UriAPI.addInCarUri);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					String data = "username="+URLEncoder.encode(username,"UTF-8")+"&product_id="+URLEncoder.encode(product_id,"UTF-8")+"&product_size="+product_size+"&addORsub=1";
					OutputStream os = conn.getOutputStream();
					os.write(data.getBytes("UTF-8"));
					os.flush();
					os.close();
					
					
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						int len = 0;
						byte buffer[] = new byte[1024];
						while((len = is.read(buffer))!= -1){
							baos.write(buffer,0,len);
						}
						is.close();
						baos.close();
						String result = baos.toString();
						if (result.equals("addSuccess")) {
							mHandler.sendEmptyMessage(3);
						}else {
							mHandler.sendEmptyMessage(4);
						}
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	private void startGetImage(){
		new Thread(){
			public void run() {

				String[] url = getProductUrl_do.returnImageUrl(product_id);
				if (url != null) {
					cycleImages.setProductImage(url);
				}
				
			};
		}.start();
	}
	
	private void startGetShow(){
		new Thread(){
			public void run() {
				String[] Pshow = getProductUrl_do.returnShow(product_id);
				if (Pshow != null) {
					product_name = Pshow[0];
					product_show = Pshow[1];
					product_size = Pshow[2];
					product_price = "￥"+Pshow[4]+"(原价￥"+Pshow[3]+")";
					
					mHandler.sendEmptyMessage(0);
				}
			};
		}.start();
	}
	
	private void startGetComment(){
		new Thread(){
			public void run() {
				String[][] strings = getProductUrl_do.returnComment(product_id);
				if (strings != null) {
					data_list = new ArrayList<Map<String,Object>>();
					for (int i = 0; i < strings.length; i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", strings[i][0]);
						map.put("time", strings[i][1]);
						map.put("detail", strings[i][2]);
						data_list.add(map);
					}
					mHandler.sendEmptyMessage(1);
				}else {
					mHandler.sendEmptyMessage(2);
				}
			};
		}.start();
	}
	
	
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				pName.setText(product_name);
				pShow.setText(product_show);
				pSize.setText(product_size);
				pPrice.setText(product_price);
				break;
			case 1:
				SimpleAdapter adapter = new SimpleAdapter(productActivity.this, data_list, R.layout.page_goods_comment_item, from, to);
				ListView listView = (ListView)findViewById(R.id.list_comment_1);
				listView.setAdapter(adapter);
				noComment.setText("");
				noComment.setHeight(0);
				ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_Good);
				scrollView.smoothScrollTo(0, 0);
				break;
			case 2:
				noComment.setText("没有评论");
				break;
			case 3:
				Toast.makeText(productActivity.this, "加入购物车成功", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(productActivity.this, "加入购物车失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
//			Toast.makeText(productActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
		};
	};

	@Override
	protected void onDestroy() {
//		cycleImages.removeBitmap();
		super.onDestroy();
	}

}
