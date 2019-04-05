/**********************************************************************************
A class for a curses implementation. Everything is pretty much handled internally, 
except for being hooked up to a timer (which is passed on to the unbound string stuff,
as long as this panel is visible).
Note getMouseColumn() and getMouseRow() are intended to be called when external mouseListeners
and mouseMotionListeners recieve events, but whatever floats your goat.

API:
    constructor:
RoguePanel()
void setColumnsAndRows(int c, int r)
void setFGColor(int x, int y, Color c)
void setBGColor(int x, int y, Color c)
void setString(int x, int y, char s)
void setString(int x, int y, String s)
void setFontName(String f)      // font is always plain, and resizes dynamically
void setTile(int x, int y, String s, Color fg)
void setTile(int x, int y, String s, Color fg, Color bg)
int columns()
int rows()
boolean isInBounds(int x, int y)
Color getFGColor(int x, int y)
Color getBGColor(int x, int y)
String getString(int x, int y)
String getFontName()
String getFont()
Color getTileBorderColor()
int getMouseColumn()    // returns the index of the row in which the mouse is currently located (ie, the X location)
int getMouseRow()       // returns the index of the column in which the mouse is currently located (ie, the Y location)
    UnboundString functions:
void addLocking(UnboundString str)
void addNonlocking(UnboundString str)
void add(UnboundString str)     // adds as nonlocking
boolean isAnimationLocked()
void clearUnboundStrings()
void setCornerCell(int x, int y)    // call this xor next function for properly displaying unbound strings
void setCenterCell(int x, int y)    // call this xor previous function for properly displaying unbound strings
    inherits from JPanel
void setTileBorderColor(Color bc)


**********************************************************************************/
package WidlerSuite;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class RoguePanel extends JPanel implements ComponentListener, ActionListener, MouseListener, MouseMotionListener, WSConstants
{
	private Color[][] fgColor = new Color[1][1];
	private Color[][] bgColor = new Color[1][1];
	private String[][] str = new String[1][1];
	private int[][] strXInset = new int[1][1];
	private int[][] strYInset = new int[1][1];
	private Font font = null;
    private FontMetrics fontMetrics = null;
	private String fontName = "Monospaced";
    private int colWidth = 0;       // in pixels
    private int rowHeight = 0;      // in pixels
    private int arrayXInset = 0;
    private int arrayYInset = 0;
    private int oddRowInset = 0;
    private UnboundStringManager unboundStringManager;
    private int[] cornerCell = {0, 0};
    private Vector<MouseListener> mouseListenerList;
    private Vector<MouseMotionListener> mouseMotionListenerList;
    private int[] mouseLoc = {-1, -1};
    private int displayMode = RECT_MODE;
    private boolean showBorders = false;
    private Color borderColor = Color.WHITE;
    


	public Font getFont(){return font;}
	public String getFontName(){return fontName;}
    public Color getTileBorderColor(){return borderColor;}
    
    public int columns(){return str.length;}
    public int rows(){return str[0].length;}
    public int mouseColumn(){return mouseLoc[0];}
    public int mouseRow(){return mouseLoc[1];}


	public void setFontName(String f){fontName = f; setFont();}
    public void showTileBorders(boolean sb){showBorders = sb;}
    public void setTileBorderColor(Color bc){borderColor = bc;}
    
    
    public RoguePanel()
    {
        super();
        unboundStringManager = new UnboundStringManager(this);
        mouseListenerList = new Vector<MouseListener>();
        mouseMotionListenerList = new Vector<MouseMotionListener>();
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        setColumnsAndRows(15, 15);
        setFont();
        setBackground(Color.BLACK);
    }
    
    // checks if the passed tile location is in the display bounds
    public boolean isInBounds(int x, int y)
    {
        if(x >= 0 && y >= 0 && x < columns() && y < rows())
            return true;
        return false;
    }
    
    // test functionn
    public void randomize()
    {
        for(int x = 0; x < columns(); x++)
        for(int y = 0; y < rows(); y++)
        {
            setBGColor(x, y, new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), (float)1.0));
            int charVal = (int)'a';
            if(Math.random() > .5)
                charVal = (int)'A';
            charVal += (int)(Math.random() * 26);
            setString(x, y, (char)charVal);
        }
    }
    
    //////////////////////////////////////////////////////
    // public setters
    
    public void setColumnsAndRows(int x, int y)
    {
        fgColor = new Color[x][y];
        bgColor = new Color[x][y];
        str = new String[x][y];
        strXInset = new int[x][y];
        strYInset = new int[x][y];
        
        setSizes();
        
        for(int c = 0; c < x; c++)
        for(int r = 0; r < y; r++)
        {
            fgColor[c][r] = Color.WHITE;
            bgColor[c][r] = Color.BLACK;
            str[c][r] = " ";
        }
    }
    
    // set foreground color of a specific tile
    public void setFGColor(int x, int y, Color c)
    {
        if(isInBounds(x, y))
            fgColor[x][y] = c;
    }
    
    // set background color of a specific tile
    public void setBGColor(int x, int y, Color c)
    {
        if(isInBounds(x, y))
            bgColor[x][y] = c;
    }
    
    // set the string (generally one character) of a specific tile
    public void setString(int x, int y, char c){setString(x, y, c + "");}
    public void setString(int x, int y, String s)
    {
        if(isInBounds(x, y))
        {
            str[x][y] = s;
            setStrInset(x, y);
        }
    }
    
    // set a tile all at once
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
    
    //////////////////////////////////////////////////////////
    // public getters
    
    public Color getFGColor(int x, int y)
    {
        if(isInBounds(x, y))
            return fgColor[x][y];
        return null;
    }
    
    public Color getBGColor(int x, int y)
    {
        if(isInBounds(x, y))
            return bgColor[x][y];
        return null;
    }
    
    public String getString(int x, int y)
    {
        if(isInBounds(x, y))
            return str[x][y];
        return "";
    }
    
    
    // private getters and setters
    //////////////////////////////////////////////////////////
    
    // determines if a row should be indented while in hex mode
    private boolean isOddRow(int rowIndex)
    {
        return displayMode == HEX_MODE && (rowIndex + cornerCell[1]) % 2 == 1;
    }
    
    // set internal values based on panel size and array size
    private void setSizes()
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
    
    // font can only be set internally
    private void setFont()
    {
        int ptHeight = rowHeight * 5 / 4;
        font = new Font(fontName, Font.PLAIN, ptHeight);
        fontMetrics = this.getFontMetrics(font);    // can't just make a FontMetrics object because it's abstract
        for(int x = 0; x < columns(); x++)
        for(int y = 0; y < rows(); y++)
        {
            setStrInset(x, y);
        }
    }
    
    // set the insets (in pixels) of a specific string
    private void setStrInset(int x, int y)
    {
        // no error checking as this can only be called internally
        strXInset[x][y] = (colWidth - fontMetrics.stringWidth(getString(x, y))) / 2;
        strYInset[x][y] = (rowHeight * 9) / 10;    // because strings are drawn from the bottom
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
    private void updateMouseLoc(MouseEvent me)
    {
        int xLoc = me.getX() - arrayXInset;
        int yLoc = me.getY() - arrayYInset;
        boolean oob = false;
        
        // adjust odd rows if in hex mode
        if(isOddRow(yLoc / rowHeight))
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
    // UnboundStringManager stuff
    
    // update unbound strings and repaint when kicked by timer
    public void actionPerformed(ActionEvent ae)
    {
        if(this.isVisible())
        {
            unboundStringManager.actionPerformed(ae);
            this.repaint();
        }
    }
    
    // add a locking unbound string
    public void addLocking(UnboundString us)
    {
        unboundStringManager.addLocking(us);
    }
    
    // add a non-locking unbound string
    public void addNonlocking(UnboundString us){add(us);}
    public void add(UnboundString us)
    {
        unboundStringManager.addNonlocking(us);
    }
    
    // check if external processes should be delayed while waiting for animation to complete
    public boolean isAnimationLocked()
    {
        return unboundStringManager.isLocked();
    }
    
    // clear all the unbound string lists
    public void clearUnboundStrings()
    {
        unboundStringManager.clear();
    }
    
    // set the corner cell, for properly displaying unbound strings
    public void setCornerCell(int x, int y)
    {
        cornerCell[0] = x;
        cornerCell[1] = y;
    }
    
    // set the corner cell by passing the center cell
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
        
        // draw each tile
        for(int x = 0; x < columns(); x++)
        for(int y = 0; y < rows(); y++)
        {
            xLoc = arrayXInset + (x * colWidth);
            yLoc = arrayYInset + (y * rowHeight);
            if(isOddRow(y))
                xLoc += oddRowInset;
            // background
            g2d.setColor(bgColor[x][y]);
            g2d.fillRect(xLoc, yLoc, colWidth, rowHeight);
            
            // foreground
            g2d.setColor(fgColor[x][y]);
            g2d.drawString(str[x][y], xLoc + strXInset[x][y], yLoc + strYInset[x][y]);
            
            // tile borders
            if(showBorders)
            {
                g2d.setColor(borderColor);
                g2d.drawRect(xLoc, yLoc, colWidth, rowHeight);
            }
        }
            
        // unbound strings
        drawUnboundStrings(g2d, unboundStringManager.getLockList());
        drawUnboundStrings(g2d, unboundStringManager.getNonlockList());
    }
    
    // draw unbound strings. These will be in front of the background and foreground.
    private void drawUnboundStrings(Graphics2D g2d, Vector<UnboundString> usList)
    {
        int xTile;
        int yTile;
        int xLoc;
        int yLoc;
        int round = fontMetrics.getHeight() / 4;
        
        for(UnboundString us : usList)
        {
            xTile = us.getXLoc() - cornerCell[0];
            yTile = us.getYLoc() - cornerCell[1];
            // only draw stuff that's on screen
            if(isInBounds(xTile, yTile))
            {
                xLoc = (xTile * colWidth) + arrayXInset + (colWidth / 2) - (fontMetrics.stringWidth(us.getString()) / 2) + (int)(us.getXOffset() * colWidth);
                yLoc = (yTile * rowHeight) + arrayYInset + (rowHeight / 2) + (int)(us.getYOffset() * rowHeight);
                
                // note that as unbound strings do not rectify their position, this stays static over the life of the US
                if(isOddRow(yTile))
                    xLoc += colWidth / 2;
                
                // draw the box, if any
                if(us.hasBackgroundBox())
                {
                    g2d.setColor(us.getBGColor());
                    g2d.fillRoundRect(xLoc - (colWidth / 4), yLoc - ((rowHeight * 3) / 4), 
                                      fontMetrics.stringWidth(us.getString()) + (colWidth / 2), rowHeight,
                                      round, round);
                }
                
                // draw the string
                g2d.setColor(us.getFGColor());
                g2d.drawString(us.getString(), xLoc, yLoc);
            }
        }
    }
}