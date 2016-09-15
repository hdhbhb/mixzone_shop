package com.example.mixzone2;

import java.io.File;

import com.example.mixzone2.tools.ConnetionDetector;

import Login_Logout_tools.Login_do;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends FragmentActivity {
	/**sharepreference*/
	private SharedPreferences preferences;
	private Editor editor;
	/**fragment*/
	private Fragment[] mFragment;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private RadioGroup radiogroup; 
	private boolean isRun = true;
	private boolean isConnecting;
	private boolean isExit = false;
	private String login_name;
	private String login_pwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		check_first();
		
//		check_connetion();
		
		check_login_save();
		
		load_fragment();
		
	}
	/**判断是否第一次开启应用*/
	private void check_first(){
		preferences = getSharedPreferences(ShareString.ShareName,Context.MODE_PRIVATE);
		if (preferences.getBoolean(ShareString.FirstStart, true)) {
			
			WindowManager wManager = this.getWindowManager();
			DisplayMetrics metrics = new DisplayMetrics();
			wManager.getDefaultDisplay().getMetrics(metrics);
			int width = metrics.widthPixels;
			int height = metrics.heightPixels;
			
			editor = preferences.edit();
			editor.putBoolean(ShareString.FirstStart, false);
			editor.putInt(ShareString.PhoneWidth, width);
			editor.putInt(ShareString.PhoneHeight, height);
			editor.commit();
			
			File cache = new File(Environment.getExternalStorageDirectory(),"Android");
			if(!cache.exists()){//如果不存在该文件夹，则创建
				cache.mkdirs();
			}
			cache = new File(cache.toString(),"data");
			if(!cache.exists()){//如果不存在该文件夹，则创建
				cache.mkdirs();
			}
			cache = new File(cache.toString(),"com.mixzone.mixzoneshop");
			if(!cache.exists()){//如果不存在该文件夹，则创建
				cache.mkdirs();
			}
			cache = new File(cache.toString(),"cache");
			if(!cache.exists()){//如果不存在该文件夹，则创建
				cache.mkdirs();
			}
			cache = new File(cache.toString(),"ImagesCache");
			if(!cache.exists()){//如果不存在该文件夹，则创建
				cache.mkdirs();
			}
			cache = new File(cache.toString(),"Goods");
			if(!cache.exists()){//如果不存在该文件夹，则创建
				cache.mkdirs();
			}
			
			Intent intent = new Intent(this, welcomeActivity.class);
			startActivity(intent);
		}
	}
	
	private void check_login_save(){
		if (preferences.getBoolean(ShareString.SaveLogin, false)) {
			login_name = preferences.getString(ShareString.Username, "");
			login_pwd = preferences.getString(ShareString.Password, "");

			Thread thread = new Thread(new login());
			thread.start();
		}
	}
	
	private class login implements Runnable {
		public void run() {
			Login_do login_do = new Login_do(login_name, login_pwd);
			if (login_do.login_success()) {
				editor = preferences.edit();
				editor.putBoolean(ShareString.isLogin, true);
				editor.commit();
			}
		}
	}
	

	
	/**加载5个fragment*/
	private void load_fragment(){
//		mFragment = new Fragment[5];
		mFragment = new Fragment[4];
		fragmentManager = getSupportFragmentManager();
		mFragment[0] = fragmentManager.findFragmentById(R.id.fragment_home_page);
		mFragment[1] = fragmentManager.findFragmentById(R.id.fragment_classification_page);
//		mFragment[2] = fragmentManager.findFragmentById(R.id.fragment_jiazhuangbaike_page);
//		mFragment[3] = fragmentManager.findFragmentById(R.id.fragment_shopping_car_page);
//		mFragment[4] = fragmentManager.findFragmentById(R.id.fragment_member_center_page);
		mFragment[2] = fragmentManager.findFragmentById(R.id.fragment_shopping_car_page);
		mFragment[3] = fragmentManager.findFragmentById(R.id.fragment_member_center_page);
		fragmentTransaction = fragmentManager.beginTransaction().hide(mFragment[0]).hide(mFragment[1]).hide(mFragment[2]).hide(mFragment[3]);
		fragmentTransaction.show(mFragment[0]).commit();
		setFragmentIndicator();
	}
	
	/**设置radiobutton的监听事件*/
	private void setFragmentIndicator(){
		
		radiogroup = (RadioGroup)findViewById(R.id.footbar);	
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				fragmentTransaction = fragmentManager.beginTransaction().hide(mFragment[0]).hide(mFragment[1]).hide(mFragment[2]).hide(mFragment[3]);
				switch(checkedId){
				case R.id.button_home_page:
					fragmentTransaction.show(mFragment[0]).commit();
					break;
				case R.id.button_classification_page:
					fragmentTransaction.show(mFragment[1]).commit();
					break;
//				case R.id.button_activities_page:
//					fragmentTransaction.show(mFragment[2]).commit();
//					break;
				case R.id.button_shopping_car_page:
					fragmentTransaction.show(mFragment[2]).commit();
					break;
				case R.id.button_menber_center_page:
					fragmentTransaction.show(mFragment[3]).commit();
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void checkout(){	
			editor = preferences.edit();
			editor.putBoolean(ShareString.isLogin, false);
			editor.commit();
		
	}
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		};
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void exit(){
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		}else {
			finish();
			System.exit(0);
		}
	}
	
	@Override
	public void finish() {
		checkout();
		super.finish();
	}

}
