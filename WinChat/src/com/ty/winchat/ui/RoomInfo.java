package com.ty.winchat.ui;

import android.R.integer;
import android.util.Property;

public class RoomInfo {
	private int roomid;
private String roomno;
private String roomip;
public RoomInfo(){
	roomid=0;
	roomno="";
	roomip="";
			
}
public void setid(int id){
	roomid=id;
}
public void setno(String no){
	roomno =no;
}
public void setip(String ip){
	roomip=ip;
}
 public String getip(){
	 return roomip;
 }
 public String getno(){
	 return roomno;
 }
}
