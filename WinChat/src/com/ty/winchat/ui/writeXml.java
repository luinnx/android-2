package com.ty.winchat.ui;

import java.io.File;  
import java.io.IOException;  
  
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
import org.w3c.dom.Text;  
import org.xml.sax.SAXException; 

import android.os.Environment;
import android.util.Log;





public class writeXml {
	
	
	 public static boolean saveParam2Xml(CallLoginfo loginfo ) {  
		  
	        // 文档生成器工厂  
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	  
	        // 实例化文档生成器  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"calllog.xml");  
	            if (!f.exists()) {  
	               // System.out.println("=======");  
	  
	                f.createNewFile();  
	  
	                // 生成一个文档  
	                Document document = builder.newDocument();  
	  
	                // 创建根节点  
	                Element Calllog = document.createElement("Calllog");  
	  
	                // 创建XML文件所需的各种对象并序列化(元素)  
	                Element call = document.createElement("call");// 创建元素节点  
	  
	                Element callip = document.createElement("callip");  
	                Element calledip = document.createElement("calledip"); 
	                Element calltype = document.createElement("calltype");
	                Element isconnected = document.createElement("isconnected");
	                Element date = document.createElement("date");
	                Element address = document.createElement("address");
	                Element no=document.createElement("no");
	                
	  
	                Text callip_text = document.createTextNode(loginfo.Getcallip());// 创建text文本  
	                Text calledip_text = document.createTextNode(loginfo.Getcalledip()); 
	                Text calltype_text = document.createTextNode(loginfo.Getcalltype());
	                Text isconnected_text = document.createTextNode(loginfo.Getisconnect());
	                Text date_text = document.createTextNode(loginfo.Getdate()); 
	                Text address_text = document.createTextNode(loginfo.Getaddress());
	                Text no_text = document.createTextNode(loginfo.Getno());
	                
	  
	                callip.appendChild(callip_text);  
	                calledip.appendChild(calledip_text); 
	                date.appendChild(date_text);
	                address.appendChild(address_text);
	                calltype.appendChild(calltype_text);
	                isconnected.appendChild(isconnected_text);
	                no.appendChild(no_text);
	  
	                call.appendChild(callip);  
	                call.appendChild(calledip);  
	                call.appendChild(calltype);
	                call.appendChild(isconnected);
	                call.appendChild(date);
	                call.appendChild(address);
	                call.appendChild(no);
	  
	                Calllog.appendChild(call);  
	  
	                document.appendChild(Calllog);// 添加到文档中  
	  
	                // 调用方法，将文档写入xml文件中  
	                return writeXml.write2Xml(document, f);  
	            } else {  
	  
	                // 解析文档  
	                Document document = builder.parse(f);  
	                Element Calllog = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中  
	  
	                // 创建XML文件所需的各种对象并序列化(元素)  
	                Element call = document.createElement("call");// 创建元素节点  
	          	  
	                Element callip = document.createElement("callip");  
	                Element calledip = document.createElement("calledip"); 
	                Element calltype = document.createElement("calltype");
	                Element isconnected = document.createElement("isconnected");
	                Element date = document.createElement("date");
	                Element address = document.createElement("address");
	                Element no=document.createElement("no");
	                
	  
	                Text callip_text = document.createTextNode(loginfo.Getcallip());// 创建text文本  
	                Text calledip_text = document.createTextNode(loginfo.Getcalledip()); 
	                Text calltype_text = document.createTextNode(loginfo.Getcalltype());
	                Text isconnected_text = document.createTextNode(loginfo.Getisconnect());
	                Text date_text = document.createTextNode(loginfo.Getdate()); 
	                Text address_text = document.createTextNode(loginfo.Getaddress());
	                Text no_text = document.createTextNode(loginfo.Getno());
	  
	                callip.appendChild(callip_text);  
	                calledip.appendChild(calledip_text); 
	                date.appendChild(date_text);
	                address.appendChild(address_text);
	                calltype.appendChild(calltype_text);
	                isconnected.appendChild(isconnected_text);
	                no.appendChild(no_text);
	  
	                call.appendChild(callip);  
	                call.appendChild(calledip);  
	                call.appendChild(calltype);
	                call.appendChild(isconnected);
	                call.appendChild(date);
	                call.appendChild(address);
	                call.appendChild(no);
	  
	                Calllog.appendChild(call);// 添加到根节点中  
	  
	                // 调用方法，将文档写入xml文件中  
	                return writeXml.write2Xml(document, f);  
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
	  
	    public static boolean write2Xml(Document document, File file) {  
	        // 创建转化工厂  
	        TransformerFactory factory = TransformerFactory.newInstance();  
	        // 创建转换实例  
	        try {  
	            Transformer transformer = factory.newTransformer();  
	  
	            // 将建立好的DOM放入DOM源中  
	            DOMSource domSource = new DOMSource(document);  
	  
	            // 创建输出流  
	            StreamResult result = new StreamResult(file);  
	  
	            // 开始转换  
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
	    public static Boolean Deleteleavelogbyno(String no){
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			  
		        // 实例化文档生成器  
		        try {  
		            DocumentBuilder builder = factory.newDocumentBuilder();  
		  
		            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"calllog.xml");   
		            if (!f.exists()) {  
		              return false;
		            } else {  
		  
		                // 解析文档  
		                Document document = builder.parse(f);  
		                Element Calllog = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中  
		  
		                // 创建XML文件所需的各种对象并序列化(元素)  
		                Element msg =null;  
		                msg=(Element) selectSingleNode("/Calllog/call[no='"+no+"']", Calllog);
		                if(msg!=null){
		                	 msg.getParentNode().removeChild(msg);	
		                }
		                else {
							return false;
						}
		                              
		  
		                // 调用方法，将文档写入xml文件中  
		                return writeXml.write2Xml(document, f);  
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
		  public static Node selectSingleNode(String express, Object source) {//查找节点，并返回第一个符合条件节点
		        Node result=null;
		        XPathFactory xpathFactory=XPathFactory.newInstance();
		        XPath xpath=xpathFactory.newXPath();
		        try {
		            result=(Node) xpath.evaluate(express, source, XPathConstants.NODE);
		        } catch (XPathExpressionException e) {
		            e.printStackTrace();
		        }
		        
		        return result;
		    }
	  

}
