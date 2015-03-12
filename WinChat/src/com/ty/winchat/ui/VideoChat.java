package com.ty.winchat.ui;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.bool;
import android.R.id;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.ty.winchat.R;
import com.ty.winchat.RecordAndPlay;
import com.ty.winchat.WinChatApplication;
import com.ty.winchat.listener.Listener;
import com.ty.winchat.listener.TCPVideoReceiveListener;
import com.ty.winchat.listener.UDPVoiceListener;
import com.ty.winchat.listener.inter.OnBitmapLoaded;
import com.ty.winchat.ui.CallMain.serverThreadRunnable;
import com.ty.winchat.util.Constant;
import com.ty.winchat.util.Util;
import com.ty.winchat.widget.VideoView;

import org.webrtc.videoengine.ViERenderer;
import org.webrtc.videoengineapp.*;

/**
 * 视屏聊天
 * 
 * @author wj
 * @creation 2013-5-15
 */
public class VideoChat extends Base implements OnClickListener, SurfaceHolder.Callback,
		Camera.PreviewCallback, OnBitmapLoaded {

	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	private Camera camera;
	private VideoView myView;

	private ViEAndroidJavaAPI vieAndroidAPI = null;

	// remote renderer
	private SurfaceView remoteSurfaceView = null;

	// local renderer and camera
	private SurfaceView svLocal = null;

	// channel number
	private int channel;
	private int cameraId;
	private int voiceChannel = -1;

	// flags
	private boolean viERunning = false;
	private boolean voERunning = false;

	// debug
	private boolean enableTrace = false;

	// Constant
	private static final String TAG = "WEBRTC";

	private LinearLayout mLlRemoteSurface = null;
	private LinearLayout mLlLocalSurface = null;

	

	public enum RenderType {
		OPENGL, SURFACE, MEDIACODEC
	}

	RenderType renderType = RenderType.OPENGL;

	
	// Audio settings

	private int voiceCodecType = 0;

	private int receivePortVoice = 11113;

	private int destinationPortVoice = 11113;


	private boolean enableAGC = true;

	private boolean enableAECM = true;

	private boolean enableNS = true;



	
	int currentOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
	int currentCameraOrientation = 0;


	private String chatterIP;// 记录当前用户ip
	// 线程池，用来发送图片数据
	private ExecutorService executors = Executors
			.newFixedThreadPool(TCPVideoReceiveListener.THREAD_COUNT);

	final int REFRESH = 0;

	private int port = Constant.VIDEO_PORT;

	private TCPVideoReceiveListener videoReceiveListener;
	private UDPVoiceListener voiceListener;

	private boolean stop;// 标识activity被遮挡
	
	private boolean isRecording = false ;
	private String recordno="";
	 
	Button btnend;
	Button btnopendoor;
	ServerSocket serverSocket;
	private String Isaccept;
	private String localip="";
	private String roomno="";
	private Boolean issaved=false;
	private ImageButton imgbtnendcall;
	private ImageButton imgbtnopendoor;
	private TextView tvvideotime;
	private int hou;
	private int min;
	private int sec;
	private Handler showtimeHandler;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			myView.setBitmap((Bitmap) msg.obj);
			if(!issaved){
				savejpg((Bitmap) msg.obj);
			}
		};
	};
	private void savejpg(Bitmap bitmap){
		try {
			File myCaptureFile = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/"+ recordno + ".jpg");
	        BufferedOutputStream bos = new BufferedOutputStream(
	                                                 new FileOutputStream(myCaptureFile));
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
	        bos.flush();
	        bos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}
	private Handler msgHandler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			try {
				Bundle bundle=msg.getData();
				if(!bundle.isEmpty()){
					String type=bundle.getString("type");
					if(type.equals("endvideo")){
						
					}
					else if (type .equals("opendoor")){
						String categort=bundle.getString("category");
						if(categort.equals("success")){
							Toast.makeText(VideoChat.this, "大门打开成功", Toast.LENGTH_LONG).show();
						}
						else if (categort.equals("failed")){
							Toast.makeText(VideoChat.this, "大门打开失败", Toast.LENGTH_LONG).show();
						}
					}
					else if(type.equals("savelog")){
						String callip=bundle.getString("callip");
						String calledip=bundle.getString("calledip");
						String calltype=bundle.getString("calltype");
						String isconnect=bundle.getString("isconnect");
						String dateString =bundle.getString("date");
						String address=bundle.getString("address");
						String no=bundle.getString("no");
						
						CallLoginfo newgetlog=new CallLoginfo();
						newgetlog.Setcallip(callip);
						newgetlog.Setcalledip(calledip);
						newgetlog.Setcalltype(calltype);
						newgetlog.Setisconnect(isconnect);
						newgetlog.Setdate( dateString);
						newgetlog.Setaddress(address);
						newgetlog.Setno(no);
		 
						
						boolean issave1=  writeXml .saveParam2Xml( newgetlog);
						if(!issave1){
							Log.e("SaveLog", "保存log失败");
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_chat1);
		initStrictMode();
		findViews();
		chatterIP = getIntent().getStringExtra("IP");
		Isaccept=getIntent().getStringExtra("isaccept");
		roomno=getIntent().getStringExtra("name");
		localip=WinChatApplication.mainInstance.getLocalIp();
		showtimeHandler=new Handler();
		showtimeHandler.postDelayed(refreshTime, 1000);
		hou = 0;
		min = 0;
		sec = 0;
		isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/");
		if(localip==null){
			showToast("请检测wifi");
		}
		// mediaManager= new MediaManager(getApplicationContext());
		// mediaManager.startAudio(chatterIP ,port);

		
			setupVoE();
			startVoiceEngine();
			recordno=   java.util.UUID.randomUUID().toString() ;
			try {
			new Thread(new serverThreadRunnable()).start();

			// voiceListener=UDPVoiceListener.getInstance(InetAddress.getByName(chatterIP));
			// voiceListener.open();
		} catch (Exception e) {
			e.printStackTrace();
			showToast("抱歉，语音聊天器打开失败");
			try {
				voiceListener.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					videoReceiveListener = TCPVideoReceiveListener
							.getInstance();
					videoReceiveListener.setBitmapLoaded(VideoChat.this);
					if (!videoReceiveListener.isRunning())
						videoReceiveListener.open();// 先监听端口，然后连接
				} catch (IOException e1) {
					e1.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showToast("非常抱歉,视屏连接失败");
							finish();
						}
					});
				}
			}
		}).start();
		// SpotManager.getInstance(this).showSpotAds(this);
	}

	

	private void findViews() {
		surfaceView = (SurfaceView) findViewById(R.id.video_chat_preview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		myView = (VideoView) findViewById(R.id.video_chat_myview);
//		btnend=(Button) findViewById(R.id.btnend);
//		btnopendoor=(Button)findViewById(R.id.btnopendoor);
//		btnend.setOnClickListener(this);
//		btnopendoor.setOnClickListener(this);
		imgbtnendcall =(ImageButton ) findViewById(R.id.imgbtnendcall);
		imgbtnopendoor =(ImageButton) findViewById(R.id.imgbtnopendoor);
		imgbtnendcall .setOnClickListener(this);
		imgbtnopendoor.setOnClickListener(this);
		tvvideotime=(TextView) findViewById(R.id.tvvideotime);
		 
		// TextView topTitle=(TextView) findViewById(R.id.toptextView);
		// topTitle.setText(getIntent().getStringExtra("name"));
	}

	@Override
	protected void onPause() {
		stop = true;
		super.onPause();
	}
	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		executors.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket(
							InetAddress.getByName(chatterIP), port);
					OutputStream out = socket.getOutputStream();

					YuvImage image = new YuvImage(data, PreviewFormat, w, h,
							null);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					Rect rect = new Rect(0, 0, w, h);
					// 1：将YUV数据格式转化成jpeg
					if (!image.compressToJpeg(rect, 100, os))
						return;

					// 2：将得到的字节数组压缩成bitmap
					Bitmap bmp = Util.decodeVideoBitmap(os.toByteArray(), 200);
					// Bitmap bmp =
					// Util.decodeSampledBitmapFromFile(os.toByteArray(), 200,
					// 200);
					// Bitmap bmp=BitmapFactory.decodeByteArray(data, offset,
					// length, opts)
					Matrix matrix = new Matrix();
					matrix.setRotate(0);
					// matrix.postScale(2.0f, 2.0f);
					// 3：旋转90
					bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
							bmp.getHeight(), matrix, true);
					// 4：将最后的bitmap转化为字节流发送
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					out.write(baos.toByteArray());
					out.flush();
					out.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onBitmapLoaded(Bitmap bitmap) {
		handler.sendMessage(handler.obtainMessage(REFRESH, bitmap));
		if (stop) {
			try {
				// 代码实现模拟用户按下back键
				String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
				Runtime runtime = Runtime.getRuntime();
				runtime.exec(keyCommand);
				stop = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		int cameras = Camera.getNumberOfCameras();
		CameraInfo info = new CameraInfo();
		for (int i = 0; i < cameras; i++) {
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				camera = Camera.open(i);
				break;
			}
		}
		// 没有前置摄像头
		if (camera == null)
			camera = Camera.open();
		try {
			camera.setPreviewDisplay(surfaceHolder);
			camera.setPreviewCallback(this);
		} catch (Exception e) {
			camera.release();// 释放资源
			camera = null;
		}
	}

	int w; // 宽度
	int h;
	int PreviewFormat;

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters parameters = camera.getParameters();// 得到相机设置参数
		Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
		w = size.width;
		h = size.height;
		parameters.setPictureFormat(PixelFormat.JPEG);// 设置图片格式
		PreviewFormat = parameters.getPreviewFormat();
		setDisplayOrientation(camera, -90);
		// if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
		// setDisplayOrientation(camera, 90);
		// } else {
		// if (getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_PORTRAIT) {
		// parameters.set("orientation", "portrait");
		// parameters.set("rotation", 90);
		// }else if (getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		// parameters.set("orientation", "landscape");
		// parameters.set("rotation", 90);
		// }
		// }
		camera.setPreviewCallback(this);
		camera.setParameters(parameters);
		camera.startPreview();// 开始预览
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		try {
			Method downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
//	@Override
//	protected void onStop(){
//		super.onStop();
//		if(isRecording){
//			isRecording=false;
//		        String contype="";
//		        String callip="";
//		        String calledip="";
//				if(Isaccept.equals("yes")){
//					contype="呼入"; 
//					callip=chatterIP;
//					calledip=localip;
//				}
//				else {
//					contype="呼出";
//					callip=localip;
//					calledip=chatterIP;
//				}
//		        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss"); 
//				Bundle bundle=new Bundle();
//				bundle.putString("type", "savelog");
//				bundle.putString("callip", callip);
//				bundle.putString("calledip",calledip );
//				bundle.putString("date", formatter.format(new   Date(System.currentTimeMillis())));
//				bundle.putString("calltype", contype);
//				bundle.putString("isconnect", "是");
//				bundle.putString("address", roomno);
//				bundle.putString("no",recordno);
//				Message msg1=new Message();
//				msg1.setData(bundle);
//				msgHandler.sendMessage(msg1);  
//		        
//		}
//	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			videoReceiveListener.close();
			serverSocket.close();
			if(isRecording){
				isRecording=false;
			}
			// voiceListener.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vieAndroidAPI != null) {

			if (voERunning) {
				voERunning = false;
				stopVoiceEngine();
			}
			if (viERunning) {
				viERunning = false;
				vieAndroidAPI.StopRender(channel);
				vieAndroidAPI.StopReceive(channel);
				vieAndroidAPI.StopSend(channel);
				vieAndroidAPI.RemoveRemoteRenderer(channel);
				vieAndroidAPI.StopCamera(cameraId);
				vieAndroidAPI.Terminate();
				mLlRemoteSurface.removeView(remoteSurfaceView);
				mLlLocalSurface.removeView(svLocal);
				remoteSurfaceView = null;
				svLocal = null;
			}
		}
	}

	/**
	 * socket池，用来缓存
	 */
	@Deprecated
	class SocketPool extends Thread {
		private List<Socket> sockets = new LinkedList<Socket>();
		private final int poolSize = 30;
		private boolean go = true;

		@Override
		public void run() {
			InetAddress address = null;
			try {
				address = InetAddress.getByName(chatterIP);
				while (go) {
					int count = sockets.size();
					if (count < poolSize) {
						for (int i = 0; i < poolSize - count; i++) {
							sockets.add(new Socket(address, port));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Socket getSocket() {
			if (!sockets.isEmpty()) {
				Socket socket = sockets.get(0);
				sockets.remove(0);
				return socket;
			}
			return null;
		}

		public void close() {
			go = false;
			for (Socket socket : sockets) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void stopVoiceEngine() {
		// Stop send
		if (0 != vieAndroidAPI.VoE_StopSend(voiceChannel)) {
			Log.d(TAG, "VoE stop send failed");
		}

		// Stop listen
		if (0 != vieAndroidAPI.VoE_StopListen(voiceChannel)) {
			Log.d(TAG, "VoE stop listen failed");
		}

		// Stop playout
		if (0 != vieAndroidAPI.VoE_StopPlayout(voiceChannel)) {
			Log.d(TAG, "VoE stop playout failed");
		}

		if (0 != vieAndroidAPI.VoE_DeleteChannel(voiceChannel)) {
			Log.d(TAG, "VoE delete channel failed");
		}
		voiceChannel = -1;

		// Terminate
		if (0 != vieAndroidAPI.VoE_Terminate()) {
			Log.d(TAG, "VoE terminate failed");
		}
	}

	private int setupVoE() {
		// Create VoiceEngine
		// Error logging is done in native API wrapper
		if (null == vieAndroidAPI) {
			vieAndroidAPI = new ViEAndroidJavaAPI(VideoChat.this);
		}
		vieAndroidAPI.Init(enableTrace);
		vieAndroidAPI.VoE_SetECStatus(enableAECM);
		vieAndroidAPI.VoE_SetNSStatus(enableNS);
		vieAndroidAPI.VoE_SetAGCStatus(enableAGC);

		vieAndroidAPI.VoE_Create(getApplicationContext());

		// Initialize
		if (0 != vieAndroidAPI.VoE_Init(enableTrace)) {
			Log.d(TAG, "VoE init failed");
			return -1;
		}

		// Create channel
		voiceChannel = vieAndroidAPI.VoE_CreateChannel();
		if (0 != voiceChannel) {
			Log.d(TAG, "VoE create channel failed");
			return -1;
		}

		// Suggest to use the voice call audio stream for hardware volume
		// controls
		setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		return 0;
	}

	private int startVoiceEngine() {
		// Set local receiver
		if (0 != vieAndroidAPI.VoE_SetLocalReceiver(voiceChannel,
				receivePortVoice)) {
			Log.d(TAG, "VoE set local receiver failed");
		}

		if (0 != vieAndroidAPI.VoE_StartListen(voiceChannel)) {
			Log.d(TAG, "VoE start listen failed");
		}

		// Route audio
		routeAudio(false);

		// set volume to default value
		// if (0 != vieAndroidAPI.VoE_SetSpeakerVolume(volumeLevel)) {
		// Log.d(TAG, "VoE set speaker volume failed");
		// }

		// Start playout
		if (0 != vieAndroidAPI.VoE_StartPlayout(voiceChannel)) {
			Log.d(TAG, "VoE start playout failed");
		}

		if (0 != vieAndroidAPI.VoE_SetSendDestination(voiceChannel,
				destinationPortVoice, getRemoteIPString())) {
			Log.d(TAG, "VoE set send  destination failed");
		}

		if (0 != vieAndroidAPI.VoE_SetSendCodec(voiceChannel, voiceCodecType)) {
			Log.d(TAG, "VoE set send codec failed");
		}

		if (0 != vieAndroidAPI.VoE_SetECStatus(enableAECM)) {
			Log.d(TAG, "VoE set EC Status failed");
		}

		if (0 != vieAndroidAPI.VoE_SetAGCStatus(enableAGC)) {
			Log.d(TAG, "VoE set AGC Status failed");
		}

		if (0 != vieAndroidAPI.VoE_SetNSStatus(enableNS)) {
			Log.d(TAG, "VoE set NS Status failed");
		}

		if (0 != vieAndroidAPI.VoE_StartSend(voiceChannel)) {
			Log.d(TAG, "VoE start send failed");
		}
		if(0!=vieAndroidAPI.VoE_StartIncomingRTPDump(voiceChannel, Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/Test.rtp")){
			Log.d(TAG, "VoE start record failed");
		}
//		if(0!=vieAndroidAPI.StartRecordingPlayout(channel,Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/Test.pcm",false)){
//			Log.e(TAG, "Recordfaild");
//		}
		voERunning = true;
		return 0;
	}

	private void routeAudio(boolean enableSpeaker) {
		if (0 != vieAndroidAPI.VoE_SetLoudspeakerStatus(enableSpeaker)) {
			Log.d(TAG, "VoE set louspeaker status failed");
		}
	}

	private String getRemoteIPString() {
		return getIntent().getStringExtra("IP");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgbtnendcall:
			
//			try {
//				try {
//					videoReceiveListener.close();
//					serverSocket.close();
//					if(isRecording){
//						isRecording=false;
//					}
//					// voiceListener.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (vieAndroidAPI != null) {
//
//					if (voERunning) {
//						voERunning = false;
//						stopVoiceEngine();
//					}
//					if (viERunning) {
//						viERunning = false;
//						vieAndroidAPI.VoE_StopIncomingRTPDump(channel);
//						vieAndroidAPI.StopRender(channel);
//						vieAndroidAPI.StopReceive(channel);
//						vieAndroidAPI.StopSend(channel);
//						vieAndroidAPI.RemoveRemoteRenderer(channel);
//						vieAndroidAPI.StopCamera(cameraId);
//						vieAndroidAPI.Terminate();
//						mLlRemoteSurface.removeView(remoteSurfaceView);
//						mLlLocalSurface.removeView(svLocal);
//						
//						remoteSurfaceView = null;
//						svLocal = null;
//					}
//				}
//				
//				 String contype="";
//			        String callip="";
//			        String calledip="";
//					if(Isaccept.equals("yes")){
//						contype="呼入"; 
//						callip=chatterIP;
//						calledip=localip;
//					}
//					else {
//						contype="呼出";
//						callip=localip;
//						calledip=chatterIP;
//					}
//			        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss"); 
//					Bundle bundle=new Bundle();
//					bundle.putString("type", "savelog");
//					bundle.putString("callip", callip);
//					bundle.putString("calledip",calledip );
//					bundle.putString("date", formatter.format(new   Date(System.currentTimeMillis())));
//					bundle.putString("calltype", contype);
//					bundle.putString("isconnect", "是");
//					bundle.putString("address", roomno);
//					bundle.putString("no",recordno);
//					Message msg1=new Message();
//					msg1.setData(bundle);
//					msgHandler.sendMessage(msg1);  
//				
//				Socket replaysocket=new Socket(InetAddress
//						.getByName(chatterIP), Constant.Videomsg_PORT);;
//				try {
//					
//					String message  =roomno.toString()+":"+ Listener.ENDVIDEO;
//					try {
//						PrintWriter out = new PrintWriter(
//								new BufferedWriter(
//										new OutputStreamWriter(
//												replaysocket
//														.getOutputStream())),
//								true);
//						out.println(message);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				} catch (Exception e) {
//					// TODO: handle exception
//				} finally{
//					replaysocket.close();
//					
//				}
				try {
					// 代码实现模拟用户按下back键
					String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
					Runtime runtime = Runtime.getRuntime();
					runtime.exec(keyCommand);
					stop = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			break;
		case R.id.imgbtnopendoor:
			try {
				Socket replaysocket=new Socket(InetAddress
						.getByName(chatterIP), Constant.Videomsg_PORT);;
				try {
					
					String message  =roomno.toString()+":"+ Listener.OPENDOOR;
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
				}
				} catch (Exception e) {
					// TODO: handle exception
				} finally{
					replaysocket.close();
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		default:
			break;
		}
	}
	public final class serverThreadRunnable implements Runnable {
		String disipString = "";

		@Override
		public void run() {

			try {
				serverSocket = new ServerSocket();
				SocketAddress address = new InetSocketAddress(
						WinChatApplication.mainInstance.getLocalIp(), Constant.Videomsg_PORT);
				serverSocket.bind(address);
				while (true) {
					final Socket socket = serverSocket.accept();
					disipString = socket.getInetAddress().getHostAddress();
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));
						String msg=in.readLine().trim();
						int meg = Integer.valueOf(msg.split(":")[1]);
					 
						// disipString=
						// socket.getInetAddress().getHostAddress();
						switch (meg) {
						case Listener.ENDVIDEO:
//							String contype="";
//							if(Isaccept.equals("yes")){
//								contype="呼入"; 
//							}
//							else {
//								contype="呼出";
//							}
//							SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss"); 
//							Bundle bundle=new Bundle();
//							bundle.putString("type", "savelog");
//							bundle.putString("callip", disipString);
//							bundle.putString("calledip",localip );
//							bundle.putString("date", formatter.format(new   Date(System.currentTimeMillis())));
//							bundle.putString("calltype", contype);
//							bundle.putString("isconnect", "是");
//							bundle.putString("address", roomno);
//							bundle.putString("no",recordno);
//							Message msg1=new Message();
//							msg1.setData(bundle);
//							msgHandler.sendMessage(msg1);
//							
//							try {
//								videoReceiveListener.close();
//								serverSocket.close();
//								if(isRecording){
//									isRecording=false;
//								}
//								// voiceListener.close();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							if (vieAndroidAPI != null) {
//
//								if (voERunning) {
//									voERunning = false;
//									stopVoiceEngine();
//								}
//								if (viERunning) {
//									viERunning = false;
//									vieAndroidAPI.StopRender(channel);
//									vieAndroidAPI.StopReceive(channel);
//									vieAndroidAPI.StopSend(channel);
//									vieAndroidAPI.RemoveRemoteRenderer(channel);
//									vieAndroidAPI.StopCamera(cameraId);
//									vieAndroidAPI.Terminate();
//									
//									mLlRemoteSurface.removeView(remoteSurfaceView);
//									mLlLocalSurface.removeView(svLocal);
//									
//									remoteSurfaceView = null;
//									svLocal = null;
//								}
//							}
							
							
							try {
								// 代码实现模拟用户按下back键
								String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
								Runtime runtime = Runtime.getRuntime();
								runtime.exec(keyCommand);
								stop = false;
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							break;
							case Listener.OPENDOOR:
								try {
									Socket replaysocket=new Socket(InetAddress
											.getByName(chatterIP), Constant.Videomsg_PORT);;
									try {
										
										String message  =roomno.toString()+":"+ Listener.OPENDOOR_SUCCES;
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
									}
									} catch (Exception e) {
										// TODO: handle exception
									} finally{
										replaysocket.close();
										
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
								break;
								case Listener.OPENDOOR_SUCCES:
									Bundle bundle=new Bundle();
									bundle.putString("type", "opendoor");
									bundle.putString("category", "success");
									Message doormsg=new Message();
									doormsg.setData(bundle);
									msgHandler.sendMessage(doormsg);
									
									break;
								case Listener.OPENDOOR_FAILED:
									Bundle bundle2=new Bundle();
									bundle2.putString("type", "opendoor");
									bundle2.putString("category", "failed");
									Message doormsg2=new Message();
									doormsg2.setData(bundle2);
									msgHandler.sendMessage(doormsg2);
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
			try {
				try {
					videoReceiveListener.close();
					serverSocket.close();
					if(isRecording){
						isRecording=false;
					}
					// voiceListener.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				showtimeHandler.removeCallbacks(refreshTime);
				if (vieAndroidAPI != null) {

					if (voERunning) {
						voERunning = false;
						stopVoiceEngine();
					}
					if (viERunning) {
						viERunning = false;
						vieAndroidAPI.VoE_StopIncomingRTPDump(channel);
						vieAndroidAPI.StopRender(channel);
						vieAndroidAPI.StopReceive(channel);
						vieAndroidAPI.StopSend(channel);
						vieAndroidAPI.RemoveRemoteRenderer(channel);
						vieAndroidAPI.StopCamera(cameraId);
						vieAndroidAPI.Terminate();
						mLlRemoteSurface.removeView(remoteSurfaceView);
						mLlLocalSurface.removeView(svLocal);
						
						remoteSurfaceView = null;
						svLocal = null;
					}
				}
				
				 String contype="";
			        String callip="";
			        String calledip="";
					if(Isaccept.equals("yes")){
						contype="呼入"; 
						callip=chatterIP;
						calledip=localip;
					}
					else {
						contype="呼出";
						callip=localip;
						calledip=chatterIP;
					}
			        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss"); 
					Bundle bundle=new Bundle();
					bundle.putString("type", "savelog");
					bundle.putString("callip", callip);
					bundle.putString("calledip",calledip );
					bundle.putString("date", formatter.format(new   Date(System.currentTimeMillis())));
					bundle.putString("calltype", contype);
					bundle.putString("isconnect", "是");
					bundle.putString("address", roomno);
					bundle.putString("no",recordno);
					Message msg1=new Message();
					msg1.setData(bundle);
					msgHandler.sendMessage(msg1);  
				
				Socket replaysocket=new Socket(InetAddress
						.getByName(chatterIP), Constant.Videomsg_PORT);;
				try {
					
					String message  =roomno.toString()+":"+ Listener.ENDVIDEO;
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
				}
				} catch (Exception e) {
					// TODO: handle exception
				} finally{
					replaysocket.close();
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	boolean isFolderExists(String strFolder) {

		 File file = new File(strFolder);
		 if (!file.exists()) {
			 if (file.mkdirs()) {
				 return true;
				 } 
			 else {
				 return false;
				 }
			 }
		 return true;
		 }
private Runnable refreshTime = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			sec++;
			handler.postDelayed(refreshTime, 1000);
			if (sec >= 60) {
				sec = sec % 60;
				min++;
			}
			if (min >= 60) {
				min = min % 60;
				hou++;
			}
			tvvideotime.setText(timeFormat(hou) + ":" + timeFormat(min) + ":" + timeFormat(sec));
		}
	};
	
	private String timeFormat(int t) {
		if (t / 10 == 0) {
			return "0" + t;
		} else {
			return t + "";
		}
	}

}
