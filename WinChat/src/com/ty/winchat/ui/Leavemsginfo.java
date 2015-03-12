package com.ty.winchat.ui;

public class Leavemsginfo {
	private String  _msgno;
	private String _msgfromip;
	private String _msgfromno;
	private String _isread;
	private String _time;
	public void setmsgno(String no){
		_msgno=no;
	}
	public void setmsgfromip(String fromip){
		_msgfromip=fromip;
	}
	public void setmsgfromno(String msgfromno) {
		_msgfromno=msgfromno;
	}
	public void setisread(String isread){
		_isread =isread;
	}
	public void settime(String time){
		_time=time;
	}
	public String getmsgno(){
		return _msgno;
	}
	public String getmsgfromip(){
		return _msgfromip;
	}
	public String getmsgfromno(){
		return _msgfromno;
	}
	public String getisread(){
		return _isread;
	}
	public String gettime(){
		return _time;
	}

	public Leavemsginfo() {
		// TODO Auto-generated constructor stub
		_msgno="";
		_msgfromip="";
		_msgfromno="";
		_isread="";
		_time="";
	}

}
