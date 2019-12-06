/*******************************************************************************************

A class for a curses implementation. Everything is pretty much handled internally,
except for being hooked up to a timer (which is passed on to the unbound string stuff,
as long as this panel is visible).
Note getMouseColumn() and getMouseRow() are intended to be called when external mouseListeners
and mouseMotionListeners recieve events, but whatever floats your goat.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/
package WidlerSuite;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class RoguePanel extends JPanel implements ComponentListener, ActionListener, MouseListener, 
                                                  MouseMotionListener, WSConstants
{
   protected Color[][] fgColor = new Color[1][1];
   protected Color[][] bgColor = new Color[1][1];
   protected char[][] ch = new char[1][1];
   protected TileSet tileSet = new TileSet("curses_16x16.png");
   protected int colWidth = 0;       // in pixels
   protected int rowHeight = 0;      // in pixels
   protected int arrayXInset = 0;
   protected int arrayYInset = 0;
   protected int oddRowInset = 0;
   protected AnimationManager animationManager;
   protected int[] cornerCell = {0, 0};
   protected Vector<MouseListener> mouseListenerList;
   protected Vector<MouseMotionListener> mouseMotionListenerList;
   protected int[] mouseLoc = {-1, -1};
   protected int displayMode = RECT_MODE;
   protected boolean showBorders = false;
   protected Color borderColor = Color.WHITE;
   protected Color oobBGColor = Color.BLACK;
   protected Color oobFGColor = Color.WHITE;
   protected String oobChar = ' ';
   protected double screenShakeTilesX = 0.0;  // in tiles
   protected double screenShakeTilesY = 0.0;  // in tiles
   protected int screenShakeDuration = 0;     // in ticks
   protected int screenShakeOffsetX = 0;      // in pixels
   protected int screenShakeOffsetY = 0;      // in pixels
   protected boolean clearShake = false;      // clean up after done shaking
   protected double xScroll = 0.0;
   protected double yScroll = 0.0;
   
   public Color getTileBorderColor(){return borderColor;}
   public Color getOOBBGColor(){return oobBGColor;}
   public Color getOOBFGColor(){return oobFGColor;}
   public String getOOBChar(){return oobChar;}
   public double getXScroll(){return xScroll;}
   public double getYScroll(){return yScroll;}
   
   public int columns(){return str.length;}
   public int rows(){return str[0].length;}
   public int mouseColumn(){return mouseLoc[0];}
   public int mouseRow(){return mouseLoc[1];}
   
   public void showTileBorders(boolean sb){showBorders = sb;}
   public void setTileBorderColor(Color bc){borderColor = bc;}
   public void setOOBBGColor(Color c){oobBGColor = c;}
   public void setOOBFGColor(Color c){oobFGColor = c;}
   public void setOOBChar(Char c){oobChar = c;}
   public void setScroll(double x, double y){xScroll = x; yScroll = y;}
   
   // constructor
   public RoguePanel()
   {
      this(15, 15);
   }
   
   // size-based constructor
   public RoguePanel(int w, int h)
   {
      super();
      animationManager = new AnimationManager(this);
      mouseListenerList = new Vector<MouseListener>();
      mouseMotionListenerList = new Vector<MouseMotionListener>();
      addMouseListener(this);
      addMouseMotionListener(this);
      addComponentListener(this);
      setColumnsAndRows(w, h);
      setBackground(Color.BLACK);
   }
   
   // checks if the passed tile location is in the display bounds
   public boolean isInBounds(Coord loc){return isInBounds(loc.x, loc.y);}
   public boolean isInBounds(int x, int y)
   {
      if(x >= 0 && y >= 0 && x < columns() && y < rows())
         return true;
      return false;
   }
   
   // checks if the screen is currently shaking
   public boolean isShaking()
   {
      return screenShakeDuration > 0;
   }
   
   // test function
   public void randomize()
   {
      for(int x = 0; x < columns(); x++)
      for(int y = 0; y < rows(); y++)
      {
         setBGColor(x, y, new Color((float)WSTools.random(), (float)WSTools.random(), (float)WSTools.random(), (float)1.0));
         int charVal = (int)'a';
         if(WSTools.random() > .5)
            charVal = (int)'A';
         charVal += (int)(WSTools.random() * 26);
         setString(x, y, (char)charVal);
      }
   }
   
   //////////////////////////////////////////////////////
   // public setters
   
   // sets the number of columns and rows
   public void setColumnsAndRows(Coord size){setColumnsAndRows(size.x, size.y);}
   public void setColumnsAndRows(int x, int y)
   {
      fgColor = new Color[x][y];
      bgColor = new Color[x][y];
      ch = new char[x][y];
      
      setSizes();
      
      for(int c = 0; c < x; c++)
      for(int r = 0; r < y; r++)
      {
         fgColor[c][r] = Color.WHITE;
         bgColor[c][r] = Color.BLACK;
         ch[c][r] = ' ';
      }
   }
   
   // set foreground color of a specific tile
   public void setFGColor(Coord loc, Color c){setFGColor(loc.x, loc.y, c);}
   public void setFGColor(int x, int y, Color c)
   {
      if(isInBounds(x, y))
         fgColor[x][y] = c;
   }
   
   // set background color of a specific tile
   public void setBGColor(Coord loc, Color c){setBGColor(loc.x, loc.y, c);}
   public void setBGColor(int x, int y, Color c)
   {
      if(isInBounds(x, y))
         bgColor[x][y] = c;
   }
   
   // set the string (generally one character) of a specific tile
   public void setString(Coord loc, String s){setString(loc.x, loc.y, s);}
   public void setString(int x, int y, String s)
   {
      if(isInBounds(x, y))
      {
         str[x][y] = s;
         setStrInset(x, y);
      }
   }
   
   // set a tile all at once
   public void setTile(Coord loc, String s, Color fg, Color bg){setTile(loc.x, loc.y, s, fg, bg);}
   public void setTile(int x, int y, String s, Color fg, Color bg)
   {
      if(isInBounds(x, y))
      {
         fgColor[x][y] = fg;
         bgColor[x][y] = bg;
         str[x][y] = s;
         setStrInset(x, y);
      }
   }
   
   // set a tile, excluding the background color, all at once
   public void setTile(Coord loc, String s, Color fg){setTile(loc.x, loc.y, s, fg);}
   public void setTile(int x, int y, String s, Color fg)
   {
      if(isInBounds(x, y))
      {
         fgColor[x][y] = fg;
         str[x][y] = s;
         setStrInset(x, y);
      }
   }
   
   // set the display to either rect (orthoginal) or hex (diagonal)
   public void setDisplayMode(int dm)
   {
      if(dm != displayMode)
      {
         if(dm == HEX_MODE)
         {
            displayMode = HEX_MODE;
         }
         else
         {
            displayMode = RECT_MODE;
         }
         setSizes();
         setFont();
      }
   }
   
   // set screen shake. Overwrites any existing screen shake
   public void setScreenShake(double radius, int duration){setScreenShake(radius, radius, duration);}
   public void setScreenShake(double radiusX, double radiusY, int duration)
   {
      screenShakeTilesX = radiusX;
      screenShakeTilesY = radiusY;
      screenShakeDuration = duration;
   }
   
   // set a rectangular area of the background a single color
   public void setBGBox(Coord loc, Coord size, Color c){setBGBox(loc.x, loc.y, size.x, size.y, c);}
   public void setBGBox(int x, int y, int w, int h, Color c)
   {
      x = WSTools.minMax(0, x, columns() - 1);
      y = WSTools.minMax(0, y, rows() - 1);
      w = WSTools.minMax(0, w, columns() - x);
      h = WSTools.minMax(0, h, rows() - y);
      for(int xPos = x; xPos < x + w; xPos++)
      for(int yPos = y; yPos < y + h; yPos++)
      {
         setBGColor(xPos, yPos, c);
      }
   }
   
   // set a rectangular area of the foreground a single color
   public void setFGBox(Coord loc, Coord size, Color c){setFGBox(loc.x, loc.y, size.x, size.y, c);}
   public void setFGBox(int x, int y, int w, int h, Color c)
   {
      x = WSTools.minMax(0, x, columns() - 1);
      y = WSTools.minMax(0, y, rows() - 1);
      w = WSTools.minMax(0, w, columns() - x);
      h = WSTools.minMax(0, h, rows() - y);
      for(int xPos = x; xPos < x + w; xPos++)
      for(int yPos = y; yPos < y + h; yPos++)
      {
         setFGColor(xPos, yPos, c);
      }
   }
   
   // set a rectangular area of the foreground to a single string and color
   public void setFGBox(Coord loc, Coord size, String s, Color c){setFGBox(loc.x, loc.y, size.x, size.y, s, c);}
   public void setFGBox(int x, int y, int w, int h, String s, Color c)
   {
      x = WSTools.minMax(0, x, columns() - 1);
      y = WSTools.minMax(0, y, rows() - 1);
      w = WSTools.minMax(0, w, columns() - x);
      h = WSTools.minMax(0, h, rows() - y);
      for(int xPos = x; xPos < x + w; xPos++)
      for(int yPos = y; yPos < y + h; yPos++)
      {
         setTile(xPos, yPos, s, c);
      }
   }
      
   // write a string in sequential tiles
   public void write(Coord loc, String str, Color c){write(loc.x, loc.y, str, c);}
   public void write(int x, int y, String str, Color c)
   {
      for(int i = 0; i < str.length(); i++)
         setTile(x + i, y, str.substring(i, i + 1), c);
   }
   
   // write a string inside an area, with word wrapping
   public void writeBox(Coord loc, Coord box, String str, Color c){writeBox(loc.x, loc.y, box.x, box.y, str, c);}
   public void writeBox(int xStart, int yStart, int w, int h, String str, Color c)
   {
      // break the string up based on whitespace
      String[] strArr = str.split("\\s");
      Vector<String> strVect = new Vector<String>(Arrays.asList(strArr));
      xStart = WSTools.minMax(0, xStart, columns());
      yStart = WSTools.minMax(0, yStart, rows());
      w = WSTools.minMax(0, w, columns() - xStart);
      h = WSTools.minMax(0, h, rows() - yStart);
      int returnPos = xStart + w;
      int bottom = yStart + h;
      int x = xStart;
      int y = yStart;
      
      // clear the box
      setFGBox(xStart, yStart, w, h, " ", c);
      
      // individual words that are too long are broken up
      int curIndex = 0;
      while(curIndex < strVect.size())
      {
         if(strVect.elementAt(curIndex).length() <= w)
            curIndex++;
         else
         {
            String substr = strVect.elementAt(curIndex);
            strVect.insertElementAt(substr.substring(w), curIndex + 1);
            strVect.set(curIndex, substr.substring(0, w));
         }
      }
      
      // insert the spaces as their own words
      Vector<String> newStrVect = new Vector<String>();
      for(int i = 0; i < strVect.size(); i++)
      {
         newStrVect.add(strVect.elementAt(i));
         newStrVect.add(" ");
      }
      strVect = newStrVect;
      
      // write each word, doing carriage returns as necessary
      for(int i = 0; i < strVect.size(); i++)
      {
         // new line if this word would overrun
         if(x + strVect.elementAt(i).length() > returnPos)
         {
            x = xStart;
            y++;
            // ignore spaces that would be at the start of a line
            if(strVect.elementAt(i).equals(" "))
               i++;
            // exit if at bottom of box
            if(y >= bottom || i >= strVect.size())
               return;
         }
         write(x, y, strVect.elementAt(i), c);
         x += strVect.elementAt(i).length();
      }
   }

   
   //////////////////////////////////////////////////////////
   // public getters
   
   // returns the foreground color of the passed location
   public Color getFGColor(Coord loc){return getFGColor(loc.x, loc.y);}
   public Color getFGColor(int x, int y)
   {
      if(isInBounds(x, y))
         return fgColor[x][y];
      return oobFGColor;
   }
   
   // returns the background color of the passed location
   public Color getBGColor(Coord loc){return getBGColor(loc.x, loc.y);}
   public Color getBGColor(int x, int y)
   {
      if(isInBounds(x, y))
         return bgColor[x][y];
      return oobBGColor;
   }
   
   // returns the string of the passed location
   public String getString(Coord loc){return getString(loc.x, loc.y);}
   public String getString(int x, int y)
   {
      if(isInBounds(x, y))
         return str[x][y];
      return oobString;
   }   
   
   // protected getters and setters
   //////////////////////////////////////////////////////////
   
   // determines if a row should be indented while in hex mode
   protected boolean isInsetRow(int rowIndex)
   {
      return displayMode == HEX_MODE && (rowIndex + cornerCell[1]) % 2 == 1;
   }
   
   // set internal values based on panel size and array size
   protected void setSizes()
   {
      colWidth = this.getWidth() / columns();
      rowHeight = this.getHeight() / rows();
      if(displayMode == HEX_MODE)
      {
         colWidth = (this.getWidth() * 2) / ((columns() * 2) + 1);
      }
      arrayXInset = (this.getWidth() - (colWidth * columns())) / 2;
      arrayYInset = (this.getHeight() - (rowHeight * rows())) / 2;
      if(displayMode == HEX_MODE)
      {
         arrayXInset -= (colWidth / 4);
         oddRowInset = colWidth / 2;
      }
      else // RECT_MODE
      {
         oddRowInset = 0;
      }
   }
   
   // font can only be set internally; externally, use setFontName(String)
   protected void setFont()
   {
      if(tightFontBorders)
      {
         int newPointSize = 1;
         while(FontLoader.getCharHeight(fontName, newPointSize) < colWidth &&
               FontLoader.getCharWidth(fontName, newPointSize) < rowHeight)
            newPointSize++;
         font = new Font(fontName, Font.PLAIN, newPointSize);
      }
      else
      {
         font = new Font(fontName, Font.PLAIN, (int)(rowHeight * textProportion));
      }
      fontMetrics = this.getFontMetrics(font); // so that we don't need a Graphics object later
      for(int x = 0; x < columns(); x++)
      for(int y = 0; y < rows(); y++)
      {
         setStrInset(x, y);
      }
   }
   
   // set the inset (in pixels) of a specific string
   protected void setStrInset(int x, int y)
   {
      // no error checking as this can only be called internally
      if(tightFontBorders)
         strXInset[x][y] = 0;
      else
         strXInset[x][y] = (colWidth - fontMetrics.stringWidth(getString(x, y))) / 2;
   }
   
   // set the vertical inset for tile strings
   protected void setStrYInset(Graphics2D g)
   {
      int stringHeight = (int)(rowHeight * textProportion * 3 / 4.0);
      if(tightFontBorders)
      {
         stringHeight = WSTools.roundToInt(g.getFontMetrics().getHeight());
      }
      strYInset = rowHeight - ((rowHeight - stringHeight) / 2);
   }
   
   // adjust display based on screen shake
   protected void shake()
   {
      int sizeMultiplier = Math.min(colWidth, rowHeight);
      int screenShakePixelsX = (int)(sizeMultiplier * screenShakeTilesX);
      int screenShakePixelsY = (int)(sizeMultiplier * screenShakeTilesY);
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
   
   ///////////////////////////////////////////////////////////////////////
   // ComponentListener stuff
   public void componentHidden(ComponentEvent ce){}
   public void componentMoved(ComponentEvent ce){}
   public void componentShown(ComponentEvent ce){}
   
   // update metrics when resized
   public void componentResized(ComponentEvent ce)
   {
      setSizes();
      setFont();
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
      // avoid a div0 exception
      if(rowHeight == 0 || colWidth == 0)
      {
         mouseLoc[0] = -1;
         mouseLoc[1] = -1;
         return;
      }
      
      // ignore screen shake, but account for scrolling
      int xLoc = me.getX() - arrayXInset - (int)(colWidth * xScroll);
      int yLoc = me.getY() - arrayYInset - (int)(rowHeight * yScroll);
      boolean oob = false;
      
      // adjust odd rows if in hex mode
      if(isInsetRow(yLoc / rowHeight))
      {
         xLoc -= colWidth / 2;
      }
      
      // check if out left or upper boundaries
      if(xLoc < 0 || yLoc < 0)
         oob = true;
      
      // note that the function variables go from being in pixels to being in tiles here
      xLoc = xLoc / colWidth;
      yLoc = yLoc / rowHeight;
      
      // check if out of right or bottom boundaries
      if(xLoc >= columns() || yLoc >= rows())
         oob = true;
      
      if(oob)
      {
         xLoc = -1;
         yLoc = -1;
      }
      mouseLoc[0] = xLoc;
      mouseLoc[1] = yLoc;
   }
   
   ///////////////////////////////////////////////////////////////////////
   // AnimationManager stuff
   
   // update unbound strings and repaint when kicked by timer
   public void actionPerformed(ActionEvent ae)
   {
      if(this.isVisible())
      {
         // update unbound strings
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
         
         // repaint screen
         this.repaint();
      }
   }
   
   // add a locking unbound string
   public void addLocking(UnboundString us)
   {
      animationManager.addLocking(us);
   }
   
   // add a non-locking unbound string
   public void addNonlocking(UnboundString us){add(us);}
   public void add(UnboundString us)
   {
      animationManager.addNonlocking(us);
   }
   
   // remove an unbound string
   public void remove(UnboundString us)
   {
      animationManager.remove(us);
   }
   
   // remove a movement script
   public void remove(MovementScript ms)
   {
      animationManager.remove(ms);
   }
   
   // add an movement script
   public void add(MovementScript scr)
   {
      animationManager.addScript(scr);
   }
   
   // check if external processes should be delayed while waiting for animation to complete
   public boolean isAnimationLocked()
   {
      return animationManager.isLocked();
   }
   
   // clear all the unbound string lists
   public void clearUnboundStrings()
   {
      animationManager.clear();
   }
   
   // set the corner cell, for properly displaying unbound strings
   public void setCornerCell(Coord loc){setCornerCell(loc.x, loc.y);}
   public void setCornerCell(int x, int y)
   {
      cornerCell[0] = x;
      cornerCell[1] = y;
   }
   
   // set the corner cell by passing the center cell
   public void setCenterCell(Coord loc){setCenterCell(loc.x, loc.y);}
   public void setCenterCell(int x, int y)
   {
      setCornerCell(x - (columns() / 2), y - (rows() / 2));
   }
   
   
   ///////////////////////////////////////////////////////////////////////
   
   // override the native paint method
   @Override
   public void paint(Graphics g)
   {
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;
      g2d.setFont(font);
      int xLoc;
      int yLoc;
      int baseXInset = screenShakeOffsetX + (int)(xScroll * colWidth);
      int baseYInset = screenShakeOffsetY + (int)(yScroll * rowHeight);
      
      setStrYInset(g2d);
      
      // draw each tile
      for(int x = 0; x < columns(); x++)
      for(int y = 0; y < rows(); y++)
      {
         xLoc = arrayXInset + (x * colWidth) + baseXInset;
         yLoc = arrayYInset + (y * rowHeight) + baseYInset;
         if(isInsetRow(y))
            xLoc += oddRowInset;
         // background
         g2d.setColor(bgColor[x][y]);
         g2d.fillRect(xLoc, yLoc, colWidth, rowHeight);
         
         // foreground
         g2d.setColor(fgColor[x][y]);
         g2d.drawString(str[x][y], xLoc + strXInset[x][y], yLoc + strYInset);
         
         // tile borders
         if(showBorders)
         {
            g2d.setColor(borderColor);
            g2d.drawRect(xLoc, yLoc, colWidth, rowHeight);
         }
      }
      
      // unbound strings
      drawUnboundStrings(g2d, animationManager.getLockList(), baseXInset, baseYInset);
      drawUnboundStrings(g2d, animationManager.getNonlockList(), baseXInset, baseYInset);
   }
   
   // draw unbound strings. These will be in front of the background and foreground.
   protected void drawUnboundStrings(Graphics2D g2d, Vector<UnboundString> usList, int baseXInset, int baseYInset)
   {
      int xTile;
      int yTile;
      int xLoc;
      int yLoc;
      int round = fontMetrics.getHeight() / 4;
      
      for(UnboundString us : usList)
      {
         // skip invisible unboundStrings
         if(us.isVisible() == false)
            continue;
            
         // unbound tiles drawn by different function
         if(us instanceof UnboundTile)
         {
            drawUnboundTile(g2d, (UnboundTile)us, baseXInset, baseYInset);
            continue;
         }
         
         xTile = us.getXLoc() - cornerCell[0];
         yTile = us.getYLoc() - cornerCell[1];
         // only draw stuff that's on screen
         if(isInBounds(xTile, yTile))
         {
            xLoc = (xTile * colWidth) + arrayXInset + (colWidth / 2) - (fontMetrics.stringWidth(us.getString()) / 2) + (int)(us.getXOffset() * colWidth);
            yLoc = (yTile * rowHeight) + arrayYInset + (rowHeight / 2) + (int)(us.getYOffset() * rowHeight);
            
            xLoc += baseXInset;
            yLoc += baseYInset;
            
            // note that as unbound strings do not rectify their position, this stays static over the life of the US
            if(isInsetRow(yTile))
               xLoc += colWidth / 2;
            
            // draw the box, if any
            if(us.hasBackgroundBox())
            {
               g2d.setColor(us.getBGColor());
               int bgBoxX = xLoc - (colWidth / 4);
               int bgBoxY = yLoc - rowHeight;
               int bgBoxW = fontMetrics.stringWidth(us.getString()) + (colWidth / 2);
               int bgBoxH = (rowHeight * 5) / 4;
               int cir = Math.max(bgBoxW, bgBoxH);
               switch(us.getBackgroundBoxType())
               {
                  case UnboundString.RECT          :  g2d.fillRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                      break;
                  case UnboundString.OVAL          :  g2d.fillOval(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                      break;
                  case UnboundString.CIRCLE        :  g2d.fillOval(bgBoxX, bgBoxY, cir, cir);
                                                      break;
                  case UnboundString.HEXAGON       :  g2d.fillPolygon(hexPointsX(bgBoxX, bgBoxW), hexPointsY(bgBoxY, bgBoxH), 6);
                                                      break;
                  case UnboundString.ROUNDED_RECT  :  g2d.fillRoundRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH, round, round);
                                                      break;
                  default                          :  break;
               }
               
               // draw the border, if it has one
               if(us.getBorder() != null)
               {
                  g2d.setColor(us.getBorder());
                  switch(us.getBackgroundBoxType())
                  {
                     case UnboundString.RECT          :  g2d.drawRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                         break;
                     case UnboundString.OVAL          :  g2d.drawOval(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                         break;
                     case UnboundString.CIRCLE        :  g2d.drawOval(bgBoxX, bgBoxY, cir, cir);
                                                         break;
                     case UnboundString.HEXAGON       :  g2d.drawPolygon(hexPointsX(bgBoxX, bgBoxW), hexPointsY(bgBoxY, bgBoxH), 6);
                                                         break;
                     case UnboundString.ROUNDED_RECT  :  g2d.drawRoundRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH, round, round);
                                                         break;
                     default                          :  break;
                  }
               }
            }
            
            // draw the string
            g2d.setColor(us.getFGColor());
            g2d.drawString(us.getString(), xLoc, yLoc);
         }
      }
   }
   
   
   // draw unbound tile. These will be in front of the background and foreground.
   protected void drawUnboundTile(Graphics2D g2d, UnboundTile ut, int baseXOffset, int baseYOffset)
   {
      int xTile;
      int yTile;
      int xOrigin;
      int yOrigin;
      int round = rowHeight / 4;
      
      xTile = ut.getXLoc() - cornerCell[0];
      yTile = ut.getYLoc() - cornerCell[1];
      // only draw stuff that's on screen
      if(isInBounds(xTile, yTile))
      {
         xOrigin = (xTile * colWidth) + arrayXInset + (int)(ut.getXOffset() * colWidth) + 1;
         yOrigin = (yTile * rowHeight) + arrayYInset + (int)(ut.getYOffset() * rowHeight) + 1;
         
         xOrigin += baseXOffset;
         yOrigin += baseYOffset;
         
         // note that as unbound strings do not rectify their position, this stays static over the life of the US
         if(isInsetRow(yTile))
            xOrigin += colWidth / 2;
      
         g2d.setColor(ut.getBGColor());
         int bgBoxX = xOrigin;
         int bgBoxY = yOrigin;
         int bgBoxW = colWidth - 2;
         int bgBoxH = rowHeight - 2;
         int cir = Math.min(bgBoxW, bgBoxH);
         switch(ut.getBackgroundBoxType())
         {
            case UnboundString.RECT          :  g2d.fillRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                break;
            case UnboundString.OVAL          :  g2d.fillOval(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                break;
            case UnboundString.CIRCLE        :  g2d.fillOval(bgBoxX, bgBoxY, cir, cir);
                                                break;
            case UnboundString.HEXAGON       :  g2d.fillPolygon(hexPointsX(bgBoxX, bgBoxW), hexPointsY(bgBoxY, bgBoxH), 6);
                                                break;
            case UnboundString.ROUNDED_RECT  :  g2d.fillRoundRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH, round, round);
                                                break;
            default                          :  break;
         }
               
         // draw the border, if it has one
         if(ut.getBorder() != null)
         {
            g2d.setColor(ut.getBorder());
            switch(ut.getBackgroundBoxType())
            {
               case UnboundString.RECT          :  g2d.drawRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                   break;
               case UnboundString.OVAL          :  g2d.drawOval(bgBoxX, bgBoxY, bgBoxW, bgBoxH);
                                                   break;
               case UnboundString.CIRCLE        :  g2d.drawOval(bgBoxX, bgBoxY, cir, cir);
                                                   break;
               case UnboundString.HEXAGON       :  g2d.drawPolygon(hexPointsX(bgBoxX, bgBoxW), hexPointsY(bgBoxY, bgBoxH), 6);
                                                   break;
               case UnboundString.ROUNDED_RECT  :  g2d.drawRoundRect(bgBoxX, bgBoxY, bgBoxW, bgBoxH, round, round);
                                                   break;
               default                          :  break;
            }
         }
         
         // draw the string
         int tileStrX = (colWidth - g2d.getFontMetrics().stringWidth(ut.getString())) / 2;
         g2d.setColor(ut.getFGColor());
         g2d.drawString(ut.getString(), xOrigin + tileStrX, yOrigin + strYInset);
      }
   }
   
   // returns a list of the x locations for graphics.fillPolygon()
   protected int[] hexPointsX(int xOrigin, int xSize)
   {
      int quarterWidth = xSize / 4;
      int farSide = xOrigin + xSize;
      int[] returnArr = {xOrigin + quarterWidth, farSide - quarterWidth, farSide,
                         farSide - quarterWidth, xOrigin + quarterWidth, xOrigin};
      return returnArr;
   }
   
   // returns a list of the y locations for graphics.fillPolygon()
   protected int[] hexPointsY(int yOrigin, int ySize)
   {
      int halfHeight = ySize / 2;
      int bottom = yOrigin + ySize;
      int[] returnArr = {yOrigin, yOrigin, yOrigin + halfHeight,
                         bottom, bottom, yOrigin + halfHeight};
      return returnArr;
   }
}
