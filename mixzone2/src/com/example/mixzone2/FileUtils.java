package com.example.mixzone2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {  
 
    /** 
     * 保存Image的目录名 
     */  
    private final static String FOLDER_NAME = "/data/com.mixzone.mixzoneshop/cache/ImagesCache";  
    private String path;  
      
    public FileUtils(Context context){  
    	createCache();
    	File cache = new File(Environment.getExternalStorageDirectory(),"Android");
    	path = cache.toString()+FOLDER_NAME;
        
    }  
      
  
    
      
    /** 
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录 
     * @param fileName  
     * @param bitmap    
     * @throws IOException 
     */  
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException{  
        if(bitmap == null){  
            return;  
        } 
        File file = new File(path,fileName);  
        FileOutputStream fos = new FileOutputStream(file);  
        bitmap.compress(CompressFormat.JPEG, 100, fos);  
        fos.flush();  
        fos.close();  
    }  
    
    private void createCache(){
		File cache = new File(Environment.getExternalStorageDirectory(),"Android");
		if(!cache.exists()){//如果不存在该文件夹，则创建
			cache.mkdirs();
		}
		cache = new File(cache.toString(),"data");
		if(!cache.exists()){//如果不存在该文件夹，则创建
			cache.mkdirs();
		}
		cache = new File(cache.toString(),"com.mixzone.mixzoneshop");
		if(!cache.exists()){//如果不存在该文件夹，则创建
			cache.mkdirs();
		}
		cache = new File(cache.toString(),"cache");
		if(!cache.exists()){//如果不存在该文件夹，则创建
			cache.mkdirs();
		}
		cache = new File(cache.toString(),"ImagesCache");
		if(!cache.exists()){//如果不存在该文件夹，则创建
			cache.mkdirs();
		}
		cache = new File(cache.toString(),"Goods");
		if(!cache.exists()){//如果不存在该文件夹，则创建
			cache.mkdirs();
		}
	}
      
    /** 
     * 从手机或者sd卡获取Bitmap 
     * @param fileName 
     * @return 
     */  
    public Bitmap getBitmap(String fileName){  
    	File file = new File(path,fileName);
    	try {
			FileInputStream inputStream = new FileInputStream(file);
			return BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
          
    }  
      
    /** 
     * 判断文件是否存在 
     * @param fileName 
     * @return 
     */  
    public boolean isFileExists(String fileName){  
        return new File(path,fileName).exists();  
    }  
      
    /** 
     * 获取文件的大小 
     * @param fileName 
     * @return 
     */  
    public long getFileSize(String fileName) {  
        return new File(path,fileName).length();  
    }  
      
      
    /** 
     * 删除SD卡或者手机的缓存图片和目录 
     */  
    public void deleteFile() {  
        File dirFile = new File(path);  
        if(! dirFile.exists()){  
            return;  
        }  
        if (dirFile.isDirectory()) {  
            String[] children = dirFile.list();  
            for (int i = 0; i < children.length; i++) {  
                new File(dirFile, children[i]).delete();  
            }  
        }  
        dirFile.delete();  
    }  
}
