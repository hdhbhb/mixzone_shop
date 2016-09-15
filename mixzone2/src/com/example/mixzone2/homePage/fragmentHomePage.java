package com.example.mixzone2.homePage;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mixzone2.Data;
import com.example.mixzone2.ImageDownLoader;
import com.example.mixzone2.MainActivity;
import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.UriAPI;
import com.example.mixzone2.myBaseAdapter;
import com.example.mixzone2.getImageUrl.getAdImageUrl_do;
import com.example.mixzone2.getImageUrl.getHomePageUrl_do;
import com.example.mixzone2.product.productActivity;
import com.example.mixzone2.search.search;
import com.example.mixzone2.tools.ConnetionDetector;
import com.example.mixzone2.tools.CycleImages;

import Login_Logout_tools.Login_do;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class fragmentHomePage extends Fragment {
	
	private View view;
	/**点的id数组*/
	private int[] dots = new int[]{
			R.id.dot_0,
			R.id.dot_1,
			R.id.dot_2,
			R.id.dot_3,
			R.id.dot_4
	};
	private getAdImageUrl_do getAd;
	private String[][] AdUrls;
	private CycleImages cycleImages;
	/**gview对象*/
	private GridView gview;
	/**simple的数据适配器*/
	private SimpleAdapter sim_adapter;
	private String From[] = new String[]{"product_image","product_name","product_price"};
	private int To[] = new int[]{R.id.image_good,R.id.text_goods_name,R.id.text_goods_price};
	
	private GridView Gridview_recommendation;
	private List<Map<String, Object>> data_list_recommendation;
	private String[] ids_recommendation;
	private List<Map<String, Object>> data_list_classification;
	
	private List<Map<String, Object>> data_list_hotsale;
	private String[] ids_hotsale;
	private List<Map<String, Object>> data_list_kitchen;
	private String[] ids_kitchen;
	private GridView gview_kitchen;
	private List<Map<String, Object>> data_list_wardrobe;
	private String[] ids_wardrobe;
	private GridView gview_wardrobe;
	private List<Map<String, Object>> data_list_led_light;
	private String[] ids_led;
	private GridView gview_led_light;
	private List<Map<String, Object>> data_list_like;
	private String[] ids_like;
	private myBaseAdapter adapter;
	private boolean firstIn = true;
	private boolean Second_in = true;
	private boolean isConnecting = false;
	private String login_name;
	private String login_pwd;
	private SharedPreferences preferences;
	private Editor editor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_home, container, false);
		
		cycleImages = new CycleImages(R.id.vp, getContext(), dots, view, R.drawable.cycle_images_default, true, true);
		preferences = getContext().getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		setDefaultImg();
		check_connetion();
