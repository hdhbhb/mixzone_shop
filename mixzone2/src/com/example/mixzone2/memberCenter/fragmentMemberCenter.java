package com.example.mixzone2.memberCenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.LoginPage.LoginActivity;
import com.example.mixzone2.LoginPage.LogoutActivity;
import com.example.mixzone2.search.search;
import com.example.mixzone2.tools.ConnetionDetector;

import Login_Logout_tools.Login_do;
import Login_Logout_tools.Logout_do;


public class fragmentMemberCenter extends Fragment {
	private SharedPreferences preferences;
	private boolean isConnecting = false;
	private boolean First_in = true;
	private TextView username_text;
	private View view;
	private myListView member_manage;
	private myListView member_center;
	private List<Map<String, Object>> data_list_manager;
	private List<Map<String, Object>> data_list_center;
	private String[] from = new String[]{"item_image","item_name"};
	private int[] to = new int[]{R.id.image_member_item,R.id.text_member_item};
	private String[] dlm = new String[]{"用户信息"};//,"账号安全"
	private int[] dlm1 = new int[]{R.drawable.ic_member_message,R.drawable.ic_member_secure};
	
	private String[] dlc = new String[]{"我的订单","收货地址"};//,"我的收藏","我的评论"
	private int[] dlc1 = new int[]{R.drawable.ic_member_mybuylist,R.drawable.ic_member_mylikelist
			,R.drawable.ic_member_myword,R.drawable.ic_member_myplace};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_membercenter, container,false);
		
		preferences = getActivity().getSharedPreferences(ShareString.ShareName,Context.MODE_PRIVATE);
		username_text = (TextView)view.findViewById(R.id.text_member_item);
		setManagerList();
		
		setCenterList();
		
		ImageView imageView = (ImageView) view.findViewById(R.id.image_search);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), search.class);
				startActivity(intent);
			}
		});
		
		ImageView user_img = (ImageView)view.findViewById(R.id.image_menber_head);
		user_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!preferences.getBoolean(ShareString.isLogin, false)) {
					Intent intent = new Intent(getContext(),LoginActivity.class);
					startActivity(intent);
				}else {
					Intent intent = new Intent(getContext(),LogoutActivity.class);
					startActivity(intent);
				}
				
			}
		});
		
		
		
		return view;
	}
	
	private void setManagerList(){
		member_manage = (myListView)view.findViewById(R.id.member_manager);
		data_list_manager = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < dlm.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item_name", dlm[i]);
			map.put("item_image",dlm1[i]);
			data_list_manager.add(map);
		}
		
		
		SimpleAdapter adapter = new SimpleAdapter(getContext(), data_list_manager, 
				R.layout.page_member_center_item, from, to);
		member_manage.setAdapter(adapter);
		
		member_manage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
	}
	
	private void setCenterList(){
		member_center = (myListView) view.findViewById(R.id.member_center);
		data_list_center = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < dlc.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item_name", dlc[i]);
			map.put("item_image",dlc1[i]);
			data_list_center.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getContext(),data_list_center,R.layout.page_member_center_item,from,to);
		member_center.setAdapter(adapter);
		member_center.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					if (!checkLogin()) {
						Intent intent = new Intent(getContext(),LoginActivity.class);
						startActivity(intent);
					}else {
						Intent intent = new Intent(getContext(),showBuylistActivity.class);
						startActivity(intent);
					}
					break;
				case 1:
					if (!checkLogin()) {
						Intent intent = new Intent(getContext(),LoginActivity.class);
						startActivity(intent);
					}else {
						Intent intent = new Intent(getContext(),editUserAddress.class);
						startActivity(intent);
					}
					break;
				case 2:
	
					break;
				case 3:
					
					
					break;

				default:
					break;
				}
			}
		});
	}


	@Override
	public void onResume() {
		if (preferences.getBoolean(ShareString.isLogin, false)) {
			username_text.setText(preferences.getString(ShareString.Username, ""));
		}else {
			username_text.setText(R.string.noLogin);
			System.out.println("没有登陆");
		}
		super.onResume();
	}
	
	private boolean checkLogin(){
		if (!preferences.getBoolean(ShareString.isLogin, false)) {
			return false;
		}else {
			return true;
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (First_in) {
			First_in = false;
			return;
		}
		if (!hidden) {
			check_connetion();
		}
	}
	
	private void check_connetion(){
		ConnetionDetector connetionDetector = new ConnetionDetector(getContext());
		isConnecting = connetionDetector.isConnectionToInternet();
		if (!isConnecting) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
			mBuilder.setCancelable(false);
			mBuilder.setTitle("没有网络连接");
			mBuilder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					check_connetion();
				}
			});
			mBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			mBuilder.create().show();
		}else {
			if (preferences.getBoolean(ShareString.isLogin, false)) {
				username_text.setText(preferences.getString(ShareString.Username, ""));
			}else {
				Login_do logout_do = new Login_do(getContext());
				logout_do.check_login_save();
				username_text.setText(R.string.noLogin);
				System.out.println("没有登陆");
			}
		}
	}

	
	

	
	
}
