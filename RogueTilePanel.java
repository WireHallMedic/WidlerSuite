package WidlerSuite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class RogueTilePanel extends JPanel implements ComponentListener, ActionListener, MouseListener, 
                                                      MouseMotionListener, WSConstants
{
	private TilePalette palette;
   private TileAnimationManager animationManager;
	private BufferedImage[][] imageArr;
   private int[][] bgColorArr;
   private int[][] fgColorArr;
   private int[][] tileIndexArr;
	private int columns;
	private int rows;
   private int sizeMultiplier;
   private int xInset;     // in pixels
   private int yInset;     // in pixels
   private int mouseLoc[];
   protected double screenShakeTilesX = 0.0;  // in tiles
   protected double screenShakeTilesY = 0.0;  // in tiles
   protected int screenShakeDuration = 0;     // in ticks
   protected int screenShakeOffsetX = 0;      // in pixels
   protected int screenShakeOffsetY = 0;      // in pixels
   protected boolean clearShake = false;      // clean up after done shaking
   protected double xScroll = 0.0;            // in tiles
   protected double yScroll = 0.0;            // in tiles
   private int oobTileIndex = 0x00;
   private int oobTileFG = Color.WHITE.getRGB();
   private int oobTileBG = Color.BLACK.getRGB();
   private BufferedImage oobTile;
   
   public void setSizeMultiplier(int sm){sizeMultiplier = sm; generateAll();}
   public void setScroll(double x, double y){xScroll = x; yScroll = y;}

	public TilePalette getPalette(){return palette;}
	public int columns(){return columns;}
	public int rows(){return rows;}
   public int getSizeMultiplier(){return sizeMultiplier;}
   public BufferedImage getOOBTile(){return oobTile;}
   public int mouseColumn(){return mouseLoc[0];}
   public int mouseRow(){return mouseLoc[1];}
   public double getXScroll(){return xScroll;}
   public double getYScroll(){return yScroll;}
   
   public RogueTilePanel(int w, int h, TilePalette p)
   {
      super();
      palette = p;
      animationManager = new TileAnimationManager(this);
      columns = w;
      rows = h;
      sizeMultiplier = 1;
      imageArr = new BufferedImage[w][h];
      bgColorArr = new int[w][h];
      fgColorArr = new int[w][h];
      tileIndexArr = new int[w][h];
      mouseLoc = new int[2];
      for(int x = 0; x < w; x++)
      for(int y = 0; y < h; y++)
      {
         imageArr[x][y] = null;
         bgColorArr[x][y] = Color.BLACK.getRGB();
         fgColorArr[x][y] = Color.WHITE.getRGB();
         generateTile(x, y);
      }
      generateOOBTile();
      addMouseListener(this);
      addMouseMotionListener(this);
      addComponentListener(this);
   }
   
   // checks if the passed tile location is in the display bounds
   public boolean isInBounds(Coord loc){return isInBounds(loc.x, loc.y);}
   public boolean isInBounds(int x, int y)
   {
      if(x >= 0 && y >= 0 && x < columns() && y < rows())
         return true;
      return false;
   }   
   
   // getters
   //////////////////////////////////////////////////////////////////
   
   // get foreground color
   public int getFGColor(Coord c){return getFGColor(c.x, c.y);}
   public int getFGColor(int x, int y)
   {
      if(!isInBounds(x, y))
         return oobTileFG;
      return fgColorArr[x][y];
   }
   
   // get background color
   public int getBGColor(Coord c){return getBGColor(c.x, c.y);}
   public int getBGColor(int x, int y)
   {
      if(!isInBounds(x, y))
         return oobTileBG;
      return bgColorArr[x][y];
   }
   
   // get icon index
   public int getIcon(Coord c){return getIcon(c.x, c.y);}
   public int getIcon(int x, int y)
   {
      if(!isInBounds(x, y))
         return oobTileIndex;
      return tileIndexArr[x][y];
   }
   
   // checks if the screen is currently shaking
   public boolean isShaking()
   {
      return screenShakeDuration > 0;
   }

   
   // setters
   //////////////////////////////////////////////////////////////////
   
   // set all values of a tile location, using RGB values
   public void setTile(Coord c, int tileIndex, int fg, int bg){setTile(c.x, c.y, tileIndex, fg, bg);}
   public void setTile(int x, int y, int tileIndex, int fg, int bg)
   {
      if(!isInBounds(x, y))
         return;
      fgColorArr[x][y] = fg;
      bgColorArr[x][y] = bg;
      tileIndexArr[x][y] = tileIndex;
      generateTile(x, y);
   }
   
   // set all values of a tile location, using colors
   public void setTile(Coord c, int tileIndex, Color fg, Color bg){setTile(c.x, c.y, tileIndex, fg, bg);}
   public void setTile(int x, int y, int tileIndex, Color fg, Color bg)
   {
      setTile(x, y, tileIndex, fg.getRGB(), bg.getRGB());
   }
   
   // set foreground color of a tile
   public void setFGColor(Coord c, int fg){setFGColor(c.x, c.y, fg);}
   public void setFGColor(int x, int y, int fg)
   {
      if(!isInBounds(x, y))
         return;
      fgColorArr[x][y] = fg;
      generateTile(x, y);
   }
   
   // set background color of a tile
   public void setBGColor(Coord c, int bg){setBGColor(c.x, c.y, bg);}
   public void setBGColor(int x, int y, int bg)
   {
      if(!isInBounds(x, y))
         return;
      bgColorArr[x][y] = bg;
      generateTile(x, y);
   }
   
   // set icon of a tile
   public void setIcon(Coord c, int tileIndex){setIcon(c.x, c.y, tileIndex);}
   public void setIcon(int x, int y, int tileIndex)
   {
      if(!isInBounds(x, y))
         return;
      tileIndexArr[x][y] = tileIndex;
      generateTile(x, y);
   }
   
   // set all tiles to a particlular set of values
   public void setAll(int i, int fg, int bg)
   {
      for(int x = 0; x < columns(); x++)
      for(int y = 0; y < rows(); y++)
      {
         setTile(x, y, i, fg, bg);
      }
   }
   
   // set the out-of-bounds tile
   public void setOOBTile(int index, int fg, int bg)
   {
      oobTileIndex = index;
      oobTileFG = fg;
      oobTileBG = bg;
   }
   public void setOOBTile(int index, Color fg, Color bg){setOOBTile(index, fg.getRGB(), bg.getRGB());}
   
   // writes the string in a box, without setting colors
   public void write(Coord loc, String s, Coord box){write(loc.x, loc.y, s, box.x, box.y);}
   public void write(int x, int y, String s, int w, int h)
   {
      write(x, y, s, -1, -1, w, h);
   }
   
   // write the string in a box, with the passed foreground and background colors
   public void write(Coord loc, String s, int fgColor, int bgColor, Coord box){write(loc.x, loc.y, s, box.x, box.y);}
   public void write(int x, int y, String s, int fgColor, int bgColor, int w, int h)
   {
      int xLoc = 0; 
      int yLoc = 0;
      char[][] charArr = new char[w][h];
      String[] stringArr = s.split(" ");
      Vector<String> stringVect = new Vector<String>();
      // initialize array just in case tile 0x00 isn't blank
      for(int xx = 0; xx < w; xx++)
      for(int yy = 0; yy < h; yy++)
         charArr[xx][yy] = ' ';
      // copy over to vector, breaking up words that are too long
      for(String str : stringArr)
      {
         if(str.length() <= w)
            stringVect.add(str);
         else
         {
            while(str.length() > 0)
            {
               int cutPoint = Math.min(str.length(), w);
               stringVect.add(str.substring(0, cutPoint));
               str = str.substring(cutPoint);
            }
         }
      }
      // copy the characters to the character array
      for(String str : stringVect)
      {
         // carriage return if needed
         if(w - xLoc < str.length())
         {
            xLoc = 0;
            yLoc++;
            if(yLoc >= h)
               break;
         }
         // set the characters
         for(int j = 0; j < str.length(); j++)
         {
            if(str.charAt(j) == '\n')
            {
               xLoc = 0;
               yLoc++;
            }
            else
            {
               charArr[xLoc][yLoc] = str.charAt(j);
               xLoc++;
            }
         }
         // space
         xLoc++;
      }
      // actually set the tiles
      for(int xx = 0; xx < w; xx++)
      for(int yy = 0; yy < h; yy++)
      {
         if(bgColor == -1 || fgColor == -1)
            setIcon(x + xx, y + yy, charArr[xx][yy]);
         else
            setTile(x + xx, y + yy, charArr[xx][yy], fgColor, bgColor);
      }
   }
   
   // set the background and foreground colors of one or all instances of a particular 
   // word in the passed area
   public void setWordColors(String word, int fgColor, int bgColor, int x, int y, 
                                int w, int h, boolean findAll)
   {
      Coord loc = new Coord(x, y);
      Vector<Coord> targetLoc = new Vector<Coord>();
      boolean continueF = true;
      while(continueF)
      {
         // found next char
         if(tileIndexArr[loc.x][loc.y] == (int)word.charAt(targetLoc.size()))
         {
            targetLoc.add(new Coord(loc));
            // word complete?
            if(targetLoc.size() == word.length())
            {
               // set colors
               for(int i = 0; i < targetLoc.size(); i++)
               {
                  setTile(targetLoc.elementAt(i), (int)word.charAt(i), fgColor, bgColor);
               }
               // after setting colors, either reset up or return
               if(findAll)
               {
                  targetLoc = new Vector<Coord>();
               }
               else
                  continueF = false;;
            }
         }
         // didn't find next char
         else
         {
            targetLoc = new Vector<Coord>();
         }
         if(continueF)
            continueF = incrementLocForWordColors(loc, x, y, w, h);
      }
   }
   public void setWordColors(String word, int fgColor, int bgColor, Coord origin, 
                                Coord size, boolean findAll)
   {
      setWordColors(word, fgColor, bgColor, origin.x, origin.y, size.x, size.y, findAll);
   }
   
   // private function for setWordColors
   private boolean incrementLocForWordColors(Coord loc, int x, int y, int w, int h)
   {
      loc.x++;
      if(loc.x == x + w)
      {
         loc.x = x;
         loc.y++;
      }
      if(loc.y == y + h)
         return false;
      return true;
   }
   
   // set screen shake. Overwrites any existing screen shake
   public void setScreenShake(double radius, int duration){setScreenShake(radius, radius, duration);}
   public void setScreenShake(double radiusX, double radiusY, int duration)
   {
      screenShakeTilesX = radiusX;
      screenShakeTilesY = radiusY;
      screenShakeDuration = duration;
   }

   
   // private internal method to actually create the image
   private void generateTile(int x, int y)
   {
      BufferedImage img = null;
      img = palette.getTile(tileIndexArr[x][y], fgColorArr[x][y], bgColorArr[x][y]);
      if(sizeMultiplier != 1)
         img = palette.magnify(img, sizeMultiplier);
      imageArr[x][y] = img;
   }

   
   // private internal method to create the out-of-bounds tile
   private void generateOOBTile()
   {
      BufferedImage img = null;
      img = palette.getTile(oobTileIndex, oobTileFG, oobTileBG);
      if(sizeMultiplier != 1)
         img = palette.magnify(img, sizeMultiplier);
      oobTile = img;
   }
   
   // private internal method to trigger all tiles to generate
   private void generateAll()
   {
      for(int x = 0; x < columns; x++)
      for(int y = 0; y < rows; y++)
         generateTile(x, y);
   }
   
   // return the current tile width in pixels
   public int getTileWidth()
   {
      return palette.getTileWidth() * sizeMultiplier;
   }
   
   // return the current tile height in pixels
   public int getTileHeight()
   {
      return palette.getTileHeight() * sizeMultiplier;
   }
   
   // center the tile board in the Container containing this (later modified by
   // scrolling and shaking, if applicapable
   public void center()
   {
      xInset = (super.getWidth() - (columns * getTileWidth())) / 2;
      yInset = (super.getHeight() - (rows * getTileHeight())) / 2;
   }
   
   // UnboundTile stuff
   //////////////////////////////////////////////////////
   
   // add a locking unbound tile
   public void addLocking(UnboundTile us)
   {
      animationManager.addLocking(us);
   }
   
   // add a non-locking unbound tile
   public void addNonlocking(UnboundTile us){add(us);}
   public void add(UnboundTile us)
   {
      animationManager.addNonlocking(us);
   }
   
   // remove an unbound tile
   public void remove(UnboundTile us)
   {
      animationManager.remove(us);
   }
   
   // remove a movement script
   public void remove(MovementScript ms)
   {
      animationManager.remove(ms);
   }
   
   // add a movement script
   public void add(MovementScript scr)
   {
      animationManager.addScript(scr);
   }
   
   // check if external processes should be delayed while waiting for animation to complete
   public boolean isAnimationLocked()
   {
      return animationManager.isLocked();
   }
   
   // adjust display based on screen shake
   protected void shake()
   {
      int shakeMult = Math.min(getTileWidth(), getTileHeight());
      int screenShakePixelsX = (int)(shakeMult * screenShakeTilesX);
      int screenShakePixelsY = (int)(shakeMult * screenShakeTilesY);
      screenShakeOffsetX = WSTools.random(screenShakePixelsX + screenShakePixelsX + 1) - screenShakePixelsX;
      screenShakeOffsetY = WSTools.random(screenShakePixelsY + screenShakePixelsY + 1) - screenShakePixelsY;
      clearShake = true;
   }
   
   // clears all the shaking variables
   protected void clearShakeVariables()
   {
      screenShakeOffsetX = 0;
      screenShakeOffsetY = 0;
      clearShake = false;
   }
   
   // clear all the unbound tile lists
   public void clearUnboundTiles()
   {
      animationManager.clear();
   }
   
   // overridden paint method
   @Override
   public void paint(Graphics g)
   {
      super.paint(g);
      if(palette == null)
         return;
      
      Graphics2D g2d = (Graphics2D)g;
      int w = palette.getTileWidth() * sizeMultiplier;
      int h = palette.getTileHeight() * sizeMultiplier;
      int totalXInset = xInset + screenShakeOffsetX + (int)(xScroll * w);
      int totalYInset = yInset + screenShakeOffsetY + (int)(yScroll * h);
      // draw main board
      for(int x = 0; x < imageArr.length; x++)
      for(int y = 0; y < imageArr[0].length; y++)
      {
         if(imageArr[x][y] != null)
         {
            g2d.drawImage(imageArr[x][y], (x * w) + totalXInset, (y * h) + totalYInset, this);
         }
      }
      
      // draw unbound tiles
      drawUnboundTiles(g2d, animationManager.getNonlockList(), w, h);
      drawUnboundTiles(g2d, animationManager.getLockList(), w, h);
   }
   
   // draw a list of unbound tiles
   private void drawUnboundTiles(Graphics2D g2d, Vector<UnboundTile> tileList, int w, int h)
   {
      int totalXInset = xInset + screenShakeOffsetX + (int)(xScroll * w);
      int totalYInset = yInset + screenShakeOffsetY + (int)(yScroll * h);
      
      for(int i = 0; i < tileList.size(); i++)
      {
         UnboundTile ut = tileList.elementAt(i);
         BufferedImage img = ut.getImage();
         int x = ut.getXLoc() * w;
         int y = ut.getYLoc() * h;
         x += (int)(ut.getXOffset() * w);
         y += (int)(ut.getYOffset() * h);
         g2d.drawImage(img, x + totalXInset, y + totalYInset, this);
      }
   }
   
   // kicked by timer
   public void actionPerformed(ActionEvent ae)
   {
      animationManager.actionPerformed(ae);
         
      // update screen shake
      if(isShaking())
      {
         shake();
         screenShakeDuration--;
      }
      else if(clearShake)
      {
         clearShakeVariables();
      }
         
      this.repaint();
   }
   
   ///////////////////////////////////////////////////////////////////////
   // ComponentListener stuff
   public void componentHidden(ComponentEvent ce){}
   public void componentMoved(ComponentEvent ce){}
   public void componentShown(ComponentEvent ce){}
   
   // update metrics when resized
   public void componentResized(ComponentEvent ce)
   {
      center();
   }
   
   // update mouseLoc where needed. External listeners notified in the usual way.
   public void mouseMoved(MouseEvent me){updateMouseLoc(me);}
   public void mouseDragged(MouseEvent me){updateMouseLoc(me);}
   public void mouseEntered(MouseEvent me){updateMouseLoc(me);}
   public void mouseExited(MouseEvent me){mouseLoc[0] = -1; mouseLoc[1] = -1;}
   public void mousePressed(MouseEvent me){}
   public void mouseReleased(MouseEvent me){}
   public void mouseClicked(MouseEvent me){}
   
   // store which tile the mouse is in, or {-1, -1} if it is out of the tiled area
   protected void updateMouseLoc(MouseEvent me)
   {
      int colWidth = palette.getTileWidth() * sizeMultiplier;
      int rowHeight = palette.getTileHeight() * sizeMultiplier;
      // avoid a div0 exception
      if(rowHeight == 0 || colWidth == 0)
      {
         mouseLoc[0] = -1;
         mouseLoc[1] = -1;
         return;
      }
      
      // ignore screen shake, but account for scrolling
      int xLoc = me.getX() - xInset;
      int yLoc = me.getY() - yInset;
      boolean oob = false;
      /*
      // adjust odd rows if in hex mode
      if(isInsetRow(yLoc / rowHeight))
      {
         xLoc -= colWidth / 2;
      }
      */
      // check if out left or upper boundaries
      if(xLoc < 0 || yLoc < 0)
         oob = true;
      
      // note that the function variables go from being in pixels to being in tiles here
      xLoc = xLoc / colWidth;
      yLoc = yLoc / rowHeight;
      
      // check if out of right or bottom boundaries
      if(xLoc >= columns() || yLoc >= rows())
         oob = true;
      /*
      if(isUndrawnHexTile(xLoc, yLoc))
         oob = true;
      */
      if(oob)
      {
         xLoc = -1;
         yLoc = -1;
      }
      mouseLoc[0] = xLoc;
      mouseLoc[1] = yLoc;
   }
   
   // main provided for testing
   public static void main(String[] args)
   {
      JFrame frame = new JFrame();
      frame.setSize(800, 800);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      TilePalette palette = new TilePalette("WidlerSuite/WSFont_16x16.png", 16, 16);
      RogueTilePanel rtp = new RogueTilePanel(20, 20, palette);
      rtp.setSizeMultiplier(2);
   
      for(int x = 0; x < 16; x++)
      for(int y = 0; y < 16; y++)
         rtp.setTile(x, y, x + (y * 16), Color.CYAN, Color.BLACK);
      
      rtp.setTile(0, 17, 'H', Color.WHITE, Color.BLACK);
      rtp.setTile(1, 17, 'e', Color.WHITE, Color.BLACK);
      rtp.setTile(2, 17, 'l', Color.WHITE, Color.BLACK);
      rtp.setTile(3, 17, 'l', Color.WHITE, Color.BLACK);
      rtp.setTile(4, 17, 'o', Color.WHITE, Color.BLACK);
      rtp.setTile(5, 17, ' ', Color.WHITE, Color.BLACK);
      rtp.setTile(6, 17, 'w', Color.WHITE, Color.BLACK);
      rtp.setTile(7, 17, 'o', Color.WHITE, Color.BLACK);
      rtp.setTile(8, 17, 'r', Color.WHITE, Color.BLACK);
      rtp.setTile(9, 17, 'l', Color.WHITE, Color.BLACK);
      rtp.setTile(10, 17, 'd', Color.WHITE, Color.BLACK);
      rtp.setTile(11, 17, '!', Color.WHITE, Color.BLACK);
      
      rtp.write(5, 18, "Writing some stuff here in a space.", 10, 2);
      rtp.write(5, 5, "Writing\nin a six by five area with a bunch of words of varying length.", 6, 5);
      for(int x = 5; x < 11; x++)
      for(int y = 5; y < 10; y++)
         rtp.setBGColor(x, y, Color.BLUE.getRGB());
      
      UnboundTile ut = rtp.palette.getUnboundTile((int)'@', Color.RED.getRGB(), Color.GRAY.getRGB(), 2, UnboundTile.CIRCLE_BACKGROUND);
      ut.setAffectedByAge(false);
      MovementScript ms = new MovementScript(ut);
      double spd = 1.0 / 15;
      ms.setImpulse(30, spd, 0.0);
      ms.setImpulse(45, 0.0 - spd, 0.0);
      ms.setNonlocksTargetOnEnd(true);
      rtp.add(ut);
      rtp.add(ms);
      
      UnboundTile orbit1 = rtp.palette.getUnboundTile((int)'X', Color.RED.getRGB(), Color.GRAY.getRGB(), 2, UnboundTile.CIRCLE_BACKGROUND);
      MovementScript script = new MovementScript(orbit1);
      script.setLoops(true);
      orbit1.setSpeed(-.2, 0.0);
      orbit1.setLoc(-1, -1);
      for(int i = 0; i < 20; i++)
      {
         script.setImpulse(i, .01, -.01);
         script.setImpulse(i + 20, .01, .01);
         script.setImpulse(i + 40, -.01, .01);
         script.setImpulse(i + 60, -.01, -.01);
      }
      orbit1.setAnchorTile(ut);
      orbit1.setLoc(5, 5);
      orbit1.setAffectedByAge(false);
      rtp.add(script);
      rtp.addNonlocking(orbit1);
      
      UnboundTile orbit2 = rtp.palette.getUnboundTile((int)'+', Color.RED.getRGB(), Color.GRAY.getRGB(), 2, UnboundTile.CIRCLE_BACKGROUND);
      script = new MovementScript(orbit2);
      script.setLoops(true);
      orbit2.setSpeed(0.0, 0.0);
      for(int i = 0; i < 20; i++)
      {
         script.setImpulse(i,       .01, 0.0);
         script.setImpulse(i + 20, -.01, 0.0);
         script.setImpulse(i + 40, -.01, 0.0);
         script.setImpulse(i + 60,  .01, 0.0);
      }
      orbit2.setAnchorTile(orbit1);
      orbit2.setAffectedByAge(false);
      orbit2.setLoc(-2, 0);
      rtp.add(script);
      rtp.addNonlocking(orbit2);
      
      UnboundTile orbit3 = rtp.palette.getUnboundTile((int)'+', Color.RED.getRGB(), Color.GRAY.getRGB(), 2, UnboundTile.CIRCLE_BACKGROUND);
      script = new MovementScript(orbit3);
      script.setLoops(true);
      orbit3.setSpeed(0.0, 0.0);
      for(int i = 0; i < 20; i++)
      {
         script.setImpulse(i,      0.0, -.01);
         script.setImpulse(i + 20, 0.0,  .01);
         script.setImpulse(i + 40, 0.0,  .01);
         script.setImpulse(i + 60, 0.0, -.01);
      }
      orbit3.setAnchorTile(orbit2);
      orbit3.setAffectedByAge(false);
      orbit3.setLoc(0, 2);
      rtp.add(script);
      rtp.addNonlocking(orbit3);
      
      int[][] borderArr = new int[20][4];
      for(int x = 0; x < 20; x++)
      for(int y = 0; y < 4; y++)
      {
         if(x == 0  || x == 19)
            borderArr[x][y] = 1;
         if(y == 0 || y == 3)
            borderArr[x][y] = 2;
      }
      borderArr = BorderBuilder.getBorderTiles(borderArr);
      for(int x = 0; x < 20; x++)
      for(int y = 0; y < 4; y++)
      {
         if(borderArr[x][y] != 0)
         {
            rtp.setIcon(x, y + 16, borderArr[x][y]);
         }
      }
      
      rtp.setTile(17, 0, WSConstants.MY_FONT_BOLT, Color.BLACK, Color.RED);
      rtp.setTile(17, 1, WSConstants.MY_FONT_BIG_CIRCLE, Color.BLACK, Color.RED);
      rtp.setTile(17, 2, WSConstants.MY_FONT_NOT_EQUAL, Color.BLACK, Color.RED);
      rtp.setTile(17, 3, WSConstants.MY_FONT_DIE_ONE, Color.BLACK, Color.RED);
      rtp.setTile(17, 4, WSConstants.MY_FONT_DIE_TWO, Color.BLACK, Color.RED);
      rtp.setTile(17, 5, WSConstants.MY_FONT_DIE_THREE, Color.BLACK, Color.RED);
      rtp.setTile(17, 6, WSConstants.MY_FONT_DIE_FOUR, Color.BLACK, Color.RED);
      rtp.setTile(17, 7, WSConstants.MY_FONT_DIE_FIVE, Color.BLACK, Color.RED);
      rtp.setTile(17, 8, WSConstants.MY_FONT_DIE_SIX, Color.BLACK, Color.RED);
      
      ut.setXLoc(3);
      ut.setYLoc(3);
      frame.add(rtp);
      rtp.setBackground(Color.BLACK);
      frame.setVisible(true);
      rtp.center();
      rtp.repaint();
      rtp.setScreenShake(.2, 30);
      
      rtp.setWordColors("Writing", Color.ORANGE.getRGB(), Color.BLUE.getRGB(), 0, 0, 20, 20, true);
      rtp.setWordColors("Writ", Color.BLUE.getRGB(), Color.ORANGE.getRGB(), 0, 0, 20, 20, false);
      
      javax.swing.Timer timer = new javax.swing.Timer(1000 / 60, rtp);
      timer.start();
   }
}