package com.example.mixzone2.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnetionDetector {
	private Context context;
	public ConnetionDetector(Context context) {
		this.context = context;
	}
	/**是否有网络连接的方法，有则返回true*/
	public boolean isConnectionToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity !=null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null){
				return true;
				}
		}
		return false;
	}
	

	
}
