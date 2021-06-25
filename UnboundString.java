/**********************************************************************************
A class representing strings (and possibly a simple background behind the string)
which are not bound to the RoguePanel's standard display grid.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import java.awt.*;
import java.awt.event.*;

public class UnboundString implements ActionListener, WSConstants, UnboundInterface
{
   protected Color bgColor;
   protected Color fgColor;
   protected Color border;
   protected String string;
   protected Coord loc;          // in tiles
   protected double xOffset;     // in tiles
   protected double yOffset;     // in tiles
   protected double xSpeed;      // in tiles
   protected double ySpeed;      // in tiles
   protected boolean backgroundBox;
   protected int backgroundBoxType;
   protected int lifespan;
   protected int age;
   protected boolean affectedByGravity;
   protected boolean affectedByAge;
   protected boolean visible;
   
   protected static int defaultLifespan = 15;
   public static final Color TRANSPARENT_BLACK = new Color(0, 0, 0, 128);
   public static final boolean GRAVITY_DEFAULT = false;
   public static final int ROUNDED_RECT = 0;
   public static final int RECT = 1;
   public static final int OVAL = 2;
   public static final int CIRCLE = 3;
   public static final int HEXAGON = 4;
   
   
   public Color getBGColor(){return bgColor;}
   public Color getFGColor(){return fgColor;}
   public Color getBorder(){return border;}
   public String getString(){return string;}
   public Coord getLoc(){return new Coord(loc);}
   public int getXLoc(){return loc.x;}
   public int getYLoc(){return loc.y;}
   public double getXOffset(){return xOffset;}
   public double getYOffset(){return yOffset;}
   public boolean hasBackgroundBox(){return backgroundBox;}
   public int getBackgroundBoxType(){return backgroundBoxType;}
   public static int getDefaultLifespan(){return defaultLifespan;}
   public static void setDefaultLifespan(int dl){defaultLifespan = dl;}
   public boolean isAffectedByGravity(){return affectedByGravity;}
   public boolean isAffectedByAge(){return affectedByAge;}
   public boolean isVisible(){return visible;}
   
   
   public void setBGColor(Color b){bgColor = b;}
   public void setFGColor(Color f){fgColor = f;}
   public void setBorder(Color b){border = b;}
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
   public void setAffectedByGravity(boolean abg){affectedByGravity = abg;}
   public void setAffectedByAge(boolean aba){affectedByAge = aba;}
   public void setVisible(boolean v){visible = v;}
   
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
      border = null;
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
      affectedByAge = true;
      visible = true;
   }
   
   // copy constructor
   public UnboundString(UnboundString that)
   {
      this.string = that.string;
      this.bgColor = that.bgColor;
      this.border = that.border;
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
      this.visible = that.visible;
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
   
   // manually expires the UnboundString
   public void forceExpire()
   {
      age = lifespan;
   }
   
   // alter existing speeds and positions
   public void adjustXSpeed(double s){xSpeed += s;}
   public void adjustYSpeed(double s){ySpeed += s;}
   public void adjustXOffset(double p){xOffset += p;}
   public void adjustYOffset(double p){yOffset += p;}
   
   // timer kick
   public void actionPerformed(ActionEvent ae)
   {
      if(affectedByAge)
         age++;
      if(affectedByGravity)
         ySpeed += GRAVITY;
      xOffset += xSpeed;
      yOffset += ySpeed;
   }
}
