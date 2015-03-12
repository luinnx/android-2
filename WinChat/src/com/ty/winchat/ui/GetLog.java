package com.ty.winchat.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.ty.winchat.R;
import com.ty.winchat.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GetLog extends ListActivity {
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seelog);
		 BindListview();
		
		
	}
	public void  BindListview() {
		List<CallLoginfo> logs;
		try {
			logs = Getcalllog.GetLog( );
			if(logs!=null){
				Log.d("logcount", logs.toString());
				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();
				for (int i=0;i<logs.size();i++){
					HashMap<String, String> map =new HashMap<String, String>();
					map.put("callip", logs.get(i).Getcallip());
					map.put("callsedip", logs.get(i).Getcalledip());
					map.put("calltype", logs.get(i).Getcalltype());
					map.put("isconnect", logs.get(i).Getisconnect());
					map.put("date", logs.get(i).Getdate());
					map.put("address", logs.get(i).Getaddress());
					map.put("no", logs.get(i).Getno());
					list.add(map);
				 
				}
				SimpleAdapter listAdapter =new SimpleAdapter(this, list, R.layout.list_log, new String[]{"callip" , "callsedip","calltype","isconnect","date","address","no"}, new int[]{R.id.tvcallip , R.id.tvcalledip,R.id.tvtype,R.id.tvisconnect,R.id.tvdate,R.id.tvaddress,R.id.tvno});
				//设置显示ListView
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
	final TextView tvno=(TextView) v.findViewById(R.id.tvno);
	new AlertDialog.Builder(this)
	.setTitle("请选择")
	.setIcon(android.R.drawable.ic_dialog_info)
	.setPositiveButton("查看",new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String audioname=tvno.getText().toString();
			Intent intent =new Intent(GetLog.this,Callaudioplay.class);
			intent.putExtra("filename", audioname);
			startActivity(intent);
		}
	})
	.setNegativeButton("删除",new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String filename=tvno.getText().toString();
			File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/winchat/videolog/"+filename+".pcm");
			if(file.exists()){
				file.delete();
			}
			writeXml.Deleteleavelogbyno(filename);
			BindListview();
			
		}
	})
	.show(); 
	
	
			
	 
	}



}
