package com.ty.winchat.ui;


import com.ty.winchat.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.KeyEvent;
import android.view.Window;


public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {
	/** 铃声设置 */
	private static final String BELL_LIST_PRE = "bell_list_preference";// 和xml中的key值对应
	/** wifi设置 */
	private static final String WIFI_SETTING = "wifi_setting_preference";
	/** 关于我们 */
	private static final String ABOUT_SETTEING = "about_setting_preference";
	/** 新手指引 */
	private static final String NEWER_GUIDE = "newer_guide_preference";
	/** 东蓝展柜 */
	private static final String SINOBPO_DISPLAY = "sinobpo_display_preference";
	/** 已收文件 */
	private static final String RECEIVED_FILE = "received_file";
	private ListPreference bellListPre;// 声音一个ListPreference对象，用来控制当前ListPreference
	SharedPreferences sp;
	private MediaPlayer mMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings_activity_pre);
		// 通过findPreference在xml中控件的key值找到该控件
		bellListPre = (ListPreference) findPreference(BELL_LIST_PRE);
		// 设置监听事件
		bellListPre.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(BELL_LIST_PRE)) {
			bellListPreChange(Integer.parseInt(newValue.toString()));
		}
		
		return true;
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		
		if (preference.getKey().equals(ABOUT_SETTEING)) {
			Intent intent = new Intent(SettingsActivity.this,
					About.class);
			startActivity(intent);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	/**
	 * 自定义铃声
	 * 
	 * @param value
	 */
	public void bellListPreChange(int value) {
		sp = getSharedPreferences("bell_list_preference", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("callremin", value);
		editor.commit();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		int playSound;
		if (value == 1) {
			playSound = R.raw.callremin;
		} else if (value == 2) {
			playSound = R.raw.callremin2;
		} else if (value == 3) {
			playSound = R.raw.callremin3;
		} else if (value == 4) {
			playSound = R.raw.callremin4;
		} else {
			return;
		}
		mMediaPlayer = MediaPlayer.create(SettingsActivity.this, playSound);
		try {
			mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复
			// DO SOMETHING
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

