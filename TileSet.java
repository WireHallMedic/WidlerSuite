/**********************************************************************************
A class font tilesets. Expects a 16 tile by 16 tile map, where characters are in 
one color and backgrounds are in one other color.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class TileSet implements CP437
{
   private BufferedImage[][] baseMap = new BufferedImage[16][16];
   private BufferedImage[][] sizedMap = new BufferedImage[16][16];
   private int baseWidth = 1;
   private int baseHeight = 1;
   private static final int BLACK_RGB = Color.BLACK.getRGB();
   private static final int WHITE_RGB = Color.WHITE.getRGB();
   
   
   public boolean load(File imgFile, boolean invertColors)
   {
      BufferedImage img = null;
      try
      {
         img = ImageIO.read(new FileInputStream(imgFile));
      }
      catch(Exception ex)
      {
         return false;
      }
      baseWidth = img.getWidth() / 16;
      baseHeight = img.getHeight() / 16;
      
      for(int x = 0; x < 16; x++)
      for(int y = 0; y < 16; y++)
      {
         BufferedImage newImg = new BufferedImage(baseWidth, baseHeight, BufferedImage.TYPE_INT_ARGB);
         // since bufferedimage values are initially transparent, only set the ones we need to
         for(int w = 0; w < baseWidth; w++)
         for(int h = 0; h < baseHeight; h++)
         {
            int xLoc = (x * baseWidth) + w;
            int yLoc = (y * baseHeight) + h;
            {
               if(img.getRGB(xLoc, yLoc) == WHITE_RGB && !invertColors)
                  newImg.setRGB(w, h, WHITE_RGB);
               if(img.getRGB(xLoc, yLoc) != WHITE_RGB && invertColors)
                  newImg.setRGB(w, h, WHITE_RGB);
            }
         }
         baseMap[x][y] = newImg;
      }
      return true;
   }
   
   // scales an image up
   private BufferedImage getScaledImage(int newW, int newH, BufferedImage oldImg)
   {
      BufferedImage newImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
      double horizRatio = (double)newW / (double)oldImg.getWidth();
      double vertRatio = (double)newH / (double)oldImg.getHeight();
      for(int x = 0; x < newW; x++)
      for(int y = 0; y < newH; y++)
      {
         newImage.setRGB(x, y, oldImg.getRGB((int)(x / horizRatio),(int)(y / vertRatio)));
      }
      return newImage;
   }
}