package com.example.mixzone2.memberCenter;


import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class addUserAddress extends Activity {
	private Spinner spinner_province;
	private Spinner spinner_city;
	private Spinner spinner_area;
	private String[] sp_province ;
	private String[] sp_city;
	private String[] sp_area;
	private String province;
	private String city;
	private String area;
	private String Arearequest;
	private String RequestArea = null;
	private ArrayAdapter<String> pAdapter;
	private ArrayAdapter<String> cAdapter;
	private ArrayAdapter<String> aAdapter;
	private String father = null;
	private EditText detail_address;
	private EditText name;
	private EditText phone;
	private ImageView back;
	private Button btn_add;
	private String username;
	private String people;
	private String detail_add;
	private String phonenumber;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_address_page);
		spinner_province = (Spinner)findViewById(R.id.spinner_province);
		spinner_city = (Spinner)findViewById(R.id.spinner_city);
		spinner_area = (Spinner)findViewById(R.id.spinner_area);
		SharedPreferences preferences = getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
		username = preferences.getString(ShareString.Username, "");
		detail_address = (EditText)findViewById(R.id.edit_address);
		name = (EditText)findViewById(R.id.edit_name);
		phone = (EditText)findViewById(R.id.edit_phone);
		back =(ImageView)findViewById(R.id.image_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_add = (Button)findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAdd();
			}
		});
		
		
		
		setArea();
		
	}
	
	private void startAdd(){
		
		
		detail_add = detail_address.getText().toString();
		phonenumber = phone.getText().toString();
		people = name.getText().toString();
		if (detail_add.equals("")||people.equals("")) {
			Toast.makeText(this, "信息不能为空", Toast.LENGTH_SHORT).show();
		}
		if (phonenumber.length() != 11) {
			Toast.makeText(this, "手机号码有误", Toast.LENGTH_SHORT).show();
			return;
		}
			
		Thread thread = new Thread(new addRun());
		thread.start();
	}
	
	private class addRun implements Runnable{

		@Override
		public void run() {
			addAddress_do addAddress_do = new addAddress_do(username, detail_add, province, city, area, people, phonenumber);
			if (addAddress_do.returnAdd()) {
				mHandler.sendEmptyMessage(3);
			}else {
				mHandler.sendEmptyMessage(4);
			}
		}
		
	}
	
	private void setArea(){
		Arearequest = "province";
		Thread thread = new Thread(new getArea());
		thread.start();
	}
	
	private class getArea implements Runnable{
		@Override
		public void run() {
			getArea_do getArea_do = new getArea_do(Arearequest,RequestArea,father);
			if (Arearequest.equals("province")) {
				sp_province = getArea_do.returnArea();
				mHandler.sendEmptyMessage(0);
			}else if (Arearequest.equals("city")) {
				sp_city = getArea_do.returnArea();
				mHandler.sendEmptyMessage(1);
			}else if (Arearequest.equals("area")) {
				sp_area = getArea_do.returnArea();
				mHandler.sendEmptyMessage(2);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (sp_province != null) {
					
					pAdapter = new ArrayAdapter<String>(addUserAddress.this, android.R.layout.simple_spinner_item, sp_province);
					spinner_province.setAdapter(pAdapter);
					spinner_province.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							Arearequest = "city";
							RequestArea =sp_province[position];
							province = RequestArea;
							Thread thread = new Thread(new getArea());
							thread.start();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				break;
			case 1:
				if (sp_city != null) {
					cAdapter = new ArrayAdapter<String>(addUserAddress.this, android.R.layout.simple_spinner_item, sp_city);
					spinner_city.setAdapter(cAdapter);
					spinner_city.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							Arearequest = "area";
							RequestArea =sp_city[position];
							city = RequestArea;
							father = province;
							Thread thread = new Thread(new getArea());
							thread.start();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				break;
			case 2:
				if (sp_area != null) {
					aAdapter = new ArrayAdapter<String>(addUserAddress.this, android.R.layout.simple_spinner_item, sp_area);
					spinner_area.setAdapter(aAdapter);
					spinner_area.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							area = sp_area[position];
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							
						}
					});
				}
				break;
			case 3:
				Toast.makeText(addUserAddress.this, "添加成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 4:
				Toast.makeText(addUserAddress.this, "添加失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	
	
}
