package com.example.cellphoneattendance;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.cellphoneattendance.attendanceConfirm.msgHandler;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.Toast;

public class personInfoConfig extends Activity {
	private EditText workIDText;
	private EditText nameText;
	private EditText phoneText;
	private EditText serverIPText;
	private LocationManager manager;
	private Button btnconfirmsysset;
	private static final String PRE_NAME = "InfoConfig";
	private static final String phoneKEY = "phone";
	private static final String nameKEY = "name";
	private static final String workIDKEY = "Username";
	private static final String serverKEY = "server";
	private SharedPreferences preferences;
	private Thread accessWSThread;
	private msgHandler showResult = new msgHandler();
	private Boolean islogin=false;
	private Button btexit;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sysset);
		islogin=getIntent().getBooleanExtra("islogin", false);
		workIDText = (EditText) findViewById(R.id.workIDText);
		nameText = (EditText) findViewById(R.id.nameText);
		phoneText = (EditText) findViewById(R.id.phoneText);
		serverIPText = (EditText) findViewById(R.id.serverIPText);
		btexit=(Button) findViewById(R.id.btexit);
		
		btnconfirmsysset = (Button) findViewById(R.id.btnconfirmsysset);
		preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_WRITEABLE);
		init();
		btexit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_MAIN);
	            intent.addCategory(Intent.CATEGORY_HOME);
	            startActivity(intent);
	            System.exit(0);
			}
			
		});
		btnconfirmsysset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(serverIPText.getText().toString().equals("")){
					Toast.makeText(personInfoConfig.this, "服务器地址不能为空!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(phoneText.getText().toString().equals("")){
					Toast.makeText(personInfoConfig.this, "手机号不能为空!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(accessWSThread == null){
					progress = ProgressDialog.show(personInfoConfig.this, "提示", "验证中……");
        			progress.setCancelable(false);
        			progress.setOnKeyListener(onKeyListener);
					accessWSThread = new Thread(new accessWSThreadRunnable());
					accessWSThread.start();
				}
				
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(workIDKEY, workIDText.getText().toString());
				editor.putString(nameKEY, nameText.getText().toString());
				editor.putString(phoneKEY, phoneText.getText().toString());
				editor.commit();
				
				 
				 
			}
		});
	}

	public void init() {
		workIDText.setText(preferences.getString(workIDKEY, ""));
		nameText.setText(preferences.getString(nameKEY, ""));
		phoneText.setText(preferences.getString(phoneKEY, ""));
		if(phoneText.getText().toString().equals(""))	phoneText.setText(getLocalNumber());
		serverIPText.setText(preferences.getString(serverKEY, ""));
		if(serverIPText.getText().toString().equals("")){
			serverIPText.setText("http://");
		}
	}

	public String getLocalNumber() {
		TelephonyManager tManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		String number = tManager.getLine1Number();
		return number;
	}

	
    public final class accessWSThreadRunnable implements Runnable {
		@Override
		public void run() {
			sendAttendance();
		}
		
		public void sendAttendance() {
			String nameSpace = xmlConfig.readWSInfo(personInfoConfig.this, "aboutInfo", "namespace");
			String methodName = xmlConfig.readWSInfo(personInfoConfig.this, "aboutInfo", "method");
			String endPoint = serverIPText.getText().toString() + xmlConfig.readWSInfo(personInfoConfig.this, "aboutInfo", "URL");
			String soapAction = nameSpace + methodName;
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER10);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE transport = new HttpTransportSE(endPoint);
			String result = "";
			try {
				transport.call(soapAction, envelope);
				SoapObject object = (SoapObject) envelope.bodyIn;
				result = object.getProperty(0).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//result ="true";  //test
			Message m = showResult.obtainMessage();
			Bundle b = new Bundle();
			b.putString("message", result);
			m.setData(b);
			m.setTarget(showResult);
			m.sendToTarget();
		}
	}
    
    public class msgHandler extends Handler{
    	
    	@Override
    	public void handleMessage(Message msg){
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
    			Toast.makeText(personInfoConfig.this, "服务器地址设置不正确或无法访问服务!",
    					Toast.LENGTH_SHORT).show();
    			progress.dismiss();
       		 	accessWSThread = null;
    			return;
    		}
//    		Toast.makeText(personInfoConfig.this, message,
//					Toast.LENGTH_SHORT).show();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(serverKEY, serverIPText.getText().toString());
			editor.commit();
			Toast.makeText(personInfoConfig.this, "设置成功!",
					Toast.LENGTH_SHORT).show();
			progress.dismiss();
    		 accessWSThread = null;
//    		 Toast.makeText(personInfoConfig.this, "绑定成功!",
//						Toast.LENGTH_SHORT).show();
				if(islogin){
					Intent intent =new Intent(personInfoConfig.this,Login.class);
					startActivity(intent);
				}
				else{
				 MainActivity.viewPager.getAdapter().notifyDataSetChanged();
				 MainActivity.viewPager.setCurrentItem(0);
				}
    		
    		  
    	}
    }
    
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
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
}

