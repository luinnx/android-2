package com.ty.winchat.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestService extends AndroidTestCase {
	private final String Tag = "Test";
	
	public void testPersons() throws Exception{
		List<Person> persons = Service.getPersons(this.getClass().getClassLoader().getResourceAsStream("test.xml"));
		for(Person p : persons){
			Log.i(Tag, p.toString());
		}
	}
	
	public void testSave() throws Exception{
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("www",19,23));
		persons.add(new Person("hhh",19,3));
		persons.add(new Person("qqq",19,24)); 
		persons.add(new Person("ooo",19,25));
		File file = new File(this.getContext().getFilesDir(),"test2.xml");
		FileOutputStream out = new FileOutputStream(file);
		Service.save(persons, out);
	}
	
}