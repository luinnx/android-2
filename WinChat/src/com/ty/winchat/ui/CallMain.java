package com.ty.winchat.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectableChannel;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ty.winchat.R;
import com.ty.winchat.WinChatApplication;
import com.ty.winchat.listener.Listener;
import com.ty.winchat.model.UDPMessage;
import com.ty.winchat.service.ChatService;
import com.ty.winchat.service.ChatService.MyBinder;
import com.ty.winchat.ui.Main.MyAdapter;
import com.ty.winchat.ui.Main.MyServiceConnection;
import com.ty.winchat.util.Constant;
import com.ty.winchat.util.LocalMemoryCache;
import com.ty.winchat.util.Util;
import   java.text.SimpleDateFormat;

public class CallMain extends Activity implements OnClickListener {
	private Button btnCancel, btnSend, btnDial, speaker;
	// private String[] numbers;
	private EditText edtNumber;
	private Map<String, Queue<UDPMessage>> messages;
	MyServiceConnection connection;
	protected static final String TAG = "Callmain";
	public static MyBinder binder;
	private boolean binded;
	private MyAdapter adapter;
	private ServerSocket serverSocket = null;
	private PopupWindow popupWindow;
	private boolean started;// 用来标识控件是否渲染完毕
	private ListView listView;
	private final int SHOW_DIALOG = 0XF1001;
	CallLoginfo newlog=null;
	private String localip="";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_main);
		initStrictMode();

		init();
		// numbers = getResources().getStringArray(R.array.numbers);
		edtNumber = (EditText) findViewById(R.id.edtNumber);
		edtNumber.setInputType(InputType.TYPE_NULL);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnDial = (Button) findViewById(R.id.btnOut);
		btnCancel.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		btnDial.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.showdialog_listview);
		findViewById(R.id.btn_0).setOnClickListener(this);
		findViewById(R.id.btn_1).setOnClickListener(this);
		findViewById(R.id.btn_2).setOnClickListener(this);
		findViewById(R.id.btn_3).setOnClickListener(this);
		findViewById(R.id.btn_4).setOnClickListener(this);
		findViewById(R.id.btn_5).setOnClickListener(this);
		findViewById(R.id.btn_6).setOnClickListener(this);
		findViewById(R.id.btn_7).setOnClickListener(this);
		findViewById(R.id.btn_8).setOnClickListener(this);
		findViewById(R.id.btn_9).setOnClickListener(this);
		findViewById(R.id.btn_10).setOnClickListener(this);
		findViewById(R.id.btn_11).setOnClickListener(this);
		started = true;
		
		localip=WinChatApplication.mainInstance.getLocalIp();
		if(localip==null){
			showToast("请检测wifi");
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnSave:
			// Intent in = new
			// Intent(MyDialerActivity.this,AfterActivity.class);
			// startActivity(in);
			break;
		case R.id.btnSend:
			 try {
				 String DisIP = "";
					try {
						DisIP = new xmlConfig(R.xml.roomtoip).readTagValue(this,
								edtNumber.getText().toString());
					} catch (Exception e) {
						// TODO: handle exception
					}
					if(DisIP!=""){
						Socket socket = new Socket(InetAddress.getByName(DisIP), Constant.CALL_PORT);
						socket.close();
						Intent intent2 =new Intent(CallMain.this ,Leavemessage.class);
						intent2.putExtra("disip", DisIP);
						intent2.putExtra("roomno", edtNumber.getText().toString());
						startActivity(intent2);
					}
					else {
						showToast("请求房号不存在，请确认");
					}
				
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(this, "对方不在线或网络错误", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btnOut:
			Socket socket;
			try {
				String roomno="";
				if(localip!=""){
				roomno = new xmlConfig(R.xml.roomtoip).readnofromip (this,
						localip);
				}
				String message  =roomno.toString()+":"+ Listener.ASK_VIDEO;
				String DisIP = "";
				try {
					DisIP = new xmlConfig(R.xml.roomtoip).readTagValue(this,
							edtNumber.getText().toString());
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (DisIP != "") {
					socket = new Socket(InetAddress.getByName(DisIP), Constant.CALL_PORT);
					try {
						PrintWriter out = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(
										socket.getOutputStream())), true);
						out.println(message);
					} catch (Exception e) {
						Toast.makeText(this, "对方不在线或网络错误", Toast.LENGTH_LONG).show();
						// TODO: handle exception
					} finally {
						socket.close();
					}
					Toast.makeText(this, "已发送视频请求，等待对方回应。。。", Toast.LENGTH_LONG).show();
					 
					newlog=new CallLoginfo();
					newlog.Setno(java.util.UUID.randomUUID().toString());
					newlog.Setcalledip(DisIP);
					newlog.Setcallip(WinChatApplication.mainInstance.getLocalIp());
					SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");   
					newlog.Setdate(formatter.format(new   Date(System.currentTimeMillis())));
					newlog.Setaddress(edtNumber.getText().toString());
					newlog.Setcalltype("呼出");
					newlog.Setisconnect("否");
				}
				else {
					showToast("请求房号不存在，请确认");
				}
				
				
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Toast.makeText(this, "对方不在线或网络错误", Toast.LENGTH_LONG).show();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Toast.makeText(this, "对方不在线或网络错误", Toast.LENGTH_LONG).show();
			}

			// String roomno=edtNumber.getText().toString();
			// String ip="192.168.43.147";
			// Intent intent=new Intent(CallMain.this,MessageChat.class );
			// intent.putExtra("IP",ip);
			// intent.putExtra("DeviceCode",
			// WinChatApplication.mainInstance.getDeviceCode());
			// intent.putExtra("name", roomno);
			// startActivity(intent);//跳转到个人聊天界面
			break;
		case R.id.btnCancel:
			setEditValue();
			break;
		default:
			Button btn = (Button) v;
			String text = btn.getText().toString();
			if (null == text || text.equals(""))
				return;
			Editable edit = edtNumber.getText();
			if (edit.length() > 0) {
				edit.insert(edit.length(), text);
			} else {
				edit.insert(0, text);
			}
			break;
		}
	}

	private void setEditValue() {
		int start = edtNumber.getSelectionStart();
		if (start > 0) {
			edtNumber.getText().delete(start - 1, start);
		}
	}
