//////////////////////////////////////////////////////////////////////////////
//
// An implementation of Bresenham's Line
//
//
//
//
///////////////////////////////////////////////////////////////////////////////

package WidlerSuite;
import java.util.*;

public class RectLine implements WSConstants
{
   public static final int REMOVE_ORIGIN = 1;
   public static final int REMOVE_TARGET = 2;
   public static final int REMOVE_ORIGIN_AND_TARGET = 3;
   
   private static boolean roundToEven = true;
   
   public static void setRoundToEven(boolean r){roundToEven = r;}
   
   public static Vector<Coord> findLine(Coord origin, Coord target){return findLine(origin, target, 0);}
   public static Vector<Coord> findLine(Coord origin, Coord target, int arguments)
   {
      double xMove = (double)(target.x - origin.x);
      double yMove = (double)(target.y - origin.y);
      int steps = (int)Math.max(Math.abs(xMove), Math.abs(yMove));
      double xStep = (double)xMove / (double)steps;
      double yStep = (double)yMove / (double)steps;
      
      Vector<Coord> list;
      list = rectLoop(origin, steps, xStep, yStep);
      
      // take out the target if requested
      if(arguments == REMOVE_TARGET || arguments == REMOVE_ORIGIN_AND_TARGET)
          if(list.size() > 1)
          {
            list.removeElementAt(list.size() - 1);
          }
      
      // take out the origin if requested
      if(arguments == REMOVE_ORIGIN || arguments == REMOVE_ORIGIN_AND_TARGET)
        list.removeElementAt(0);
        
      return list;
   }
   
    private static Vector<Coord> rectLoop(Coord origin, int steps, double xStep, double yStep)
    {
        Vector<Coord> list = new Vector<Coord>();
        for(int i = 0; i <= steps; i++)
        {
            Coord c = new Coord();
            if(roundToEven)
            {
                c.y = MathTools.roundToEven(i * yStep);
                c.x = MathTools.roundToEven(i * xStep);
            }
            else
            {
                c.y = MathTools.roundToInt(i * yStep);
                c.x = MathTools.roundToInt(i * xStep);
            }
            c.x += origin.x;
            c.y += origin.y;
            list.add(c);
        }
        return list;
    }
   
   
   public static Vector<Coord> add(Vector<Coord> line1, Vector<Coord> line2)
   {
      Vector<Coord> newLine = new Vector<Coord>();
      for(int i = 0; i < line1.size(); i++)
         newLine.add(line1.elementAt(i));
      for(int i = 0; i < line2.size(); i++)
         newLine.add(line2.elementAt(i));
      return newLine;
   }
}