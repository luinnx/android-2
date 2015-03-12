package com.ty.winchat.ui;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlPullParserException;

import com.ty.winchat.R;
import com.ty.winchat.R.drawable;
import com.ty.winchat.WinChatApplication;
import com.ty.winchat.util.Constant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.ty.winchat.listener.Listener;

public class RoomMain extends Activity implements OnCompletionListener {
	private ServerSocket serverSocket = null;
	private ServerSocket leavemsgserverSocket = null;
	private PopupWindow popupWindow;
	private boolean started;// 用来标识控件是否渲染完毕
	protected static final String TAG = "roommain";
	private final int SHOW_DIALOG = 0XF1001;
	private View listView;
	private String localip = "";
	Boolean isRecording = true;
	private int msgcount = 0;
	private TextView tvleavemsgnotify;
	private LinearLayout lilaybtn;
	private MyImageButton btnleavemsg;
	private MyImageButton btnleavemsgcount;
	BadgeView badge5;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roommain);
		initStrictMode();
		lilaybtn = (LinearLayout) findViewById(R.id.lilaybtn);
		localip = WinChatApplication.mainInstance.getLocalIp();
		if (localip == null) {
			showToast("请检测wifi");
		}
		try {
			msgcount = GetLeavemsglog.Getnoreadcount();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tvleavemsgnotify = (TextView) findViewById(R.id.tvleavemsgnotify);
		if (msgcount > 0) {
			tvleavemsgnotify.setText("您有" + msgcount + "条未读留言");
			btnleavemsg = new MyImageButton(this, R.drawable.leavemsg,
					R.string.leavemsg);
			
//			InputStream is = getResources()
//					.openRawResource(R.drawable.leavemsg);
//			Bitmap mBitmap = BitmapFactory.decodeStream(is);
//			Bitmap bmpcount = generatorContactCountIcon(mBitmap);
//			btnleavemsg = new MyImageButton(this, bmpcount, R.string.leavemsg);
//			try {
//				is.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} else {
			btnleavemsg = new MyImageButton(this, R.drawable.leavemsg,
					R.string.leavemsg);
			tvleavemsgnotify.setText("");
		}

		MyImageButton btncall = new MyImageButton(this, R.drawable.call,
				R.string.call);
		MyImageButton btnlog = new MyImageButton(this, R.drawable.log,
				R.string.log);
		MyImageButton btncamera = new MyImageButton(this, R.drawable.camera,
				R.string.camera);
		MyImageButton btnsysset = new MyImageButton(this, R.drawable.sysset,
				R.string.sysset);
		MyImageButton btnadd = new MyImageButton(this, R.drawable.add,
				R.string.add );
		
		lilaybtn.addView(btncall);
		lilaybtn.addView(btnlog);
		lilaybtn.addView(btncamera);
		lilaybtn.addView(btnsysset);
		lilaybtn.addView(btnleavemsg);
		lilaybtn .addView(btnadd);
		
		badge5 = new BadgeView(this, btnleavemsg,lilaybtn);
        badge5.setText(msgcount+"");
        badge5.setBackgroundResource(R.drawable.badge_ifaux);
    	badge5.setTextSize(16);
    	badge5.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
    	if(msgcount>0){
    	badge5.show();
    	}
		tvleavemsgnotify.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(RoomMain.this, GetLeavemsglog.class);
				startActivity(intent);

			}
		});
		btncall.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				String ip = WinChatApplication.mainInstance.getLocalIp();
				if (ip == null) {
					showToast("请检测wifi");
				} else {
					Intent intent = new Intent(RoomMain.this, CallMain.class);
					startActivity(intent);
				}
			}
		});

		btnlog.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RoomMain.this, GetLog.class);
				startActivity(intent);
			}
		});
		btncamera.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				isRecording = false;
			}
		});
		btnsysset.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RoomMain.this, SettingsActivity.class));
			}
		});
		btnleavemsg.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RoomMain.this, GetLeavemsglog.class));
			}
		});

		listView = (View) findViewById(R.id.viewshowdialog);
		String ip = WinChatApplication.mainInstance.getLocalIp();
		if (ip == null) {
			showToast("请检测wifi");
		} else {
			init();
			started = true;
		}

	}

	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

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
					} else if (type.equals("showdialog")) {
						final String disip = bundle.getString("disip");
						final String roomno = bundle.getString("roomno");
						if (popupWindow != null) {
							popupWindow.showAtLocation(listView,
									Gravity.CENTER, 0, 0);
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									int playSound = R.raw.callremin;
									SharedPreferences sp = getSharedPreferences(
											"bell_list_preference",
											Context.MODE_PRIVATE);
									int callremin = sp.getInt("callremin", 1);
									if (mMediaPlayer != null) {
										mMediaPlayer.release();
										mMediaPlayer = null;
									}
									if (callremin == 1) {
										playSound = R.raw.callremin;
									} else if (callremin == 2) {
										playSound = R.raw.callremin2;
									} else if (callremin == 3) {
										playSound = R.raw.callremin3;
									} else if (callremin == 4) {
										playSound = R.raw.callremin4;
									} else {

										return;
									}
									mMediaPlayer = MediaPlayer.create(
											RoomMain.this, playSound);
									mMediaPlayer
											.setOnCompletionListener(RoomMain.this);
									while (popupWindow.isShowing()) {
										msgRemind();
									}
								}
							}).start();
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(20000);
										if (popupWindow.isShowing()) {
											popupWindow.dismiss();

											SimpleDateFormat formatter = new SimpleDateFormat(
													"yyyy年MM月dd日   HH:mm:ss");
											CallLoginfo newgetlog = new CallLoginfo();
											newgetlog.Setno(java.util.UUID
													.randomUUID().toString());
											newgetlog.Setcallip(disip);
											newgetlog.Setcalledip(localip);
											newgetlog.Setcalltype("呼入");
											newgetlog.Setisconnect("否");
											newgetlog.Setdate(formatter.format(new Date(
													System.currentTimeMillis())));
											newgetlog.Setaddress(roomno);

											boolean issave1 = writeXml
													.saveParam2Xml(newgetlog);
											if (!issave1) {
												Log.e("SaveLog", "保存log失败");
											}
											Socket replaysocket;
											try {
												String message = roomno
														.toString()
														+ ":"
														+ Listener.ASK_NOTREPLAY;
												replaysocket = new Socket(
														InetAddress
																.getByName(disip),
														Constant.CALL_REPLAY_PORT);
												try {
													PrintWriter out = new PrintWriter(
															new BufferedWriter(
																	new OutputStreamWriter(
																			replaysocket
																					.getOutputStream())),
															true);
													out.println(message);
												} catch (Exception e) {
													// TODO: handle exception
												} finally {
													replaysocket.close();
												}

											} catch (Exception e) {
												// TODO: handle exception
												e.printStackTrace();
											}

										}
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}).start();
						}
					} else if (type.equals("saveleavemsglog")) {
						Leavemsginfo leavemsginfo = new Leavemsginfo();
						leavemsginfo.setmsgno(bundle.getString("no"));
						leavemsginfo.setmsgfromip(bundle.getString("fromip"));
						leavemsginfo.setmsgfromno(bundle.getString("fromno"));
						leavemsginfo.setisread(bundle.getString("isread"));
						leavemsginfo.settime(bundle.getString("time"));

						boolean issave1 = Leavemsgwritexml
								.saveParam2Xml(leavemsginfo);
						msgcount++;
						 badge5.setText(msgcount+"");
					     if(msgcount>0){
					        	badge5.show();
					        	tvleavemsgnotify.setText("您有" + msgcount + "条未读留言");
					        	}
					        else {
								badge5.hide();
								tvleavemsgnotify.setText("");
							}
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

	/**
	 * 在给定的图片的右上角加上联系人数量。数量用红色表示
	 * 
	 * @param icon
	 *            给定的图片
	 * @return 带联系人数量的图片
	 */
	private Bitmap generatorContactCountIcon(Bitmap icon) {
		// 初始化画布
		int iconSize = 64;
		Log.d(TAG, "the icon size is " + iconSize);
		Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(contactIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		// 在图片上创建一个覆盖的联系人个数
		int contacyCount = msgcount;
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.RED);
		countPaint.setTextSize(20f);

		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(String.valueOf(contacyCount), iconSize - 18, 25,
				countPaint);
		return contactIcon;
	}

	private void init() {
		new Thread(new serverThreadRunnable()).start();
		new Thread(new leavemsgserverThtradRunnable()).start();

	}

	public final class leavemsgserverThtradRunnable implements Runnable {
		String disipString = "";

		@Override
		public void run() {
			if (isFolderExists(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/winchat/leavemsg/")) {
				try {
					leavemsgserverSocket = new ServerSocket();
					SocketAddress address = new InetSocketAddress(
							WinChatApplication.mainInstance.getLocalIp(),
							Constant.LEAVEMSG_PORT);
					leavemsgserverSocket.bind(address);
					while (true) {

						int i = 0;
						final Socket socket = leavemsgserverSocket.accept();
						disipString = socket.getInetAddress().getHostAddress();
						String filename = System.currentTimeMillis() + "";
						File recorFile;
						String recordFileString = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/winchat/leavemsg/" + filename + ".3gp";
						;
						try {
							recorFile = new File(recordFileString);
							if (recorFile.exists()) {
								recorFile.delete();
							}
							recorFile.createNewFile();
							OutputStream outputStream = new FileOutputStream(
									recorFile);
							DataOutputStream dataOutputStream = new DataOutputStream(
									outputStream);
							byte bufferBytes[] = new byte[1024 * 4];
							int readInt = -1;
							int offset = 0;
							InputStream inputStream = socket.getInputStream();
							DataInputStream dataInputStream = new DataInputStream(
									inputStream);
							while ((readInt = dataInputStream.read(bufferBytes)) != -1) {
								dataOutputStream.write(bufferBytes, 0, readInt);
								offset += readInt;
								bufferBytes = new byte[1024 * 4];
							}
							dataOutputStream.close();
							dataInputStream.close();
							socket.close();

							SimpleDateFormat formatter = new SimpleDateFormat(
									"yyyy年MM月dd日   HH:mm:ss");
							String roomno = new xmlConfig(R.xml.roomtoip)
									.readnofromip(RoomMain.this, disipString);
							Bundle bundle = new Bundle();
							bundle.putString("no", filename);
							bundle.putString("type", "saveleavemsglog");
							bundle.putString("fromip", disipString);
							bundle.putString("fromno", roomno);
							bundle.putString("isread", "否");
							bundle.putString("time", formatter.format(new Date(
									System.currentTimeMillis())));
							Message msg = new Message();
							msg.setData(bundle);
							handler.sendMessage(msg);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	public final class serverThreadRunnable implements Runnable {
		String disipString = "";

		@Override
		public void run() {

			try {
				serverSocket = new ServerSocket();
				SocketAddress address = new InetSocketAddress(
						WinChatApplication.mainInstance.getLocalIp(),
						Constant.CALL_PORT);
				serverSocket.bind(address);
				while (true) {
					final Socket socket = serverSocket.accept();
					disipString = socket.getInetAddress().getHostAddress();
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));
						String msg = in.readLine().trim();
						int meg = Integer.valueOf(msg.split(":")[1]);
						final String roomno = msg.split(":")[0].toString();
						final String myroomno = new xmlConfig(R.xml.roomtoip)
								.readnofromip(RoomMain.this, localip);
						// disipString=
						// socket.getInetAddress().getHostAddress();
						switch (meg) {
						case Listener.ASK_VIDEO:
							showDialog("对方请求视屏,同意吗？", new OnClickListener() {
								@Override
								public void onClick(View v) {
									Socket replaysocket;
									try {
										String message = myroomno.toString()
												+ ":"
												+ Listener.REPLAY_VIDEO_ALLOW;
										replaysocket = new Socket(InetAddress
												.getByName(disipString),
												Constant.CALL_REPLAY_PORT);
										try {
											PrintWriter out = new PrintWriter(
													new BufferedWriter(
															new OutputStreamWriter(
																	replaysocket
																			.getOutputStream())),
													true);
											out.println(message);
										} catch (Exception e) {
											// TODO: handle exception
											Log.e("Roommain", e.getMessage());
										} finally {
											replaysocket.close();
										}
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block

										e1.printStackTrace();
										Log.e("Roommain", e1.getMessage());
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										Log.e("Roommain", e1.getMessage());
									}
									// SimpleDateFormat formatter = new
									// SimpleDateFormat
									// ("yyyy年MM月dd日   HH:mm:ss");
									// Bundle bundle=new Bundle();
									// bundle.putString("callip", disipString);
									// bundle.putString("calledip",localip );
									// bundle.putString("date",
									// formatter.format(new
									// Date(System.currentTimeMillis())));
									// bundle.putString("calltype", "呼入");
									// bundle.putString("isconnect", "是");
									// bundle.putString("address", roomno);
									// Message msg=new Message();
									// msg.setData(bundle);
									// handler.sendMessage(msg);

									Intent intent = new Intent(RoomMain.this,
											VideoChat.class);
									intent.putExtra("name", roomno);
									intent.putExtra("IP", disipString);
									intent.putExtra("isaccept", "yes");
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);

									if (popupWindow != null)
										popupWindow.dismiss();
									if (mMediaPlayer != null
											&& mMediaPlayer.isPlaying()) {
										mMediaPlayer.stop();
										mMediaPlayer.release();
									}

								}
							}, new OnClickListener() {
								@Override
								public void onClick(View v) {
									Socket replaysocket;
									try {
										String message = myroomno.toString()
												+ ":"
												+ Listener.REPLAY_VIDEO_NOT_ALLOW;
										replaysocket = new Socket(InetAddress
												.getByName(disipString),
												Constant.CALL_REPLAY_PORT);
										try {
											PrintWriter out = new PrintWriter(
													new BufferedWriter(
															new OutputStreamWriter(
																	replaysocket
																			.getOutputStream())),
													true);
											out.println(message);

											SimpleDateFormat formatter = new SimpleDateFormat(
													"yyyy年MM月dd日   HH:mm:ss");
											Bundle bundle = new Bundle();
											bundle.putString("type", "savelog");
											bundle.putString("callip",
													disipString);
											bundle.putString("calledip",
													localip);
											bundle.putString(
													"date",
													formatter.format(new Date(
															System.currentTimeMillis())));
											bundle.putString("calltype", "呼入");
											bundle.putString("isconnect", "否");
											bundle.putString("address", roomno);
											Message msg = new Message();
											msg.setData(bundle);
											handler.sendMessage(msg);

											if (popupWindow != null)
												popupWindow.dismiss();
											if (mMediaPlayer != null
													&& mMediaPlayer.isPlaying()) {
												mMediaPlayer.stop();
												mMediaPlayer.release();
											}

										} catch (Exception e) {
											// TODO: handle exception
										} finally {
											replaysocket.close();
										}

									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									if (popupWindow != null)
										popupWindow.dismiss();
								}
							}, true, disipString, roomno);
							break;
						// case Listener.REPLAY_VIDEO_ALLOW:
						//
						// Intent intent = new Intent(RoomMain.this,
						// VideoChat.class);
						// intent.putExtra("name", "");
						// intent.putExtra("IP", disipString);
						// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// startActivity(intent);
						// break;
						// case Listener.REPLAY_VIDEO_NOT_ALLOW:
						// showToast("对方拒绝通话");
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
		if (leavemsgserverSocket != null) {
			try {
				leavemsgserverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// @Override
	// protected void onPause() {
	// super.onPause();
	// if (serverSocket != null) {
	// try {
	// serverSocket.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	// @Override
	// protected void onStop() {
	// super.onStop();
	// if (serverSocket != null) {
	// try {
	// serverSocket.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	@Override
	protected void onRestart() {
		super.onRestart();
		try {
			msgcount = GetLeavemsglog.Getnoreadcount();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			lilaybtn.removeView(btnleavemsg);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		try {
//			lilaybtn.removeView(btnleavemsgcount);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}

//			InputStream is = getResources().openRawResource(
//					R.drawable.leavemsg);
//			Bitmap mBitmap = BitmapFactory.decodeStream(is);
//			Bitmap bmpcount = generatorContactCountIcon(mBitmap);
//
//			btnleavemsgcount = new MyImageButton(
//					RoomMain.this, bmpcount, R.string.leavemsg);
//			btnleavemsgcount
//					.setOnClickListener(new Button.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							startActivity(new Intent(
//									RoomMain.this,
//									GetLeavemsglog.class));
//						}
//					});
//			lilaybtn.addView(btnleavemsgcount);
			
	        badge5.setText(msgcount+"");
	        if(msgcount>0){
	        	badge5.show();
	        	tvleavemsgnotify.setText("您有" + msgcount + "条未读留言");
	        	}
	        else {
				badge5.hide();
				tvleavemsgnotify.setText("");
			}
			
		
		// String ip = WinChatApplication.mainInstance.getLocalIp();
		// if (ip == null) {
		// showToast("请检测wifi");
		// } else {
		// init();
		// started = true;
		// }
	}

	/**
	 * 防止出现NetworkOnMainThreadException 异常处理
	 */
	private void initStrictMode() {
		// 判断操作系统是Android版本3.0以上版本
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		}
	}

	private Boolean ispalyBoolean = false;
	MediaPlayer mMediaPlayer = null;

	// 铃声提示用户收到消息
	public void msgRemind() {
		if (!ispalyBoolean) {
			try {
				mMediaPlayer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	boolean isFolderExists(String strFolder) {

		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		ispalyBoolean = false;
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
	        finish();
	        System.exit(0);
	    }
	                 
	    return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}


}
