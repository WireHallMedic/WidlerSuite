package WidlerSuite;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class UnboundTile implements ActionListener, UnboundInterface
{
	private int xLoc;             // in tiles
	private int yLoc;             // in tiles
	private double xOffset;       // in tiles
	private double yOffset;       // in tiles
   protected double xSpeed;      // in tiles per tick
   protected double ySpeed;      // in tiles per tick
	private int bgColor;
	private int fgColor;
	private int iconIndex;
	private BufferedImage image;
	private TilePalette parentPalette;
   private int sizeMultiplier;
   private boolean bgType;
   protected int lifespan;
   protected int age;
   protected double gravity;     // tiles per tick
   protected boolean affectedByAge;
   protected boolean visible;
   
   public static final boolean BOX_BACKGROUND = false;
   public static final boolean CIRCLE_BACKGROUND = true;


	public int getXLoc(){return xLoc;}
	public int getYLoc(){return yLoc;}
	public double getXOffset(){return xOffset;}
	public double getYOffset(){return yOffset;}
	public int getBGColor(){return bgColor;}
	public int getFGColor(){return fgColor;}
	public int getIconIndex(){return iconIndex;}
	public BufferedImage getImage(){return image;}
	public TilePalette getParentPalette(){return parentPalette;}
   public int getSizeMultiplier(){return sizeMultiplier;}
   public boolean getBGType(){return bgType;}
   public double getGravity(){return gravity;}
   public boolean isAffectedByAge(){return affectedByAge;}
   public boolean isVisible(){return visible;}


	public void setXLoc(int x){xLoc = x;}
	public void setYLoc(int y){yLoc = y;}
	public void setXOffset(double x){xOffset = x;}
	public void setYOffset(double y){yOffset = y;}
	public void setBGColor(int b){bgColor = b;}
	public void setFGColor(int f){fgColor = f;}
	public void setIconIndex(int i){iconIndex = i;}
	public void setImage(BufferedImage i){image = i;}
	public void setParentPalette(TilePalette p){parentPalette = p;}
   public void setSizeMultiplier(int sm){sizeMultiplier = sm;}
   public void setBGType(boolean b){bgType = b;}
   public void setLifespan(int l){lifespan = l;}
   public void setAge(int a){age = a;}
   public void setyGravity(double g){gravity = g;}
   public void setAffectedByAge(boolean aba){affectedByAge = aba;}
   public void setVisible(boolean v){visible = v;}

   public UnboundTile(TilePalette pp)
   {
      parentPalette = pp;
      xLoc = 0;
      yLoc = 0;
      xOffset = 0.0;
      yOffset = 0.0;
      bgColor = Color.BLACK.getRGB();
      fgColor = Color.WHITE.getRGB();
      iconIndex = 0;
      image = null;
      sizeMultiplier = 1;
      bgType = BOX_BACKGROUND;
      xSpeed = 0.0;
      ySpeed = 0.0;
      gravity = 0.0;
      affectedByAge = true;
      visible = true;
      age = 0;
      lifespan = 15;
   }
   
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
      ySpeed += gravity;
      xOffset += xSpeed;
      yOffset += ySpeed;
   }
}