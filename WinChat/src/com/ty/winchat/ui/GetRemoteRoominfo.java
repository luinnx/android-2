package com.ty.winchat.ui;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetRemoteRoominfo {
	public String getRemoteipbyno(String roomno) {
		String roomip;
		// 命名空间
		String nameSpace = "http://10.162.198.111/WinchatService/";
		// 调用的方法名称
		String methodName = "GetRoomIPbyno";
		// EndPoint
		String endPoint = "http://10.162.198.111/WinchatService/webservice/GetRoominfo.asmx";
		// SOAP Action
		String soapAction = "http://10.162.198.111/GetRoomIPbyno";

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// 设置需调用WebService接口需要传入的参数
		rpc.addProperty("roomno", roomno);

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);

		envelope.bodyOut = rpc;
		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = true;
		// 等价于envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取返回的数据
		SoapObject object = (SoapObject) envelope.bodyIn;
		// 获取返回的结果
		roomip = object.getProperty(0).toString();

		// 将WebService返回的结果显示在TextView中
		 return roomip;
	}
	public String getRemotenobyip(String roomip) {
		String roomno;
		// 命名空间
		String nameSpace = "http://10.162.198.111/WinchatService/";
		// 调用的方法名称
		String methodName = "GetRoomnobyip";
		// EndPoint
		String endPoint = "http://10.162.198.111/WinchatService/webservice/GetRoominfo.asmx";
		// SOAP Action
		String soapAction = "http://10.162.198.111/GetRoomnobyip";

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// 设置需调用WebService接口需要传入的参数
		rpc.addProperty("roomno", roomip);

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);

		envelope.bodyOut = rpc;
		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = true;
		// 等价于envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取返回的数据
		SoapObject object = (SoapObject) envelope.bodyIn;
		// 获取返回的结果
		roomno = object.getProperty(0).toString();

		// 将WebService返回的结果显示在TextView中
		 return roomno;
	}

}
