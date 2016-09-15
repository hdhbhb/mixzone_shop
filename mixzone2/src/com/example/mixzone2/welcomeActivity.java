package com.example.mixzone2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class welcomeActivity extends Activity {
	
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_welcome);
		
		preferences = this.getSharedPreferences("mixzoneshop", Context.MODE_PRIVATE);
		int width = preferences.getInt("phoneWidth", 0);
		int heigth = preferences.getInt("phoneHeight", 0);
		TextView textView = (TextView)findViewById(R.id.text_car_good_name);
		textView.setText("ÆÁÄ»¿í£º"+width+"ÆÁÄ»¸ß£º"+heigth);
		
	}
}
