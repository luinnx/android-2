package com.ty.winchat.ui;

import com.ty.winchat.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

public class Seeleavemsg extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seeleavemsg);
		String filename=getIntent().getStringExtra("filename");
		VideoView vvleavemsg=(VideoView) findViewById(R.id.vvleavemsg);
		vvleavemsg.setVideoPath(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/leavemsg/"+filename);
		vvleavemsg.setMediaController(new MediaController(Seeleavemsg.this));
		vvleavemsg.requestFocus();
		vvleavemsg.start();

		
		
	}
}
