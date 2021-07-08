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
   
   public int getImageWidth(){return imageWidth;}
   public int getImageHeight(){return imageHeight;}
   public int getRows(){return rows;}
   public int getColumns(){return columns;}
   public int getTileWidth(){return tileWidth;}
   public int getTileHeight(){return tileHeight;}
   
   
   public TilePalette(String fileName, int w, int t)
   {
      this.loadFromFile(fileName, w, t);
   }
   
   public TilePalette(BufferedImage image, int w, int t)
   {
      this.loadFromBufferedImage(image, w, t);
   }
   
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
   
   public BufferedImage getTile(int i)
   {
      return getTile(i, FG_COLOR, BG_COLOR);
   }
   public BufferedImage getTile(int x, int y){return getTile(flatten(x, y));}
   
   public BufferedImage getTile(int i, Color fg, Color bg)
   {
      return getTile(i, fg.getRGB(), bg.getRGB());
   }
   public BufferedImage getTile(int x, int y, Color fg, Color bg){return getTile(flatten(x, y), fg, bg);}
   
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
   
   public BufferedImage magnify(BufferedImage img, int m)
   {
      if(m == 1)
         return img;
      BufferedImage newImg = new BufferedImage(tileWidth * m, tileHeight * m, BufferedImage.TYPE_INT_ARGB);
      int color;
      for(int x = 0; x < tileWidth; x++)
      for(int y = 0; y < tileHeight; y++)
      {
         color = img.getRGB(x, y);
         for(int xx = 0; xx < m; xx++)
         for(int yy = 0; yy < m; yy++)
         {
            newImg.setRGB((x * m) + xx, (y * m) + yy, color);
         }
      }
      return newImg;
   }
   
   public UnboundTile getUnboundTile(int tileIndex, int fgColor, int bgColor, int sm, boolean bgType)
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
   public UnboundTile getUnboundTile(int tileIndex, int fgColor, int sm)
   {
      return getUnboundTile(tileIndex, fgColor, TRANSPARENT, sm, UnboundTile.BOX_BACKGROUND);
   }
   
   public void setUnboundTile(UnboundTile tile)
   {
      BufferedImage img = getTile(tile.getIconIndex(), tile.getFGColor(), tile.getBGColor());
      img = magnify(img, tile.getSizeMultiplier());
      // if circle background, remove extra
      if(tile.getBGType() == UnboundTile.CIRCLE_BACKGROUND)
      {
         BufferedImage stencil = getTile(2, FG_COLOR, BG_COLOR);
         int m = tile.getSizeMultiplier();
         stencil = magnify(stencil, m);
         for(int x = 0; x < tileWidth * m; x++)
         for(int y = 0; y < tileHeight * m; y++)
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
      TilePalette tp = new TilePalette("RogueTilePanel/MyFont_16x16.png", 16, 16);
      BufferedImage bi = tp.getTile(1, 1, Color.BLUE.getRGB(), Color.BLACK.getRGB());
   }
}