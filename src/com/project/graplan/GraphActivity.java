/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * Activity that constructs the graph, uses SurfaceView class
 * 
 */

package com.project.graplan;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GraphActivity extends Activity implements OnGestureListener{

    private GestureDetector gestureScanner;
    private String selectedVertex = "1";
    
    private float downX;
    private float downY;

    
    //private Bundle graphInfo;
    private Hashtable<String,String> vertices;
    private Hashtable<String,String> edges;
    //private int difficultyLevel;
    
	private GraphView graphView;
	private Bundle inputGraph;
	private int height;
	private int width;
	
	// counts the number of times vertices have been moved
	private int count;
	//private int recordSteps;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);

        gestureScanner = new GestureDetector(this);
        //count = 0;
        graphView = (GraphView) findViewById(R.id.GraphView);
    	Intent myIntent = getIntent();
		inputGraph = myIntent.getExtras();
		extractGraph(inputGraph);
		updateGraphInfo();      
    }

    private Bundle bundleGraph(){
    	Enumeration<String> v = vertices.keys();
    	Enumeration<String> e = edges.keys();
    	Bundle vs = new Bundle();
    	Bundle es = new Bundle();
    	
    	while( v.hasMoreElements() )
    	{
    		String key = v.nextElement();
    		vs.putString(key, vertices.get(key));
    		//Log.d("Adding vertex "+key,"with value "+vertices.get(key));
    	}
    	
    	while( e.hasMoreElements() )
    	{
    		String key = e.nextElement();
    		es.putString(key, edges.get(key));
    		//Log.d("Adding edge "+key,"with value "+edges.get(key));
    	}
    	
    	Bundle graphInfo = new Bundle();
    	
    	graphInfo.putBundle("vertices", vs);
    	graphInfo.putBundle("edges", es);
    	//graphInfo.putBundle("edges",inputGraph.getBundle("edges"));
    	
    	graphInfo.putInt("currentSteps", count);
    	//graphInfo.putInt("recordSteps", recordSteps);
    	graphInfo.putInt("level",inputGraph.getInt("level"));
    	
    	return graphInfo;
    }
    
    private void extractGraph(Bundle graphInfo){
		Bundle vs = graphInfo.getBundle("vertices");
		Bundle es = graphInfo.getBundle("edges");
		Set<String> v = vs.keySet();
		Set<String> e = es.keySet();
		//difficultyLevel = graphInfo.getInt("level");
		vertices = new Hashtable<String,String>();
		edges    = new Hashtable<String,String>();
		
		Iterator<String> it = v.iterator();
		while( it.hasNext() ){
			String key = (String)it.next();
			vertices.put(key, (String)vs.get(key));
			//Log.d("Vertex "+key,"coords:"+vertices.get(key));
		}
		
		it = e.iterator();
		while( it.hasNext() ){
			String key = (String)it.next();
			edges.put(key, (String)es.get(key));
			//Log.d("Edge "+key,"coords:"+edges.get(key));
		}
		
		//recordSteps = graphInfo.getInt("recordSteps");
		count = graphInfo.getInt("currentSteps");
    }
    
    private void updateGraphInfo(){
    	//Bundle graph = new Bundle();
    	boolean noIntersections = ! GraplanIntersectionCheck.hasIntersectingEdges( bundleGraph() );
    	if( noIntersections )
    	    graphView.setGraph(vertices, edges, selectedVertex, "planar!", count+"" );
    	else
    		graphView.setGraph(vertices, edges, selectedVertex, "", count+"" );
    		
    }
   
    @Override
    public void onBackPressed() {
    	// must save the graph state here
    	Bundle graphInfo = bundleGraph();
    	Intent intent = new Intent();
    	intent.putExtras(graphInfo);
		setResult  (RESULT_OK, intent);
		finish();
		return;
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	// must save the graph state here
    	Bundle graphInfo = bundleGraph();
    	Intent intent = new Intent();
    	intent.putExtras(graphInfo);
		setResult  (RESULT_OK, intent);
		finish();
		return;
    }
    
    public void onStop(){
    	super.onStop();
    	// must save the graph state here
    	Bundle graphInfo = bundleGraph();
    	Intent intent = new Intent();
    	intent.putExtras(graphInfo);
		setResult  (RESULT_OK, intent);
		finish();
		return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        return gestureScanner.onTouchEvent(me);
    }


	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		float rawX = e.getRawX();
		float rawY = e.getRawY();
		
		downX = rawX / 10.0f;
		downY = rawY / 10.0f - 5;
		
        width = graphView.getWidth();
        height = graphView.getHeight();
        // Log.d("Width, height --->",width+","+height);
        
		//Log.d("DOWN pressed !", "location is :" +e.getRawX()+", "+e.getRawY());
		if( ! downOnVertex() && rawX < (float)width && rawY < (float)height ){
			if( ! vertices.containsKey(selectedVertex) ){
				Log.e("onDown error", "Vertex not it list of vertice !!");
				System.exit(1);
			}
			vertices.remove(selectedVertex);
			// update selected vertex position
			vertices.put( selectedVertex, ((int)downX)+","+ ((int)downY) );
			
			count++;
		}
		updateGraphInfo();
	    return true;
	}
	
	private boolean downOnVertex(){
		float vertexX , vertexY;
		Enumeration<String> vertexList = vertices.keys();
		while( vertexList.hasMoreElements() ){
			String vertex = vertexList.nextElement();
			String[] coordinates = (vertices.get(vertex)).split(",");
			vertexX = new Float( coordinates[0] ).floatValue();
			vertexY = new Float( coordinates[1] ).floatValue();
			
			if( inVicinity(vertexX, vertexY) )
			{
				selectedVertex = vertex;
				return true;
			}
			
		}
		return false;
	}

	private boolean inVicinity(float x, float y){	
		if( Math.abs(x - downX)  <= Constants.TAP_MARGIN 
				&& Math.abs(y - downY) <= Constants.TAP_MARGIN )
			return true;
		
		return false;
	}
	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}


	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}


	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}


	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}


	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}