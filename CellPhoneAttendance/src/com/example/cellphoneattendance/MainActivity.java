package com.example.cellphoneattendance;

import java.util.ArrayList;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends ActivityGroup {

	public static ViewPager viewPager;
	private static final String TAG = "MainActivity";
	private static final String PRE_NAME = "InfoConfig";
	private static final String phoneKEY = "phone";
	private static final String serverKEY = "server";
	private SharedPreferences preferences;
	private ArrayList<Fragment> fragmentsList;
	private ArrayList<View> pageViews;
	private LinearLayout lilaybtn;
	private ImageView ivBottomLine;
	private int currIndex = 0;
	private Resources resources;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private int position_three;
	private int itemint = 0;

	MyImageButton btndaka, btnchaxun, btntongxun, btnsysset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		itemint = getIntent().getIntExtra("itemint", 0);
		resources = getResources();
		InitWidth();
		initView();
		lilaybtn = (LinearLayout) findViewById(R.id.lilaybtn);
		initbtn();
		initviewpage();
		preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_WRITEABLE);
		if (itemint == 0) {
			if (!checkInfo()) {
				Toast.makeText(MainActivity.this, "请设置手机号和服务地址!",
						Toast.LENGTH_SHORT).show();
				viewPager.setCurrentItem(3);
			} else {
				viewPager.setCurrentItem(0);
			}
		}
		else{
			viewPager.setCurrentItem(itemint);
		}
	}

	private void initbtn() {
		btndaka = new MyImageButton(this, R.drawable.attendanceup,
				R.string.daka, offset);
		btnchaxun = new MyImageButton(this, R.drawable.attendancecheck,
				R.string.chaxun, offset);
		btntongxun = new MyImageButton(this, R.drawable.infocheck,
				R.string.tongxunlu, offset);
		btnsysset = new MyImageButton(this, R.drawable.config, R.string.shezhi,
				offset);

		lilaybtn.addView(btndaka);
		lilaybtn.addView(btnchaxun);
		lilaybtn.addView(btntongxun);
		lilaybtn.addView(btnsysset);

		btndaka.setOnClickListener(new MyOnClickListener(0));
		btnchaxun.setOnClickListener(new MyOnClickListener(1));
		btntongxun.setOnClickListener(new MyOnClickListener(2));
		btnsysset.setOnClickListener(new MyOnClickListener(3));
	}

	private void initviewpage() {
		viewPager = (ViewPager) findViewById(R.id.my_main_viewpager);

		viewPager.setAdapter(new PagerAdapter() {

			@Override
			public int getItemPosition(Object object) {
				return POSITION_NONE;
			}

			public int getCount() {
				return pageViews.size();
			}

			public boolean isViewFromObject(View view, Object objcet) {
				return view == objcet;
			}

			// 这里会对需要进行水平切换的页面进行了加载和初始化 android:tileMode="repeat"
			public Object instantiateItem(View view, int id) {
				((ViewPager) view).addView(pageViews.get(id));
				return pageViews.get(id);
			}

			public void destroyItem(View view, int id, Object arg2) {
				((ViewPager) view).removeView(pageViews.get(id));
			}
		});
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);

		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		Log.i("MainActivity", "screenW=" + screenW);
		int perwidth = (int) (screenW / 4);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				perwidth, 4);
		layout.setMargins(0, 0, 0, 0);

		ivBottomLine.setLayoutParams(layout);
		int btnwidth = 120;

		offset = (int) screenW / 4;

		Log.i("MainActivity", "offset=" + offset);

		position_one = (int) (screenW / 4.0);
		position_two = position_one * 2;
		position_three = position_one * 3;

	}

	public boolean checkInfo() {
		if (!preferences.equals(null)) {
			if (!preferences.getString(phoneKEY, "").equals("")
					&& !preferences.getString(serverKEY, "").equals("")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	void initView() {
		pageViews = new ArrayList<View>();
		View view1 = getLocalActivityManager().startActivity("打卡",
				new Intent(this, attendanceConfirm.class)).getDecorView();
		View view2 = getLocalActivityManager().startActivity("查询",
				new Intent(this, checkAttendance.class)).getDecorView();
		View view3 = getLocalActivityManager().startActivity("通讯簿",
				new Intent(this, checkCustomerInfo.class)).getDecorView();
		View view4 = getLocalActivityManager().startActivity("设置",
				new Intent(this, personInfoConfig.class)).getDecorView();
		pageViews.add(0, view1);
		pageViews.add(1, view2);
		pageViews.add(2, view3);
		pageViews.add(3, view4);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					btnchaxun.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two, 0, 0, 0);
					btntongxun.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three, 0, 0, 0);
					btnsysset.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				// btndaka.setText(Html.fromHtml("<u>"+"上报打卡"+"</u>"));
				btndaka.setTextColor(resources.getColor(R.color.white));
				// viewPager.getAdapter().notifyDataSetChanged();
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_one, 0, 0);
					btndaka.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_one, 0, 0);
					btntongxun.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_one, 0, 0);
					btnsysset.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				btnchaxun.setTextColor(resources.getColor(R.color.white));
				// viewPager.getAdapter().notifyDataSetChanged();
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_two, 0, 0);
					btndaka.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_two, 0, 0);
					btnchaxun.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_two, 0, 0);
					btnsysset.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				btntongxun.setTextColor(resources.getColor(R.color.white));
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_three, 0, 0);
					btndaka.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_three, 0, 0);
					btnchaxun.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_three, 0, 0);
					btntongxun.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				btnsysset.setTextColor(resources.getColor(R.color.white));
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	};

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
