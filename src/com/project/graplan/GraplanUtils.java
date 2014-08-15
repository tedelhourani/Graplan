/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * utility methods for reading and writing to xml files
 * 
 */
package com.project.graplan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

public class GraplanUtils {
	private Context context;
	
	public GraplanUtils(Context c){
		context = c;
	}
	
	public Hashtable<String, String> xmlUnsolvedChallengeList(String xmlFile){
		Hashtable<String, String>  challengeList = new Hashtable<String, String>();

		XmlPullParser xpp;
		xpp = getXmlPullParser( xmlFile );
		
		if( xpp == null ){
			Log.d("Graplan","xpp is null therefore loading from raw file");
			// copy raw state file to memory
			for( int i = 0 ; i < Constants.RESOURCE_STRING.length ; i++ )
			{
				Log.d("Graplan", "copying file "+Constants.RESOURCE_STRING[i]+" to memory");
				rawToMemory( i );
			}
		
			// reread file 
			xpp = getXmlPullParser( xmlFile );
			
			
		}

		try {
			//Log.d("XML parser depth count : ", ""+xpp.getDepth());

			while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
				//Log.d("XMLparser enters while loop : ", ""+xpp.getEventType());

				if (xpp.getEventType()==XmlPullParser.START_TAG) {
					//Log.d("XML parser is : ", "jvhswifhrew iuh  rhjghre======");

					if(xpp.getName().equals("Challenge")) {
						String difficulty = xpp.getAttributeValue(0);
						String fileName   = xpp.getAttributeValue(1);
						String solved     = xpp.getAttributeValue(2);

						if( solved.equals("false"))
							challengeList.put(fileName,difficulty);
					}
				}
				xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Log.d("challengeList hashtable size : ", challengeList.size()+"");
		
		if( challengeList.size() == 0 ){
			// copy raw state file to memory
			for( int i = 0 ; i < Constants.RESOURCE_STRING.length ; i++ )
				rawToMemory( i );
			// reread file 
			xpp = getXmlPullParser( xmlFile );
			//	Log.d("XML parser is : ", xpp.toString());
			return xmlUnsolvedChallengeList( xmlFile );
		}

		return challengeList;
	}

	public Hashtable<String, String> xmlSolvedChallengeList(String xmlFile){
		Hashtable<String, String>  challengeList = new Hashtable<String, String>();

		XmlPullParser xpp;
		xpp = getXmlPullParser( xmlFile );

		try {
		//	Log.d("XML parser depth count : ", ""+xpp.getDepth());

			while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
			//	Log.d("XMLparser enters while loop : ", ""+xpp.getEventType());

				if (xpp.getEventType()==XmlPullParser.START_TAG) {
				//	Log.d("XML parser is : ", "jvhswifhrew iuh  rhjghre======");

					if(xpp.getName().equals("Challenge")) {
						String difficulty = xpp.getAttributeValue(0);
						String fileName   = xpp.getAttributeValue(1);
						String solved     = xpp.getAttributeValue(2);

						if( solved.equals("true"))
							challengeList.put(fileName,difficulty);
					}
				}
				xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("challengeList hashtable size : ", challengeList.size()+"");

		return challengeList;
	}
	
	public LinkedList<int[]> xmlChallengeToEdgeArrays(String xmlFile){
		LinkedList<int[]> graph = new LinkedList<int[]>();
		Vector<String> tails = new Vector<String>();
		Vector<String> heads = new Vector<String>();

		XmlPullParser xpp = getXmlPullParser( xmlFile );

		try {
			while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
				if (xpp.getEventType()==XmlPullParser.START_TAG) {
					
					if(xpp.getName().equals("Edge")) {
						String firstVertex = xpp.getAttributeValue(0);
						String secondVertex = xpp.getAttributeValue(1);
						tails.add( firstVertex  );
						heads.add( secondVertex );
					}

				}
				xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		int[] headsArray = new int[heads.size()];
		int[] tailsArray = new int[tails.size()];

		// -1 because the boost library is zero indexed
		for(int i = 0 ; i < heads.size(); i++){
			headsArray[i] = new Integer((String)heads.get(i)).intValue() - 1;
			tailsArray[i] = new Integer((String)tails.get(i)).intValue() - 1;
		}

		graph.add(tailsArray);

		graph.add(headsArray);

		return graph;
	}
	
	/** writes state file with passed challenge tagged as solved **/
	public void stateToXml(String xmlFile, String solvedChallenge ){
		Hashtable<String,String> solved = xmlSolvedChallengeList( xmlFile );
		Hashtable<String,String> unsolved = xmlUnsolvedChallengeList( xmlFile );
		
		Enumeration<String> solvedEnumerator = solved.keys();
		Enumeration<String> unsolvedEnumerator = unsolved.keys();
		
		// rewrite the status file
		File newxmlfile = new File(xmlFile);

		try{
			newxmlfile.delete();
			newxmlfile.createNewFile();
		}catch(IOException e){
			Log.e("IOException", "exception in createNewFile() method");
		}

		// bind file with a FileOutputStream instance
		FileOutputStream fileos = null;        
		try{
			fileos = new FileOutputStream(newxmlfile);
		}catch(FileNotFoundException e){
			Log.e("FileNotFoundException", "can't create FileOutputStream");
		}
		//we create a XmlSerializer in order to write xml data
		XmlSerializer serializer = Xml.newSerializer();
		
		Log.d("Graplan", "Solved Challenge is "+solvedChallenge);
		Log.d("Graplan", "Number unsolved "+unsolved.size());
		try {	
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			
			//start a tag called "Challenges"
			serializer.startTag(null, "Challenges");
            serializer.attribute(null, "color", "#483d8b");

            
            while( solvedEnumerator.hasMoreElements() )
			{
				String key = solvedEnumerator.nextElement();
				String level =  solved.get(key);
				serializer.startTag(null, "Challenge");
				serializer.attribute(null, "difficulty", level);
				serializer.attribute(null, "fileName", key);
				serializer.attribute(null, "solved", "true");
				serializer.endTag(null, "Challenge");
			}
			
            while( unsolvedEnumerator.hasMoreElements() )
			{
				String key = unsolvedEnumerator.nextElement();
				String level =  unsolved.get(key);
				serializer.startTag(null, "Challenge");
				serializer.attribute(null, "difficulty", level);
				serializer.attribute(null, "fileName", key);
				Log.d("Graplan","Still Unsolved "+key+"");
				if( key.equals(solvedChallenge) ){
				serializer.attribute(null, "solved", "true");
				Log.d("Graplan","Now "+key+" is solved!");
				}
				else
					serializer.attribute(null, "solved", "false");
				serializer.endTag(null, "Challenge");
			}
		
            serializer.endTag(null, "Challenges");
			serializer.endDocument();
			serializer.flush();
			fileos.close();
		}
		catch (Exception e) {
			Log.e("Exception","error occurred while creating xml file");
		}
		
	}

	/** writes challenge to file xmlFile **/
	public void bundleToXmlChallenge(String xmlFile, Bundle challenge){
		Bundle vertices = challenge.getBundle("vertices");
		Bundle edges    = challenge.getBundle("edges");
		int level = challenge.getInt("level");
		int currentSteps = challenge.getInt("currentSteps");
		//int recordSteps = challenge.getInt("recordSteps");
			
			
		Iterator<?> vs = vertices.keySet().iterator();
		Iterator<?> es = edges.keySet().iterator();

		//create file
		File newxmlfile = new File(xmlFile);

		try{
			newxmlfile.delete();
			newxmlfile.createNewFile();
		}catch(IOException e){
			Log.e("IOException", "exception in createNewFile() method");
		}

		// bind file with a FileOutputStream instance
		FileOutputStream fileos = null;        
		try{
			fileos = new FileOutputStream(newxmlfile);
		}catch(FileNotFoundException e){
			Log.e("FileNotFoundException", "can't create FileOutputStream");
		}
		//we create a XmlSerializer in order to write xml data
		XmlSerializer serializer = Xml.newSerializer();
		
		try {	
			//we set the FileOutputStream as output for the serializer, using UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");
			
			//Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
			serializer.startDocument(null, Boolean.valueOf(true));
			//set indentation option
			//serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			
			//start a tag called "Vertices"
			serializer.startTag(null, "Vertices");
            serializer.attribute(null, "color", "#483d8b");
			while( vs.hasNext() )
			{
				String key = (String) vs.next();
				String[] coordinates = ((String)vertices.get(key)).split(",");
				serializer.startTag(null, "Vertex");
				serializer.attribute(null, "id", key);
				serializer.attribute(null, "x", coordinates[0]);
				serializer.attribute(null, "y", coordinates[1]);
				serializer.endTag(null, "Vertex");
				//Log.d("Vertex",""+key+"->"+coordinates[0]+","+coordinates[1]);
			}
			serializer.endTag(null, "Vertices");

			
			//Log.d("BundleToXml","finished writing vertices -- start with edges");
			//start a tag called "Edges"
			serializer.startTag(null, "Edges");
            serializer.attribute(null, "color", "#483d8b");
			
            
            while( es.hasNext() )
			{
				String key = (String) es.next();
				String[] vw = key.split(","); 
				//Log.d("Edge: "+key, "Coords: "+)
				//String[] coordinates = ((String)edges.get(key)).split(",");
				String color =  (String)edges.get(key);
				serializer.startTag(null, "Edge");
				serializer.attribute(null, "first_end", vw[0]);
				serializer.attribute(null, "second_end",vw[1]);
				serializer.attribute(null, "color", color);
				//serializer.attribute(null, "y", coordinates[1]);
				serializer.endTag(null, "Edge");
				//Log.d("Edge",""+vw[0]+","+vw[1]);//+"->"+coordinates[0]+","+coordinates[1]);
			}
			
			serializer.endTag(null, "Edges");

			
			//Log.d("BundleToXml","finished writing edges -- write level");
			
			serializer.startTag(null, "Metadata");
			serializer.attribute(null,"level",level+"");
			//Log.d("Metadata","level: "+level);
			serializer.endTag(null, "Metadata");

			serializer.startTag(null, "CurrentSteps");
			serializer.attribute(null,"steps",currentSteps+"");
			serializer.endTag(null, "CurrentSteps");
			//serializer.startTag(null, "RecordSteps");
			//serializer.attribute(null,"steps",recordSteps+"");
			//serializer.endTag(null, "RecordSteps");
			
			serializer.endDocument();
			serializer.flush();
		}
		catch (Exception e) {
			Log.e("Exception","error occurred while writing to xml file");
		}
		
		try{
			fileos.close();
		}catch(IOException e){
			Log.e("IOException", "unable to close file");
		}
		
	}

	public Bundle xmlChallengeToBundle(String xmlFile){
		Bundle bundle = new Bundle();
		Bundle vertices = new Bundle();
		Bundle edges = new Bundle();
		String level = null;
		String currentSteps = null;
		//String recordSteps = null;
		
		
		XmlPullParser xpp = getXmlPullParser( xmlFile );

		try {
			while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
				if (xpp.getEventType()==XmlPullParser.START_TAG) {
					if (xpp.getName().equals("Vertex")) {
						String name = xpp.getAttributeValue(0);
						String x    = xpp.getAttributeValue(1);
						String y    = xpp.getAttributeValue(2);
						vertices.putString(name, x+","+y);
					}

					if(xpp.getName().equals("Edge")) {
						String firstVertex = xpp.getAttributeValue(0);
						String secondVertex = xpp.getAttributeValue(1);
						String color    = xpp.getAttributeValue(2);
						//String y    = xpp.getAttributeValue(2);
						edges.putString(firstVertex+","+secondVertex, color);//x+","+y);
					}
					
					if(xpp.getName().equals("Metadata")) {
						level = xpp.getAttributeValue(0);
					}
					
					if(xpp.getName().equals("CurrentSteps")) {
						currentSteps = xpp.getAttributeValue(0);
					}
					
			//		if(xpp.getName().equals("RecordSteps")) {
				//		recordSteps = xpp.getAttributeValue(0);
					//}
				}
				xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bundle.putInt("level", new Integer(level).intValue());
		
		//if(recordSteps.equals("Inf"))
		//	bundle.putInt("recordSteps", 100000000);
		//else
		//	bundle.putInt("recordSteps", new Integer(recordSteps).intValue());
		
		bundle.putInt("currentSteps", new Integer(currentSteps).intValue());
		bundle.putBundle("vertices", vertices);
		bundle.putBundle("edges", edges);

		return bundle;
	}
	
	public void rawToMemory(int resource){
		// get system file directory for this application 
		//String directory = "/data/data/com.project.graplan/files/";
		
		int size = 0;
		InputStream is = null;
		is = (context.getResources()).openRawResource( Constants.RESOURCES[resource] );
		try {
			size = is.available();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] buffer = new byte[size];
		try {
			is.read(buffer);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		FileOutputStream fos;
		try {
			// check if file exists then remove it
			context.deleteFile(Constants.RESOURCE_STRING[resource]);
			fos = context.openFileOutput(Constants.RESOURCE_STRING[resource], 0);
			//new FileOutputStream(directory + Constants.RESOURCE_STRING[resource]);
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/** XmlPullParser generator **/
	public XmlPullParser getXmlPullParser(String xmlFile){

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream ( xmlFile );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		}

		InputStreamReader inputStreamReader = null;
		inputStreamReader =	new InputStreamReader(fileInputStream);
		
		XmlPullParserFactory factory = null;
		try {
			factory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		factory.setNamespaceAware(true);
		XmlPullParser parser = null;
		try {
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			parser.setInput(inputStreamReader);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parser;
	}

}
