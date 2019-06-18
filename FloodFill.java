/*******************************************************************************************

An implementation of a flood fill algorithm, intended to be used statically.
Accepts a boolean array and starting location; false is impassable,
true is passable. Returns an array. Not thread safe (will collide with self)

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;


public class FloodFill implements WSConstants
{
   protected static boolean[][] returnArea;
   protected static boolean[][] alreadySearched;
   protected static int mode = RECT_MODE;
   protected static boolean searchDiagonal = false;
   
   // primary method. Returns a boolean array where true is filled, and false is what was not.
   public static boolean[][] fill(boolean area[][], int x, int y)
   {
      returnArea = new boolean[area.length][area[0].length];
      alreadySearched = new boolean[area.length][area[0].length];
      
      floodFill(area, x, y);
      
      return returnArea;
   }
   
   // alternate calling of primary method
   public static boolean[][] fill(boolean area[][], Coord loc)
   {
      return fill(area, loc.x, loc.y);
   }
   
   // fills the array with true, using false as the boundary
   // walks the original map, while filling the new one
   protected static void floodFill(boolean area[][], int x, int y)
   {
      // reject invalid or already-searched cells
      if(isInBounds(x, y) && !alreadySearched[x][y])
      {
         // mark as searched, mark as true or false, add neighbors to recursion
         alreadySearched[x][y] = true;
         if(area[x][y] == true)
         {
            returnArea[x][y] = true;
            int[][] searchPattern = null;
            if(mode == HEX_MODE)
               if(y % 2 == 1)
                  searchPattern = HEX_ODD_ROW;
               else
                  searchPattern = HEX_EVEN_ROW;
            else // RECT_MODE
               if(searchDiagonal)
                  searchPattern = RECT_DIAG;
               else
                  searchPattern = RECT_ORTHO;
            for(int[] cell : searchPattern)
               floodFill(area, x + cell[0], y + cell[1]);
         }
      }
   }
   
   // checks if the passed tile location is in the display bounds
   public static boolean isInBounds(int x, int y)
   {
      if(x >= 0 && y >= 0 && x < returnArea.length && y < returnArea[0].length)
         return true;
      return false;
   }
   
   // not necessary, but often useful
   public static void invertMap(boolean[][] map)
   {
      for(int x = 0; x < map.length; x++)
      for(int y = 0; y < map[0].length; y++)
         map[x][y] = !map[x][y];
   }
}
