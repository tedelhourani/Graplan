/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * Class handles all functions related to a Graplan challenge
 * 
 */

package com.project.graplan;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


/**
 * Class handles all functions related to a Graplan challenge
 * 
 * Whenever a challenge is solved GraplanChallenge generates the next challenge 
 * 
 * @author telhoura
 *
 */
public class GraplanChallenge {
	
	static	{
	    System.loadLibrary("PlanarityTest");
	}
	
	final private int TAILS = 0;
	final private int HEADS = 1;
	final private String CHALLENGE_STATE_FILE = "state.xml"; 
	private LinkedList<String> challengeFiles;
	private int currentDifficulty;
	
	private String currentChallenge;
	private boolean challengeAvailable = true;
	private int difficultyLevel;
	private int maxDifficultyLevel;	
	private static GraplanChallenge graplanChallenge = null;
	private GraplanUtils utils;
	final private static String directory = "/data/data/com.project.graplan/files/";
	final private static String libDirectory = "/data/data/com.project.graplan/lib/";
	
	public static synchronized GraplanChallenge getInstance(Context context){
		if( graplanChallenge == null){
			graplanChallenge = new GraplanChallenge(context);
		}
		return graplanChallenge;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/** private constructor as this is a singleton **/
	private GraplanChallenge(Context context){
		utils = new GraplanUtils( context );
		recoverChallengeState();
	}
	
	/**  method reads the challenge state file **/
	private void recoverChallengeState(){
		Hashtable<String, String> unsolvedChallenges = utils.xmlUnsolvedChallengeList(directory  + CHALLENGE_STATE_FILE);
		Enumeration<String> keyEnumeration = unsolvedChallenges.keys();
			
		challengeFiles = new LinkedList<String>();
		int smallest = 1000000000;
		while (keyEnumeration.hasMoreElements()) 
		{
			String key = keyEnumeration.nextElement();
			challengeFiles.add( key );
			int level = new Integer( unsolvedChallenges.get(key)).intValue();
			if ( level < smallest ){
				currentChallenge = key;
				smallest = level;
			}
		}
		String dl = unsolvedChallenges.get(currentChallenge);
		difficultyLevel = new Integer( dl ).intValue();
	}
	
	/** getter method used by test cases **/
	public String getChallengeFile(){
		return currentChallenge;
	}
	
	public int getLevel(){
		return difficultyLevel;
	}
	
	/**  method must be called from the StateController Activity**/
	public void saveChallengeState(Bundle challenge){
		utils.bundleToXmlChallenge( directory + currentChallenge , challenge);
	}

	public Bundle getChallenge(){
		return utils.xmlChallengeToBundle( directory + currentChallenge );
	}
	
	public boolean testPlanarity(){
		LinkedList<int[]> edges;
		edges = utils.xmlChallengeToEdgeArrays( directory + currentChallenge );
		return planar( (int[])edges.get(TAILS), (int[])edges.get(HEADS) );
	}
	
	public LinkedList<int[]> getEdges(){
		LinkedList<int[]> edges;
		edges = utils.xmlChallengeToEdgeArrays( directory + currentChallenge );
		return edges;
	}
	
	/** when challenge is solved update status.xml and reload unsolved challenges **/
	public void challengeSolved(  ){
		Log.d("Graplan","Challenge solved!");
		utils.stateToXml( directory  + CHALLENGE_STATE_FILE
				, currentChallenge);
		recoverChallengeState();
	}
	
	public boolean intersectingEdges(){
		return  GraplanIntersectionCheck.
					hasIntersectingEdges( getChallenge() );
	}
	
	public GraplanUtils getGraplanUtils(){
		return utils;
	}
	private native boolean planar(int[] tails, int[] heads);
}
