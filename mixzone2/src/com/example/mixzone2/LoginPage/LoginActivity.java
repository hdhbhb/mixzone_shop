package com.example.mixzone2.LoginPage;



import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;

import Login_Logout_tools.Login_do;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private SharedPreferences preferences;
	private Editor editor;
	private ImageView close_img;
	private EditText editText1;
	private EditText editText2;
	private CheckBox check_save;
	private Button btn_login;
	private TextView text_register;
	private String username;
	private String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_login);
		
		login();
		
	}
	
	private void login(){
		close_img = (ImageView)findViewById(R.id.image_logout_close);
		close_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		editText1 = (EditText)findViewById(R.id.edit_username);
		editText2 = (EditText)findViewById(R.id.edit_password);
		preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		if (!preferences.getString(ShareString.Username, "").equals("")) {
			editText1.setText(preferences.getString(ShareString.Username, ""));
		}
		
		
		check_save = (CheckBox)findViewById(R.id.check_save_login);
		btn_login = (Button)findViewById(R.id.btn_register);
		text_register = (TextView)findViewById(R.id.text_register);
		text_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				username = editText1.getText().toString();
				password = editText2.getText().toString();
				new Thread(){
					public void run() {
						Login_do login_do = new Login_do(username, password);
						if (login_do.login_success()) {
							
							editor = preferences.edit();
							editor.putBoolean(ShareString.isLogin, true);
							if (check_save.isChecked()) {
								mHandler.sendEmptyMessage(2);
								editor.putBoolean(ShareString.SaveLogin,true);
								editor.putString(ShareString.Username, username);
								editor.putString(ShareString.Password, password);
							}
							editor.commit();
							mHandler.sendEmptyMessage(0);
							finish();
						}else{
							mHandler.sendEmptyMessage(1);
						}
					};
				}.start();;
			}
		});
	}
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(LoginActivity.this,"登陆失败，请检查用户名或密码是否有误",Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(LoginActivity.this,"保存密码",Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			
			
		};
	};

	
}
