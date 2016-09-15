package com.example.mixzone2.shoppingCar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import com.example.mixzone2.R;
import com.example.mixzone2.ShareString;
import com.example.mixzone2.UriAPI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShoppingCarAdapter extends BaseAdapter {
	
	private Context context ;
	private fragmentShoppingCarPage carPage;
	private List<Map<String, Object>> data_list;
	private static HashMap<Integer, Boolean> isSelected;
	private boolean isDoing = false; 
	private boolean isSuccess = false;
	private ViewHolder viewHolder;
	
	
	public class ViewHolder{
		CheckBox checkBox;
		ImageView imageView;
		TextView name;
		TextView price;
		TextView size;
		ImageView img_sub;
		ImageView img_add;
		TextView product_count;
		Button btn_delete;
		
		
	}
	
	@SuppressLint("UseSparseArrays")
	public ShoppingCarAdapter(Context context,fragmentShoppingCarPage carPage,List<Map<String, Object>> data_list) {
		this.context = context;
		this.carPage = carPage;
		this.data_list = data_list;
		isSelected = new HashMap<Integer, Boolean>();
		initDate();
	}
	
	
	
	private void initDate(){
		for (int i = 0; i < data_list.size(); i++) {
			getIsSelected().put(i,false );
		}
	}

	@Override
	public int getCount() {
		return data_list.size();
	}

	@Override
	public Object getItem(int position) {
		return data_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		Bitmap bitmap = (Bitmap) data_list.get(position).get("Image");
		LayoutInflater inflater = LayoutInflater.from(context);
		final int location = position;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.page_shopping_car_item, null);
			holder = new ViewHolder();
			
			holder.imageView = (ImageView)convertView.findViewById(R.id.image_car_good);
			holder.imageView.setImageBitmap(bitmap);
			
			holder.name = (TextView)convertView.findViewById(R.id.text_car_good_name);
			holder.name.setText((String)data_list.get(position).get("product_name"));
			
			holder.size = (TextView)convertView.findViewById(R.id.text_size);
			holder.size.setText((String)data_list.get(position).get("product_size"));
			
			holder.price = (TextView)convertView.findViewById(R.id.text_price);
			holder.price.setText((String)data_list.get(position).get("product_price"));
			
			holder.checkBox = (CheckBox)convertView.findViewById(R.id.check_choose);
			holder.img_add = (ImageView)convertView.findViewById(R.id.image_car_good_add);
			holder.img_sub = (ImageView)convertView.findViewById(R.id.image_car_good_sub);
			
			holder.product_count = (TextView)convertView.findViewById(R.id.text_car_good_count);
			holder.product_count.setText((String)data_list.get(position).get("product_count"));
			
			holder.btn_delete = (Button)convertView.findViewById(R.id.btn_delete_car_good);
			
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				Toast.makeText(context, "check"+location, Toast.LENGTH_SHORT).show();
				if (isChecked) {
					isSelected.put(location, true);
					setIsSelected(isSelected);
					carPage.updateCheck();
				}else {
					isSelected.put(location, false);
					setIsSelected(isSelected);
					carPage.updateCheck();
				}
			}
		});
		holder.checkBox.setChecked(getIsSelected().get(location));
		holder.img_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isSuccess = false;
				isDoing = true;
				ProductaddInCar( data_list.get(location).get("product_id").toString(), data_list.get(location).get("product_size").toString(),"1");
				while (isDoing) {
				}
				if (isSuccess) {
					holder.checkBox.setChecked(true);
					isSelected.put(location, true);
					setIsSelected(isSelected);
					data_list.get(location).put("product_count", Integer.parseInt(data_list.get(location).get("product_count").toString())+1+"");
					holder.product_count.setText(data_list.get(location).get("product_count").toString());
					carPage.data_list.get(location).put("product_count", data_list.get(location).get("product_count"));
					carPage.updateCheck();
				}else {
					Toast.makeText(context, "Ìí¼ÓÊ§°Ü", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		holder.img_sub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int count = Integer.parseInt(data_list.get(location).get("product_count").toString());
				if (count > 1) {
					isSuccess = false;
					isDoing = true;
					ProductaddInCar( data_list.get(location).get("product_id").toString(), data_list.get(location).get("product_size").toString(),"-1");
					while (isDoing) {
					}
					if (isSuccess) {
					count -= 1;
					holder.checkBox.setChecked(true);
					isSelected.put(location, true);
					setIsSelected(isSelected);
					data_list.get(location).put("product_count", count+"");
					holder.product_count.setText(count+"");
					carPage.data_list.get(location).put("product_count", data_list.get(location).get("product_count"));
					carPage.updateCheck();
					}
				}
			}
		});
		holder.btn_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isSuccess = false;
				isDoing = true;
				deleteFromCar(data_list.get(location).get("product_id").toString(), data_list.get(location).get("product_size").toString());
				while (isDoing) {
				}
				if (isSuccess) {
					carPage.startGetShoppingCar();
				}
			}
		});
		return convertView;
	}
	
	
	
	private void ProductaddInCar(final String product_id,final String product_size,final String addORsub){
			new Thread(){
				public void run() {
					try {
						SharedPreferences preferences = context.getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
						String username = preferences.getString(ShareString.Username, ""); 
						URL url = new URL(UriAPI.addInCarUri);
						HttpURLConnection conn = (HttpURLConnection)url.openConnection();
						conn.setConnectTimeout(5000);
						conn.setReadTimeout(5000);
						conn.setDoInput(true);
						conn.setDoOutput(true);
						String data = "username="+URLEncoder.encode(username,"UTF-8")+"&product_id="+URLEncoder.encode(product_id,"UTF-8")+"&product_size="+product_size+"&addORsub="+addORsub;
						OutputStream os = conn.getOutputStream();
						os.write(data.getBytes("UTF-8"));
						os.flush();
						os.close();

						if (conn.getResponseCode() == 200) {
							InputStream is = conn.getInputStream();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							int len = 0;
							byte buffer[] = new byte[1024];
							while((len = is.read(buffer))!= -1){
								baos.write(buffer,0,len);
							}
							is.close();
							baos.close();
							String result = baos.toString();
							if (result.equals("addSuccess")) {
								isSuccess = true;
								isDoing = false;
							}else {
								isSuccess = false;
								isDoing = false;
							}
						}else {
							isSuccess = false;
							isDoing = false;
						}
						
						} catch (Exception e) {
							isSuccess = false;
							isDoing = false;
							e.printStackTrace();
						}
					
				};
			}.start();
	}
	
	private void deleteFromCar(final String product_id,final String product_size){
		new Thread(){
			public void run() {
				try {
					SharedPreferences preferences = context.getSharedPreferences(ShareString.ShareName, Context.MODE_PRIVATE);
					String username = preferences.getString(ShareString.Username, ""); 
					URL url = new URL(UriAPI.deleteFromCar);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					String data = "username="+URLEncoder.encode(username,"UTF-8")+"&product_id="+URLEncoder.encode(product_id,"UTF-8")+"&product_size="+URLEncoder.encode(product_size,"UTF-8");
					OutputStream os = conn.getOutputStream();
					os.write(data.getBytes("UTF-8"));
					os.flush();
					os.close();

					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						int len = 0;
						byte buffer[] = new byte[1024];
						while((len = is.read(buffer))!= -1){
							baos.write(buffer,0,len);
						}
						is.close();
						baos.close();
						String result = baos.toString();
						if (result.equals("deleteSuccess")) {
							isSuccess = true;
							isDoing = false;
						}else {
							isSuccess = false;
							isDoing = false;
						}
					}else {
						isSuccess = false;
						isDoing = false;
					}
					
					} catch (Exception e) {
						isSuccess = false;
						isDoing = false;
						e.printStackTrace();
					}
			};
		}.start();
	}
	
	public static HashMap<Integer, Boolean> getIsSelected(){
		return isSelected;
	}
	
	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {  
        ShoppingCarAdapter.isSelected = isSelected;  
    }  

}
