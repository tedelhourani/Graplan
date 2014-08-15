/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * Handles main menu activity
 * 
 */
package com.project.graplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuView extends Activity implements OnClickListener{
	
	private Bundle message = new Bundle();
	private Bundle levelMessage;
	private String level;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	Intent myIntent = getIntent();
		levelMessage = myIntent.getExtras();
        
		level = levelMessage.getString("level");
		
		
        Button play = (Button) findViewById(R.id.play);
        Button quit = (Button) findViewById(R.id.quit);
        Button nonPlanar = (Button) findViewById(R.id.non_planar);
        Button planar = (Button) findViewById(R.id.planar);
        
        Log.d("MainMenuView","Level is set to "+level);
        TextView tv = (TextView) findViewById(R.id.level);
        tv.setText(level);
        
        play.setOnClickListener(this);
        quit.setOnClickListener(this);
        nonPlanar.setOnClickListener(this);
        planar.setOnClickListener(this);
    }


    //@Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	switch( v.getId() ){
    	case R.id.quit:
    		message.putInt(Constants.ACTION,Constants.QUIT);
    		finishMainMenuView();
    	case R.id.play:
    		message.putInt(Constants.ACTION,Constants.PLAY);
    		finishMainMenuView();
    	case R.id.planar:
    		message.putInt(Constants.ACTION,Constants.PLANAR);
    		finishMainMenuView();
    	case R.id.non_planar:
    		message.putInt(Constants.ACTION,Constants.NON_PLANAR);
    		finishMainMenuView();
    	}
    }
    
    private void finishMainMenuView(){
    	Intent intent = new Intent();
    	intent.putExtras(message);
		setResult  (RESULT_OK, intent);
    	finish();
    }
    
    
    public Bundle getMessage(){
    	return message;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
    		message.putInt(Constants.ACTION,Constants.QUIT);
    		finishMainMenuView();
        }
        return super.onKeyDown(keyCode, event);
    }
}