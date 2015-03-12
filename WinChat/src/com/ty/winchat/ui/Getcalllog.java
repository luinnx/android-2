package com.ty.winchat.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.location.GpsStatus.Listener;
import android.os.Environment;

public class Getcalllog {
public static List<CallLoginfo> GetLog( ) throws FileNotFoundException, XmlPullParserException{
	
	File xmlFlie = new File(Environment.getExternalStorageDirectory()+File.separator+"calllog.xml");  
	InputStream inputStream = new FileInputStream(xmlFlie); 
	
	 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser xmlParser = factory.newPullParser();
	    xmlParser.setInput(inputStream, null);
	    List<CallLoginfo> callsinfo=null;
	    CallLoginfo callinfo=null;
	try {
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                	callsinfo= new ArrayList<CallLoginfo>();
                    break;
                case XmlPullParser.START_TAG:
                    String tagName = xmlParser.getName();
                    if(tagName.equals("call")){
                    	callinfo=new CallLoginfo();
                    }
                    if(callinfo!=null){
                      if(tagName.equals("callip")){
                    	callinfo.Setcallip(xmlParser.nextText());
                    }
                    else if(tagName.equals("calledip")){
                    	callinfo.Setcalledip(xmlParser.nextText());
                    }
                    else if(tagName.equals("calltype")){
                    	callinfo.Setcalltype(xmlParser.nextText());
                    }
                    else if(tagName.equals("isconnected")){
                    	callinfo.Setisconnect(xmlParser.nextText());
                    }
                    else if(tagName.equals("date")){
                    	callinfo.Setdate(xmlParser.nextText());
                    }
                    else if(tagName.equals("address")){
                    	callinfo.Setaddress(xmlParser.nextText());
                    }
                    else if (tagName.equals("no")){
                    	callinfo.Setno(xmlParser.nextText());
                    }
                    }
                    break;
                case XmlPullParser.END_TAG:
                	String endtagName = xmlParser.getName();
                	if (endtagName.equals("call")){
                		callsinfo .add(callinfo);
                		callinfo=null;
                	}
                    break;
                case XmlPullParser.END_DOCUMENT:
                    break;
            }
            eventType = xmlParser.next();
        }
        if (callsinfo!=null){
        	return callsinfo;
        }
        else {
			return null;
		}
    } catch (XmlPullParserException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
    }catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
    }
}
}