//		startGetAd();
//		
//		setDefaultImg();
//		
//		startGetGoodsImages();
		
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
	

	
	
	
	/**获取轮播图片*/
	private void startGetAd(){
		new Thread(){
			public void run() {

					getAd = new getAdImageUrl_do();
					boolean a = getAd.getAdImageUrl_success();
					if (a) {
						AdUrls = getAd.returnStrings();
						cycleImages.setAdImage(AdUrls);
					}else {
						System.out.println("失败");
					}
				
			};
		}.start();
	}
	/**加载首页默认图片*/
	private void setDefaultImg(){
		
		data_list_recommendation = new ArrayList<Map<String,Object>>();
		for(int i=0;i<4;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("product_image",R.drawable.recommendation_default);
			map.put("product_name"," ");
			map.put("product_price"," ");
			data_list_recommendation.add(map);
		}
		setGridViewAdapter(R.id.gView_recommendation, data_list_recommendation,R.layout.page_homepage_item_goods, From, To,null);
		
		data_list_classification = new ArrayList<Map<String,Object>>();
		int[] classification = new int[]{
				R.drawable.icon_chufang,
				R.drawable.icon_yigui,
				R.drawable.icon_jiaju,
				R.drawable.icon_gongneng
			};
		for (int i = 0; i < classification.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("image", classification[i]);
				data_list_classification.add(map);
		}
		setGridViewAdapter(R.id.gView_classification, data_list_classification, R.layout.page_homepage_only_image, new String[]{"image"}, new int[]{R.id.image_only_image},null);
		
		data_list_hotsale = new ArrayList<Map<String,Object>>();
		for(int i=0;i<3;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("product_image",R.drawable.hotsell_default);
			map.put("product_name"," ");
			map.put("product_price","  ");
			data_list_hotsale.add(map);
		}
		setGridViewAdapter(R.id.gView_hotsale, data_list_hotsale, R.layout.page_homepage_item_goods, From, To,null);
		
		data_list_kitchen = new ArrayList<Map<String,Object>>();
		data_list_wardrobe = new ArrayList<Map<String,Object>>();
		data_list_led_light = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("product_image",R.drawable.icon_kitchen_system);
		data_list_kitchen.add(map1);
		
		map1 = new HashMap<String, Object>();
		map1.put("product_image",R.drawable.icon_wardrobe_system);
		data_list_wardrobe.add(map1);
		map1 = new HashMap<String, Object>();
		map1.put("product_image",R.drawable.icon_led_light_system);
		data_list_led_light.add(map1);
		map1 = new HashMap<String, Object>();
		map1.put("product_image",R.drawable.icon_hardware_system);

		for (int i = 0; i < 5; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("product_image",R.drawable.system_default);
			map.put("product_name","  ");
			map.put("product_price","  ");
			data_list_kitchen.add(map);
			data_list_wardrobe.add(map);
			data_list_led_light.add(map);
		}
		gview_kitchen = (GridView)view.findViewById(R.id.gView_kitchen);
		gview_wardrobe = (GridView)view.findViewById(R.id.gView_wardrobe);
		gview_led_light = (GridView)view.findViewById(R.id.gView_led_light);
		adapter = new myBaseAdapter(getContext(), data_list_kitchen);
		gview_kitchen.setAdapter(adapter);
		adapter = new myBaseAdapter(getContext(), data_list_wardrobe);
		gview_wardrobe.setAdapter(adapter);
		adapter = new myBaseAdapter(getContext(), data_list_led_light);
		gview_led_light.setAdapter(adapter);
		
//		data_list_like = new ArrayList<Map<String,Object>>();
//		for (int i = 0; i < 6; i++) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("product_image",R.drawable.system_default);
//			map.put("product_name","  ");
//			map.put("product_price","  ");
//			
//			data_list_like.add(map);
//		}
//		setGridViewAdapter(R.id.gView_like, data_list_like, R.layout.page_homepage_item_goods, From, To,null);	
	}
	
	private void startGetGoodsImages(){
		new Thread(){
			public void run() {
				getHomePageUrl_do getHomePageUrl_do = new getHomePageUrl_do(UriAPI.getHomePageUri,"RecommendationCall");
				if (getHomePageUrl_do.getAdImageUrl_success()) {
					String[][] strings = getHomePageUrl_do.returnStrings();
					data_list_recommendation = new ArrayList<Map<String,Object>>();
					ids_recommendation = new String[strings.length];
					for (int i = 0; i < strings.length; i++) {
						ImageDownLoader imageDownLoader = new ImageDownLoader(getContext());
						String url = UriAPI.mainUri+strings[i][0].toString();
						Bitmap bitmap = imageDownLoader.showCacheBitmap(url,"recommendation"+url.replaceAll("[^\\w]", "") );
						ids_recommendation[i] = strings[i][1];
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("product_image",bitmap);
						map.put("product_name",strings[i][2].toString());
						map.put("product_price","￥"+strings[i][3].toString());
						data_list_recommendation.add(map);
					}
					mHandler.sendEmptyMessage(0);
				}
				getHomePageUrl_do = new getHomePageUrl_do(UriAPI.getHomePageUri, "HotsaleCall");
				if (getHomePageUrl_do.getAdImageUrl_success()) {
					String[][] strings = getHomePageUrl_do.returnStrings();
					data_list_hotsale = new ArrayList<Map<String,Object>>();
					ids_hotsale = new String[strings.length];
					for (int i = 0; i < strings.length; i++) {
						ImageDownLoader imageDownLoader = new ImageDownLoader(getContext());
						String url = UriAPI.mainUri+strings[i][0].toString();
						Bitmap bitmap = imageDownLoader.showCacheBitmap(url,"hotsale"+url.replaceAll("[^\\w]", "") );
						ids_hotsale[i] = strings[i][1];
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("product_image",bitmap);
						map.put("product_name",strings[i][2].toString());
						map.put("product_price","￥"+strings[i][3].toString());
						data_list_hotsale.add(map);
					}
					mHandler.sendEmptyMessage(1);
				}
				getHomePageUrl_do = new getHomePageUrl_do(UriAPI.getHomePageUri, "KitchenCall");
				if (getHomePageUrl_do.getAdImageUrl_success()) {
					String[][] strings = getHomePageUrl_do.returnStrings();
					data_list_kitchen = new ArrayList<Map<String,Object>>();
					ids_kitchen = new String[strings.length];
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("product_image",R.drawable.icon_kitchen_system);
					data_list_kitchen.add(map1);

					for (int i = 0; i < strings.length; i++) {
						ImageDownLoader imageDownLoader = new ImageDownLoader(getContext());
						String url = UriAPI.mainUri+strings[i][0].toString();
						Bitmap bitmap = imageDownLoader.showCacheBitmap(url,"kitchen"+url.replaceAll("[^\\w]", "") );
						ids_kitchen[i] = strings[i][1];
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("product_image",bitmap);
						map.put("product_name",strings[i][2].toString());
						map.put("product_price","￥"+strings[i][3].toString());
						data_list_kitchen.add(map);
					}
					mHandler.sendEmptyMessage(2);
				}
				getHomePageUrl_do = new getHomePageUrl_do(UriAPI.getHomePageUri, "WardrobeCall");
				if (getHomePageUrl_do.getAdImageUrl_success()) {
					String[][] strings = getHomePageUrl_do.returnStrings();
					data_list_wardrobe = new ArrayList<Map<String,Object>>();
					ids_wardrobe = new String[strings.length];
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("product_image",R.drawable.icon_wardrobe_system);
					data_list_wardrobe.add(map1);

					for (int i = 0; i < strings.length; i++) {
						ImageDownLoader imageDownLoader = new ImageDownLoader(getContext());
						String url = UriAPI.mainUri+strings[i][0].toString();
						Bitmap bitmap = imageDownLoader.showCacheBitmap(url,"wardrobe"+url.replaceAll("[^\\w]", "") );
						ids_wardrobe[i] = strings[i][1];
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("product_image",bitmap);
						map.put("product_name",strings[i][2].toString());
						map.put("product_price","￥"+strings[i][3].toString());
						data_list_wardrobe.add(map);
					}
					mHandler.sendEmptyMessage(3);
				}
				getHomePageUrl_do = new getHomePageUrl_do(UriAPI.getHomePageUri, "LedCall");
				if (getHomePageUrl_do.getAdImageUrl_success()) {
					String[][] strings = getHomePageUrl_do.returnStrings();
					data_list_led_light = new ArrayList<Map<String,Object>>();
					ids_led = new String[strings.length];
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("product_image",R.drawable.icon_led_light_system);
					data_list_led_light.add(map1);

					for (int i = 0; i < strings.length; i++) {
						ImageDownLoader imageDownLoader = new ImageDownLoader(getContext());
						String url = UriAPI.mainUri+strings[i][0].toString();
						Bitmap bitmap = imageDownLoader.showCacheBitmap(url,"led"+url.replaceAll("[^\\w]", "") );
						ids_led[i] = strings[i][1];
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("product_image",bitmap);
						map.put("product_name",strings[i][2].toString());
						map.put("product_price","￥"+strings[i][3].toString());
						data_list_led_light.add(map);
					}
					mHandler.sendEmptyMessage(4);
				}
			};
		}.start();
	}
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				setGridViewAdapter(R.id.gView_recommendation, data_list_recommendation, R.layout.page_homepage_item_goods, From, To, ids_recommendation);
				break;
			case 1:
				setGridViewAdapter(R.id.gView_hotsale, data_list_hotsale,R.layout.page_homepage_item_goods, From, To, ids_hotsale);
				break;
			case 2:
				adapter = new myBaseAdapter(getContext(), data_list_kitchen);
				gview_kitchen.setAdapter(adapter);
				gview_kitchen.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (position > 0) {
							Intent intent = new Intent(getActivity(), productActivity.class);
							intent.putExtra("product_id", ids_kitchen[position-1]);
							startActivity(intent);
						}
						
					}
				});
				break;
			case 3:
				adapter = new myBaseAdapter(getContext(), data_list_wardrobe);
				gview_wardrobe.setAdapter(adapter);
				gview_wardrobe.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (position > 0) {
							Intent intent = new Intent(getActivity(), productActivity.class);
							intent.putExtra("product_id", ids_wardrobe[position-1]);
							startActivity(intent);
						}
						
					}
				});
				break;
			case 4:
				adapter = new myBaseAdapter(getContext(), data_list_led_light);
				gview_led_light.setAdapter(adapter);
				gview_led_light.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (position > 0) {
							Intent intent = new Intent(getActivity(), productActivity.class);
							intent.putExtra("product_id", ids_led[position-1]);
							startActivity(intent);
						}
						
					}
				});
				break;
				
			default:
				break;
			}
			
		};
	};
	
	private void check_connetion(){
		ConnetionDetector connetionDetector = new ConnetionDetector(getContext());
		isConnecting = connetionDetector.isConnectionToInternet();
		if (!isConnecting) {
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
			Login_do login_do= new Login_do(getContext());
			login_do.check_login_save();
			startGetAd();
			
			setDefaultImg();
			
			startGetGoodsImages();
			

		}
	}
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (firstIn) {
			firstIn = false;
			return;
		}

		if (Second_in) {
			Second_in = false;
			return;
		}
		if (!hidden) {
			check_connetion();
		}
	}
	

	
	/**设置gridview的适配器*/
	private void setGridViewAdapter(int gviewId,List<Map<String, Object>> data_list,int itemLayoutId,String[] From,int[] To,final String ids[]){
		GridView gview = (GridView) view.findViewById(gviewId);
		
		//获取数据
		sim_adapter = new SimpleAdapter(getContext(), data_list,itemLayoutId,From,To);
		sim_adapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if ((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ImageView imageView = (ImageView) view;
                    Bitmap bitmap = (Bitmap) data;
                    imageView.setImageBitmap(bitmap);
                    return true;
                }
                return false;
			}
		});
		//配置适配器
		gview.setAdapter(sim_adapter);
		
		if (ids != null) {
			gview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(getActivity(), productActivity.class);
					intent.putExtra("product_id", ids[position]);
					startActivity(intent);
				}
			});
		}else {
			gview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String cla = "";
					switch (position) {
					case 0:
						cla = "厨房系统";
						break;
					case 1:
						cla = "衣柜空间";
						break;
					case 2:
						cla = "家居灯饰";
						break;

					default:
						break;
					}
					Intent intent = new Intent(getActivity(), search.class);
					intent.putExtra("search_code","class_a");
					intent.putExtra("class", cla);
					startActivity(intent);
					
				}
			});
		}
		gview.getMeasuredHeight();
		
		
	};

	


}
