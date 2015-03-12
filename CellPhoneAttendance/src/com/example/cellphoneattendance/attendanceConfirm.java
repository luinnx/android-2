package com.example.cellphoneattendance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Provider;
import java.util.List;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.ant.liao.GifView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

public class attendanceConfirm extends Activity {
	private ImageView thunbPhoto;
	// private EditText clientText;
	private EditText phoneNumText;
	private TextView coordinatetext;
	private TextView placetext;
	private ImageButton openCamera;
	private GifView gf1;
	private Chronometer chronometer1;
	private LocationManager manager;
	private double currentLongitude = 0;
	private double currentLatitude = 0;
	private String currentPlace = "";
	private Uri photoFileUri;
	private static final String PRE_NAME = "InfoConfig";
	private static final String phoneKEY = "phone";
	private static final String nameKEY = "name";
	private static final String serverKEY = "server";
	private SharedPreferences preferences;
	private Thread accessWSThread;
	private msgHandler showResult = new msgHandler();
	private ProgressDialog progress;
	private Button btnattendanceconfirm;
	private static final String USERID = "Username";
	private static final String PASSWORD = "Userpassword";
	private EditText Etname;

	// private EditText etcontact;
	// private EditText etcontactphone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportedattendance);

		init();
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

		btnattendanceconfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection() && checkInternetGPS()) {
					if (validateInfo()) {

						if (accessWSThread == null) {

							progress = ProgressDialog.show(
									attendanceConfirm.this, "提示", "上报中……");
							progress.setCancelable(false);
							progress.setOnKeyListener(onKeyListener);

							accessWSThread = new Thread(
									new accessWSThreadRunnable());
							accessWSThread.start();
						}
					}
				}
			}
		});
	}

	// 获取最佳的定位服务
	public String getProvideString() {
		// 创建一个Criteria对象
		Criteria criteria = new Criteria();
		// 设置精确度
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// 设置是否需要返回海拔信息
		criteria.setAltitudeRequired(false);
		// 设置是否需要返回方位信息
		criteria.setBearingRequired(false);
		// 设置是否允许付费服务
		criteria.setCostAllowed(true);
		// 设置电量消耗等级
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 设置是否需要返回速度信息
		criteria.setSpeedRequired(false);
		// 根据设置的Criteria对象，获取最符合此标准的provider对象
		String currentProvider = manager.getBestProvider(criteria, true);
		return currentProvider;

	}

	public void init() {
		phoneNumText = (EditText) findViewById(R.id.phoneNumText);
		// clientText = (EditText) findViewById(R.id.clientText);
		thunbPhoto = (ImageView) findViewById(R.id.thunbPhoto);
		coordinatetext = (TextView) findViewById(R.id.coordinatetext);
		placetext = (TextView) findViewById(R.id.placetext);
		openCamera = (ImageButton) findViewById(R.id.openCamera);
		btnattendanceconfirm = (Button) findViewById(R.id.btnattendanceconfirm);
		chronometer1 = (Chronometer) findViewById(R.id.chronometer1);
		Etname = (EditText) findViewById(R.id.Etname);
		// etcontact=(EditText )findViewById(R.id.etcontact);
		// etcontactphone =(EditText )findViewById(R.id.etcontactphone );
		gf1 = (GifView) findViewById(R.id.gif1);
		gf1.setGifImage(R.drawable.gps);
		chronometer1
				.setOnChronometerTickListener(new OnChronometerTickListenerImpl());
		chronometer1.start();
		manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		String providesString = getProvideString();
		if (providesString != null) {

			manager.requestLocationUpdates(getProvideString(), 1000, 0,
					new locationlisten());

			// manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
			// 1000, 0, new locationlisten());
		}
		preferences = this.getSharedPreferences(PRE_NAME, MODE_WORLD_READABLE);
		String bindPhoneString = "";
		bindPhoneString = getLocalNumber();
		if (bindPhoneString.equals(""))
			bindPhoneString = preferences.getString(phoneKEY, "");
		phoneNumText.setText(bindPhoneString);
		Etname.setText(preferences.getString(nameKEY, ""));
	}

	public boolean validateInfo() {
		if (coordinatetext.getText().equals("")
				|| placetext.getText().equals("")) {
			Toast.makeText(attendanceConfirm.this, "手机正在定位，请稍后!",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (phoneNumText.getText().equals("")) {
			Toast.makeText(attendanceConfirm.this, "请输入手机号码!",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// if (clientText.getText().equals("")) {
		// Toast.makeText(attendanceConfirm.this, "请填写拜访客户!",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		if (photoFileUri != null && photoFileUri.getPath() != "") {

			String photoPathString = photoFileUri.getPath();
			File photoFile = new File(photoPathString);
			if (!photoFile.exists()) {
				Toast.makeText(attendanceConfirm.this, "请上传照片!",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(attendanceConfirm.this, "请上传照片!", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Bundle bundle = msg.getData();
				if (!bundle.isEmpty()) {
					String type = bundle.getString("type");
					if (type.equals("settext")) {
						String textviewid = bundle.getString("textviewid");
						String textcontent=bundle.getString("content");
						if(textviewid.equals("coordinatetext")){
							coordinatetext.setText(textcontent);
						}
						else if (textviewid .equals("placetext")){
							gf1.setVisibility(View.GONE);
							currentPlace = textcontent;
							placetext.setText(textcontent);
							chronometer1.stop();
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// 更新坐标和地点
	private void updateView(Location location) {
		String buffer = "";
		String latLongString = "";
		if (location != null) {
			currentLongitude = location.getLongitude();
			currentLatitude = location.getLatitude();
			buffer = (int) currentLongitude + "," + (int) currentLatitude;
			Bundle bundle1=new Bundle();
			bundle1.putString("type", "settext");
			bundle1.putString("textviewid", "coordinatetext");
			bundle1.putString("content", buffer.toString());
			Message msg1 =new Message();
			msg1.setData(bundle1);
			handler.sendMessage(msg1);
			
			List<Address> addList = null;
			Geocoder ge = new Geocoder(this);
			try {
				addList = ge.getFromLocation(currentLatitude, currentLongitude,
						1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (addList != null && addList.size() > 0) {
				for (int i = 0; i < addList.size(); i++) {
					Address ad = addList.get(i);
					if (ad.getCountryName() != null)
						latLongString += ad.getCountryName();
					if (ad.getAdminArea() != null)
						latLongString += ad.getAdminArea();
					if (ad.getSubAdminArea() != null)
						latLongString += ad.getSubAdminArea();
					if (ad.getLocality() != null)
						latLongString += ad.getLocality();
					if (ad.getSubLocality() != null)
						latLongString += ad.getSubLocality();
					if (ad.getThoroughfare() != null)
						latLongString += ad.getThoroughfare();
					if (ad.getSubThoroughfare() != null)
						latLongString += ad.getSubThoroughfare();
				}
			}
			Bundle bundle=new Bundle();
			bundle.putString("type", "settext");
			bundle.putString("textviewid", "placetext");
			bundle.putString("content", latLongString);
			Message msg =new Message();
			msg.setData(bundle);
			handler.sendMessage(msg);
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

	// 处理拍照的照片
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
		} else if (requestCode == 0) {
			String providesString = getProvideString();
			if (providesString != null) {
				manager.requestLocationUpdates(getProvideString(), 1000, 0,
						new locationlisten());
				// manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				// 1000, 0, new locationlisten());
			}
		}
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

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	public final class accessWSThreadRunnable implements Runnable {
		@Override
		public void run() {
			sendAttendance();
		}

		public void sendAttendance() {
			// 命名空间
			String nameSpace = xmlConfig.readWSInfo(attendanceConfirm.this,
					"attendanceUp", "namespace");
			// 调用的方法名称
			String methodName = xmlConfig.readWSInfo(attendanceConfirm.this,
					"attendanceUp", "method");
			// EndPoint
			String endPoint = preferences.getString(serverKEY, "");
			if (endPoint.equals(""))
				return;
			endPoint = endPoint
					+ xmlConfig.readWSInfo(attendanceConfirm.this,
							"attendanceUp", "URL");
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);

			// 设置需调用WebService接口传入的参数
			rpc.addProperty("userId", preferences.getString(USERID, ""));
			rpc.addProperty("password", preferences.getString(PASSWORD, ""));
			rpc.addProperty("phone", phoneNumText.getText().toString());
			// rpc.addProperty("customer", clientText.getText().toString());
			// rpc.addProperty("customer", "");
			rpc.addProperty("coordinateX", currentLongitude + " ");
			rpc.addProperty("coordinateY", currentLatitude + " ");
			rpc.addProperty("place", currentPlace);
			// rpc.addProperty("attendanceTime", "");

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
				// rpc.addProperty("customer_linker",
				// etcontact.getText().toString());
				// rpc.addProperty("customer_linkphone", etcontactphone
				// .getText().toString());
				// rpc.addProperty("customer_linker", "");
				// rpc.addProperty("customer_linkphone", "");

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
			} else if (message.toLowerCase().equals("false")) {
				message = "上报失败!";
			} else {
				message = "服务器地址设置不正确或无法访问服务!";
			}
			Toast.makeText(attendanceConfirm.this, message, Toast.LENGTH_SHORT)
					.show();
			progress.dismiss();
			accessWSThread = null;
		}
	}

	public class locationlisten implements LocationListener {

		@Override
		public void onLocationChanged(final Location arg0) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateView(arg0);
				}
			}).start();

		}

		@Override
		public void onProviderDisabled(String arg0) {

		}

		@Override
		public void onProviderEnabled(String arg0) {

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

		}
	};

	public class OnChronometerTickListenerImpl implements
			OnChronometerTickListener {

		@Override
		public void onChronometerTick(Chronometer chronometer) {
			// TODO Auto-generated method stub
			String time = chronometer.getText().toString();
			if (time.equals("00:10") && currentPlace.equals("")) {
				Toast.makeText(attendanceConfirm.this,
						"暂时无法获取目前位置，请在室外空旷地带尝试! 或者打开网络定位功能进行大致定位",
						Toast.LENGTH_LONG).show();
				chronometer1.stop();
				// chronometer1.setBase(SystemClock.elapsedRealtime());
				// chronometer1.start();
			}
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

				// finish();
				// System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean checkInternetGPS() {
		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (coordinatetext.getText().equals("")
				|| placetext.getText().equals("")) {
			if (!manager
					.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				Toast.makeText(attendanceConfirm.this, "请开启GPS！",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 0);
				return false;
			}
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
		Toast.makeText(attendanceConfirm.this, "请开启网络连接！", Toast.LENGTH_SHORT)
				.show();
		Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		startActivityForResult(intent, 0);
		return false;
	}

}
