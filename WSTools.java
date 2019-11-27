/*******************************************************************************************

A collection of utility functions which can be called statically.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

public class WSTools implements WSConstants
{
   // a central random number generator with good performance (comparied to Math.random(), at least)
   private static java.util.Random rng = new java.util.Random();
   private static void setRNGSeed(long val){rng.setSeed(val);}
   private static void setRNGSeed(){rng.setSeed((int)(Math.random() * Long.MAX_VALUE));}
   
   // returns a random number between 0.0 and 1.0 exclusive
   public static double random()
   {
      return rng.nextDouble();
   }
   
   // returns a random integer from 0 to (n-1) inclusive
   public static int random(int n)
   {
      return (int)(rng.nextDouble() * n);
   }
   
   // returns a random color
   public static Color randomColor()
   {
      return new Color((float)random(), (float)random(), (float)random());
   }
   
   // rounds a double to an int
   public static int roundToInt(double value)
   {
      if(value > 0.0)
         value += .5;
      if(value < 0.0)
         value -= .5;
      return (int)value;
   }
   
   // rounds to an intiger, but .5 rounds to the nearest even int
   public static int roundToEven(double value)
   {
      int returnVal = roundToInt(value);
      
      if(Math.abs(value % 1) == .5)
      {
         if(returnVal % 2 == 1)
            returnVal--;
         if(returnVal % 2 == -1)
            returnVal++;
      }
      return returnVal;
   }
   
   //	removes full circles (including negative circles) from an angle
   public static double simplifyAngle(double angle)
   {
      while(angle <= 0.0)
         angle += FULL_CIRCLE;
      return angle % FULL_CIRCLE;
   }
   
   // bounds an int by the passed values
   public static int minMax(int min, int value, int max)
   {
      value = Math.min(value, max);
      return Math.max(value, min);
   }
   
   // bounds a double by the passed values
   public static double minMax(double min, double value, double max)
   {
      value = Math.min(value, max);
      return Math.max(value, min);
   }
   
   //	returns the distance of a passed path
   //	assumes that the passed vector contains a sequential list of adjacent cells
   public static double pathDistance(Coord origin, Vector<Coord> path, double diagonalCost)
   {
      if(diagonalCost == 1.0)
         return (double)path.size();
      
      double dist = 0.0;
      Coord curStep = origin;
      Coord nextStep;
      
      try
      {
         nextStep = path.elementAt(0);
         
         if(curStep.x == nextStep.x || curStep.y == nextStep.y)
            dist += 1.0;
         else
            dist += diagonalCost;
      }
      catch(ArrayIndexOutOfBoundsException arrEx){}
      
      for(int i = 0; i < path.size() - 1; i++)
      {
         curStep = path.elementAt(i);
         nextStep = path.elementAt(i+1);
         
         if(curStep.x == nextStep.x || curStep.y == nextStep.y)
            dist += 1.0;
         else
            dist += diagonalCost;
      }
      
      return dist;
   } 
   public static double pathDistance(Coord origin, Vector<Coord> path){return pathDistance(origin, path, 1.41);}
   
   
   // returns a string representing a double as a (integer) percentage. Ex: .346 returns "34%"
   public static String doubleToPercent(double d)
   {
      d *= 100;
      return (int)d + "%";
   }
   
   // returns all the long axis plus half the short axis
   public static int getAngbandMetric(Coord start, Coord end){return getAngbandMetric(start.x, start.y, end.x, end.y);}
   public static int getAngbandMetric(int startX, int startY, int endX, int endY)
   {
      int totalX = endX - startX;
      int totalY = endY - startY;
      
      if(Math.abs(totalX) > Math.abs(totalY))
         return Math.abs(totalX) + Math.abs(totalY / 2);
      
      return Math.abs(totalY) + Math.abs(totalX / 2);
   }
   
   // returns the square of the hypotenuse; used for quickly calculating which of several distances is longer
   public static int getDistanceMetric(Coord start, Coord end){return getDistanceMetric(start.x, start.y, end.x, end.y);}
   public static int getDistanceMetric(int startX, int startY, int endX, int endY)
   {
      int a = Math.abs(endX - startX);
      int b = Math.abs(endY - startY);
      return (a * a) + (b * b);
   }
   
   // returns the actual distance
   public static double getDistance(Coord start, Coord end){return getDistance(start.x, start.y, end.x, end.y);}
   public static double getDistance(int startX, int startY, int endX, int endY)
   {
      return Math.sqrt(getDistanceMetric(startX, startY, endX, endY));
   }
   
   
   // returns the value of a point between two values.  For example, if the passed values are 2 and 4, and the xOffset
   // is .5 (halfway between the two), this will return 3.
   public static double interpolateLinear(double p1, double p2, double xOff)
   {
      return (double)(p1 + ((p2 - p1) * xOff));
   }
   
   
   // returns a value similar to interpolateLinear(), but on an s-curve so that results are more heavily weighted towards
   // the closer of the two points.
   public static double interpolateCosine(double p1, double p2, double xOff)
   {
      xOff = ((-1.0f * (float)Math.cos(Math.PI * xOff)) *.5f) + .5f;
      return interpolateLinear(p1, p2, xOff);
   }
   
   
   // returns the the fractional portion of max which cur is as a double
   public static double getRatio(int cur, int max){return getRatio((double)cur, (double)max);}
   public static double getRatio(double cur, double max)
   {
      if(max == 0.0)
         return 1.0;
      return cur / max;
   }
   
   // returns the index of the tile in which the pixel lies, accounting for hex offset
   public static Coord getHexIndex(double x, double y){return getHexIndex(x, y, false);}
   public static Coord getHexIndex(double x, double y, boolean roundToEven)
   {
      Coord c = new Coord();
      if(roundToEven)
         c.y = roundToEven(y);
      else
         c.y = roundToInt(y);
      if(c.y % 2 == 1)
         x -= .5;
      if(roundToEven)
         c.x = roundToEven(x);
      else
         c.x = roundToInt(x);
      return c;
   }
   
   // returns the actual x position of a passed hex tile (every odd row is indented by .5 tiles)
   public static double getHexX(int rectX, int rectY)
   {
      double hexX = rectX;
      if(rectY % 2 == 1)
         hexX += .5;
      return hexX;
   }
   public static double getHexX(Coord c){return getHexX(c.x, c.y);}
   
   // returns the indices of the six hexes adjacent to the passed location
   public static Coord[] getAdjacentHexes(int originX, int originY)
   {
      Coord[] cellArr = new Coord[6];
      int[][] stepArr = WSConstants.HEX_EVEN_ROW;
      if(originY % 2 == 1)
         stepArr = WSConstants.HEX_ODD_ROW;
      for(int i = 0; i < 6; i++)
         cellArr[i] = new Coord(originX + stepArr[i][0], originY + stepArr[i][1]);
      return cellArr;
   }
   public static Coord[] getAdjacentHexes(Coord origin){return getAdjacentHexes(origin.x, origin.y);}
   
   // returns the angle from 0, 0 to the passed point
   public static double getAngle(double x, double y)
   {
      double angle = 0.0;
      
      if(x == 0.0)
      {
         if(y > 0.0)
            angle = QUARTER_CIRCLE;
         else
            angle = THREE_QUARTER_CIRCLE;
      }
      else
      {
         angle = Math.atan(y / x);
         if(x < 0.0)
            angle = angle + HALF_CIRCLE;
      }
      return angle;
   }
   
   // returns a color gradient from start to end, in a set number of steps
   public static Color[] getGradient(Color startColor, Color endColor, int steps)
   {
      int[] startComp = {startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()};
      int[] endComp = {endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()};
      double[] stepComp = new double[4];
      for(int i = 0; i < 4; i++)
      {
         stepComp[i] = (double)(endComp[i] - startComp[i]) / (double)steps;
      }
      Color[] gradient = new Color[steps];
      gradient[0] = startColor;
      gradient[steps - 1] = endColor;
      
      for(int i = 1; i < steps - 1; i++)
      {
         int[] midColorArr = new int[4];
         for(int j = 0; j < 4; j++)
         {
            midColorArr[j] = startComp[j] + WSTools.roundToInt(stepComp[j] * (double)i);
         }
         gradient[i] = new Color(midColorArr[0], midColorArr[1], midColorArr[2], midColorArr[3]);
      }
      return gradient;
   }
   
   // takes a larger image, and reduces it to a (smaller) color map
   public static Color[][] getBlit(int width, int height, BufferedImage src)
   {
      Color[][] map = new Color[width][height];
      int srcWidth = src.getWidth();
      int srcHeight = src.getHeight();
      
      int tileWidth = srcWidth / width;      // original pixels per blitted pixel
      int tileHeight = srcHeight / height;   // original pixels per blitted pixel
      int srcPixelsPerBlitPixel = tileWidth * tileHeight;
      int r;
      int g;
      int b;
      Color curColor;
      for(int xTile = 0; xTile < width; xTile++)
      for(int yTile = 0; yTile < height; yTile++)
      {
         r = 0;
         g = 0;
         b = 0;
         for(int x2 = 0; x2 < tileWidth; x2++)
         for(int y2 = 0; y2 < tileHeight; y2++)
         {
            curColor = new Color(src.getRGB((xTile * tileWidth) + x2, (yTile * tileHeight) + y2));
            r += curColor.getRed();
            g += curColor.getGreen();
            b += curColor.getBlue();
         }
         r /= srcPixelsPerBlitPixel;
         g /= srcPixelsPerBlitPixel;
         b /= srcPixelsPerBlitPixel;
         map[xTile][yTile] = new Color(r, g, b);
      }
      
      return map;
   }
}
