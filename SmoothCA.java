/*******************************************************************************************
  
    A smoothing cellular automata class. Iterates over a copy of a 2D boolean array, making 
    each cell more like its neighbors. The original boolean map is not modified.
    
    In rect mode, a tile is set to false if 5 of the 9 tiles in the 3x3 area centered on it are
    false, else it is set to true.
    
    In hex mode, a tile is set to false if 4 of the 7 tiles made up of itself and all adjacent 
    tiles are false, else it is set to true.
     
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.
  
*******************************************************************************************/

package WidlerSuite;


public class SmoothCA implements WSConstants
{
   protected boolean[][] boolMap;
   protected boolean[][] tempMap;
   protected int width;
   protected int height;
   protected int mode;
   
   public boolean[][] getMap(){return boolMap;}
   
   // standard constructor, taking a boolean array indicating which tiles are searchable
   public SmoothCA(boolean[][] searchableMap){this(searchableMap, WSConstants.RECT_MODE);}
   public SmoothCA(boolean[][] searchableMap, int tileMode)
   {
      setBoolMap(searchableMap);
      process();
   }
   
   // static method which returns a smoothed version of the passed map. User can specify iterations and mode, if desired.
   public static boolean[][] smooth(boolean[][] bm){return smooth(bm, 1, WSConstants.RECT_MODE);}
   public static boolean[][] smooth(boolean[][] bm, int iterations){return smooth(bm, iterations, WSConstants.RECT_MODE);}
   public static boolean[][] smooth(boolean[][] bm, int iterations, int tileMode)
   {
      SmoothCA smooth = new SmoothCA(bm, tileMode);
      for(int i = 0; i < iterations - 1; i++)
         smooth.process();
      return smooth.getMap();
   }
   
   // sets the internal map.
   protected void setBoolMap(boolean[][] bm)
   {
      width = bm.length;
      height = bm[0].length;
      boolMap = new boolean[width][height];
      for(int x = 0; x < width; x++)
      for(int y = 0; y < height; y++)
         boolMap[x][y] = bm[x][y];
      tempMap = new boolean[width][height];
   }
   
   // returns the number of tiles in a 3x3 area which are false
   protected int sumNeighborsRect(int xLoc, int yLoc)
   {
      int neighbors = 0;
      for(int x = xLoc - 1; x < xLoc + 2; x++)
      for(int y = yLoc - 1; y < yLoc + 2; y++)
      {
         if(!getCell(x, y))
            neighbors++;
      }
      return neighbors;
   }
   
   // returns the number of tiles including and adjacent to the passed location which are false
   protected int sumNeighborsHex(int xLoc, int yLoc)
   {
      int neighbors = 0;
      if(!getCell(xLoc, yLoc))
         neighbors++;
      Coord[] adjArr = WSTools.getAdjacentHexes(xLoc, yLoc);
      for(Coord c : adjArr)
      {
         if(!getCell(c.x, c.y))
            neighbors++;
      }
      return neighbors;
   }
   
   // returns the value of a cell, or false if the cell is out of bounds
   protected boolean getCell(int x, int y)
   {
      if(x >= width || y >= height || x < 0 || y < 0)
         return false;
      return boolMap[x][y];
   }
   
   // run one iteration
   public void process()
   {
      if(mode == WSConstants.RECT_MODE)
         rectProcess();
      else
         hexProcess();
   }
   
   // run one iteration in rect mode
   protected void rectProcess()
   {
      for(int x = 0; x < width; x++)
      for(int y = 0; y < height; y++)
      {
         tempMap[x][y] = !(sumNeighborsRect(x, y) >= 5);
      }
      boolMap = tempMap;
   }
   
   // run one iteration in hex mode
   protected void hexProcess()
   {
      for(int x = 0; x < width; x++)
      for(int y = 0; y < height; y++)
      {
         tempMap[x][y] = !(sumNeighborsRect(x, y) >= 4);
      }
      boolMap = tempMap;
   }
   
}