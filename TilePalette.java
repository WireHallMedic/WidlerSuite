/**********************************************************************************
A class for holding an image to use as a tile palette for classes like TileRoguePanel.
Loads from an image or imagefile.

Copyright 2021 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import java.io.*;
   
public class TilePalette
{
   private int[][] masterImage = null;          // int array of the ARGB colors of the main image
   private BufferedImage[] imageStrip = null;   // kept as a strip so we can access with chars
   private int imageWidth;
   private int imageHeight;
   private int columns;
   private int rows;
   private int tileWidth;
   private int tileHeight;
   private int BG_COLOR = Color.BLACK.getRGB();
   private int FG_COLOR = Color.WHITE.getRGB();
   private static final int TRANSPARENT = new Color(0.0f, 0.0f, 0.0f, 0.0f).getRGB();
   private int scaleMethod = Image.SCALE_SMOOTH;
   
   public int getImageWidth(){return imageWidth;}
   public int getImageHeight(){return imageHeight;}
   public int getRows(){return rows;}
   public int getColumns(){return columns;}
   public int getTileWidth(){return tileWidth;}
   public int getTileHeight(){return tileHeight;}
   
   public void setScaleMethodFast(){scaleMethod = Image.SCALE_FAST;}
   public void setScaleMethodSmooth(){scaleMethod = Image.SCALE_SMOOTH;}
   
   
   // constructor from file
   public TilePalette(String fileName, int w, int t)
   {
      this.loadFromFile(fileName, w, t);
   }
   
   // constructor from BufferedImage
   public TilePalette(BufferedImage image, int w, int t)
   {
      this.loadFromBufferedImage(image, w, t);
   }
   
   // load image from file
   public void loadFromFile(String fileName, int w, int t)
   {
      // load the image
      BufferedImage rawImage = null;
      try
      {
         rawImage = ImageIO.read(new File(fileName));
      }
      catch(Exception ex)
      {
         System.out.println(String.format("Unable to load '%s': %s", fileName, ex.toString()));
         masterImage = null;
         imageStrip = null;
         return;
      }
      
      loadFromBufferedImage(rawImage, w, t);
   }
   
   // turn 2d coordinates into an array index
   public int flatten(int x, int y)
   {
      return (y * columns) + x;
   }
   
   // load from a BufferedImage
   public void loadFromBufferedImage(BufferedImage rawImage, int w, int t)
   {
      
      // create masterImage in int form
      imageWidth = rawImage.getWidth();
      imageHeight = rawImage.getHeight();
      masterImage = new int[imageWidth][imageHeight];
      // use upper rightmost pixel as bg color
      int bgColor = rawImage.getRGB(0, 0);
      // set masterImage from rawImage
      for(int x = 0; x < imageWidth; x++)
      for(int y = 0; y < imageHeight; y++)
      {
         if(rawImage.getRGB(x, y) == bgColor)
            masterImage[x][y] = BG_COLOR;
         else
            masterImage[x][y] = FG_COLOR;
      }
      
      // set size variables
      columns = w;
      rows = t;
      tileWidth = imageWidth / w;
      tileHeight = imageHeight / t;
      
      partitionImage();
   }
   
   // partition the image based on declared values
   private void partitionImage()
   {
      // early exit if master image doesn't exist
      if(masterImage == null)
         return;
      
      imageStrip = new BufferedImage[columns * rows];
      int curColorInt;
      for(int xImage = 0; xImage < columns; xImage++)
      for(int yImage = 0; yImage < rows; yImage++)
      {
         BufferedImage newBI = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
         for(int xTile = 0; xTile < tileWidth; xTile++)
         for(int yTile = 0; yTile < tileHeight; yTile++)
         {
            curColorInt = masterImage[(xImage * tileWidth) + xTile][(yImage * tileHeight) + yTile];
            newBI.setRGB(xTile, yTile, curColorInt);
         }
         imageStrip[flatten(xImage, yImage)] = newBI;
      }
   }
   
   // get tile at passed index
   public BufferedImage getTile(int i)
   {
      return getTile(i, FG_COLOR, BG_COLOR);
   }
   public BufferedImage getTile(int x, int y){return getTile(flatten(x, y));}
   
   // get tile in specified colors
   public BufferedImage getTile(int i, Color fg, Color bg)
   {
      return getTile(i, fg.getRGB(), bg.getRGB());
   }
   public BufferedImage getTile(int x, int y, Color fg, Color bg){return getTile(flatten(x, y), fg, bg);}
   
   // get tile in specified colors as RGB values
   public BufferedImage getTile(int i, int fg, int bg)
   {
      BufferedImage img = imageStrip[i];
      BufferedImage newImg = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
      for(int x = 0; x < tileWidth; x++)
      for(int y = 0; y < tileHeight; y++)
      {
         if(img.getRGB(x, y) == BG_COLOR)
            newImg.setRGB(x, y, bg);
         else
            newImg.setRGB(x, y, fg);
      }
      return newImg;
   }
   public BufferedImage getTile(int x, int y, int fg, int bg){return getTile(flatten(x, y), fg, bg);}
   
   // magnify image by specified magnitude
   public BufferedImage magnify(BufferedImage img, double multiplier)
   {
      int width = (int)(tileWidth * multiplier);
      int height = (int)(tileHeight * multiplier);
      Image newImage = img.getScaledInstance(width, height, scaleMethod);
      BufferedImage newBuffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      newBuffered.getGraphics().drawImage(newImage, 0, 0 , null);
      return newBuffered;
   }
   
   // get an UnboundTile based on passed values
   public UnboundTile getUnboundTile(int tileIndex, int fgColor, int bgColor, double sm, boolean bgType)
   {
      UnboundTile tile = new UnboundTile(this);
      tile.setBGColor(bgColor);
   	tile.setFGColor(fgColor);
   	tile.setIconIndex(tileIndex);
      tile.setSizeMultiplier(sm);
      tile.setBGType(bgType);
      setUnboundTile(tile);
      return tile;
   }
   
   // Unbound tiles without background specifications have a transparent background
   public UnboundTile getUnboundTile(int tileIndex, int fgColor, double sm)
   {
      return getUnboundTile(tileIndex, fgColor, TRANSPARENT, sm, UnboundTile.BOX_BACKGROUND);
   }
   
   // once the fields are set, apply them
   private void setUnboundTile(UnboundTile tile)
   {
      BufferedImage img = getTile(tile.getIconIndex(), tile.getFGColor(), tile.getBGColor());
      img = magnify(img, tile.getSizeMultiplier());
      // if circle background, remove extra
      if(tile.getBGType() == UnboundTile.CIRCLE_BACKGROUND)
      {
         BufferedImage stencil = getTile(2, FG_COLOR, BG_COLOR);
         double m = tile.getSizeMultiplier();
         stencil = magnify(stencil, m);
         for(int x = 0; x < (int)(tileWidth * m); x++)
         for(int y = 0; y < (int)(tileHeight * m); y++)
         {
            if(stencil.getRGB(x, y) == BG_COLOR)
               img.setRGB(x, y, TRANSPARENT);
         }
      }
      tile.setImage(img);
   }
   
   // test function
   public static void main(String[] args)
   {
      TilePalette tp = new TilePalette("WidlerSuite/WSFont_16x16.png", 16, 16);
      BufferedImage bi = tp.getTile(1, 1, Color.BLUE.getRGB(), Color.BLACK.getRGB());
   }
}