package com.example.mixzone2.shoppingCar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mixzone2.ImageDownLoader;
import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.UriAPI;
import com.example.mixzone2.getImageUrl.getProductUrl_do;
import com.example.mixzone2.memberCenter.getAddress;
import com.example.mixzone2.product.productActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class addInBuylistActivity extends Activity {
	private ImageView back;
	private Spinner spinner_address;
	private Spinner spinner_money_way;
	private ArrayAdapter<String> addressAdapter;
	private ArrayAdapter<String> pay_wayAdapter;
	private ListView productListview;
	private TextView all_money;
	private TextView add_it;
	private String username;
	private String[] pay_way = new String[]{
			"货到付款","在线支付"
	};
	private String[][] address;
	private String[] address_detail;
	private String[] address_id;
	private String address_id_s;
	private String pay_way_s;
	
	private String all_price;
	private List<Map<String, Object>> product_list;
	private JSONObject buylist_detail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_shoppingcar_add_in_buylist);
		
		back = (ImageView)findViewById(R.id.image_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		spinner_address = (Spinner)findViewById(R.id.spinner_address);
		spinner_address.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				address_id_s = address_id[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		spinner_money_way = (Spinner)findViewById(R.id.spinner_money_way);
		spinner_money_way.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pay_way_s = position+"";
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		productListview = (ListView)findViewById(R.id.listview_product);
		all_money = (TextView)findViewById(R.id.text_buylist_all_price);
		add_it = (TextView)findViewById(R.id.add_it);
		add_it.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Thread thread = new Thread(new addIt());
				thread.start();
			}
		});
		
		
		SharedPreferences preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		username = preferences.getString(ShareString.Username, "");
		
		pay_wayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,pay_way);
		spinner_money_way.setAdapter(pay_wayAdapter);
		
		getIntentValue();
		
		Thread thread = new Thread(new returnAddress());
		thread.start();
	}
	
	private void getIntentValue(){
		Intent intent = getIntent();
		String string = intent.getStringExtra("product");
		try {
			JSONArray jsonArray = new JSONArray(string);
			product_list = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = new JSONObject();
				object = jsonArray.getJSONObject(i);
				product_list.add(getMap(object.toString()));
			}
			
//			JSONObject object = new JSONObject(string);
//			String string2 = (String) object.get("pro");
			
			all_price = intent.getStringExtra("all_price_string");
			all_money.setText("合计：￥"+all_price);
			
			SimpleAdapter adapter = new SimpleAdapter(addInBuylistActivity.this, product_list, R.layout.page_shopping_add_in_buylist_product_item, new String[]{"Image","product_name","product_size","product_price","product_count"}, new int[]{R.id.image_good,R.id.search_text_name,R.id.text_size,R.id.text_price,R.id.text_count});
			adapter.setViewBinder(new ViewBinder() {
				
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) {
					 if((view instanceof ImageView) && (data instanceof Bitmap)) {  
			                ImageView imageView = (ImageView) view;  
			                Bitmap bmp = (Bitmap) data;  
			                imageView.setImageBitmap(bmp);  
			                return true;  
			            }  
			            return false; 
				}
			});
			productListview.setAdapter(adapter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private class getImage implements Runnable{
		@Override
		public void run() {
			for (int i = 0; i < product_list.size(); i++) {
				ImageDownLoader imageDownLoader = new ImageDownLoader(addInBuylistActivity.this);
				Bitmap bitmap = imageDownLoader.showCacheBitmap(product_list.get(i).get("url").toString(), product_list.get(i).get("url").toString().replaceAll("[^\\w]", ""));
				
				if (bitmap != null) {
					product_list.get(i).put("Image",bitmap );
				}
			}
			mHandler.sendEmptyMessage(1);
			
		}
	}
	private Map<String, Object> getMap(String jsonString){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			Iterator<String> keyIter = jsonObject.keys(); 
			String key; 
			Object value; 
			Map<String, Object> valueMap = new HashMap<String, Object>(); 
			while (keyIter.hasNext()) 
			{ 
				key = (String) keyIter.next(); 
				value = jsonObject.get(key); 
				valueMap.put(key, value); 
			} 
			return valueMap;  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}
	
	private class addIt implements Runnable{

		@Override
		public void run() {
			try {
				buylist_detail = new JSONObject();
				for (int i = 0; i < product_list.size(); i++) {
					JSONObject object2 = new JSONObject();
					object2.put("product_id", product_list.get(i).get("product_id"));
					object2.put("product_price", product_list.get(i).get("product_price"));
					object2.put("product_size", product_list.get(i).get("product_size"));
					object2.put("product_count", product_list.get(i).get("product_count"));
					buylist_detail.put("product"+i,object2);
				}
				URL url = new URL(UriAPI.addit);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setReadTimeout(5000);
				conn.setConnectTimeout(5000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				String data = "username="+URLEncoder.encode(username,"UTF-8")+"&all_price="+URLEncoder.encode(all_price,"UTF-8")+"&address_id="+URLEncoder.encode(address_id_s,"UTF-8")+"&buylist_detail="+URLEncoder.encode(buylist_detail.toString(),"UTF-8")+"&payway="+URLEncoder.encode(pay_way_s,"UTF-8");
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
						mHandler.sendEmptyMessage(2);
					}
				}
				
			} catch (Exception e) {
				mHandler.sendEmptyMessage(3);
				e.printStackTrace();
			}
		}
		
	}
	
	private class returnAddress implements Runnable{
		@Override
		public void run() {
			getAddress getAddress = new getAddress(username);
			address = getAddress.returnAdd();
			if (address != null) {
				address_detail = new String[address.length];
				address_id = new String[address.length];
				for (int i = 0; i < address.length; i++) {
					address_id[i] = address[i][0];
					address_detail[i] = address[i][2]+" "+address[i][3]+"\n"+address[i][1];
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
				addressAdapter = new ArrayAdapter<String>(addInBuylistActivity.this, R.layout.spinner_layout,address_detail);
				spinner_address.setAdapter(addressAdapter);
				break;
			case 1:
				SimpleAdapter adapter = new SimpleAdapter(addInBuylistActivity.this, product_list, R.layout.page_shopping_add_in_buylist_product_item, new String[]{"Image","product_name","product_size","product_price","product_count"}, new int[]{R.id.image_good,R.id.search_text_name,R.id.text_size,R.id.text_price,R.id.text_count});
				adapter.setViewBinder(new ViewBinder() {
					
					@Override
					public boolean setViewValue(View view, Object data, String textRepresentation) {
						 if((view instanceof ImageView) && (data instanceof Bitmap)) {  
				                ImageView imageView = (ImageView) view;  
				                Bitmap bmp = (Bitmap) data;  
				                imageView.setImageBitmap(bmp);  
				                return true;  
				            }  
				            return false; 
					}
				});
				productListview.setAdapter(adapter);
				break;
			case 2:
				addInBuylistActivity.this.finish();
				Toast.makeText(addInBuylistActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(addInBuylistActivity.this, "下单不成功，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
}
