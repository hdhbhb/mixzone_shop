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

	/**����ֲ�ͼƬ���б� */
	private ArrayList<ImageView> imageViews;
	/**�ֲ�ͼƬ��viewpager*/
	private ViewPager viewPager;
	/**�ֲ�ͼƬ������*/
	private CycleImagesAdapter cycleImages;
	/**�ֲ�СԲ�����ʽ�б�*/
	private List<View> dots;
	private int[] dotCount;
	/**������ת��һ��ͼƬ��ŵı�ʶ*/
	private static int currentItem;
	/**��һ��ͼƬ��ŵı�ʶ*/
	private int oldPosition = 0;
	/**�ж��Ƿ��һ�������Զ��ֲ��̵߳Ĳ���ֵ*/
	private boolean first_in = true;
	/**�ж��Ƿ����ڻ����ֲ�ͼƬ�Ĳ���ֵ*/
	private boolean isScrolling = false;	
	/**����ҳfragment��layout�ļ�*/
	private View view;
	/**���¹��ͼƬ�ֲ�����Ϣ���*/
	private int CHANGE_IMAGE = 0 ;
	/**ͼƬ�ֲ��߳�*/
	private Thread cycle_image_thread;

	/**�ֲ�ͼƬ�õ�bitmap*/
	private Bitmap bitmaps[];

	private String CYCLE_IMAGE = "cycleimg";
	/**viewPager�Ŀؼ�id*/
	private int viewPagerId;
	/**�ֲ�ͼƬ������*/
	private int count;
	/**�����Ķ���*/
	private Context context;

	/**����ͼƬ��ַ����*/
	private String[] urls;
	/**�ֲ�ͼƬ��id����*/
	private String[] product_ids;
	/**�����Ƿ�ѭ��*/
	private boolean CycleRun;
	/**�����Ƿ��Զ�����*/
	private boolean CyclePlay;
	/**�ֲ�ͼƬѭ������*/
	private int CycleCount = 2000;
	private int CycleFocus = CycleCount/2;
	
	//����˯��ʱ�������
	/**��ʼ˯��ʱ��*/
	private int firstSleepTime = 0;
	/**ͼƬ�Զ��л�ʱ��*/
	private int SleepTime = 4000;
	private List<Map<String, Object>> data;
	private String[] removeUrl;
	
	
	/**
	 * @param viewPagerId viewPager��id����
	 * @param defaultImg Ĭ��ͼƬ
	 * @param context ������
	 * @param dotCount ��ؼ�id����
	 * @param view �����view
	 * @param CycleRun �Ƿ�ѭ��
	 * @param CyclePlay �Ƿ񲥷�
	 * */
	public CycleImages(int viewPagerId,Context context,int[] dotCount,View view,int defaultImage,boolean CycleRun,boolean CyclePlay){
		this.viewPagerId = viewPagerId;
		this.context = context;
		this.view = view;
		this.dotCount = dotCount;
		this.CycleRun = CycleRun;
		this.CyclePlay = CyclePlay;
		viewPager = (ViewPager) view.findViewById(viewPagerId);//��ʼ��viewpager�Ĳ��ֿؼ�
		imageViews = new ArrayList<ImageView>();//��ʼ���ֲ�ͼƬ�б�	
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
		viewPager.setAdapter(cycleImages);//Ϊviewpager����������
		if (this.CycleRun) {
			viewPager.setCurrentItem(imageViews.size()*CycleFocus);//��viewpager����Ϊ�м�λ��
			currentItem = imageViews.size()*CycleFocus;
		}else {
			viewPager.setCurrentItem(0);//��viewpager����Ϊ�м�λ��
			currentItem = imageViews.size()*CycleFocus;
		}
		
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			
			//�÷�����ͼƬ������ɺ����
			@Override
			public void onPageSelected(int arg0) {			
				currentItem = arg0;
				arg0 %=imageViews.size();
				dots.get(arg0).setBackgroundResource(R.drawable.dot_focused);
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				oldPosition = arg0;
			}		
			//�÷���
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}		
			//�÷�����ͼƬ����״̬�仯��ʱ�����
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
	
	/**ͼƬ����ֲ��߳����з���*/
	private class CycleViewPager implements Runnable{
		@Override
		public void run() {
			//�ж��Ƿ��һ�����и��̣߳�˯��ʱ��������ҳһ�£���ֹͼƬ�����л��������ÿ���ȥ�����ж�
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
					//�ж��Ƿ�����ָ������״̬�������Զ��л�ͼƬ
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

	
	
	/**�ֲ�ͼƬ������*/
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

			//����ȡ����ȡviewlist�е�ͼƬ������ʾ
			position %=imageViews.size();
			
			if (position<0) {
				position = imageViews.size()+position;
			}
			
			//���и��ؼ���Ҫ�Ƚ����ؼ�ȥ�������ܼ����µĿؼ�
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
