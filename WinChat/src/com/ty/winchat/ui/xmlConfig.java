package com.ty.winchat.ui;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Xml;

public class xmlConfig {
	private int xmlSource;
	
	public xmlConfig() {
		// TODO Auto-generated constructor stub
		xmlSource=0;
	}
	
	public xmlConfig(int _xmlSource){
		// TODO Auto-generated constructor stub
		xmlSource=_xmlSource;
	}

	public  String readTagValue(Activity _activity, String _tag){
		if(xmlSource==0) return "";
		XmlResourceParser xmlParser = _activity.getResources().getXml(xmlSource);
		try {
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = xmlParser.getName();
                        if(tagName.equals("room")){
                            String roomNum=xmlParser.getAttributeValue( null , "num" );
                            if(roomNum.equals(_tag)){
                            	String roomIp=xmlParser.getAttributeValue( null , "ip" );
                            	return roomIp;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
		return "";
	}
	public  String readnofromip(Activity _activity, String _tag){
		if(xmlSource==0) return "";
		XmlResourceParser xmlParser = _activity.getResources().getXml(xmlSource);
		try {
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = xmlParser.getName();
                        if(tagName.equals("room")){
                        	String roomIp =xmlParser.getAttributeValue( null , "ip" );
                            if(roomIp.equals(_tag)){
                            	String roomNum=xmlParser.getAttributeValue( null , "num" );
                            	
                            	return roomNum;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
		return "";
	}
	
}

