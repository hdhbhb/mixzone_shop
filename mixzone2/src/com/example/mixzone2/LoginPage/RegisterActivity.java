package com.example.mixzone2.LoginPage;

import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;

import Login_Logout_tools.Register_do;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private String username;
	private String password;
	private String password2;
	private EditText user_name;
	private EditText user_pwd;
	private EditText user_pwd2;
	private ImageView close;
	private Button btn_register;
	private CheckBox check_save;
	private Register_do register_do;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);
		user_name = (EditText)findViewById(R.id.edit_username);
		user_pwd = (EditText)findViewById(R.id.edit_password);
		user_pwd2 = (EditText)findViewById(R.id.edit_password_check);
		close = (ImageView)findViewById(R.id.image_logout_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		check_save = (CheckBox)findViewById(R.id.check_save_login);
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
	}
	private void register(){
		username = user_name.getText().toString();
		password = user_pwd.getText().toString();
		password2 = user_pwd2.getText().toString();
		if (username.equals("")) {
			Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.equals("")) {
			Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password2.equals("")) {
			Toast.makeText(RegisterActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!checkString(username,20,6)) {
			Toast.makeText(RegisterActivity.this, "用户名不合法", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!checkString(password,30,6)) {
			Toast.makeText(RegisterActivity.this, "密码不合法", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!checkString(password2,30,6)) {
			Toast.makeText(RegisterActivity.this, "确认密码不合法", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!password.equals(password2)) {
			Toast.makeText(RegisterActivity.this, "两次密码不一样", Toast.LENGTH_SHORT).show();
			return;
		}
		Thread thread = new Thread(new register());
		thread.start();
	}
	
	private class register implements Runnable{

		@Override
		public void run() {
			register_do = new Register_do(RegisterActivity.this,username, password);
			if (register_do.register_success()) {
				mHandler.sendEmptyMessage(0);
			}else {
				mHandler.sendEmptyMessage(1);
			}
		}
		
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				SharedPreferences preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putBoolean(ShareString.isLogin, true);
				editor.putString(ShareString.Username, username);
				editor.commit();
				if (check_save.isChecked()) {
					editor.putBoolean(ShareString.SaveLogin, true);
					editor.putString(ShareString.Password, password);
				}
				Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 1:
				Toast.makeText(RegisterActivity.this, register_do.failed_result+",请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	
	private boolean checkString(String string,int MaxLength,int MinLength){
		int count = string.length();
		if (!(count<=MaxLength&&count>=MinLength)) {
			return false;
		}
		for (int i = 0; i < string.length(); i++) {
			char a = string.charAt(i);
			if (!((a>='0'&&a<='9')||(a>='a'&&a<='z')||(a>='A'&&a<='Z'))) {
				return false;
			}
		}
		return true;
	}
}
