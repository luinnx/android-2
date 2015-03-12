package com.ty.winchat.ui;

import javax.security.auth.Subject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//自定义ImageButton，模拟ImageButton，并在其下方显示文字 
//提供Button的部分接口 

public class MyImageButton extends LinearLayout {
	public MyImageButton(Context context, int imageResId, int textResId) {
		super(context);
		mButtonImage = new ImageView(context);
		mButtonText = new TextView(context);
		setImageResource(imageResId);
		mButtonImage.setPadding(30, 0, 0, 0);
		setText(textResId);
		setTextColor(0xFFFFFFFF);
		mButtonText.setPadding(50, 0, 0, 0);
		// 设置本布局的属性
		setClickable(true); // 可点击
		setFocusable(true); // 可聚焦
		setBackgroundColor(0xFFFFFF);
		//setBackgroundResource(android.R.drawable.btn_default); // 布局才用普通按钮的背景
		setOrientation(LinearLayout.VERTICAL); // 垂直布局 3
		// 首先添加Image，然后才添加Text
		// 添加顺序将会影响布局效果
		addView(mButtonImage);
		addView(mButtonText);
	}
	public MyImageButton(Context context, Bitmap bm, int textResId) {
		super(context);
		mButtonImage = new ImageView(context);
		mButtonText = new TextView(context);
		setImagebitmap(bm);
		mButtonImage.setPadding(30, 0, 0, 0);
		setText(textResId);
		setTextColor(0xFF000000);
		mButtonText.setPadding(30, 0, 0, 0);
		// 设置本布局的属性
		setClickable(true); // 可点击
		setFocusable(true); // 可聚焦
		setBackgroundColor(0xFFFFFF);
		//setBackgroundResource(android.R.drawable.btn_default); // 布局才用普通按钮的背景
		setOrientation(LinearLayout.VERTICAL); // 垂直布局 3
		// 首先添加Image，然后才添加Text
		// 添加顺序将会影响布局效果
		addView(mButtonImage);
		addView(mButtonText);
	}

	// ----------------public method-----------------------------
	/*
	 * setImageResource方法
	 */
	public void setImageResource(int resId) {
		mButtonImage.setImageResource(resId);
		
	}
	public void setImagebitmap(Bitmap bm) {
		mButtonImage.setImageBitmap(bm);
		
	}
	
	/*
	 * setText方法
	 */
	public void setText(int resId) {
		mButtonText.setText(resId);
		mButtonText.setTextSize(20);
	}

	public void setText(CharSequence buttonText) {
		mButtonText.setText(buttonText);
	}

	/*
	 * setTextColor方法
	 */
	public void setTextColor(int color) {
		mButtonText.setTextColor(color);
	}

	// ----------------private attribute-----------------------------
	private ImageView mButtonImage = null;
	private TextView mButtonText = null;
	
}
