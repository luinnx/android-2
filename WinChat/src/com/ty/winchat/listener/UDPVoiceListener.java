package com.ty.winchat.listener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;

import com.ty.winchat.util.Constant;
/**
 * 语音收发器
 * @author wj
 * @creation 2013-5-16
 */
public class UDPVoiceListener extends UDPListener{

		//文本消息监听端口
		private final int port=Constant.VOICE_PORT;
		private final int BUFFER_SIZE=1024*6;//6k的数据缓冲区
		
		static final int frequency = 11025;  
	    int recBufSize,playBufSize;  
	    private AudioRecord audioRecord;  
	    private AudioTrack audioTrack;  
	    private InetAddress address; 
	    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");
	    OutputStream os;
	    BufferedOutputStream bos;
	    DataOutputStream dos;
	    
	    private static UDPVoiceListener instance;
	    
	    private boolean go=true;
	    
	    public static UDPVoiceListener getInstance(InetAddress address){
	    	if(instance==null){
	    		instance=new UDPVoiceListener(address);
	    	}
	    	return instance;
	    }
	     
	    private UDPVoiceListener(InetAddress address){
	    	this.address=address;
	    }
	    

	@Override
	    void init() {
		setPort(port);
		setBufferSize(BUFFER_SIZE);
		
		if (file.exists())
	        file.delete();
		  try {

		        file.createNewFile();

		      } catch (IOException e) {

		        throw new IllegalStateException("Failed to create " + file.toString());

		      }
		  try {
			    os = new FileOutputStream(file);

		          bos = new BufferedOutputStream(os);

		          dos = new DataOutputStream(bos);
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		  
		recBufSize = AudioRecord.getMinBufferSize(frequency, AudioFormat. CHANNEL_CONFIGURATION_MONO,  AudioFormat.ENCODING_PCM_16BIT);  
        playBufSize=AudioTrack.getMinBufferSize(frequency,   AudioFormat. CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);  
        
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,  AudioFormat. CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, recBufSize*10);  
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,   AudioFormat. CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, playBufSize, AudioTrack.MODE_STREAM);  
        
	    audioTrack.setStereoVolume(0.8f, 0.8f);//设置当前音量大小  
	    
	    audioRecord.startRecording();//开始录制  
	    audioTrack.play();//开始播放  
      
	      new Thread(new Runnable() {
			@Override
			public void run() {
				while(go){
					send();
				}
			}
		}).start();
	}
	
	/**
	 * 发送语音流
	 */
	void send() {
		byte[]  buffer = new byte[recBufSize]; 
		 
        //从MIC保存数据到缓冲区  
        int bufferReadResult = audioRecord.read(buffer, 0,   recBufSize);  
        
//        try {
//			 for (int i = 0; i <  bufferReadResult; i++)
//			 { 
//		            dos.writeByte(buffer[i]);
//			 }
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//        
        //写入数据即播放,发送数据
        if(bufferReadResult>0)
		 send(buffer,bufferReadResult, address, port);
	}
	
	
	@Override
	public void onReceive(byte[] data, DatagramPacket packet) {
		try {
			 for (int i = 0; i <  packet.getLength(); i++)
			 {
		            dos.writeByte(data[i]);
			 }   
		} catch (Exception e) {
			// TODO: handle exception
		}
		audioTrack.write(data, 0, packet.getLength());
	}

	@Override
	void noticeOffline() throws IOException {
		
	}

	@Override
	void noticeOnline() throws IOException {
		
	}

	@Override
	void sendMsgFailure() {
		
	}
	
	@Override
	public void open() throws IOException {
		super.open();
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		go=false;
		if(audioTrack!=null)
		 audioTrack.stop();  
		if(audioRecord!=null)
         audioRecord.stop();  
         instance=null;
         dos.close();
	}

}
