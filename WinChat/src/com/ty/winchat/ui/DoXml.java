package com.ty.winchat.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.view.View;
import com.ty.winchat.R;

import android.util.Xml;

public class DoXml extends Base {

	public String GetIPByRoomNo(Context context, String roomNo) throws Exception
			 {
		String roomIpString="";
		InputStream inStream=context.getResources().openRawResource(R.xml.test);
		 Service.getPersons(inStream);
		
		return roomIpString;
	
		
	}
//		List<RoomInfo> list = null;
//		RoomInfo room = null;
//
//		String ipString = "";
//		InputStream inStream=context.getResources().openRawResource(R.xml.roominfo);
//		XmlPullParser parser =XmlPullParserFactory. newInstance().newPullParser();    
//		parser.setInput(inStream, "UTF-8");
//		
//		
//		int eventType = parser.getEventType();
//		
// 		while (eventType != XmlPullParser.END_DOCUMENT) {
//			// 判断文件是否是文件的结尾，END_DOCUMENT文件结尾常量
//			switch (eventType) {
//			case XmlPullParser.START_DOCUMENT:
//				list = new ArrayList<RoomInfo>();
//				break;
//
//			case XmlPullParser.START_TAG:
//				// 元素标签开始，START_TAG标签开始常量
//
//				String name = parser.getName();
//
//				if ("room".equals(name)) {
//					room = new RoomInfo();
//				}
//				if (room != null) {
//					if("no".equals(name)){
//						room.setno(parser.nextText());
//					}
//					if ("ip".equals(name)) {
//						room.setip(parser.nextText());
//					}
//				}
//				break;
//			case XmlPullParser.END_TAG:
//				if("room".equals(parser.getName()))
//				{
//					list.add(room);
//					room=null;
//				}
//				break;
//
//			}
//			// 获取当前元素标签的类型
//			eventType = parser.next();
//		}
//		inStream.close();
//		if (room.getip() != "") {
//			ipString = room.getip();
//		}
//		return ipString;
//	}
}