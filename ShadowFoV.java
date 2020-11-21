/*******************************************************************************************

Abstract parent class for ShadowFoVHex and ShadowFoVRect. Reduce code duplication,
ensures consistent interface, allow objects to be unaware of what kind of FoV they're
using.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

public abstract class ShadowFoV implements WSConstants
{
   protected boolean[][] transparencyMap;    // which tiles are visible
   protected int[][] visibilityMap;          // which tiles can be seen from most recent calcFoV()
   protected int[][] addedMap;               // which tiles have been added to processList
   protected int width;
   protected int height;
   protected int flag;                       // used to avoid unnecessary reassignment to refresh visibilityMap
   
   // constructor
   public ShadowFoV(boolean[][] transpMap)
   {
      reset(transpMap);
   }
   
   // generally only needs to be called if a different transparency map is needed
   public void reset(boolean[][] transpMap)
   {
      transparencyMap = transpMap;                // intentional shallow copy
      width = transparencyMap.length;
      height = transparencyMap[0].length;
      visibilityMap = new int[width][height];
      flag = 0;
   }
   
   // checks if a location is in the map bounds
   public boolean isInBounds(Coord loc){return isInBounds(loc.x, loc.y);}
   public boolean isInBounds(int x, int y)
   {
      return x >= 0 && y >= 0 && x < width && y < height;
   }
   
   // checks if a square blocks LoS
   public boolean blocksLoS(Coord loc){return blocksLoS(loc.x, loc.y);}
   public boolean blocksLoS(int x, int y)
   {
      if(isInBounds(x, y))
         return !transparencyMap[x][y];
      return false;
   }
   
   // checks if a square is visible
   public boolean isVisible(Coord loc){return isVisible(loc.x, loc.y);}
   public boolean isVisible(int x, int y)
   {
      return isInBounds(x, y) && visibilityMap[x][y] == flag;
   }
   
   // returns the visibility array in the rectangle passed
   public boolean[][] getArray(int startX, int startY, int w, int h)
   {
      boolean[][] visArr = new boolean[w][h];
      for(int x = 0; x < w; x++)
      for(int y = 0; y < h; y++)
      {
         visArr[x][y] = isVisible(startX + x, startY + y);
      }
      return visArr;
   }
   
   protected void incrementFlag()
   {
      flag += 1;
      if(flag == Integer.MAX_VALUE)
      {
         reset(transparencyMap);
      }
   }
   
   // main function in child classes
   public abstract void calcFoV(int xLoc, int yLoc, int radius);
}
