/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * Class checks for intersecting edges
 * 
 */

package com.project.graplan;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import android.os.Bundle;
import android.util.Pair;

public class GraplanIntersectionCheck {
	
	public static boolean hasIntersectingEdges(Bundle graph){
		LinkedList<String> intersectingEdges = getIntersectingEdges( graph );
		return ! intersectingEdges.isEmpty() ;
	}
	
	public static LinkedList<String> getIntersectingEdges(Bundle graph){
		LinkedList<String> intersectingEdges = new LinkedList<String>();
		
		Bundle vs = graph.getBundle("vertices");
		Bundle es = graph.getBundle("edges");
		Set<String> v = vs.keySet();
		Set<String> e = es.keySet();

		Hashtable<String, Pair<Double,Double>> vertices = new Hashtable<String,Pair<Double,Double>>();
		Hashtable<String, Pair<String,String>> edges    = new Hashtable<String,Pair<String,String>>();
		
		Iterator<String> it = v.iterator();
		while( it.hasNext() ){
			String key = (String)it.next();
			
			String[] coordinates = ((String) vs.get(key)).split(",");
			Double vertexX = new Double( coordinates[0] );
			Double vertexY = new Double( coordinates[1] );

			vertices.put(key, new Pair<Double,Double>(vertexX, vertexY));
		}
		
		it = e.iterator();
		while( it.hasNext() ){
			String key = (String)it.next();
			String[] temp = key.split(",");
			edges.put(key, new Pair<String,String>(temp[0], temp[1]));
		}
		
		String[] edgeKeys =   (edges.keySet()).toArray( new String[0] );

		// all pairs of edges
		for(int i = 0; i < edgeKeys.length; i++)
			for(int j = i+1 ; j < edgeKeys.length; j++)
			{
				String edge1 = edgeKeys[i];
				String edge2 = edgeKeys[j];
								
				String vertex1 = edges.get(edge1).first;
				String vertex2 = edges.get(edge1).second;
				
				double aX = vertices.get(vertex1).first.floatValue();
				double aY = vertices.get(vertex1).second.floatValue();
				double bX = vertices.get(vertex2).first.floatValue();;
				double bY = vertices.get(vertex2).second.floatValue();;
				
				
				vertex1 = edges.get(edge2).first;
				vertex2 = edges.get(edge2).second;

				double cX = vertices.get(vertex1).first.floatValue();
				double cY = vertices.get(vertex1).second.floatValue();
				double dX = vertices.get(vertex2).first.floatValue();;
				double dY = vertices.get(vertex2).second.floatValue();;
				
				boolean intersect = edgesIntersect(aX,aY,bX,bY,cX,cY,dX,dY);
				if ( intersect ){
					//Log.d("Graplan","Edges "+edge1+","+edge2+" intersect !");
					//return true;
					intersectingEdges.add(edge1);
					intersectingEdges.add(edge2);
				}
			}
				
		//Log.d("Graplan","No edges intersect !");
		return intersectingEdges;
	}
		
	
	private static boolean edgesIntersect(double aX,double aY,double bX,double bY,double cX,double cY,double dX,double dY)
	{
			  double  distAB, theCos, theSin, newX, ABpos;//, intX,intY;
		
			  // edge is vertex, this should not happen as we ensure that vertices are non-overlapping
			  if( aX == bX && aY == bY || cX == dX && cY == dY ) 
				  return false;
			  // segments have same vertex 
			  if( aX == cX && aY == cY || bX == cX && bY == cY
					  ||  aX == dX && aY == dY || bX == dX && bY == dY ) 
				  return false; 

			  // translate to 0,0
			  bX -= aX; 
			  bY -= aY;
			  cX -= aX;
			  cY -= aY;
			  dX -= aX; 
			  dY -= aY;

			  distAB= Math.sqrt( bX * bX   +   bY * bY );

			  theCos = bX / distAB;
			  theSin = bY / distAB;
			  newX   = cX * theCos + cY * theSin;
			  cY     = cY * theCos - cX * theSin; 
			  cX     = newX;
			  newX   = dX * theCos + dY * theSin;
			  dY     = dY * theCos - dX * theSin; 
			  dX     = newX;
			  if ( cY < 0.0 && dY < 0.0 || cY >= 0.000001 && dY >= 0.000001) 
				  return false;
			  ABpos = dX + ( cX - dX ) * dY / ( dY - cY );
			  if ( ABpos < 0.0 || ABpos > distAB) 
				  return false;
			  //intX = aX + ABpos * theCos;
			  //intY = aY + ABpos * theSin;
			  //Log.d("Intersect","Intersection point "+intX+","+intY);
			  // intersection point found
			  return true; 
	}
}
