package com.ty.winchat.ui;

import java.io.IOException;
import java.net.InetAddress;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.ty.winchat.R;
import com.ty.winchat.listener.Listener;
import com.ty.winchat.listener.UDPVoiceListener;
import com.ty.winchat.util.Constant;

/**
 * 语音聊天
 * @author wj
 * @creation 2013-5-7
 */
public class VoiceChat extends Base{

		private String chatterIP;//记录当前用户ip
		private UDPVoiceListener voiceListener;
		private int port=Constant.VIDEO_PORT;

	    @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.voice_chat);  
	        findViews();
	        try {
	        	 
//	        	Intent serviceIntent = new Intent(getApplicationContext(), MediaService.class);
//	        	getApplicationContext().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
	    		chatterIP=getIntent().getStringExtra("IP");
	    		    	
	    		
				voiceListener=UDPVoiceListener.getInstance(InetAddress.getByName(chatterIP));
				 voiceListener.open();
			} catch (Exception e) {
				e.printStackTrace();
//				finish();
				showToast("抱歉，语音聊天器打开失败");
			}
	    }  
	    
	    private void findViews(){
	    	
	    }
	    
	    
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        try {
				voiceListener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }  
	
}
