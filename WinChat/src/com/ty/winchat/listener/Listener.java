package com.ty.winchat.listener;

import java.io.IOException;

public abstract class Listener extends Thread{
	
	public static final int NONE=0;//没有动作
	
	public static final int ADD_USER=1;//增加用户
	public static final int LOGIN_SUCC=2;//增加用户成功
	public static final int REMOVE_USER=3;//删除用户
	
	public static final int RECEIVE_MSG=4;//接收消息
	public static final int RECEIVE_FILE=5;//接收文件
	
	public static final int HEART_BEAT=6;//发送心跳包
	public static final int HEART_BEAT_REPLY=7;//心跳包回复
	
	public static final int ASK_SEND_FILE=8;//请求发送文件
	public static final int REPLAY_SEND_FILE=9;//回复请求发送文件
	
	public static final int REQUIRE_ICON=10;//请求发送头像
	
	public static final int ASK_VIDEO=11;//请求视屏聊天

	public static final int REPLAY_VIDEO_ALLOW=12;//请求视屏聊天
	public static final int REPLAY_VIDEO_NOT_ALLOW=13;//请求视屏聊天
	
	public static final int TO_ALL_MESSAGE=14;//所有在线群聊信息
	public static final int ASK_VOICE=15;//请求语音聊天
	public static final int REPLAY_VOICE_ALLOW=16;//请求视屏聊天
	public static final int REPLAY_VOICE_NOT_ALLOW=17;//请求视屏聊天
	public static final int ASK_NOTREPLAY=18;//請求未迴應
	public static final int ENDVIDEO=0; //結束視頻
	public static final int OPENDOOR=1; //开门请求
	public static final int OPENDOOR_SUCCES=2; //开门成功
	public static final int OPENDOOR_FAILED=3; //开门失败
	
	/**打开监听器*/
	abstract void open() throws IOException;
	/**关闭监听器*/
	abstract void close() throws IOException;
}
