/*******************************************************************************************

An implementation of Voronoi diagramming as part of WidlerSuite.

Accepts a list of points and an array size, returns a Voronoi map. The map is represented
as a two-dimensional int array, with the value stored being the Vector index of the point
closest. A second two-dimensional array is used to track best so far during generation.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

import java.util.Vector;

public class Voronoi
{
   protected static int[][] map;
   protected static int[][] distance;
   public static boolean ANGBAND = false;
   public static boolean TRUE_DIST = true;    // this is the default
   
   // only public functions
   public static int[][] generate(Vector<Coord> pointList, Coord size){return generate(pointList, size.x, size.y, TRUE_DIST);}
   public static int[][] generate(Vector<Coord> pointList, int sizeX, int sizeY){return generate(pointList, sizeX, sizeY, TRUE_DIST);}
   public static int[][] generate(Vector<Coord> pointList, Coord size, boolean distType){return generate(pointList, size.x, size.y, distType);}
   public static int[][] generate(Vector<Coord> pointList, int sizeX, int sizeY, boolean distType)
   {
      initializeMap(sizeX, sizeY);
      
      if(distType == ANGBAND)
      {
         for(int i = 0; i < pointList.size(); i++)
            angbandProcess(pointList.elementAt(i), i);
      }
      else // distType == TRUE_DIST
      {
         for(int i = 0; i < pointList.size(); i++)
            trueDistProcess(pointList.elementAt(i), i);
      }
      
      return map;
   }
   
   // initializes the internal maps
   protected static void initializeMap(int sizeX, int sizeY)
   {
      map = new int[sizeX][sizeY];
      distance = new int[sizeX][sizeY];
      int maxDist = (sizeX * sizeX) + (sizeY + sizeY) + 1;
      
      for(int x = 0; x < sizeX; x++)
      for(int y = 0; y < sizeY; y++)
      {
         map[x][y] = -1;
         distance[x][y] = maxDist;
      }
   }
   
   // checks the passed point to flag each cell that is closer to it than to its current target, using the Angband metric
   protected static void angbandProcess(Coord loc, int index)
   {
      int curDist;
      
      for(int x = 0; x < map.length; x++)
      for(int y = 0; y < map[0].length; y++)
      {
         curDist = WSTools.getAngbandMetric(loc.x, loc.y, x, y);
         
         if(curDist < distance[x][y])
         {
            map[x][y] = index;
            distance[x][y] = curDist;
         }
      }
   }
   
   // checks the passed point to flag each cell that is closer to it than to its current target, using Pythagorean theorem
   protected static void trueDistProcess(Coord loc, int index)
   {
      int curDist;
      
      for(int x = 0; x < map.length; x++)
      for(int y = 0; y < map[0].length; y++)
      {
         curDist = WSTools.getDistanceMetric(loc.x, loc.y, x, y);
         
         if(curDist < distance[x][y])
         {
            map[x][y] = index;
            distance[x][y] = curDist;
         }
      }
   }
   
}
