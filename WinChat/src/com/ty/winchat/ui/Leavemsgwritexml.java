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
import javax.xml.xpath.*;





public class Leavemsgwritexml {
	
	
	 public static boolean saveParam2Xml(Leavemsginfo msginfo ) {  
		  
	        // 文档生成器工厂  
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	  
	        // 实例化文档生成器  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
	            if (!f.exists()) {  
	               // System.out.println("=======");  
	  
	                f.createNewFile();  
	  
	                // 生成一个文档  
	                Document document = builder.newDocument();  
	  
	                // 创建根节点  
	                Element Leavemsglog = document.createElement("Leavemsglog");  
	  
	                // 创建XML文件所需的各种对象并序列化(元素)  
	                Element msg = document.createElement("msg");// 创建元素节点  
	  
	                Element no = document.createElement("no");  
	                Element fromip = document.createElement("fromip"); 
	                Element fromno = document.createElement("fromno");
	                Element isread = document.createElement("isread");
	                Element time = document.createElement("time");
	                
	                
	  
	                Text no_text = document.createTextNode(msginfo.getmsgno());// 创建text文本  
	                Text fromip_text = document.createTextNode(msginfo.getmsgfromip()); 
	                Text fromno_text = document.createTextNode(msginfo.getmsgfromno());
	                Text isread_text = document.createTextNode(msginfo.getisread());
	                Text time_text = document.createTextNode(msginfo.gettime()); 
	              
	                
	  
	                no.appendChild(no_text);  
	                fromip.appendChild(fromip_text); 
	                fromno.appendChild(fromno_text);
	                isread.appendChild(isread_text);
	                time.appendChild(time_text);
	               
	  
	                msg.appendChild(no);  
	                msg.appendChild(fromip);  
	                msg.appendChild(fromno);
	                msg.appendChild(isread);
	                msg.appendChild(time);
	              
	                Leavemsglog.appendChild(msg);  
	  
	                document.appendChild(Leavemsglog);// 添加到文档中  
	  
	                // 调用方法，将文档写入xml文件中  
	                return writeXml.write2Xml(document, f);  
	            } else {  
	  
	                // 解析文档  
	                Document document = builder.parse(f);  
	                Element Leavemsglog = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中  
	  
	                // 创建XML文件所需的各种对象并序列化(元素)  
	                Element msg = document.createElement("msg");// 创建元素节点  
	          	  
	                Element no = document.createElement("no");  
	                Element fromip = document.createElement("fromip"); 
	                Element fromno = document.createElement("fromno");
	                Element isread = document.createElement("isread");
	                Element time = document.createElement("time");
	                
	                
	  
	                Text no_text = document.createTextNode(msginfo.getmsgno());// 创建text文本  
	                Text fromip_text = document.createTextNode(msginfo.getmsgfromip()); 
	                Text fromno_text = document.createTextNode(msginfo.getmsgfromno());
	                Text isread_text = document.createTextNode(msginfo.getisread());
	                Text time_text = document.createTextNode(msginfo.gettime()); 
	              
	                
	  
	                no.appendChild(no_text);  
	                fromip.appendChild(fromip_text); 
	                fromno.appendChild(fromno_text);
	                isread.appendChild(isread_text);
	                time.appendChild(time_text);
	               
	  
	                msg.appendChild(no);  
	                msg.appendChild(fromip);  
	                msg.appendChild(fromno);
	                msg.appendChild(isread);
	                msg.appendChild(time);
	              
	                Leavemsglog.appendChild(msg);  
	  
	               
	  
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
	 public static Boolean UpdateIsreadlog(String no){
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		  
	        // 实例化文档生成器  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
	            if (!f.exists()) {  
	              return false;
	            } else {  
	  
	                // 解析文档  
	                Document document = builder.parse(f);  
	                Element Leavemsglog = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中  
	  
	                // 创建XML文件所需的各种对象并序列化(元素)  
	                Element msg =null;  
	                msg=(Element) selectSingleNode("/Leavemsglog/msg[no="+no+"]", Leavemsglog);
	                
	                msg.getElementsByTagName("isread").item(0).setTextContent("是");	               
	  
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
	 public static Boolean Deleteleavelogbyno(String no){
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		  
	        // 实例化文档生成器  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
	            if (!f.exists()) {  
	              return false;
	            } else {  
	  
	                // 解析文档  
	                Document document = builder.parse(f);  
	                Element Leavemsglog = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中  
	  
	                // 创建XML文件所需的各种对象并序列化(元素)  
	                Element msg =null;  
	                msg=(Element) selectSingleNode("/Leavemsglog/msg[no="+no+"]", Leavemsglog);
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
	  

}

