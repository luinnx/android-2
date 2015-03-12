package com.example.cellphoneattendance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.security.auth.PrivateCredentialPermission;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.cellphoneattendance.attendanceConfirm.accessWSThreadRunnable;
import com.example.cellphoneattendance.attendanceConfirm.locationlisten;
import com.example.cellphoneattendance.attendanceConfirm.msgHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomerVisits extends Activity {
	private String CustomernameString;
	private String ContactnameString;
	private String ContactPhoneString;
	private String addressString;
	private EditText etcustomername;
	private EditText etcontactname;
	private EditText etcontactphone;
	private EditText etaddress;
	private EditText etcontent;
	private ImageButton openCamera;
	private ImageView thunbPhoto;
	private Uri photoFileUri;
	private Button btncusvisit;
	private static final String PRE_NAME = "InfoConfig";
	private static final String phoneKEY = "phone";
	private static final String nameKEY = "name";
	private static final String serverKEY = "server";
	private SharedPreferences preferences;
	private Thread accessWSThread;
	private ProgressDialog progress;
	private static final String USERID = "Username";
	private static final String PASSWORD = "Userpassword";
	private msgHandler showResult = new msgHandler();

	public CustomerVisits() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customervisits);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_READABLE);
		findview();
		if (getIntent().getIntExtra("isadd", 0) == 0) {
			CustomernameString = getIntent().getStringExtra("Customername");
			ContactnameString = getIntent().getStringExtra("ContactName");
			ContactPhoneString = getIntent().getStringExtra("ContactPhone");
			addressString = getIntent().getStringExtra("Address");
		} else {
			CustomernameString="";
			ContactnameString="";
			ContactPhoneString="";
			addressString="";
		}
		findview();
		initclick();
	}

	private void findview() {
		etaddress = (EditText) findViewById(R.id.etaddress);
		etcontactname = (EditText) findViewById(R.id.etcontactname);
		etcontactphone = (EditText) findViewById(R.id.etphone);
		etcontent = (EditText) findViewById(R.id.etcontent);
		etcustomername = (EditText) findViewById(R.id.etcustomername);
		thunbPhoto = (ImageView) findViewById(R.id.thunbcusPhoto);
		openCamera = (ImageButton) findViewById(R.id.opencusCamera);
		btncusvisit = (Button) findViewById(R.id.btncusvisit);
		etcustomername.setText(CustomernameString);
		etaddress.setText(addressString);
		etcontactname.setText(ContactnameString);
		etcontactphone.setText(ContactPhoneString);
		etcontent.requestFocus();

	}

	private void initclick() {
		thunbPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 照片预览
				String photoString = photoFileUri.getPath();
				if (photoString.equals(""))
					return;
				File photoFile = new File(photoString);
				if (photoFile.exists()) {
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(photoFile), "image/*");
					startActivity(intent);
				}
			}
		});

		openCamera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开摄像头拍照
				saveFullImage();
			}
		});
		btncusvisit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (validateInfo()) {

					if (accessWSThread == null) {

						progress = ProgressDialog.show(CustomerVisits.this,
								"提示", "上报中……");
						progress.setCancelable(false);
						progress.setOnKeyListener(onKeyListener);

						accessWSThread = new Thread(
								new accessWSThreadRunnable());
						accessWSThread.start();
					}
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 处理照片
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			String photoString = photoFileUri.getPath();
			Uri imageUri = null;
			if (data != null) {
				if (data.hasExtra("data")) {
					Bitmap thunbnail = data.getParcelableExtra("data"); // 处理缩略图
					thunbPhoto.setImageBitmap(thunbnail);
				}
			} else {
				// 处理mOutPutFileUri中的完整图像
				if (photoString.equals(""))
					return;
				File photoFile = new File(photoString);
				if (photoFile.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(photoString);
					thunbPhoto.setImageBitmap(bitmap);
				}
			}
		}
	}

	// 打开相机拍照
	private void saveFullImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 文件夹attendancepic
		String picPath = Environment.getExternalStorageDirectory().toString()
				+ "/DCIM/attendancepic/";
		File picFile = new File(picPath);
		if (!picFile.exists()) {
			picFile.mkdirs();
		}
		File photoFile = new File(picFile, System.currentTimeMillis() + ".jpg");
		photoFileUri = Uri.fromFile(photoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
		startActivityForResult(intent, 1); // 设置拍照Activity返回后传回1，后面onActivityResult调用中用到
	}

	// 取得压缩比例小于800*400
	private String getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();

		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		Bitmap comBitmap = compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		int suffIndex = srcPath.indexOf(".");
		String comPath = "";
		if (suffIndex != -1) {
			comPath = srcPath.substring(0, suffIndex) + "-com."
					+ srcPath.substring(suffIndex + 1, srcPath.length());
		} else {
			comPath = srcPath + "-com.jpg";
		}
		File f = new File(comPath);
		if (!f.exists()) {
			try {
				FileOutputStream out = new FileOutputStream(f);
				comBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
				return comPath;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}
		return comPath;
	}

	// 压缩照片
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	private Boolean validateInfo() {
		if (photoFileUri != null && photoFileUri.getPath() != "") {

			String photoPathString = photoFileUri.getPath();
			File photoFile = new File(photoPathString);
			if (!photoFile.exists()) {
				Toast.makeText(CustomerVisits.this, "请上传照片!",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(CustomerVisits.this, "请上传照片!", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (!checkInternetConnection()) {
			return false;
		}
		return true;
	}

	public boolean checkInternetConnection() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		Toast.makeText(CustomerVisits.this, "请开启网络连接！", Toast.LENGTH_SHORT)
				.show();
		Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		startActivity(intent);
		return false;
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
				if (accessWSThread != null) {
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

	public final class accessWSThreadRunnable implements Runnable {
		@Override
		public void run() {
			sendAttendance();
		}

		public void sendAttendance() {
			// 命名空间
			String nameSpace = xmlConfig.readWSInfo(CustomerVisits.this,
					"customervisit", "namespace");
			// 调用的方法名称
			String methodName = xmlConfig.readWSInfo(CustomerVisits.this,
					"customervisit", "method");
			// EndPoint
			String endPoint = preferences.getString(serverKEY, "");
			if (endPoint.equals(""))
				return;
			endPoint = endPoint
					+ xmlConfig.readWSInfo(CustomerVisits.this,
							"customervisit", "URL");
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);

			// 设置需调用WebService接口传入的参数
			rpc.addProperty("userId", preferences.getString(USERID, ""));
			rpc.addProperty("password", preferences.getString(PASSWORD, ""));
			rpc.addProperty("CompanyName", etcustomername.getText().toString());
			//rpc.addProperty("CustomName", etcustomername.getText().toString());
			rpc.addProperty("Linker", etcontactname.getText().toString());
			rpc.addProperty("LinkPhone", etcontactphone.getText().toString());
			rpc.addProperty("place", etaddress.getText().toString());
			rpc.addProperty("Content", etcontent.getText().toString());
			String photoPath = photoFileUri.getPath();
			String comPhoto = getimage(photoPath);
			if (comPhoto.equals(""))
				return;
			FileInputStream fis;
			try {
				fis = new FileInputStream(comPhoto);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count = 0;
				try {
					while ((count = fis.read(buffer)) >= 0) {
						baos.write(buffer, 0, count);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				String uploadBuffer = new String(Base64.encode(baos
						.toByteArray())); // 进行Base64编码
				rpc.addProperty("photoBuffer", uploadBuffer);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}

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

	public class msgHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				Thread.sleep(2000);
				progress.dismiss();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bundle b = msg.getData();
			String message = b.getString("message");
			if (message.toLowerCase().equals("true")) {
				message = "上报成功!";
				Toast.makeText(CustomerVisits.this, message, Toast.LENGTH_SHORT)
						.show();
				progress.dismiss();
				accessWSThread = null;
				try {
					// 代码实现模拟用户按下back键
					String keyCommand = "input keyevent "
							+ KeyEvent.KEYCODE_BACK;
					Runtime runtime = Runtime.getRuntime();
					runtime.exec(keyCommand);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (message.toLowerCase().equals("false")) {
				message = "上报失败!";
				Toast.makeText(CustomerVisits.this, message, Toast.LENGTH_SHORT)
						.show();
				progress.dismiss();
				accessWSThread = null;
			} else {
				message = "服务器地址设置不正确或无法访问服务!";
				Toast.makeText(CustomerVisits.this, message, Toast.LENGTH_SHORT)
						.show();
				progress.dismiss();
				accessWSThread = null;
			}

		}
	}

	private long exitTime = 0;

}
