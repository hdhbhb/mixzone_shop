package com.example.mixzone2.jiaZhuangBaiKe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mixzone2.R;
public class fragmentJiaZhuangBaiKePage extends Fragment {
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.page_jiazhuangbaike, container, false);
		return view;
	}
}
