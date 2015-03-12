package com.ty.winchat.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ty.winchat.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GetLeavemsglog extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seeleavemsglog);
		BindListview();
		
		
	}
	public void BindListview(){
		List<Leavemsginfo> logs;
		try {
			logs = GetLog( );
			if(logs!=null){
				Log.d("logcount", logs.toString());
				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();
				for (int i=0;i<logs.size();i++){
					HashMap<String, String> map =new HashMap<String, String>();
					map.put("no", logs.get(i).getmsgno());
					map.put("fromip", logs.get(i).getmsgfromip());
					map.put("fromno", logs.get(i).getmsgfromno());
					map.put("isread", logs.get(i).getisread());
					map.put("time", logs.get(i).gettime());
					 
					list.add(map);
				
				}
				SimpleAdapter listAdapter =new SimpleAdapter(this, list, R.layout.list_leavemsglog, new String[]{"no" , "fromip","fromno","isread","time"}, new int[]{R.id.tvmsgno , R.id.tvfromip,R.id.tvfromno,R.id.tvisread,R.id.tvtime});
				//…Ë÷√œ‘ æListView
				setListAdapter(listAdapter);
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	// TODO Auto-generated method stub
	super.onListItemClick(l, v, position, id);
	final TextView tvmsgno=(TextView) v.findViewById(R.id.tvmsgno);
	final TextView tvisread=(TextView) v.findViewById(R.id.tvisread);
	new AlertDialog.Builder(this)
	.setTitle("«Î—°‘Ò")
	.setIcon(android.R.drawable.ic_dialog_info)
	.setPositiveButton("≤Èø¥",new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String noString=tvmsgno.getText().toString();
			if(tvisread.getText().equals("∑Ò")){
			Leavemsgwritexml.UpdateIsreadlog(noString);
			BindListview();
			}
			String filename=noString+".3gp";
			Intent intent =new Intent(GetLeavemsglog.this,Seeleavemsg.class);
			intent.putExtra("filename", filename);
			startActivity(intent);
		}
	})
	.setNegativeButton("…æ≥˝",new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String filename=tvmsgno.getText().toString();
			File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/leavemsg/"+filename+".3gp");
			if(file.exists()){
				file.delete();
			}
			Leavemsgwritexml.Deleteleavelogbyno(filename);
			BindListview();
			
		}
	})
	.show();
	
	
			
	 
	}
	
	public static List<Leavemsginfo> GetLog( ) throws FileNotFoundException, XmlPullParserException{
		
		File xmlFlie = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
		InputStream inputStream = new FileInputStream(xmlFlie); 
		
		 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    factory.setNamespaceAware(true);
		    XmlPullParser xmlParser = factory.newPullParser();
		    xmlParser.setInput(inputStream, null);
		    List<Leavemsginfo> leavemsgs=null;
		    Leavemsginfo msginfo=null;
		try {
	        int eventType = xmlParser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	            switch (eventType) {
	                case XmlPullParser.START_DOCUMENT:
	                	leavemsgs= new ArrayList<Leavemsginfo>();
	                    break;
	                case XmlPullParser.START_TAG:
	                    String tagName = xmlParser.getName();
	                    if(tagName.equals("msg")){
	                    	msginfo=new Leavemsginfo();
	                    }
	                    if(msginfo!=null){
	                      if(tagName.equals("no")){
	                    	  msginfo.setmsgno(xmlParser.nextText());
	                    }
	                    else if(tagName.equals("fromip")){
	                    	msginfo.setmsgfromip(xmlParser.nextText());
	                    }
	                    else if(tagName.equals("fromno")){
	                    	msginfo.setmsgfromno(xmlParser.nextText());
	                    }
	                    else if(tagName.equals("isread")){
	                    	msginfo.setisread(xmlParser.nextText());
	                    }
	                    else if(tagName.equals("time")){
	                    	msginfo.settime(xmlParser.nextText());
	                    }
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                	String endtagName = xmlParser.getName();
	                	if (endtagName.equals("msg")){
	                		leavemsgs .add(msginfo);
	                		msginfo=null;
	                	}
	                    break;
	                case XmlPullParser.END_DOCUMENT:
	                    break;
	            }
	            eventType = xmlParser.next();
	        }
	        if (leavemsgs!=null){
	        	return leavemsgs;
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
public static int Getnoreadcount( ) throws FileNotFoundException, XmlPullParserException{
		
		File xmlFlie = new File(Environment.getExternalStorageDirectory()+File.separator+"leavemsglog.xml");  
		InputStream inputStream = new FileInputStream(xmlFlie); 
		int noreadcount=0;
		 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    factory.setNamespaceAware(true);
		    XmlPullParser xmlParser = factory.newPullParser();
		    xmlParser.setInput(inputStream, null);
		    
		try {
	        int eventType = xmlParser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	            switch (eventType) {
	                case XmlPullParser.START_DOCUMENT:
	                	 
	                    break;
	                case XmlPullParser.START_TAG:
	                    String tagName = xmlParser.getName();
	                     if(tagName.equals("isread")){
	                    	if(xmlParser.nextText().equals("∑Ò")){
	                    		noreadcount++;
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
	        return noreadcount;
	    } catch (XmlPullParserException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return 0;
	    }catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return 0;
	    }
	}

}
