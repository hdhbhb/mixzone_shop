package com.example.mixzone2.LoginPage;



import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;

import Login_Logout_tools.Logout_do;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class LogoutActivity extends Activity {
	private ImageView close_img;
	private Button logout;
	private SharedPreferences preferences;
	private Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logout_page);
		preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		close_img = (ImageView)findViewById(R.id.image_logout_close);
		close_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		logout = (Button)findViewById(R.id.btn_logout);
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				logout_check();
			}
		});
	}
	
	private void logout_check(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle("确认退出？");
		builder.setPositiveButton("确认",new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editor = preferences.edit();
				editor.putBoolean(ShareString.isLogin, false);
				editor.putBoolean(ShareString.SaveLogin, false);
				editor.putString(ShareString.Password, "");
				editor.commit();
				LogoutActivity.this.finish();
			}
		});
		builder.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
}
