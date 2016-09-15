package com.example.mixzone2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class ImageDownLoader {
	private FileUtils fileUtils;
	private static int maxMemory = (int) Runtime.getRuntime().maxMemory();
	private static int mLruCacheSize = maxMemory / 4;
	private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(mLruCacheSize){
		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes()*value.getHeight();
		}
		@Override
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			super.entryRemoved(evicted, key, oldValue, newValue);
			if (evicted && oldValue != null) {
				oldValue.recycle();
			}
		}

	};
	private LruCache<String, Bitmap> getLrucache(){
		return mLruCache;
	}
	private Bitmap getBitmapFromLrucache(String url){
		return mLruCache.get(url);
	}
	private void putBitmapToLrucache(String url,Bitmap bitmap){
		mLruCache.put(url, bitmap);
	}
	private void removeBitmapFromLrucache(String[] url){
		for (int i = 0; i < url.length; i++) {
			mLruCache.remove(url[i]);
			
		}
		
	}
	
	public ImageDownLoader(Context context){
		
		fileUtils = new FileUtils(context);

	}
	
	
	
	private Bitmap getBitmapFromMemoryCache(String url){
		return getBitmapFromLrucache(url);
	}
	
	private void addBitmapToMemoryCache(String key,Bitmap bitmap){
		if (getBitmapFromMemoryCache(key) == null && bitmap != null) {
			putBitmapToLrucache(key, bitmap);
		}
	}
	
	/**此方法有联网的可能性，需要在线程中调用
	 * @param url 图片的网络地址
	 * @param imgName 图片的名称
	 * */
	public Bitmap showCacheBitmap(String url,String imgName){
		if (getBitmapFromMemoryCache(imgName) != null) {
			return getBitmapFromLrucache(url);
		}else if (fileUtils.isFileExists(imgName) && fileUtils.getFileSize(imgName) != 0) {
			Bitmap bitmap = fileUtils.getBitmap(imgName);
			addBitmapToMemoryCache(imgName,bitmap);
			return bitmap;
		}else {
			Bitmap bitmap = getBitmapFromUrl(url);
			if (bitmap == null) {
				return bitmap;
			}
			try {
				fileUtils.savaBitmap(imgName, bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
			addBitmapToMemoryCache(url, bitmap);
			return bitmap;
		}
	}
	private  Bitmap getBitmapFromUrl(String url){
		URL imageUrl = null;
		Bitmap bitmap = null;
		try{
			imageUrl = new URL(url);
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream iStream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(iStream);
			iStream.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
}
