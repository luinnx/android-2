package com.ty.winchat.ui;

import javax.security.auth.Subject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//�Զ���ImageButton��ģ��ImageButton���������·���ʾ���� 
//�ṩButton�Ĳ��ֽӿ� 

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
		// ���ñ����ֵ�����
		setClickable(true); // �ɵ��
		setFocusable(true); // �ɾ۽�
		setBackgroundColor(0xFFFFFF);
		//setBackgroundResource(android.R.drawable.btn_default); // ���ֲ�����ͨ��ť�ı���
		setOrientation(LinearLayout.VERTICAL); // ��ֱ���� 3
		// �������Image��Ȼ������Text
		// ���˳�򽫻�Ӱ�첼��Ч��
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
		// ���ñ����ֵ�����
		setClickable(true); // �ɵ��
		setFocusable(true); // �ɾ۽�
		setBackgroundColor(0xFFFFFF);
		//setBackgroundResource(android.R.drawable.btn_default); // ���ֲ�����ͨ��ť�ı���
		setOrientation(LinearLayout.VERTICAL); // ��ֱ���� 3
		// �������Image��Ȼ������Text
		// ���˳�򽫻�Ӱ�첼��Ч��
		addView(mButtonImage);
		addView(mButtonText);
	}

	// ----------------public method-----------------------------
	/*
	 * setImageResource����
	 */
	public void setImageResource(int resId) {
		mButtonImage.setImageResource(resId);
		
	}
	public void setImagebitmap(Bitmap bm) {
		mButtonImage.setImageBitmap(bm);
		
	}
	
	/*
	 * setText����
	 */
	public void setText(int resId) {
		mButtonText.setText(resId);
		mButtonText.setTextSize(20);
	}

	public void setText(CharSequence buttonText) {
		mButtonText.setText(buttonText);
	}

	/*
	 * setTextColor����
	 */
	public void setTextColor(int color) {
		mButtonText.setTextColor(color);
	}

	// ----------------private attribute-----------------------------
	private ImageView mButtonImage = null;
	private TextView mButtonText = null;
	
}