private Handler showhander=new Handler(){
	public void handleMessage(Message msg){
		Bundle bundle = msg.getData();
		btnDial.setText("呼叫");
		showToast(bundle.getString("showinfo"));
	}
};
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Bundle bundle = msg.getData();
				if (!bundle.isEmpty()) {
					String type = bundle.getString("type");
					if (type.equals("savelog")) {
						String callip = bundle.getString("callip");
						String calledip = bundle.getString("calledip");
						String calltype = bundle.getString("calltype");
						String isconnect = bundle.getString("isconnect");
						String dateString = bundle.getString("date");
						String address = bundle.getString("address");

						CallLoginfo newgetlog = new CallLoginfo();
						newgetlog.Setno(java.util.UUID.randomUUID().toString());
						newgetlog.Setcallip(callip);
						newgetlog.Setcalledip(calledip);
						newgetlog.Setcalltype(calltype);
						newgetlog.Setisconnect(isconnect);
						newgetlog.Setdate(dateString);
						newgetlog.Setaddress(address);

						boolean issave1 = writeXml.saveParam2Xml(newgetlog);
						if (!issave1) {
							Log.e("SaveLog", "保存log失败");
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	private void init() {
		new Thread(new serverThreadRunnable()).start();

	}

	public final class serverThreadRunnable implements Runnable {
		String disipString = "";

		@Override
		public void run() {

			try {
				serverSocket = new ServerSocket();
				SocketAddress address = new InetSocketAddress(
						WinChatApplication.mainInstance.getLocalIp(),Constant.CALL_REPLAY_PORT );
				serverSocket.bind(address);
				while (true) {
					final Socket socket = serverSocket.accept();
					disipString = socket.getInetAddress().getHostAddress();
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));
						String msg=in.readLine().trim();
						int meg = Integer.valueOf(msg.split(":")[1]);
						final String roomno=msg.split(":")[0].toString();
						// disipString=
						// socket.getInetAddress().getHostAddress();
						switch (meg) {
						
						case Listener.REPLAY_VIDEO_ALLOW:
							
							
//							Bundle bundle2=new Bundle();
//							bundle2.putString("callip", newlog.Getcallip());
//							bundle2.putString("calledip",newlog.Getcalledip() );
//							bundle2.putString("date",  newlog.Getdate());
//							bundle2.putString("calltype", newlog.Getcalltype());
//							bundle2.putString("isconnect", "是");
//							bundle2.putString("address", newlog.Getaddress());
//							Message msg2=new Message();
//							msg2.setData(bundle2);
//							handler.sendMessage(msg2);
							
							
							Intent intent = new Intent(CallMain.this,
									VideoChat.class);
							intent.putExtra("name", roomno);
							intent.putExtra("IP", disipString);
							intent.putExtra("isaccept", "no");
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							break;
						case Listener.REPLAY_VIDEO_NOT_ALLOW:
							
							Bundle bundle3=new Bundle();
							bundle3.putString("type", "savelog");
							bundle3.putString("callip", newlog.Getcallip());
							bundle3.putString("calledip",newlog.Getcalledip() );
							bundle3.putString("date",  newlog.Getdate());
							bundle3.putString("calltype", newlog.Getcalltype());
							bundle3.putString("isconnect", "否");
							bundle3.putString("address", newlog.Getaddress());
							Message msg3=new Message();
							msg3.setData(bundle3);
							handler.sendMessage(msg3);
							
							Bundle showBundle=new Bundle();
							showBundle.putString("showinfo", "对方拒绝通话");
							Message showMessage=new Message();
							showMessage.setData(showBundle);
							showhander.sendMessage(showMessage);
							
							break;
						case Listener.ASK_NOTREPLAY:
							Bundle bundle4=new Bundle();
							bundle4.putString("type", "savelog");
							bundle4.putString("callip", newlog.Getcallip());
							bundle4.putString("calledip",newlog.Getcalledip() );
							bundle4.putString("date",  newlog.Getdate());
							bundle4.putString("calltype", newlog.Getcalltype());
							bundle4.putString("isconnect", "否");
							bundle4.putString("address", newlog.Getaddress());
							Message msg4=new Message();
							msg4.setData(bundle4);
							handler.sendMessage(msg4);
						//	showToast("对方未响应转入留言模式");
							
							Bundle showBundle2=new Bundle();
							showBundle2.putString("showinfo", "对方未响应转入留言模式");
							Message showMessage2=new Message();
							showMessage2.setData(showBundle2);
							showhander.sendMessage(showMessage2);
							
							 
							Intent intent2 =new Intent(CallMain.this ,Leavemessage.class);
							intent2.putExtra("disip", disipString);
							intent2.putExtra("roomno", roomno);
							startActivity(intent2);
							
							break;
						}

					} catch (Exception e) {
						Log.e("Showdialog", e.toString());
						// TODO: handle exception
					} finally {
						socket.close();
					}
				}

			} catch (IOException e) {
				Log.e(TAG, "Could not create server socket", e);
			}

		}
	}

	// 显示提醒框的高宽
	private final int width = WinChatApplication.width * 3 / 4;
	private final int height = WinChatApplication.height * 2 / 7;

	/**
	 * 显示提醒框
	 * 
	 * @param txt
	 * @param ok
	 */
	private void showDialog(String txt, OnClickListener ok,
			OnClickListener cancl, boolean buttonShow, final String disip,
			final String roomno) {
		if (popupWindow != null)
			popupWindow.dismiss();
		popupWindow = new PopupWindow(getApplicationContext());
		popupWindow.setWidth(width);
		popupWindow.setHeight(height);
		popupWindow.setFocusable(false);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		View view = getLayoutInflater().inflate(R.layout.confirm_dialog, null);
		TextView textView = (TextView) view
				.findViewById(R.id.confirm_dialog_txt);
		Button confirm = (Button) view
				.findViewById(R.id.confirm_dialog_confirm);
		Button cancle = (Button) view.findViewById(R.id.confirm_dialog_cancle);
		if (!buttonShow) {
			confirm.setVisibility(View.INVISIBLE);
			cancle.setVisibility(View.INVISIBLE);
		} else {
			confirm.setOnClickListener(ok);
			cancle.setOnClickListener(cancl);
		}
		popupWindow.setContentView(view);
		textView.setText(txt);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!started) {
						Thread.sleep(500);
					}
					Bundle bundle = new Bundle();
					bundle.putString("type", "showdialog");
					bundle.putString("disip", disip);
					bundle.putString("roomno", roomno);
					Message msg = new Message();
					msg.setData(bundle);
					handler.sendMessage(msg);
					 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	protected void onPause() {
//		super.onPause();
//		if (serverSocket != null) {
//			try {
//				serverSocket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		if (serverSocket != null) {
//			try {
//				serverSocket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	@Override
	protected void onRestart() {
		super.onRestart();
		String ip = WinChatApplication.mainInstance.getLocalIp();
		if (ip == null) {
			showToast("请检测wifi");
		} else {
			if(serverSocket==null|| !serverSocket.isBound()){
			init();
			started = true;
			}
		}
	}
	/**
	* 防止出现NetworkOnMainThreadException 异常处理
	*/
	private void initStrictMode() {
	// 判断操作系统是Android版本3.0以上版本
	if(android.os.Build.VERSION.SDK_INT >= 11) {
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	.detectDiskReads()
	.detectDiskWrites()
	.detectNetwork() 
	.penaltyLog()
	.build());

	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	.detectLeakedSqlLiteObjects()
	.penaltyLog()
	.penaltyDeath()
	.build());
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
