package com.example.mixzone2.shoppingCar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mixzone2.Data;
import com.example.mixzone2.ImageDownLoader;
import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.product.productActivity;
import com.example.mixzone2.shoppingCar.ShoppingCarAdapter.ViewHolder;
import com.example.mixzone2.tools.ConnetionDetector;

import Login_Logout_tools.Login_do;
public class fragmentShoppingCarPage extends Fragment {
	private View view;
	private String From[] = new String[]{
		"Image","product_name","product_size","product_price","product_count"
	};
	private int To[] = new int[]{R.id.image_car_good,R.id.text_car_good_name,R.id.text_size,R.id.text_price,R.id.text_car_good_count};
	public List<Map<String, Object>> data_list;
//	private SimpleAdapter adapter;
	private ShoppingCarAdapter sAdapter;
	private ListView listview;
	private ImageDownLoader imageDownLoader;
	private String[] urls;
	private TextView all_product_price;
	private TextView all_price;
	private boolean First_in = true;
	private boolean Second_in = true;
	private RelativeLayout layout_buy_it;
	private TextView noGood;
	private CheckBox all_check;
	private boolean isConnecting = false;
	private String all_price_string;
	private List<Map<String, Object>> product_selected;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		view = inflater.inflate(R.layout.page_shoppingcar, container, false);
		all_product_price = (TextView)view.findViewById(R.id.text_buylist_all_price);
		all_price = (TextView)view.findViewById(R.id.text_shopping_car_count);
		layout_buy_it = (RelativeLayout)view.findViewById(R.id.layout_buy_it);
		noGood = (TextView)view.findViewById(R.id.text_nogood);
		noGood.setVisibility(View.GONE);
		setDefault();
		all_check = (CheckBox)view.findViewById(R.id.all_check);
		all_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					for (int i = 0; i < data_list.size(); i++) {
						sAdapter.getIsSelected().put(i, true);
					}
					refreshData();
				}else {
					int a = 0;
					for (int i = 0; i < data_list.size(); i++) {
						if (!sAdapter.getIsSelected().get(i)) {
							a++;
						}
					}
					if (a == 0){
						for (int i = 0; i < data_list.size(); i++) {
							sAdapter.getIsSelected().put(i, false);
						}
						refreshData();
					} 
					
				}
			}
		});
		all_price_string = "";
		TextView buyit = (TextView)view.findViewById(R.id.add_it);
		buyit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!all_price_string.equals("")) {
					if (Float.parseFloat(all_price_string) != 0) {
						
						Intent intent = new Intent(getContext(),addInBuylistActivity.class);
						try {
							for (int i = 0; i < product_selected.size(); i++) {
								product_selected.get(i).remove("Image");
								product_selected.get(i).remove("product_url");
							}
							JSONArray object= new JSONArray(product_selected.toString());
							intent.putExtra("product", object.toString());
							intent.putExtra("all_price_string", all_price_string);
							startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
					}
				}
				
				
			};
	});

		
		
		return view;
	}
	

	
	private void setDefault(){
		data_list = new ArrayList<Map<String,Object>>();
		sAdapter = new ShoppingCarAdapter(getContext(), fragmentShoppingCarPage.this, data_list);
		listview = (ListView)view.findViewById(R.id.list_shopping_car);
		listview.setAdapter(sAdapter);
		
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (First_in) {
			First_in = false;
			return;
		}
		if (!hidden) {
			setDefault();
			check_connetion();
		}else {
			if (urls != null) {
				Data.removeBitmapFromLrucache(urls);
			}
			
		}
	}
	@Override
	public void onResume() {
		setDefault();
		check_connetion();
		super.onResume();
	}
	private void check_connetion(){
		ConnetionDetector connetionDetector = new ConnetionDetector(getContext());
		isConnecting = connetionDetector.isConnectionToInternet();
		if (!isConnecting) {
			layout_buy_it.setVisibility(View.GONE);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
			mBuilder.setCancelable(false);
			mBuilder.setTitle("没有网络连接");
			mBuilder.setPositiveButton("重试", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					check_connetion();
				}
			});
			mBuilder.setNeutralButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
	
				}
			});
			mBuilder.create().show();
		}else {
			Login_do logout_do = new Login_do(getContext());
			logout_do.check_login_save();
			startGetShoppingCar();
		}
	}
	
	public void startGetShoppingCar(){
		new Thread(){
			public void run() {
				if (urls != null) {
					Data.removeBitmapFromLrucache(urls);
				}
				SharedPreferences preferences = getContext().getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
				if(preferences.getBoolean(ShareString.isLogin, false)){
					getShoppingCar_do getShoppingCar_do = new getShoppingCar_do(preferences.getString(ShareString.Username, ""));
					String[][] strings = getShoppingCar_do.getShoppingCar();
					if (strings != null) {
						data_list = new ArrayList<Map<String,Object>>();
						urls = new String[strings.length];
						for (int i = 0; i < strings.length; i++) {
							imageDownLoader = new ImageDownLoader(getContext());
							Bitmap bitmap = imageDownLoader.showCacheBitmap(strings[i][0],strings[i][0].replaceAll("[^\\w]", "") );
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Image", bitmap);
							map.put("product_url",strings[i][0]);
							map.put("product_name", strings[i][1]);
							map.put("product_size", strings[i][2]);
							map.put("product_price", "￥"+strings[i][3]);
							map.put("product_count", strings[i][4]);
							map.put("product_id", strings[i][5].toString());
							
							data_list.add(map);
							urls[i] = strings[i][0].replaceAll("[^\\w]", "");
						}
						
						mHandler.sendEmptyMessage(0);
					}else {
						mHandler.sendEmptyMessage(2);
					}
				}else {
					mHandler.sendEmptyMessage(1);
				}
				
				
			};
			
		}.start();
	}
	
	@SuppressWarnings("static-access")
	public void updateCheck(){
		float products_price = 0;
		int count = 0;
		product_selected = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < data_list.size(); i++) {
			if (sAdapter.getIsSelected().get(i)) {
				product_selected.add(data_list.get(i));
				
				products_price += Float.parseFloat(data_list.get(i).get("product_price").toString().replace("￥", ""))*Float.parseFloat(data_list.get(i).get("product_count").toString());
				count++;
			}
		}
		if (count == data_list.size()) {
			all_check.setChecked(true);
		}else {
			all_check.setChecked(false);
		}
		DecimalFormat dFormat = new DecimalFormat("0.00");
		all_price_string = dFormat.format(products_price);
//		Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
		all_product_price.setText("合计：￥"+all_price_string);
		all_price.setText("总额：￥"+all_price_string );
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				sAdapter = new ShoppingCarAdapter(getContext(),fragmentShoppingCarPage.this, data_list);
				listview.setAdapter(sAdapter);
				
				listview.setOnItemClickListener(new OnItemClickListener() {

					@SuppressWarnings("static-access")
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						ViewHolder holder = (ViewHolder) view.getTag();
						holder.checkBox.toggle();
						sAdapter.getIsSelected().put(position, holder.checkBox.isChecked());

					}
				});
				layout_buy_it.setVisibility(View.VISIBLE);
				noGood.setVisibility(View.GONE);
				updateCheck();
				break;
			case 1:
				layout_buy_it.setVisibility(View.GONE);
				Toast.makeText(getContext(), "尚未登陆", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				setDefault();
				layout_buy_it.setVisibility(View.GONE);
				noGood.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		};
	};
	
	private void refreshData(){
		sAdapter.notifyDataSetChanged();
	}
	
}
