package com.example.cellphoneattendance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.integer;
import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.Log;

public class xmlConfig {
	
	public static String xmlConfigPath = Environment.getExternalStorageDirectory() + "/cellphoneAttendance/";
	// 读取指定的XML檔的节点值
	public static String readTagValue(String xmlPathString, String nodePath, String TagName) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// 实例化文档生成器
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(xmlConfigPath + xmlPathString);
			if (!f.exists()) {
				return "";
			} else {
				// 解析文档
				Document document = builder.parse(f);
				Element xmlElement = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中

				// 创建XML文件所需的各种对象并序列化(元素)
				Element xmlTagElement = null;
				xmlTagElement = (Element) selectSingleNode(nodePath, xmlElement);

				return xmlTagElement.getElementsByTagName(TagName).item(0)
						.getTextContent();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();

			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// 修改指点XML节点的值
	public static Boolean modifyTagValue(String xmlPathString, String nodePath, String TagName, String modifyValue) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// 实例化文档生成器
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			File f = new File(xmlConfigPath + xmlPathString);
			if (!f.exists()) {
				return false;
			} else {

				// 解析文档
				Document document = builder.parse(f);
				Element xmlElement = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中

				// 创建XML文件所需的各种对象并序列化(元素)
				Element xmlTagElement = null;
				xmlTagElement = (Element) selectSingleNode(nodePath, xmlElement);

				xmlTagElement.getElementsByTagName(TagName).item(0).setTextContent(modifyValue);

				// 调用方法，将文档写入xml文件中
				return xmlConfig.writeBackXml(document, f);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static Boolean deleteXmlTag(String xmlPathString, String nodePath) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			File f = new File(xmlConfigPath + xmlPathString);
			if (!f.exists()) {
				return false;
			} else {

				Document document = builder.parse(f);
				Element xmlElement = document.getDocumentElement();
				Element xmlTagElement = null;
				xmlTagElement = (Element) selectSingleNode(nodePath, xmlElement);
				if (xmlTagElement != null) {
					xmlTagElement.getParentNode().removeChild(xmlTagElement);
				} else {
					return false;
				}
				return xmlConfig.writeBackXml(document, f);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();

			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean bindPersonInfo(String xmlPath, String cellphoneString, String nameString, String workIdString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			File destDir = new File(xmlConfigPath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			File f = new File(xmlConfigPath + xmlPath);
			if (!f.exists()) {
				f.createNewFile();
				Document document = builder.newDocument();
				Element personinfo = document.createElement("personinfo");
				Element cellphone = document.createElement("cellphone");
				Element name = document.createElement("name");
				Element workID = document.createElement("workID");

				Text cellphone_text = document.createTextNode(cellphoneString);
				Text name_text = document.createTextNode(nameString);
				Text workID_text = document.createTextNode(workIdString);
				cellphone.appendChild(cellphone_text);
				name.appendChild(name_text);
				workID.appendChild(workID_text);
				personinfo.appendChild(cellphone);
				personinfo.appendChild(name);
				personinfo.appendChild(workID);

				document.appendChild(personinfo);
				return xmlConfig.writeBackXml(document, f);
			} else {
				if(!xmlConfig.modifyTagValue(xmlPath, "/personinfo", "cellphone", cellphoneString)) return false;
				if(!xmlConfig.modifyTagValue(xmlPath, "/personinfo", "name", nameString)) return false;
				if(!xmlConfig.modifyTagValue(xmlPath, "/personinfo", "workID", workIdString)) return false;
				return true;
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static ArrayList<HashMap<String, Object>> readContactXml(String xmlPath){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		// 实例化文档生成器
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(xmlConfigPath + xmlPath);
			if (f.exists()) {
				// 解析文档
				Document document = builder.parse(f);
				Element xmlElement = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中
	
				// 创建XML文件所需的各种对象并序列化(元素)
				/*
				Element xmlTagElement = null;
				xmlTagElement = (Element) selectSingleNode("/contacts/contact", xmlElement);
				NodeList companyList = xmlTagElement.getElementsByTagName("company");
				NodeList contactmanList = xmlTagElement.getElementsByTagName("contactman");
				NodeList phoneList = xmlTagElement.getElementsByTagName("phone");
				NodeList addressList = xmlTagElement.getElementsByTagName("address");
				for(int i=0; i<companyList.getLength(); i++){
					HashMap<String, Object> map = new HashMap<String, Object>();
					String companyname = "";
					if(companyList.getLength() > i) companyname = companyList.item(i).getTextContent();
					String contactman = "";
					if(contactmanList.getLength() > i) contactman = contactmanList.item(i).getTextContent();
					String phone = "";
					if(phoneList.getLength() > i) phone = phoneList.item(i).getTextContent();
					String address = "";
					if(addressList.getLength() > i) address = addressList.item(i).getTextContent();
					map.put("companyView", companyname);
	            	map.put("contactView", contactman);
	            	map.put("phoneView", phone);
	            	map.put("addressView", address);
	            	listItem.add(map);
				}
				*/
				NodeList companyList = selectNodeList("/contacts/contact/company", xmlElement);
				NodeList contactmanList = selectNodeList("/contacts/contact/contactman", xmlElement);
				NodeList phoneList = selectNodeList("/contacts/contact/phone", xmlElement);
				NodeList addressList = selectNodeList("/contacts/contact/address", xmlElement);
				for(int i=0; i<companyList.getLength(); i++){
					HashMap<String, Object> map = new HashMap<String, Object>();
					String companyname = "";
					if(companyList.getLength() > i) companyname = companyList.item(i).getTextContent();
					String contactman = "";
					if(contactmanList.getLength() > i) contactman = contactmanList.item(i).getTextContent();
					String phone = "";
					if(phoneList.getLength() > i) phone = phoneList.item(i).getTextContent();
					String address = "";
					if(addressList.getLength() > i) address = addressList.item(i).getTextContent();
					map.put("companyView", companyname);
	            	map.put("contactView", contactman);
	            	map.put("phoneView", phone);
	            	map.put("addressView", address);
	            	listItem.add(map);
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listItem;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listItem;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listItem;
		}
		return listItem;
	}
	
	public static boolean updateContactXml(String xmlPath, ArrayList<HashMap<String, Object>> listItem){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File destDir = new File(xmlConfigPath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			File contactXml = new File(xmlConfigPath + xmlPath);
			if(contactXml.exists()) contactXml.delete();
			contactXml.createNewFile();
			Document document = builder.newDocument();
			Element contacts = document.createElement("contacts");
			for(int i = 0; i < listItem.size();i++){
				Element contact = document.createElement("contact");
				Element company = document.createElement("company");
				Element contactman = document.createElement("contactman");
				Element phone = document.createElement("phone");
				Element address = document.createElement("address");
				
				HashMap<String, Object> map = listItem.get(i);
				Text company_text = document.createTextNode(map.get("companyView").toString());
				Text contactman_text = document.createTextNode(map.get("contactView").toString());
				Text phone_text = document.createTextNode(map.get("phoneView").toString());
				Text address_text = document.createTextNode(map.get("addressView").toString());
				company.appendChild(company_text);
				contactman.appendChild(contactman_text);
				phone.appendChild(phone_text);
				address.appendChild(address_text);
				contact.appendChild(company);
				contact.appendChild(contactman);
				contact.appendChild(phone);
				contact.appendChild(address);
				contacts.appendChild(contact);
			}
			document.appendChild(contacts);
			return xmlConfig.writeBackXml(document, contactXml);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean writeBackXml(Document document, File file) {

		TransformerFactory factory = TransformerFactory.newInstance();

		try {
			Transformer transformer = factory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(domSource, result);

			return true;

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();

			return false;
		} catch (TransformerException e) {
			e.printStackTrace();
			Log.e("transxml", e.getMessage());
			return false;
		}
	}

	// 选取指定条件的节点
	public static Node selectSingleNode(String express, Object source) {
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 选取指定条件的所有节点
	public static NodeList selectNodeList(String express, Object source) {
		NodeList result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			 result = (NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//读取Webservice的信息
	public static String readWSInfo(Activity _activity, String _nodeName, String _tag){
		
		XmlResourceParser xmlParser = _activity.getResources().getXml(R.xml.wsinfo);
		try {
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = xmlParser.getName();
                        if(tagName.equals("WebServer")){
                            String searchAttribute=xmlParser.getAttributeValue( null , "name" );
                            if(searchAttribute.equals(_nodeName)){
                            	String tagAttribute=xmlParser.getAttributeValue( null , _tag);
                            	return tagAttribute;
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

