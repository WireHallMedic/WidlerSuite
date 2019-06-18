/*******************************************************************************************
  
    A smoothing cellular automata class. Iterates over a 2D boolean array, making each cell
    more like its neighbors.
  
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
   
   public boolean[][] getMap(){return boolMap;}
   
   public SmoothCA(boolean[][] bm)
   {
      setBoolMap(bm);
      process();
   }
   
   public static boolean[][] smooth(boolean[][] bm){return smooth(bm, 1);}
   public static boolean[][] smooth(boolean[][] bm, int iterations)
   {
      SmoothCA smooth = new SmoothCA(bm);
      for(int i = 0; i < iterations - 1; i++)
         smooth.process();
      return smooth.getMap();
   }
   
   public void setBoolMap(boolean[][] bm)
   {
      width = bm.length;
      height = bm[0].length;
      boolMap = new boolean[width][height];
      for(int x = 0; x < width; x++)
      for(int y = 0; y < height; y++)
         boolMap[x][y] = bm[x][y];
      tempMap = new boolean[width][height];
   }
   
   protected int sumNeighbors(int xLoc, int yLoc)
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
   
   protected boolean getCell(int x, int y)
   {
      if(x >= width || y >= height || x < 0 || y < 0)
         return false;
      return boolMap[x][y];
   }
   
   public void process()
   {
      for(int x = 0; x < width; x++)
      for(int y = 0; y < height; y++)
      {
         tempMap[x][y] = !(sumNeighbors(x, y) >= 5);
      }
      boolMap = tempMap;
   }
   
   
   
   /////////////////////////////////////////////////////
   public static void main(String[] args)
   {
      int w = 100; int h = 20;
      boolean[][] ba = new boolean[w][h];
      for(int x = 0; x < w; x++)
      for(int y = 0; y < h; y++)
         ba[x][y] = Math.random() > .45;
      printArr(ba);
      System.out.println();
      ba = SmoothCA.smooth(ba, 4);
      printArr(ba);
   }
   
   protected static void printArr(boolean[][] ba)
   {
      for(int y = 0; y < ba[0].length; y++)
      {
         for(int x = 0; x < ba.length; x++)
         {
            if(ba[x][y] == true)
               System.out.print(" ");
            else
               System.out.print("#");
         }
         System.out.println();
      }
   }
}