package com.ty.winchat.ui;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetRemoteRoominfo {
	public String getRemoteipbyno(String roomno) {
		String roomip;
		// �����ռ�
		String nameSpace = "http://10.162.198.111/WinchatService/";
		// ���õķ�������
		String methodName = "GetRoomIPbyno";
		// EndPoint
		String endPoint = "http://10.162.198.111/WinchatService/webservice/GetRoominfo.asmx";
		// SOAP Action
		String soapAction = "http://10.162.198.111/GetRoomIPbyno";

		// ָ��WebService�������ռ�͵��õķ�����
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// ���������WebService�ӿ���Ҫ����Ĳ���
		rpc.addProperty("roomno", roomno);

		// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);

		envelope.bodyOut = rpc;
		// �����Ƿ���õ���dotNet������WebService
		envelope.dotNet = true;
		// �ȼ���envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// ����WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ��ȡ���ص�����
		SoapObject object = (SoapObject) envelope.bodyIn;
		// ��ȡ���صĽ��
		roomip = object.getProperty(0).toString();

		// ��WebService���صĽ����ʾ��TextView��
		 return roomip;
	}
	public String getRemotenobyip(String roomip) {
		String roomno;
		// �����ռ�
		String nameSpace = "http://10.162.198.111/WinchatService/";
		// ���õķ�������
		String methodName = "GetRoomnobyip";
		// EndPoint
		String endPoint = "http://10.162.198.111/WinchatService/webservice/GetRoominfo.asmx";
		// SOAP Action
		String soapAction = "http://10.162.198.111/GetRoomnobyip";

		// ָ��WebService�������ռ�͵��õķ�����
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// ���������WebService�ӿ���Ҫ����Ĳ���
		rpc.addProperty("roomno", roomip);

		// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);

		envelope.bodyOut = rpc;
		// �����Ƿ���õ���dotNet������WebService
		envelope.dotNet = true;
		// �ȼ���envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// ����WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ��ȡ���ص�����
		SoapObject object = (SoapObject) envelope.bodyIn;
		// ��ȡ���صĽ��
		roomno = object.getProperty(0).toString();

		// ��WebService���صĽ����ʾ��TextView��
		 return roomno;
	}

}
