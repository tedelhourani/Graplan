/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * The graph drawing class
 * 
 */

package com.project.graplan;

import java.util.Hashtable;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * GraphView
 *        
 **/
public class GraphView extends SurfaceView implements SurfaceHolder.Callback 
{	
	/**
     * Canvas Thread is responsible for all components of display
     **/
	class CanvasThread extends Thread {
		private SurfaceHolder surfaceHolder;
		@SuppressWarnings("unused")
		private int canvasWidth, canvasHeight;
		private boolean run = false;

		private final float RADIUS = 10.0f;
		private final float SCALING = 10;

		public CanvasThread(SurfaceHolder surfaceHolder, Context context) {
			this.surfaceHolder = surfaceHolder;
		}

		public void setRunning(boolean run) {
			this.run = run;
		}
		
		/**
		 * 
		 * Constantly redraws 
	     *        
	     **/
		@Override
		public void run() {
			Canvas c;
			while (run) {
				c = null;
				try {
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						doDraw(c);
					}
				} finally {
					if (c != null) {
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		public void setSurfaceSize(int width, int height) {
			// TODO Auto-generated method stub
			synchronized (surfaceHolder) {
				canvasWidth = width;
				canvasHeight = height;
			}
		}

		private void doDraw(Canvas canvas) {
			if( edges != null && vertices != null)
			synchronized(surfaceHolder){
				Paint paint = new Paint();
				
				canvas.drawColor(Color.BLACK);
				
				// set paint's text style
				paint.setStyle(Paint.Style.FILL);
				paint.setAntiAlias(true);
				paint.setColor(Color.WHITE);
				
				paint.setTextSize(30);
				
				paint.setStrokeWidth(4);
				
				paint.setColor(Color.GRAY);
				// draw text first
				//if( solved )
				canvas.drawText(solved,Constants.WIDTH/2 , Constants.LENGTH+ 220, paint);
				canvas.drawText(steps,Constants.WIDTH/2 + 120, Constants.LENGTH+ 220, paint);
				
				paint.setColor(Color.WHITE);
				// draw the edges
				Iterator<String> edgekeys = edges.keySet().iterator();
				
				while( edgekeys.hasNext() )
				{
					String edge  = edgekeys.next();
					//Log.d("Edge to paint: ",edge);
					String[ ] ends = edge.split(",");
					String first = ends[0];
					String second = ends[1];
					
					String position = vertices.get( first );
					String[] xy = position.split(",");
					int startX = new Integer(xy[0]).intValue();
					int startY = new Integer(xy[1]).intValue();
			
					position = vertices.get( second );
					xy = position.split(",");
					int stopX = new Integer(xy[0]).intValue();
					int stopY = new Integer(xy[1]).intValue();			
					//Log.v("GraphView Message ", " Drawing vertex "+startX+" ,"+startY);
					if( true ){
						//paint.setColor("#cd6600");
						paint.setColor(Color.RED);
					}
					canvas.drawLine(startX * SCALING, startY * SCALING, stopX * SCALING, stopY *SCALING, paint);
				}
				
				paint.setColor(Color.WHITE);
				
				// draw the vertices
				Iterator<String> keys =  vertices.keySet().iterator();
				while( keys.hasNext() )
				{
					String vertex = keys.next();
					String position = vertices.get( vertex );
					String[] xy = position.split(",");
					int cx = new Integer(xy[0]).intValue();
					int cy = new Integer(xy[1]).intValue();
					
					if( vertex.equals(selectedVertex))
						paint.setColor(Color.GREEN);
						
					//Log.v("GraphView Message ", " Drawing vertex "+cx+" ,"+cy);
					canvas.drawCircle((float) cx * SCALING,
		                       (float) cy * SCALING,
		                       RADIUS,
		                       paint);
					
					paint.setColor(Color.WHITE);
				}
			}
		}


	}

	/**  **/
	CanvasThread canvasthread;

	private Hashtable<String,String> vertices;
	private Hashtable<String,String> edges;
	private String selectedVertex;
	private String solved;
	private int difficultyLevel;
	private Context myContext;
	private String steps;
	
	/**
     * creates the canvas thread that we use for displaying everything, absolutely everything      
     **/
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// this doesn't start the canvasthread
		myContext = context;
		canvasthread = new CanvasThread(getHolder(), myContext);
		setFocusable(true);
	}

	@SuppressWarnings("unchecked")
	public void setGraph(Hashtable<String, String> v, Hashtable<String, String> e, String sv, String noIntersect, String count)
	{
		canvasthread.setRunning(false);
		//canvasthread.stop();
		//canvasthread = null;
		steps = new String( count );
		solved = new String(noIntersect);
		//solved = "planar !";
		//steps = "5";
		selectedVertex = new String(sv);
		vertices = (Hashtable<String, String>) v.clone();
		edges = (Hashtable<String, String>) e.clone();
		//canvasthread = new CanvasThread(getHolder(), myContext);
		canvasthread.setRunning(true);
		//canvasthread.start();
	}
	
	//@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {
		// TODO Auto-generated method stub
		canvasthread.setSurfaceSize(width, height);
	}

	//@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		canvasthread.setRunning(true);
		// starting the canvas thread here
		canvasthread.start();
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		boolean retry = true;
		canvasthread.setRunning(false);
		while(retry) {
			try {
				canvasthread.join();
				retry = false;
			}catch(InterruptedException e){
			}
		}
	}
	
	public int getDifficultyLevel(){
		return difficultyLevel;
	}



}
