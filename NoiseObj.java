/*******************************************************************************************

A class for a Perlin-style noise, which automatically wraps. This generates a single octave 
of noise; see NoiseChoir for layering them together.

Essentially after creation, call in intervals << 1.0 to get smooth (or linear, your call)
noise.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

import java.util.*;

public class NoiseObj
{
	private double[][] vertex;
	private boolean smooth;
   
   public static final int DEFAULT_DIAMETER = 100;

	public double[][] getVertexArr(){return vertex;}
	public boolean isSmooth(){return smooth;}
   public int getWidth(){return vertex.length;}
   public int getHeight(){return vertex[0].length;}


	public void setVertexArr(double[][] v){vertex = v;}
	public void setSmooth(boolean s){smooth = s;}
   
   // default constructor
   public NoiseObj()
   {
      this(DEFAULT_DIAMETER, DEFAULT_DIAMETER);
   }
   
   // size-defined constructor
   public NoiseObj(int width, int height)
   {
      smooth = true;
      generate(width, height);
   }
   
   // set size and populate verticees
   public void generate(int width, int height)
   {
      vertex = new double[width][height];
      
      for(int x = 0; x < getWidth(); x++)
      for(int y = 0; y < getHeight(); y++)
      {
         vertex[x][y] = WSTools.random();
      }
   }
   public void generate(){generate(DEFAULT_DIAMETER, DEFAULT_DIAMETER);}
   
   // determine the value at a point
   public double getValue(double xOff, double yOff)
   {
      // normalize within bounds
      xOff = xOff % (double)getWidth();
      yOff = yOff % (double)getHeight();
      
      // set vertex points
      int vx = (int)xOff;
      int vy = (int)yOff;
      int vx2 = (vx + 1) % getWidth();
      int vy2 = (vy + 1) % getHeight();
      double p1 = vertex[vx][vy];
      double p2 = vertex[vx2][vy];
      double p3 = vertex[vx][vy2];
      double p4 = vertex[vx2][vy2];
      
      double px1 = 0.0;
      double px2 = 0.0;
      
      double xInset = (double)xOff % 1.0;
      double yInset = (double)yOff % 1.0;
      
      // smooth uses cosine interpolation, resulting in curves
      if(smooth)
      {
         px1 = WSTools.interpolateCosine(p1, p2, xInset);
         px2 = WSTools.interpolateCosine(p3, p4, xInset);
         return WSTools.interpolateCosine(px1, px2, yInset);
      }
      // non-smooth uses linear interpolation, resulting in lines
      else
      {
         px1 = WSTools.interpolateLinear(p1, p2, xInset);
         px2 = WSTools.interpolateLinear(p3, p4, xInset);
         return WSTools.interpolateLinear(px1, px2, yInset);
      }
   }
   
   // makes a rolling-hills sort of effect. amt must be in range of (0, 1)
   public void applyBillow(double amt)
   {
      double resultantMax = 1.0 - amt;
      double reshift = 1.0 / resultantMax;
      for(int x = 0; x < getWidth(); x++)
      for(int y = 0; y < getHeight(); y++)
      {
         vertex[x][y] = Math.max(0.0, Math.min(1.0, (Math.abs(vertex[x][y] - amt)) * reshift));
      }
   }
   public void applyBillow(){applyBillow(0.5);}
   
   // makes sharp alpine ridges.
   public void applyRidged(double expo)
   {
      for(int x = 0; x < getWidth(); x++)
      for(int y = 0; y < getHeight(); y++)
      {
         vertex[x][y] = Math.pow(vertex[x][y], expo);
      }
   }
   public void applyRidged(){applyRidged(2.0);}
}