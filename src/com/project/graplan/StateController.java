/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * Central activity controlling all other activities
 * 
 */

package com.project.graplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class StateController extends Activity {
	private GraplanChallenge graplanChallenge;
	private Intent mainMenuViewIntent; // = new Intent(this, MainMenuView.class);
	private int gameState = Constants.WAIT_FOR_INPUT;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.processing);
		//mainMenuViewIntent = new Intent(this, MainMenuView.class);
		graplanChallenge = GraplanChallenge.getInstance( getApplication() );
		
		
		String level = graplanChallenge.getLevel() + "";
		Intent mainMenuViewIntent = new Intent(this, MainMenuView.class);
		mainMenuViewIntent.putExtra("level", level);

		startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
	}
	
	@Override
    protected void onDestroy(){
		super.onDestroy();
	}

	/**  **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent ){
		if ( resultCode == RESULT_OK ) {
			if( requestCode == Constants.MAIN_MENU_VIEW_REQUEST ) {
				int decision;

				Bundle message = intent.getExtras();
				decision = message.getInt(Constants.ACTION);

				switch( decision ){
				case Constants.QUIT:
					Log.v("Graplan", "Quitting without saving");
					finish();
					break;

				case Constants.PLAY:
					Log.v("Graplan", "Continuing the game !");
					play();
					break;
				case Constants.PLANAR:
					Log.v("Graplan", "Player decides graph is planar !");
					planar();
					break;	
				case Constants.NON_PLANAR:
					Log.v("Graplan", "Player decides graph is non-planar !");
					nonPlanar();
					break;	

				default:
					startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
					break;

				}
			}
			else
				if( requestCode == Constants.GRAPH_ACTIVITY_REQUEST ) {
					togglePlaying();
					// get graphInfo bundle and save it
					graplanChallenge.saveChallengeState( intent.getExtras() );
					String level = graplanChallenge.getLevel() + "";
					Intent mainMenuViewIntent = new Intent(this, MainMenuView.class);
					mainMenuViewIntent.putExtra("level", level);

					startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
				}
		}
	}
	
	private void play(){
		Bundle challenge = graplanChallenge.getChallenge();
		Intent graphViewIntent = new Intent(this, GraphActivity.class);
		graphViewIntent.putExtras(challenge);
		togglePlaying();
		startActivityForResult(graphViewIntent, Constants.GRAPH_ACTIVITY_REQUEST);
	}

	private void planar(){
		// test intersections here
		if( graplanChallenge.testPlanarity() && ! graplanChallenge.intersectingEdges())
		{
			Toast.makeText(this, "GRAPH IS PLANAR !", 15000).show();
			graplanChallenge.challengeSolved();
		
			String level = graplanChallenge.getLevel() + "";
			Intent mainMenuViewIntent = new Intent(this, MainMenuView.class);
			mainMenuViewIntent.putExtra("level", level);
		
			startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
		}
		else{
			
			String level = graplanChallenge.getLevel() + "";
			Intent mainMenuViewIntent = new Intent(this, MainMenuView.class);
			mainMenuViewIntent.putExtra("level", level);

			startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
		}
	}
	
	private void nonPlanar(){
		if( graplanChallenge.testPlanarity() ){
			
			String level = graplanChallenge.getLevel() + "";
			Intent mainMenuViewIntent = new Intent(this, MainMenuView.class);
			mainMenuViewIntent.putExtra("level", level);

			startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
		}
		else{
			
			Toast.makeText(this, "GRAPH IS NON-PLANAR !", 15000).show();
			graplanChallenge.challengeSolved();
			
			String level = graplanChallenge.getLevel() + "";
			Intent mainMenuViewIntent = new Intent(this, MainMenuView.class);
			mainMenuViewIntent.putExtra("level", level);

			startActivityForResult(mainMenuViewIntent, Constants.MAIN_MENU_VIEW_REQUEST);
		}
	}
	
	private void togglePlaying(){
		if(	gameState == Constants.PLAYING )
			gameState = Constants.WAIT_FOR_INPUT;
		else
			gameState = Constants.PLAYING;
	}
	
	public int getGameState(){
		return gameState;
	}
	
	public GraplanChallenge getGraplanChallenge(){
		return graplanChallenge;
	}

}