package com.example.cellphoneattendance;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.cellphoneattendance.attendanceConfirm.accessWSThreadRunnable;
import com.example.cellphoneattendance.attendanceConfirm.msgHandler;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class checkAttendance extends Activity {
	private ListView attendanceTitleList;
	private ListView attendanceList;
	private Button btnsearch;
	private static final String PRE_NAME = "InfoConfig";
	private static final String phoneKEY = "phone";
	private static final String serverKEY = "server";
	private SharedPreferences preferences;
	private Thread accessWSThread;
	private bindListHandler showResult = new bindListHandler();
	private ProgressDialog progress;
	private int checkDays = 7;		//查询的考勤天数
	private Boolean isFirstBoolean=true;
	private EditText etdays;
	private EditText etphone;
	private static final String USERID = "Username";
	private static final String PASSWORD = "Userpassword";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchattendance );
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		attendanceTitleList = (ListView) findViewById(R.id.attendanceTitleList);
		attendanceList = (ListView) findViewById(R.id.attendanceList);
		btnsearch = (Button) findViewById(R.id.btnchaxunattendancenew);
		etdays =(EditText) findViewById(R.id .etdays);
		etphone=(EditText )findViewById(R.id.etphone);
		init();
		
		
	}
	private void init(){
		etdays.setText("7");
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		bindHeaderList(listItem);
		// 生成适配器的Item和动态数组对应的元素
		SimpleAdapter listItemAdapter = new SimpleAdapter(checkAttendance.this, listItem,// 数据源
							R.layout.attendancelist,// ListItem的XML实现
							new String[] {"nameView","phoneView",  "timeView","placeView" },// 动态数组与ImageItem对应的子项
							new int[] { R.id.tvname,R.id.tvphone, R.id.timeView,R.id.placeView});// ImageItem的XML文件里面的一个ImageView,两个TextView ID
		// 添加并且显示
		attendanceTitleList.setAdapter(listItemAdapter);
		preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_READABLE);

		
		btnsearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (accessWSThread == null) {
					progress = ProgressDialog.show(checkAttendance.this, "提示",
							"查询中……");
					progress.setCancelable(false);
        			progress.setOnKeyListener(onKeyListener);
					accessWSThread = new Thread(
							new getAttendanceThreadRunnable());
					accessWSThread.start();
				}
			}
		});
		String bindPhoneString = "";
		bindPhoneString = getLocalNumber();
		if (bindPhoneString.equals(""))
			bindPhoneString = preferences.getString(phoneKEY, "");
		etphone.setText(bindPhoneString);
	}

	public final class getAttendanceThreadRunnable implements Runnable {
		@Override
		public void run() {
			getAttendance();
		}

		public void getAttendance() {
			String methodName="";
			if(etphone.getText().toString().trim().equals("")){
				methodName= xmlConfig.readWSInfo(checkAttendance.this,
						"attendanceCheckbuuser", "method");
			}
			else{
				methodName= xmlConfig.readWSInfo(checkAttendance.this,
						"attendanceCheckbyphone", "method");
			}
			// 命名空间
			String nameSpace = xmlConfig.readWSInfo(checkAttendance.this,
					"attendanceCheckbuuser", "namespace");
			// 调用的方法名称
			 
			// EndPoint
			String endPoint = preferences.getString(serverKEY, "");
			if(endPoint.equals("")) return;
			endPoint =endPoint + xmlConfig.readWSInfo(checkAttendance.this, "attendanceCheckbuuser", "URL");
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);

			// 设置需调用WebService接口传入的参数
			String phoneString = etphone.getText().toString().trim();
			if(!etdays.getText().toString().trim().equals("")){
			checkDays=Integer.parseInt(etdays.getText().toString());
			}
			
			rpc.addProperty("userId",  preferences.getString(USERID, ""));
			rpc.addProperty("password", preferences.getString(PASSWORD, ""));
			if(!etphone.getText().toString().trim().equals("")){
				rpc.addProperty("phone", phoneString);
			}
			rpc.addProperty("checkDays", checkDays);
			

			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER10);

			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService
			envelope.dotNet = true;
			// 等价于envelope.bodyOut = rpc;
			envelope.setOutputSoapObject(rpc);

			HttpTransportSE transport = new HttpTransportSE(endPoint);
			String result = "";
			try {
				// 调用WebService
				transport.call(soapAction, envelope);
				// 获取返回的数据
				SoapObject object = (SoapObject) envelope.bodyIn;
				// 获取返回的结果
				result = object.getProperty(0).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将WebService返回的结果显示
			Message m = showResult.obtainMessage();
			Bundle b = new Bundle();
			b.putString("message", result);
			m.setData(b);
			m.setTarget(showResult);
			m.sendToTarget();
		}
	}

	public class bindListHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bundle b = msg.getData();
			String message = b.getString("message");
			if(message.equals("")){
    			Toast.makeText(checkAttendance.this, "服务器地址设置不正确或无法访问服务!",
    					Toast.LENGTH_SHORT).show();
    			progress.dismiss();
       		 	accessWSThread = null;
    			return;
    		}
			else if(message .equals( "anyType{}")){
				Toast.makeText(checkAttendance.this, "密码错误!",
    					Toast.LENGTH_SHORT).show();
    			progress.dismiss();
       		 	accessWSThread = null;
    			return;
			}
			ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
			//bindHeaderList(listItem);
			try {
				if(message.equals("record:[]")){
					Toast.makeText(checkAttendance.this, "无数据!",
							Toast.LENGTH_SHORT).show();
					progress.dismiss();
					accessWSThread = null;
					return;
				}
				else{
				JSONArray jsonArary = new JSONArray(message);
				if (jsonArary.length() == 0) {
					Toast.makeText(checkAttendance.this, "无数据!",
							Toast.LENGTH_SHORT).show();
					progress.dismiss();
					accessWSThread = null;
					return;
				}
				for (int i = 0; i < jsonArary.length(); i++) {
					JSONObject attendance = jsonArary.getJSONObject(i);
					JSONObject attendancerecord = attendance
							.getJSONObject("attendancerecord");
					// JSON对象的操作
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("nameView", attendancerecord.getString("name"));
					//map.put("phoneView", attendancerecord.getString("phone"));
					map.put("phoneView", attendancerecord.getString("phone"));
					map.put("timeView", attendancerecord.getString("time"));
					map.put("placeView", attendancerecord.getString("place"));
//					map.put("customerView",
//							attendancerecord.getString("customer"));
					listItem.add(map);
				}
				}
			} catch (JSONException ex) {
				// 异常处理代码
				Toast.makeText(checkAttendance.this, "查询返回的结果集格式有误!",
						Toast.LENGTH_SHORT).show();
				progress.dismiss();
				accessWSThread = null;
				return;
			}

			// 生成适配器的Item和动态数组对应的元素
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					checkAttendance.this, listItem,// 数据源
					R.layout.attendancelist,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] {"nameView","phoneView",  "timeView",
							"placeView"  },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] {R.id.tvname,R.id.tvphone , R.id.timeView,
							R.id.placeView,  });

			// 添加并且显示
			attendanceList.setAdapter(listItemAdapter);
			progress.dismiss();
			accessWSThread = null;
		}
	}

	public void bindHeaderList(ArrayList<HashMap<String, Object>> listItem) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("nameView", "姓名");
		map.put("phoneView", "手机");
		map.put("timeView", "时间");
		map.put("placeView", "地点");
//		map.put("customerView", "客户");
		listItem.add(map);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//	
	/**
     * add a keylistener for progress dialog
     */
    private OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                dismissDialog();
                if(accessWSThread!=null){
                		Thread dummy = accessWSThread;
                		accessWSThread = null;
                		dummy.interrupt();                	 
                }
            }
            return false;
        }
    };
    /**
     * dismiss dialog
     */
    public void dismissDialog() {
        if (isFinishing()) {
            return;
        }
        if (null != progress && progress.isShowing()) {
        	progress.dismiss();
        }
    }
    /**
     * cancel progress dialog if nesseary
     */
    @Override
    public void onBackPressed() {
        if (progress != null && progress.isShowing()) {
            dismissDialog();
        } else {
            super.onBackPressed();
        }
    }
    private long exitTime = 0;

	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	                 
	    if((System.currentTimeMillis()-exitTime) > 2000){
	        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
	        exitTime = System.currentTimeMillis();
	    }
	    else{
	    	Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
	    }
	                 
	    return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	// 获得当前手机号
		private String getLocalNumber() {
			TelephonyManager tManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			String number = tManager.getLine1Number();
			if (number != null) {
				return number;
			} else {
				return "";
			}
		}


}
