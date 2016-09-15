package com.example.mixzone2;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class myBaseAdapter extends BaseAdapter {

	/**上下文*/
	private Context context;
	/**要绑定的数据列表*/
	private List<Map<String, Object>> list;
	/**布局*/
	private LayoutInflater layoutInflater;
	/**图片的view*/
	private ImageView imageView;
	
	
	public myBaseAdapter(Context context, List<Map<String, Object>> list) {
		this.list = list;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}


	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.list.get(position);
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
			if (position == 0) {
				convertView = layoutInflater.inflate(R.layout.page_homepage_only_image, null);
				convertView.setPadding(0, 0, 0, 0);
				ImageView imageView = (ImageView)convertView.findViewById(R.id.image_only_image);
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setImageResource((Integer) list.get(position).get("product_image"));

			}else {
				convertView = layoutInflater.inflate(R.layout.page_homepage_item_goods, null);
				try{
					imageView = (ImageView)convertView.findViewById(R.id.image_good);
					imageView.setImageBitmap((Bitmap)list.get(position).get("product_image"));
				}catch(Exception e){
					imageView.setImageResource((Integer) list.get(position).get("product_image"));
				}

				TextView textView = (TextView)convertView.findViewById(R.id.text_goods_name);
				TextView textView2 = (TextView)convertView.findViewById(R.id.text_goods_price);
				textView.setText(list.get(position).get("product_name").toString());
				textView2.setText(list.get(position).get("product_price").toString());
			}
		
		
		return convertView;
	}

}
