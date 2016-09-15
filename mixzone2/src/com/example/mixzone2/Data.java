package com.example.mixzone2;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class Data{
	private static int maxMemory = (int) Runtime.getRuntime().maxMemory();
	private static int mLruCacheSize = maxMemory / 4;
	private static LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(mLruCacheSize){
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
	public static LruCache<String, Bitmap> getLrucache(){
		return mLruCache;
	}
	public static Bitmap getBitmapFromLrucache(String url){
		return mLruCache.get(url);
	}
	public static void putBitmapToLrucache(String url,Bitmap bitmap){
		mLruCache.put(url, bitmap);
	}
	public static void removeBitmapFromLrucache(String[] url){
		for (int i = 0; i < url.length; i++) {
			mLruCache.remove(url[i]);
			
		}
		
	}

}
