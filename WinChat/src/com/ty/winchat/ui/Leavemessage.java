package com.ty.winchat.ui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.ty.winchat.R;
import com.ty.winchat.util.Constant;
import com.ty.winchat.util.Util;

import android.app.Activity;
import android.app.PendingIntent.CanceledException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Leavemessage extends Activity implements  SurfaceHolder.Callback,OnClickListener {

	private static final String TAG = "LeaveMessage";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Button btncancle;
	private Button btnsend;
	private MediaRecorder mediaRecorder;
	private Camera camera;
	private boolean previewRunning;
	private Thread clientThread;
	private String disip;
	private String roomno;
	private String tempfile;
	private Handler handler;
	private TextView textView_time;
	private int hou;
	private int min;
	private int sec;
	int w; // 宽度
	int h;
	int PreviewFormat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leavemessage);
		tempfile=Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/leavemsg/leavemessage.3gp";
		
		disip=getIntent().getStringExtra("disip");
		roomno=getIntent().getStringExtra("roomno");
		btncancle=(Button) findViewById(R.id.btncancleleave);
		btnsend=(Button) findViewById(R.id.btnsendleave);
		btncancle.setOnClickListener(this);
		btnsend.setOnClickListener(this);
		textView_time = (TextView)this.findViewById(R.id.textView_time);
		 handler = new Handler();
		mSurfaceView = (SurfaceView)findViewById(R.id.mSurfaceViewleavemsg);
        mSurfaceHolder=mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.setVisibility(View.VISIBLE);
        
        int cameras = Camera.getNumberOfCameras();
		CameraInfo info = new CameraInfo();
		for (int i = 0; i < cameras; i++) {
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				camera = Camera.open(i);
				setDisplayOrientation(camera, -90);
				camera.unlock();
				
				break;
			}
		}
		// 没有前置摄像头
		if (camera == null){
			camera = Camera.open();
			camera.setDisplayOrientation(-90);
			camera.unlock();
		
			
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btncancleleave:
			previewRunning=false;
			handler.removeCallbacks(refreshTime);
			 if (mediaRecorder != null) {
					// 停止录制
				 mediaRecorder.stop();
					// 释放资源
				 mediaRecorder.release();
				 mediaRecorder = null;
				}
			 try {
					// 代码实现模拟用户按下back键
					String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
					Runtime runtime = Runtime.getRuntime();
					runtime.exec(keyCommand);
				} catch (IOException e) {
					e.printStackTrace();
				}
			break;
		case R.id.btnsendleave:
			if(btnsend.getText().equals("开始")){
				btnsend.setText("完成并发送");
				previewRunning=true;
				handler.postDelayed(refreshTime, 1000);
				hou = 0;
				min = 0;
				sec = 0;
				clientThread = new Thread(new clientThreadRunnable());
				clientThread.start();
			}
			else{
			previewRunning=false;
			handler.removeCallbacks(refreshTime);
			 if (mediaRecorder != null) {
					// 停止录制
				 mediaRecorder.stop();
					// 释放资源
				 mediaRecorder.release();
				 mediaRecorder = null;
				}
			 try {
					// 代码实现模拟用户按下back键
					String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
					Runtime runtime = Runtime.getRuntime();
					runtime.exec(keyCommand);
				} catch (IOException e) {
					e.printStackTrace();
				}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Socket socket = new Socket();
					try {
						socket.connect( new InetSocketAddress(disip, Constant.LEAVEMSG_PORT), 10000);
					} catch (IOException e) {
						Log.e(TAG, "Connect to Server Failed!", e);
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(socket.isConnected()) {
						DataOutputStream dataOutputStream;
						try{
							OutputStream outputStream = socket.getOutputStream();
							dataOutputStream = new DataOutputStream(outputStream);
							File recordFile = new File(tempfile);
							InputStream fileInputStream = new FileInputStream(recordFile);
							byte bufferBytes[] = new byte[1024*4];
							int readInt = -1;
							int offset = 0;
							while ((readInt=fileInputStream.read(bufferBytes, 0, bufferBytes.length))!=-1) {
								dataOutputStream.write(bufferBytes, 0, readInt);
								offset += readInt;
								bufferBytes = new byte[1024*4];
							}
							dataOutputStream.close();
							fileInputStream.close();
							socket.close();
						}catch(IOException e){
							Log.e(TAG, "Get OutStream Failed!", e);
							//return;
						}
					}
				}
			}).start();
			}
			break;
		default:
			break;
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// surfaceDestroyed的时候同时对象设置为null
		mSurfaceView = null;
		mSurfaceHolder = null;
		mediaRecorder  = null;
		camera.release();
		camera=null;
	}
	 public final class clientThreadRunnable implements Runnable {
			@Override
			public void run() {
				mediaRecorder = new MediaRecorder();
				if(isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/leavemsg/")){

				
			    	try { 
			    		File outputFile = new File(tempfile);
			    		if(outputFile.exists()){
			    			outputFile.delete();
			    		}
			    		
			    		mediaRecorder.setCamera(camera);
			    		mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());//预览 
			    		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源 
			    		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //录音源为麦克风 
			    		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//输出格式为3gp 
			    		//mediaRecorder.setVideoSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());//视频尺寸 
			    		//mediaRecorder.setVideoSize(100, 100);//视频尺寸 
			    		mediaRecorder.setVideoFrameRate(30);//视频帧频率 
			    		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//视频编码 
			    		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//音频编码 
			    		//mediaRecorder.setMaxDuration(5000);//最大期限 
			    		mediaRecorder.setOutputFile(tempfile);//保存路径 
			    		if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) 
			    		{
			    			Toast.makeText(Leavemessage.this, "无SD卡",Toast.LENGTH_LONG).show();
			    		}
			    		mediaRecorder.prepare(); 
			    		mediaRecorder.start();
			    		try {
							Thread.sleep(5000);
							//mediaRecorder.stop();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			    		
					} catch (IOException e) { 
						e.printStackTrace(); 
					} 
				}
		    }
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
			textView_time.setText(timeFormat(hou) + ":" + timeFormat(min) + ":" + timeFormat(sec));
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
