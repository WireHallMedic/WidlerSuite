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

public class TileSet
{
   private BufferedImage[][] baseMap = new BufferedImage[16][16];
   private BufferedImage[][] sizedMap = new BufferedImage[16][16];
   private static final int BLACK_RGB = Color.BLACK.getRGB();
   private static final int WHITE_RGB = Color.WHITE.getRGB();
   private int charWidth = 0;
   private int charHeight = 0;
   
   public int getCharWidth(){return charWidth;}
   public int getCharHeight(){return charHeight;}
   
   // constructor
   public TileSet(String s)
   {
      load(s, false);
   }
   
   // basic getters. No array bounds protection.
   public BufferedImage get(int x, int y)
   {
      return sizedMap[x][y];
   }
   public BufferedImage get(int i){return get(i % 16, i / 16);}
   
   // Return a colored copy. No array bounds protection.
   public BufferedImage get(int x, int y, Color c)
   {
      return getColorCopy(sizedMap[x][y], c);
   }
   public BufferedImage get(int i, Color c){return get(i % 16, i / 16, c);}
   
   // Return an array representing a string
   public BufferedImage[] get(String s, Color c)
   {
      BufferedImage[] arr = new BufferedImage[s.length()];
      for(int i = 0; i < s.length(); i++)
         arr[i] = get((int)s.charAt(i), c);
      return arr;
   }
   
   
   // sets the size of the tiles
   public void setSize(int w, int h)
   {
      if(w < 1 || h < 1)
         return;
      charWidth = w;
      charHeight = h;
      for(int x = 0; x < 16; x++)
      for(int y = 0; y < 16; y++)
         sizedMap[x][y] = getScaledImage(w, h, baseMap[x][y]);
   }
   
   public boolean load(String fileName, boolean invertColors)
   {
      // load from external file or .jar resource
      BufferedImage img = null;
      try
      {
         img = ImageIO.read(new FileInputStream(fileName));
      }
      catch(Exception ex){}
      if(img == null)
      {
         try
         {
            img = ImageIO.read(this.getClass().getResourceAsStream("/" + fileName));
         }
         catch(Exception ex){}
      }
      if(img == null)
         return false;
         
      int baseWidth = img.getWidth() / 16;
      int baseHeight = img.getHeight() / 16;
      
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
         sizedMap[x][y] = newImg;
      }
      charWidth = baseWidth;
      charHeight = baseHeight;
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
   
   // returns a copy of the base (white and transparent) image in the specified color
   private BufferedImage getColorCopy(BufferedImage baseImg, Color c)
   {
      int w = baseImg.getWidth();
      int h = baseImg.getHeight();
      int newColor = c.getRGB();
      BufferedImage newImg = new BufferedImage(baseImg.getWidth(), baseImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
      for(int x = 0; x < w; x++)
      for(int y = 0; y < h; y++)
      {
         if(baseImg.getRGB(x, y) == WHITE_RGB)
            newImg.setRGB(x, y, newColor);
      }
      return newImg;
   }
}