package com.example.cellphoneattendance;

import java.lang.reflect.InvocationTargetException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class Login extends Activity implements OnClickListener {
	private EditText login_edit_account, login_edit_pwd;
	private Button login_btn_login;
	private SharedPreferences preferences;
	private static final String PRE_NAME = "InfoConfig";
	private static final String USERID = "Username";
	private static final String PASSWORD = "Userpassword";
	private static final String serverKEY = "server";
	private ProgressDialog progress;
	private Thread accesloginThread;
	private TextView tvsetserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findview();
		preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_READABLE);
		String endPoint = preferences.getString(serverKEY, "");
		if (endPoint.equals("")) {
			Intent intent = new Intent(Login.this, personInfoConfig.class);
			intent.putExtra("islogin", true);
			startActivity(intent);
		}
		init();

	}

	private void findview() {
		login_edit_account = (EditText) findViewById(R.id.login_edit_account);
		login_edit_pwd = (EditText) findViewById(R.id.login_edit_pwd);
		login_btn_login = (Button) findViewById(R.id.login_btn_loginnew);
		login_btn_login.setOnClickListener(this);
		tvsetserver = (TextView) findViewById(R.id.tvsetserver);
		login_edit_account
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							String hint = login_edit_account.getHint()
									.toString();
							login_edit_account.setTag(hint);
							login_edit_account.setHint("");

						} else {
							login_edit_account.setHint(login_edit_account
									.getTag().toString());
						}
					}

				});
		login_edit_pwd
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							String hint = login_edit_pwd.getHint().toString();
							login_edit_pwd.setTag(hint);
							login_edit_pwd.setHint("");

						} else {
							login_edit_pwd.setHint(login_edit_pwd.getTag()
									.toString());
						}
					}

				});
		tvsetserver.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_btn_loginnew:
			progress = ProgressDialog.show(Login.this, "提示", "登录中……");
			progress.setCancelable(false);
			progress.setOnKeyListener(onKeyListener);
			accesloginThread = new Thread(new acceswslogin());
			accesloginThread.start();
			break;
		case R.id.tvsetserver:
			Intent intent = new Intent(Login.this, personInfoConfig.class);
			intent.putExtra("islogin", true);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void init() {
		String curuser = preferences.getString(USERID, "");
		String curpass = preferences.getString(PASSWORD, "");
		if (curuser != "") {
			login_edit_account.setText(curuser);
		}
		if (curpass != "") {
			login_edit_pwd.setText(curpass);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Bundle bundle = msg.getData();
				if (bundle != null) {
					String typeString = bundle.getString("type");
					if (typeString.equals("login")) {
						int result = bundle.getInt("result");
						switch (result) {
						case 10:

							SharedPreferences.Editor editor = preferences
									.edit();
							editor.putString(USERID, login_edit_account
									.getText().toString());
							editor.putString(PASSWORD, login_edit_pwd.getText()
									.toString());
							editor.commit();
							Intent intent = new Intent(Login.this,
									MainActivity.class);
							startActivity(intent);
							break;
						case 1:
							progress.dismiss();
							Toast.makeText(Login.this, "服务器设置错误",
									Toast.LENGTH_LONG).show();
							if (accesloginThread != null) {
								Thread dummy = accesloginThread;
								accesloginThread = null;
								dummy.interrupt();
							}
							break;
						case 2:
							progress.dismiss();
							Toast.makeText(Login.this, "用户名或密码错误",
									Toast.LENGTH_LONG).show();
							if (accesloginThread != null) {
								Thread dummy = accesloginThread;
								accesloginThread = null;
								dummy.interrupt();
							}
							break;

						default:
							break;
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	public final class acceswslogin implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Checklogin();

		}

		private void Checklogin() {
			int ischecked = 0;
			//由于原定的webservice有问题，所以使用考勤查询方法代替
			String nameSpace = xmlConfig.readWSInfo(Login.this,
					"logincheck", "namespace");
			// 调用的方法名称
			String methodName = xmlConfig.readWSInfo(Login.this,
					"logincheck", "method");
			// EndPoint
			String endPoint = preferences.getString(serverKEY, "");
			if (endPoint.equals("")) {
				ischecked = 1;
			} else {
				endPoint = endPoint
						+ xmlConfig.readWSInfo(Login.this,
								"logincheck", "URL");
				// SOAP Action
				String soapAction = nameSpace + methodName;

				// 指定WebService的命名空间和调用的方法名
				SoapObject rpc = new SoapObject(nameSpace, methodName);
				rpc.addProperty("userId", login_edit_account.getText()
						.toString());
				rpc.addProperty("password", login_edit_pwd.getText().toString());
				rpc.addProperty("DeviceToken", null);

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
					if (result.equals("anyType{}") || result.equals(null)
							|| result.equals("")) {
						ischecked = 2;
					} else {
						ischecked = 10;
					}
				} catch (Exception e) {
					ischecked = 1;
					e.printStackTrace();
				}
			}
			//ischecked = 10;   //TEST
			Bundle bundle = new Bundle();
			bundle.putString("type", "login");
			bundle.putInt("result", ischecked);
			Message msg = new Message();
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}

	}

	/**
	 * add a keylistener for progress dialog
	 */
	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				dismissDialog();
				if (accesloginThread != null) {
					Thread dummy = accesloginThread;
					accesloginThread = null;
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
}
