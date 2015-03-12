package com.ty.winchat.ui;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.webrtc.videoengineapp.ViEAndroidJavaAPI;

import com.ty.winchat.R;
import com.ty.winchat.ui.VideoChat.RenderType;

import android.app.Activity;
import android.drm.DrmStore.Playback;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Callaudioplay extends Activity {
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callaudioplay);
		String filename=getIntent().getStringExtra("filename");
		//play(filename);
		ImageView jpgView = (ImageView)findViewById(R.id.jpgview);
		String myJpgPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/"+filename+".jpg";
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
		if (bm!=null){
		jpgView.setImageBitmap(bm);
		}
	
//		setupVoE();
//		vieAndroidAPI.VoE_StartPlayingFileLocally(-1, Environment.getExternalStorageDirectory().getAbsolutePath()+"/winchat/videolog/Test.rtp", false);
		}
	
	private int setupVoE() {
		// Create VoiceEngine
		// Error logging is done in native API wrapper
		if (null == vieAndroidAPI) {
			vieAndroidAPI = new ViEAndroidJavaAPI(Callaudioplay.this);
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
	
	
	private void play(String filename){
		
		 File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/"+filename+".rtp");

	      // Get the length of the audio stored in the file (16 bit so 2 bytes per short)

	      // and create a short array to store the recorded audio.

	      int musicLength = (int)(file.length());

	      byte[] music = new byte[musicLength];

	 
	 
	      try {

	        // Create a DataInputStream to read the audio data back from the saved file.

	        InputStream is = new FileInputStream(file);

	        BufferedInputStream bis = new BufferedInputStream(is);

	        DataInputStream dis = new DataInputStream(bis);

	         
	        // Read the file into the music array.

	        int i = 0;
	       
	        while (dis.available() > 0) {
	          music[i] = dis.readByte();

	          i++;

	        }

	 
	 
	        // Close the input streams.

	        dis.close();    
	 
	 
	        // Create a new AudioTrack object using the same parameters as the AudioRecord

	        // object used to create the file.

	        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,

	                                               11025,

	                                               AudioFormat.CHANNEL_CONFIGURATION_MONO,

	                                               AudioFormat.ENCODING_PCM_16BIT,

	                                               musicLength*2,

	                                               AudioTrack.MODE_STREAM);

	        // Start playback

	        audioTrack.play();

	     
	        // Write the music buffer to the AudioTrack object

	        audioTrack.write(music, 0, musicLength);

	 
	        audioTrack.stop() ;

	 
	      } catch (Throwable t) {

	        Log.e("AudioTrack","Playback Failed");

	      }
	}

}
