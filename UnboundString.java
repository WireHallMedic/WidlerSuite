/**********************************************************************************
A class representing strings (and possibly a rounded rectangle behind the string)
which are not bound to the RoguePanel's standard display grid.

API:
    constructors:
UnboundString(String s)                                 // defaults to white foreground at tile 0, 0
UnboundString(String s, Color fg, int x, int y)
    setters:
void setBGColor(Color b)
void setFGColor(Color f)
void setString(String s)
void setXLoc(int x)                                 // in tiles
void setYLoc(int y)                                 // in tiles
void setXOffset(double x)                           // in fractional tiles
void setYOffset(double y)                           // in fractional tiles
void setBackgroundBox(boolean b)                    // defaults to false
void setLifespan(int l)                             // in ticks
void setSpeed(double x, double y)                   // in fractional tiles per tick
static void setDefaultLifespan(int dl)              // in ticks
    getters:
Color getBGColor(){return bgColor;}
Color getFGColor(){return fgColor;}
String getString(){return string;}
int getXLoc()                                       // in whole tiles
int getYLoc()                                       // in whole tiles
double getXOffset()                                 // in fractional tiles
double getYOffset()                                 // in fractional tiles
boolean hasBackgroundBox()
static int getDefaultLifespan()
    constants:
TRANSPARENT_BLACK                                   // common background color
  
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import java.awt.*;
import java.awt.event.*;

public class UnboundString implements ActionListener, WSConstants
{
	private Color bgColor;
	private Color fgColor;
	private String string;
	private Coord loc;          // in tiles
	private double xOffset;     // in tiles
	private double yOffset;     // in tiles
    private double xSpeed;      // in tiles
    private double ySpeed;      // in tiles
	private boolean backgroundBox;
   private int backgroundBoxType;
	private int lifespan;
	private int age;
    private boolean affectedByGravity;
    
    private static int defaultLifespan = 15;
    public static final Color TRANSPARENT_BLACK = new Color(0, 0, 0, 128);
    public static final boolean GRAVITY_DEFAULT = false;
    public static final int ROUNDED_RECT = 0;
    public static final int RECT = 1;
    public static final int OVAL = 2;
    public static final int CIRCLE = 3;


	public Color getBGColor(){return bgColor;}
	public Color getFGColor(){return fgColor;}
	public String getString(){return string;}
    public Coord getLoc(){return new Coord(loc);}
	public int getXLoc(){return loc.x;}
	public int getYLoc(){return loc.y;}
	public double getXOffset(){return xOffset;}
	public double getYOffset(){return yOffset;}
	public boolean hasBackgroundBox(){return backgroundBox;}
   public int getBackgroundBoxType(){return backgroundBoxType;}
    public static int getDefaultLifespan(){return defaultLifespan;}
    public boolean isAffectedByGravity(){return affectedByGravity;}


	public void setBGColor(Color b){bgColor = b;}
	public void setFGColor(Color f){fgColor = f;}
	public void setString(String s){string = s;}
    public void setLoc(int x, int y){loc = new Coord(x, y);}
    public void setLoc(Coord l){loc = new Coord(l);}
	public void setXLoc(int x){loc.x = x;}
	public void setYLoc(int y){loc.y = y;}
	public void setXOffset(double x){xOffset = x;}
	public void setYOffset(double y){yOffset = y;}
	public void setBackgroundBox(boolean b){backgroundBox = b;}
   public void setBackgroundBoxType(int bt){backgroundBoxType = bt;}
	public void setLifespan(int l){lifespan = l;}
	public void setAge(int a){age = a;}
    public void setDefaultLifespan(int dl){defaultLifespan = dl;}
    public void setAffectedByGravity(boolean abg){affectedByGravity = abg;}
    
    // minimal constructor
    public UnboundString(String s)
    {
        this(s, Color.WHITE, 0, 0);
    }
    
    // setting constructors
    public UnboundString(String s, Color fg, Coord l){this(s, fg, l.x, l.y);}
    public UnboundString(String s, Color fg, int x, int y)
    {
        string = s;
        bgColor = TRANSPARENT_BLACK;
        fgColor = fg;
        loc = new Coord(x, y);
        xOffset = 0.0;
        yOffset = 0.0;
        lifespan = defaultLifespan;
        age = 0;
        backgroundBox = false;
        backgroundBoxType = ROUNDED_RECT;
        xSpeed = 0.0;
        ySpeed = 0.0;
        affectedByGravity = GRAVITY_DEFAULT;
    }
    
    // copy constructor
    public UnboundString(UnboundString that)
    {
        this.string = that.string;
        this.bgColor = that.bgColor;
        this.fgColor = that.fgColor;
        this.loc = new Coord(that.loc);
        this.xOffset = that.xOffset;
        this.yOffset = that.yOffset;
        this.lifespan = that.lifespan;
        this.age = that.age;
        this.backgroundBox = that.backgroundBox;
        this.backgroundBoxType = that.backgroundBoxType;
        this.xSpeed = that.xSpeed;
        this.ySpeed = that.ySpeed;
        this.affectedByGravity = that.affectedByGravity;
    }
    
    // if an unbound string should be removed by the manager
    public boolean isExpired()
    {
        return age >= lifespan;
    }
    
    // how many tiles are moved per tick
    public void setSpeed(double x, double y)
    {
        xSpeed = x;
        ySpeed = y;
    }
    
    // timer kick
    public void actionPerformed(ActionEvent ae)
    {
        age++;
        if(affectedByGravity)
            ySpeed += GRAVITY;
        xOffset += xSpeed;
        yOffset += ySpeed;
    }
}