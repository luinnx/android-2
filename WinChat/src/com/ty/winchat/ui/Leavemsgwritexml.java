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
		  
	        // �ĵ�����������  
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	  
	        // ʵ�����ĵ�������  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
	            if (!f.exists()) {  
	               // System.out.println("=======");  
	  
	                f.createNewFile();  
	  
	                // ����һ���ĵ�  
	                Document document = builder.newDocument();  
	  
	                // �������ڵ�  
	                Element Leavemsglog = document.createElement("Leavemsglog");  
	  
	                // ����XML�ļ�����ĸ��ֶ������л�(Ԫ��)  
	                Element msg = document.createElement("msg");// ����Ԫ�ؽڵ�  
	  
	                Element no = document.createElement("no");  
	                Element fromip = document.createElement("fromip"); 
	                Element fromno = document.createElement("fromno");
	                Element isread = document.createElement("isread");
	                Element time = document.createElement("time");
	                
	                
	  
	                Text no_text = document.createTextNode(msginfo.getmsgno());// ����text�ı�  
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
	  
	                document.appendChild(Leavemsglog);// ��ӵ��ĵ���  
	  
	                // ���÷��������ĵ�д��xml�ļ���  
	                return writeXml.write2Xml(document, f);  
	            } else {  
	  
	                // �����ĵ�  
	                Document document = builder.parse(f);  
	                Element Leavemsglog = document.getDocumentElement();// �õ����ڵ㣬�Ѻ��洴�����ӽڵ����������ڵ���  
	  
	                // ����XML�ļ�����ĸ��ֶ������л�(Ԫ��)  
	                Element msg = document.createElement("msg");// ����Ԫ�ؽڵ�  
	          	  
	                Element no = document.createElement("no");  
	                Element fromip = document.createElement("fromip"); 
	                Element fromno = document.createElement("fromno");
	                Element isread = document.createElement("isread");
	                Element time = document.createElement("time");
	                
	                
	  
	                Text no_text = document.createTextNode(msginfo.getmsgno());// ����text�ı�  
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
	  
	               
	  
	                // ���÷��������ĵ�д��xml�ļ���  
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
		  
	        // ʵ�����ĵ�������  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
	            if (!f.exists()) {  
	              return false;
	            } else {  
	  
	                // �����ĵ�  
	                Document document = builder.parse(f);  
	                Element Leavemsglog = document.getDocumentElement();// �õ����ڵ㣬�Ѻ��洴�����ӽڵ����������ڵ���  
	  
	                // ����XML�ļ�����ĸ��ֶ������л�(Ԫ��)  
	                Element msg =null;  
	                msg=(Element) selectSingleNode("/Leavemsglog/msg[no="+no+"]", Leavemsglog);
	                
	                msg.getElementsByTagName("isread").item(0).setTextContent("��");	               
	  
	                // ���÷��������ĵ�д��xml�ļ���  
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
		  
	        // ʵ�����ĵ�������  
	        try {  
	            DocumentBuilder builder = factory.newDocumentBuilder();  
	  
	            File f = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
	            if (!f.exists()) {  
	              return false;
	            } else {  
	  
	                // �����ĵ�  
	                Document document = builder.parse(f);  
	                Element Leavemsglog = document.getDocumentElement();// �õ����ڵ㣬�Ѻ��洴�����ӽڵ����������ڵ���  
	  
	                // ����XML�ļ�����ĸ��ֶ������л�(Ԫ��)  
	                Element msg =null;  
	                msg=(Element) selectSingleNode("/Leavemsglog/msg[no="+no+"]", Leavemsglog);
	                if(msg!=null){
	                msg.getParentNode().removeChild(msg);	     
	                }
	                else {
						return false;
					}
	  
	                // ���÷��������ĵ�д��xml�ļ���  
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
	  public static Node selectSingleNode(String express, Object source) {//���ҽڵ㣬�����ص�һ�����������ڵ�
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
	        // ����ת������  
	        TransformerFactory factory = TransformerFactory.newInstance();  
	        // ����ת��ʵ��  
	        try {  
	            Transformer transformer = factory.newTransformer();  
	  
	            // �������õ�DOM����DOMԴ��  
	            DOMSource domSource = new DOMSource(document);  
	  
	            // ���������  
	            StreamResult result = new StreamResult(file);  
	  
	            // ��ʼת��  
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

