package com.example.mixzone2.shoppingCar;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;


public class SerInfo implements Parcelable{
	private List<Map<String, Object>> list;
	public SerInfo(List<Map<String, Object>> list) {
		this.list = list;
	}
	public List<Map<String, Object>> getList(){
		return this.list;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
}
