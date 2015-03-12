package com.ty.winchat.ui;

public class CallLoginfo {
	private String _no;
private String  _callip;
private String _calledip;
private String _date;
private String _calltype;
private String _isconnected;
private String _address;
public CallLoginfo(){
	_calledip="";
	_callip="";
	_no="";
	_date="";
	_calltype ="";
	_isconnected="";
	_address="";
}
public String Getno(){
	return _no;
}
public String Getcallip(){
	return _callip;
}
public String Getcalledip(){
	return _calledip;
}
public String Getdate(){
	return _date;
}
public String Getcalltype(){
	return _calltype;
}
public String Getisconnect(){
	return _isconnected;
}
public String Getaddress(){
	return _address;
}
public void Setno(String no){
	_no=no;
}
public void Setcallip(String callip){
	_callip=callip;
}
public void Setcalledip(String calledip){
	_calledip=calledip;
}
public void Setcalltype(String calltype){
	_calltype=calltype;
}
public void Setisconnect(String isconnect){
	_isconnected=isconnect;
}
public void Setaddress(String address){
	_address=address;
}
public void Setdate(String date){
	_date=date;
}
}
