package com.example.mixzone2.tools;

import java.io.File;
import java.net.URI;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mixzone2.Data;
import com.example.mixzone2.ImageDownLoader;
import com.example.mixzone2.R;
import com.example.mixzone2.UriAPI;
import com.example.mixzone2.getImageUrl.getAdImageUrl_do;


import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CycleImages {

	/**存放轮播图片的列表 */
	private ArrayList<ImageView> imageViews;
	/**轮播图片的viewpager*/
	private ViewPager viewPager;
	/**轮播图片适配器*/
	private CycleImagesAdapter cycleImages;
	/**轮播小圆点的样式列表*/
	private List<View> dots;
	private int[] dotCount;
	/**用于跳转下一张图片标号的标识*/
	private static int currentItem;
	/**上一次图片标号的标识*/
	private int oldPosition = 0;
	/**判断是否第一次运行自动轮播线程的布尔值*/
	private boolean first_in = true;
	/**判断是否正在滑动轮播图片的布尔值*/
	private boolean isScrolling = false;	
	/**本首页fragment的layout文件*/
	private View view;
	/**更新广告图片轮播的消息标号*/
	private int CHANGE_IMAGE = 0 ;
	/**图片轮播线程*/
	private Thread cycle_image_thread;

	/**轮播图片用的bitmap*/
	private Bitmap bitmaps[];

	private String CYCLE_IMAGE = "cycleimg";
	/**viewPager的控件id*/
	private int viewPagerId;
	/**轮播图片的数量*/
	private int count;
	/**上下文对象*/
	private Context context;

	/**网络图片地址数组*/
	private String[] urls;
	/**轮播图片的id数组*/
	private String[] product_ids;
	/**设置是否循环*/
	private boolean CycleRun;
	/**设置是否自动播放*/
	private boolean CyclePlay;
	/**轮播图片循环数量*/
	private int CycleCount = 2000;
	private int CycleFocus = CycleCount/2;
	
	//控制睡眠时间的属性
	/**初始睡眠时间*/
	private int firstSleepTime = 0;
	/**图片自动切换时间*/
	private int SleepTime = 4000;
	private List<Map<String, Object>> data;
	private String[] removeUrl;
	
	
	/**
	 * @param viewPagerId viewPager的id对象
	 * @param defaultImg 默认图片
	 * @param context 上下文
	 * @param dotCount 点控件id数组
	 * @param view 对象的view
	 * @param CycleRun 是否循环
	 * @param CyclePlay 是否播放
	 * */
	public CycleImages(int viewPagerId,Context context,int[] dotCount,View view,int defaultImage,boolean CycleRun,boolean CyclePlay){
		this.viewPagerId = viewPagerId;
		this.context = context;
		this.view = view;
		this.dotCount = dotCount;
		this.CycleRun = CycleRun;
		this.CyclePlay = CyclePlay;
		viewPager = (ViewPager) view.findViewById(viewPagerId);//初始化viewpager的布局控件
		imageViews = new ArrayList<ImageView>();//初始化轮播图片列表	
		for (int i = 0; i < dotCount.length; i++) {
			ImageView imageView = new ImageView(context);
			imageView.setBackgroundResource(defaultImage);
			imageViews.add(imageView);
		}
		dots = new ArrayList<View>();
		for(int i = 0;i < dotCount.length;i++){
			dots.add(view.findViewById(dotCount[i]));
		}
		cycleImages = new CycleImagesAdapter(imageViews);
		viewPager.setAdapter(cycleImages);//为viewpager配置适配器
		if (this.CycleRun) {
			viewPager.setCurrentItem(imageViews.size()*CycleFocus);//把viewpager设置为中间位置
			currentItem = imageViews.size()*CycleFocus;
		}else {
			viewPager.setCurrentItem(0);//把viewpager设置为中间位置
			currentItem = imageViews.size()*CycleFocus;
		}
		
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			
			//该方法在图片更新完成后调用
			@Override
			public void onPageSelected(int arg0) {			
				currentItem = arg0;
				arg0 %=imageViews.size();
				dots.get(arg0).setBackgroundResource(R.drawable.dot_focused);
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				oldPosition = arg0;
			}		
			//该方法
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}		
			//该方法在图片滑动状态变化的时候调用
			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == ViewPager.SCROLL_STATE_DRAGGING) {
					isScrolling = true;
				}else {
					isScrolling = false;
				}
			}
		});		
		
		if (CyclePlay) {
			cycle_image_thread = new Thread(new CycleViewPager());
			cycle_image_thread.start();
		}
	}
	
	public void setAdImage(String[][] url){
		urls = new String[url.length];
		product_ids = new String[url.length];
		for (int i = 0; i < url.length; i++) {
			this.urls[i] = UriAPI.mainUri+url[i][0];
			this.product_ids[i] = url[i][1];
		}
		Thread thread = new Thread(new startImageDownloader());
		thread.start();
	}
	
	public void setProductImage(String[] url){
		this.urls = new String[url.length];
		for (int i = 0; i < url.length; i++) {
			urls[i] = UriAPI.mainUri+url[i];
		}
		Thread thread = new Thread(new startImageDownloader());
		thread.start();
	}
	
	private class startImageDownloader implements Runnable {

		@Override
		public void run() {
			bitmaps = new Bitmap[urls.length];
			data = new ArrayList<Map<String,Object>>();
			removeUrl = new String[urls.length];
			for (int i = 0; i < urls.length; i++) {

				ImageDownLoader imageDownLoader = new ImageDownLoader(context);
				String imgName = urls[i].toString().replaceAll("[^\\w]", "");
				removeUrl[i] = imgName;
				bitmaps[i] = imageDownLoader.showCacheBitmap(urls[i].toString(), imgName);
				if (bitmaps[i] == null) {
					break;
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("img", bitmaps[i]);
				data.add(map);
			}
			if (bitmaps[0] != null) {
				mHandler.sendEmptyMessage(1);
			}
		}

	}
	

	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){	
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				viewPager.setCurrentItem(currentItem);
				break;
			case 1:	
				for (int i = 0; i < bitmaps.length; i++) {
					ImageView imageView = new ImageView(context);
					imageView.setImageBitmap(bitmaps[i]);					
					imageViews.set(i, imageView);
				}
				cycleImages = new CycleImagesAdapter(imageViews);
				viewPager.setAdapter(cycleImages);
				break;
			default:
				break;
			}
			
		};
	};
	
	/**图片广告轮播线程运行方法*/
	private class CycleViewPager implements Runnable{
		@Override
		public void run() {
			//判断是否第一次运行该线程，睡眠时间与引导页一致，防止图片提早切换，若无用可以去掉该判断
			if (first_in) {
				first_in = false;
				try {
					Thread.sleep(firstSleepTime);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}	
			while (true) {
				try {
					Thread.sleep(SleepTime);
					//判断是否处于手指滑动的状态，是则不自动切换图片
					if (!isScrolling) {
						currentItem += 1;
						mHandler.sendEmptyMessage(CHANGE_IMAGE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public void removeBitmap(){
		Data.removeBitmapFromLrucache(removeUrl);
	}

	
	
	/**轮播图片适配器*/
	private class CycleImagesAdapter extends PagerAdapter {

		private ArrayList<ImageView> imageViews;

		public CycleImagesAdapter(ArrayList<ImageView> views) {
			this.imageViews = views;
		}
		
		@Override
		public int getCount() {
			if (!CycleRun) {
				return imageViews.size();
			}
			return imageViews.size()*CycleCount;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			//根据取余结果取viewlist中的图片出来显示
			position %=imageViews.size();
			
			if (position<0) {
				position = imageViews.size()+position;
			}
			
			//若有父控件，要先将父控件去掉，才能加载新的控件
			ViewParent viewParent = imageViews.get(position).getParent();
			if (viewParent != null) {
				ViewGroup parent = (ViewGroup)viewParent;
				parent.removeView(imageViews.get(position));;
			}
			
			final int i = position;
			
			ImageView view = imageViews.get(position);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch (i) {
					case 0:
						System.out.println(0);
						break;
					case 1:
						System.out.println(1);
						break;
					case 2:
						
						System.out.println(2);
						break;
					case 3:
						System.out.println(3);
						break;
					case 4:
						System.out.println(4);
						break;
					default:
						break;
					}

				}
			});
			
			container.addView(imageViews.get(position));
			return imageViews.get(position);
		}

	}
}
