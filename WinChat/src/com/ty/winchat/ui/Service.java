package com.ty.winchat.ui;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class Service {
	/**
	 * 获取XML文件中的数据
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static List<Person> getPersons(InputStream xml) throws Exception {
		List<Person> persons = null;
		XmlPullParser parser = XmlPullParserFactory.newInstance()
				.newPullParser();
		// parser = Xml.newPullParser();
		parser.setInput(xml, "UTF-8");
		int event = parser.getEventType();
		Person p = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				persons = new ArrayList<Person>();
				break;
			case XmlPullParser.START_TAG:
				if("person".equals(parser.getName())){
					p = new Person();
					int id = Integer.parseInt(parser.getAttributeValue(0));
					p.setId(id);
				}
				if("name".equals(parser.getName())){
					String name = parser.nextText();
					p.setName(name);
				}
				if("age".equals(parser.getName())){
					int age = Integer.parseInt(parser.nextText());
					p.setAge(age);
				}
				break;
			case XmlPullParser.END_TAG:
				if("person".equals(parser.getName())){
					persons.add(p);
					p = null;
				}
				break;
			}
			event = parser.next();
		}
		return persons;
	}
	
	/**
	 * 保存数据到XML文件中
	 * @param persons
	 * @param out
	 * @throws Exception
	 */
	public static void save(List<Person> persons , OutputStream out) throws Exception{
		XmlSerializer serializer  =  Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "persons");
		for(Person p: persons){
			serializer.startTag(null, "person");
			serializer.attribute(null, "person", p.getId()+"");
			
			serializer.startTag(null, "name");
			serializer.text(p.getName());
			serializer.endTag(null, "name");
			
			serializer.startTag(null, "age");
			serializer.text(p.getAge()+"");
			serializer.endTag(null, "age");
			
			serializer.endTag(null, "person");
		}
		serializer.endTag(null, "persons");
		serializer.endDocument();
		out.flush();
		out.close();
	}
}