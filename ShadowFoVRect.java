/*******************************************************************************************

An implementation of a shadow casting FoV algorithm. This is my translation of Björn Bergström's
Python implementation. Made to be persistent; just call calcFoV() with a new origin, or after
updating the transparency map.

To be honest, while I understand the idea, the actual implementation is a little dense for me. Don't
ask me to pick this one apart for you.

Description:
http://roguebasin.roguelikedevelopment.org/index.php?title=FOV_using_recursive_shadowcasting

Original Implementation:
http://roguebasin.roguelikedevelopment.org/index.php?title=PythonShadowcastingImplementation

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

public class ShadowFoVRect extends ShadowFoV
{
   // multipliers for transforming octants
   private static int[][] multipliers = {{1,  0,  0, -1, -1,  0,  0,  1},
                                         {0,  1, -1,  0,  0, -1,  1,  0},
                                         {0,  1,  1,  0,  0, -1, -1,  0},
                                         {1,  0,  0,  1, -1,  0,  0, -1}};
   
   // constructor
   public ShadowFoVRect(boolean[][] transpMap)
   {
      super(transpMap);
   }
   
   // Calculate visible squares from a given location and radius
   public void calcFoV(int xLoc, int yLoc, int radius)
   {
      flag += 1;
      if(flag == Integer.MAX_VALUE)
      {
         reset(transparencyMap);
      }
      for(int oct = 0; oct < 8; oct += 1)
      {
         castLightInOctant(xLoc, yLoc, oct, radius);
      }
      visibilityMap[xLoc][yLoc] = flag;
   }
   
   private void castLightInOctant(int xLoc, int yLoc, int oct, int radius)
   {
      castLight(xLoc, yLoc,         // starting coordinates
                1,                  // row number
                1.0, 0.0, radius,   // bounding slopes and radius
                multipliers[0][oct], multipliers[1][oct], multipliers[2][oct], multipliers[3][oct]); // octant multipliers
   }
   
   // sets a square as visible using the current flag
   private void setVisible(int x, int y)
   {
      if(isInBounds(x, y))
         visibilityMap[x][y] = flag;
   }
   
   // casts light
   private void castLight(int cx, int cy,                       // starting coordinates
                          int row,                              // row number
                          double start, double end, int radius, // terminal slopes, and max light radius
                          int xx, int xy, int yx, int yy)       // multipliers for octant
   {
      if(start < end)
         return;
      
      int RADIUS_SQUARED = radius * radius;
      
      // main loop; iterates out from observer
      for(int j = row; j < radius + 1; j += 1)
      {
         int dx = -j - 1;
         int dy = -j;
         boolean blocked = false;
         double newStart = 0.0;
         
         while(dx <= 0)
         {
            dx += 1;
            
            // translate the dx, dy coordinates into map coordinates
            int x = cx + dx * xx + dy * xy;
            int y = cy + dx * yx + dy * yy;
            
            // calculate the left and right slopes of the square under consideration
            double leftSlope = (dx - .5) / (dy + .5);
            double rightSlope = (dx + .5) / (dy - .5);
            
            if(start < rightSlope) // not in beam yet
               continue;
            else if (end > leftSlope) // beyond beam
               break;
            else
            {
               // observer has LoS to the square; mark accordingly
               if(dx * dx + dy * dy < RADIUS_SQUARED)
               {
                  setVisible(x, y);
               }
               if(blocked) // we're scanning a row of blocked squares
               {
                  if(blocksLoS(x, y))
                  {
                     newStart = rightSlope;
                     continue;
                  }
                  else
                  {
                     blocked = false;
                     start = newStart;
                  }
               }
               else    // have been scanning transparent squares
               {
                  if(blocksLoS(x, y) && j < radius) // start a child
                  {
                     blocked = true;
                     castLight(cx, cy, j + 1, start, leftSlope, radius, xx, xy, yx, yy);
                     newStart = rightSlope;
                  }
               }
            }
         } // end while loop
         if(blocked)
            break;
      }
   }  // end castLight()
   
}
