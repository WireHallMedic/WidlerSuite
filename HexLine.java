/*******************************************************************************************

Creates a straight line between two points on a hex grid.
I'll be super honest here; this one's got a dirty workaround in it. The only good
solution I came up with involves handling the coordinates in an entirely different manner,
and then it clashes with the rest of the suite. So it works fine, but if anyone has a
cleaner solution shoot me an email.

Can be called directly or throught StraightLine, when StraightLine is in Hex Mode. Static.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;
import java.util.*;

public class HexLine extends StraightLine
{
   public static final double THIRTY_DEGREE_SLOPE = SIXTH_CIRCLE;
   
   // returns the line between two points, subject to arguments
   public static Vector<Coord> findLine(Coord tileOrigin, Coord tileTarget){return findLine(tileOrigin, tileTarget, 0);}
   public static Vector<Coord> findLine(Coord tileOrigin, Coord tileTarget, int arguments)
   {
      double xOrigin = WSTools.getHexX(tileOrigin.x, tileOrigin.y);
      double yOrigin = tileOrigin.y;
      double xTarget = WSTools.getHexX(tileTarget.x, tileTarget.y);
      double yTarget = tileTarget.y;
      double totalX = xTarget - xOrigin;
      double totalY = yTarget - yOrigin;
      int steps = countSteps(tileOrigin, tileTarget);
      double xStep = totalX / steps;
      double yStep = totalY / steps;
      int x;
      int y;
      
      // create the list
      Vector<Coord> list = new Vector<Coord>();
      for(int i = 0; i <= steps; i++)
      {
         y = getRow(yOrigin + (i * yStep));
         x = getColumn(xOrigin + (i * xStep), y);
         list.add(new Coord(x, y));
      }
      
      // fill in gaps which occur in x-dominant lines
      smooth(list);
      
      // remove head and/or tail as specified
      trim(list, arguments);
      
      return list;
   }
   
   // returns the row in which a y position lies
   protected static int getRow(double y)
   {
      if(roundToEven)
         return WSTools.roundToEven(y);
      return WSTools.roundToInt(y);
   }
   
   // returns in which column and x, y position lies
   protected static int getColumn(double x, int y)
   {
      if(y % 2 == 1)
         x -= .5;
      if(roundToEven)
         return WSTools.roundToEven(x);
      return WSTools.roundToInt(x);
   }
   
   // returns the number of steps in the shortest possible line
   protected static int countSteps(Coord origin, Coord target)
   {
      Vector<Coord> minWalk = walkTo(origin, target);
      int xMove = 0;
      int yMove = 0;
      for(int i = 0; i < minWalk.size() - 1; i++)
      {
         if(minWalk.elementAt(i).y == minWalk.elementAt(i + 1).y)
            xMove += 2;
         else
         {
            xMove += 1;
            yMove += 2;
         }
      }
      return Math.max(xMove, yMove) / 2;
   }
   
   // a hacky solution for having gaps in the line
   protected static void smooth(Vector<Coord> list)
   {
      int extraStep = -1;
      if(list.elementAt(0).x < list.lastElement().x)
         extraStep = 1;
      
      for(int i = 0; i < list.size() - 1; i++)
      {
         if(areAdjacent(list.elementAt(i), list.elementAt(i + 1)) == false)
         {
            Coord bridge = new Coord(list.elementAt(i));
            bridge.x += extraStep;
            list.insertElementAt(bridge, i + 1);
         }
      }
   }
   
   // walks towards the target diagonally, until it can walk horizontally
   protected static Vector<Coord> walkTo(Coord origin, Coord target)
   {
      Vector<Coord> list = new Vector<Coord>();
      list.add(new Coord(origin));
      while(list.lastElement().equals(target) == false)
         list.add(getStepTowards(list.lastElement(), target));
      return list;
   }
   
   // returns the single next step towards the target
   protected static Coord getStepTowards(Coord origin, Coord target)
   {
      int hextant = getHextant(origin, target);
      Coord step = new Coord(0, 0);
      
      // select correct array
      int[][] intArr = HEX_EVEN_ROW;
      if(origin.y % 2 == 1)
         intArr = HEX_ODD_ROW;
      
      // set the next step
      step.x = intArr[hextant][0] + origin.x;
      step.y = intArr[hextant][1] + origin.y;
      
      return step;
   }
   
   // calculates the angle from origin to target in hex mode
   protected static double getAngle(Coord origin, Coord target)
   {
      double x = WSTools.getHexX(target.x, target.y) - WSTools.getHexX(origin.x, origin.y);
      double y = (double)(target.y - origin.y);
      return WSTools.getAngle(x, y);
   }
   
   // calculates in which hextant the line from origin to target (relative to origin) lies
   protected static int getHextant(Coord origin, Coord target)
   {
      double angle = getAngle(origin, target);
      angle += WSTools.TWELFTH_CIRCLE;
      int hextant = (int)(angle / WSTools.SIXTH_CIRCLE) + E;
      return hextant % 6;
   }
   
   // checks if two cells are adjacent by brute force
   protected static boolean areAdjacent(Coord a, Coord b)
   {
      for(Coord c : getAdjArray(a))
      {
         if(c.equals(b))
            return true;
      }
      return false;
   }
   
   // returns an array of the six tiles adjacent to the argument
   protected static Coord[] getAdjArray(Coord c)
   {
      Coord[] arr = new Coord[6];
      int[][] stepArr = HEX_EVEN_ROW;
      if(c.y % 2 == 1)
         stepArr = HEX_ODD_ROW;
      for(int i = 0; i < 6; i++)
      {
         arr[i] = new Coord(c.x + stepArr[i][0], c.y + stepArr[i][1]);
      }
      return arr;
   }
}
