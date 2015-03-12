package com.example.cellphoneattendance;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import com.example.cellphoneattendance.checkAttendance.getAttendanceThreadRunnable;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class checkCustomerInfo extends Activity {
	private ListView infoList;
	private ImageButton RefreshButton;
	private ImageButton addbutton;
	private static final String PRE_NAME = "InfoConfig";
	private static final String serverKEY = "server";
	private static final String phoneKEY = "phone";
	private SharedPreferences preferences;
	private Thread accessWSThread;
	private bindListHandler showResult = new bindListHandler();
	private ProgressDialog progress;
	private String localContactXml = "contact.xml";
	private static final String USERID = "Username";
	private static final String PASSWORD = "Userpassword";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infocheck);
        infoList = (ListView)findViewById(R.id.infoList);
        RefreshButton = (ImageButton)findViewById(R.id.RefreshButton);
        addbutton=(ImageButton)findViewById(R.id.addbutton);
        preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_READABLE);
        addbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent  = new Intent(checkCustomerInfo .this ,CustomerVisits .class );   
				intent.putExtra("isadd", 1); 
				startActivity(intent);
			}
        	
        });
        
        
        RefreshButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
        		if(accessWSThread == null){
        			progress = ProgressDialog.show(checkCustomerInfo.this, "提示", "刷新中……");
        			progress.setCancelable(false);
        			progress.setOnKeyListener(onKeyListener);
					accessWSThread = new Thread(new getInfoThreadRunnable());
	        		accessWSThread.start();
	        		


        		}
			}
		});
        /*
        infoList.setOnItemClickListener(new OnItemClickListener() {
        	
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3){
        		HashMap item = (HashMap)arg0.getItemAtPosition(arg2);
        		String phone = String.valueOf(item.get("phoneView"));
        		if(!phone.equals(null) && !phone.equals("")){
	        		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
	                startActivity(intent);
        		}
        	}
		});
		*/

       //添加长按点击
       infoList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("请选择");
				menu.setHeaderIcon(checkCustomerInfo.this.getResources().getDrawable(R.drawable.contactmenu));
				menu.add(0, 0, 0, "通话");
				menu.add(0, 1, 0, "发送短信");
				menu.add(0, 3, 0, "拜访登记");
			}
		}); 
    }
	//长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		//int id = (int)info.id;
		int mListPos = info.position;
		HashMap<String, Object> map = (HashMap<String, Object>)infoList.getItemAtPosition(mListPos);
		String phoneString = map.get("phoneView").toString();
		String Customername=map.get("companyView").toString();
		String contactNameString=map.get("contactView").toString();
		String address=map.get("addressView").toString();
		switch(item.getItemId()){
		case 0:
    		if(!phoneString.equals(null) && !phoneString.equals("")){
        		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneString));
                startActivity(intent);
    		}
    		break;
		case 1:
			Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneString));          
			it.putExtra("sms_body", "");          
			startActivity(it);
			break;
		case 2:
			Toast.makeText(checkCustomerInfo.this, "已存入!", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			Intent intent  = new Intent(checkCustomerInfo .this ,CustomerVisits .class ); 
			intent.putExtra("isadd", 0); 
			intent.putExtra("Customername", Customername);  
			intent.putExtra("ContactName", contactNameString);
			intent.putExtra("ContactPhone", phoneString);
			intent.putExtra("Address", address);
			startActivity(intent);
		}
		return super.onContextItemSelected(item);
	}
    
	//初始化通讯簿
    public void initContact(){
    	File contactXml = new File(xmlConfig.xmlConfigPath + localContactXml);
    	if(contactXml.exists()){
    		ArrayList<HashMap<String, Object>> itemList= xmlConfig.readContactXml(localContactXml);
    		SimpleAdapter adapter = new SimpleAdapter(checkCustomerInfo.this, itemList, R.layout.infolist,
    									new String[] {"companyView","contactView", "phoneView","addressView","contentView"},
    									new int[] {R.id.companyView,R.id.contactView,R.id.phoneView,R.id.addressView,R.id.contentView});
    		infoList.setAdapter(adapter);
    	}
    	else{
    		if(accessWSThread == null){
    			progress = ProgressDialog.show(checkCustomerInfo.this, "提示", "刷新中……");
    			progress.setCancelable(false);
    			progress.setOnKeyListener(onKeyListener);
				accessWSThread = new Thread(new getInfoThreadRunnable());
        		accessWSThread.start();
        		
    		}
    	}
    }
	
    
    public final class getInfoThreadRunnable implements Runnable {
		@Override
		public void run() {
			getInfo();
		}
		
		public void getInfo() {
			// 命名空间
			String nameSpace = xmlConfig.readWSInfo(checkCustomerInfo.this, "infoCheck", "namespace");
			// 调用的方法名称
			String methodName = xmlConfig.readWSInfo(checkCustomerInfo.this, "infoCheck", "method");
			// EndPoint
			//String endPoint = xmlConfig.readWSInfo(checkCustomerInfo.this, "infoCheck", "URL");
			String endPoint = preferences.getString(serverKEY, "");
			if(endPoint.equals("")) return;
			endPoint =endPoint + xmlConfig.readWSInfo(checkCustomerInfo.this, "infoCheck", "URL");
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);

			// 设置需调用WebService接口传入的参数
			 
			 
			rpc.addProperty("userId",  preferences.getString(USERID, ""));
			rpc.addProperty("password", preferences.getString(PASSWORD, ""));

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

    
    public class bindListHandler extends Handler{
    	
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
    			Toast.makeText(checkCustomerInfo.this, "服务器地址设置不正确或无法访问服务!",
    					Toast.LENGTH_SHORT).show();
                progress.dismiss();
       		 	accessWSThread = null;
    			return;
    		}
    		else if(message .equals( "anyType{}")){
				Toast.makeText(checkCustomerInfo .this, "密码错误!",
    					Toast.LENGTH_SHORT).show();
    			progress.dismiss();
       		 	accessWSThread = null;
    			return;
			}
    		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    		try { 
    		    JSONArray jsonArary = new JSONArray(message);  
    		    if(jsonArary.length() == 0){
    		    	progress.dismiss();
    		    	Toast.makeText(checkCustomerInfo.this, "无数据!", Toast.LENGTH_SHORT).show();
    		    	accessWSThread = null;
    				return;
    		    }
    		    for(int i=0;i<jsonArary.length();i++){
    		    	JSONObject attendance = jsonArary.getJSONObject(i);
    		    	JSONObject attendancerecord = attendance.getJSONObject("customerinfo");
	    		    // JSON对象的操作
	    		    HashMap<String, Object> map = new HashMap<String, Object>();
	            	map.put("companyView", attendancerecord.getString("companyname"));
	            	map.put("contactView", attendancerecord.getString("contactman"));
	            	map.put("phoneView", attendancerecord.getString("phone"));
	            	map.put("addressView", attendancerecord.getString("place"));
	            	map.put("contentView", attendancerecord.getString("content"));
	            	
	            	listItem.add(map);
    		    }
    		} catch (JSONException ex) {   
    		    // 异常处理代码   
    			progress.dismiss();
    			Toast.makeText(checkCustomerInfo.this, "查询返回的结果集格式有误!", Toast.LENGTH_SHORT).show();
    			accessWSThread = null;
				return;
    		}   
        	
            //生成适配器的Item和动态数组对应的元素
            SimpleAdapter listItemAdapter = new SimpleAdapter(checkCustomerInfo.this,listItem,//数据源 
                R.layout.infolist,//ListItem的XML实现
                //动态数组与ImageItem对应的子项        
                new String[] {"companyView","contactView", "phoneView","addressView","contentView"}, 
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.companyView,R.id.contactView,R.id.phoneView,R.id.addressView,R.id.contentView}
            );
           
            //添加并且显示
            infoList.setAdapter(listItemAdapter);
            progress.dismiss();
            if(xmlConfig.updateContactXml(localContactXml, listItem)){
            	Toast.makeText(checkCustomerInfo.this, "更新通讯簿成功！", Toast.LENGTH_SHORT).show();
            }
            else{
            	Toast.makeText(checkCustomerInfo.this, "更新通讯簿失败！", Toast.LENGTH_SHORT).show();
            }
            accessWSThread = null;
    	}
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
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


    
}
