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

public class StraightLine
{
   public static final int REMOVE_ORIGIN = 1;
   public static final int REMOVE_TARGET = 2;
   public static final int REMOVE_ORIGIN_AND_TARGET = 3;
   
   public static boolean roundToEven = true;
   
   public static void setRoundToEven(boolean r){roundToEven = r;}
   
   public static Vector<Coord> findLine(Coord origin, Coord target){return findLine(origin, target, 0);}
   public static Vector<Coord> findLine(Coord origin, Coord target, int arguments)
   {
      Vector<Coord> list = new Vector<Coord>();
      int xMove = target.x - origin.x;
      int yMove = target.y - origin.y;
      int steps = Math.max(Math.abs(xMove), Math.abs(yMove));
      double xStep = (double)xMove / (double)steps;
      double yStep = (double)yMove / (double)steps;
      
      for(int i = 0; i <= steps; i++)
      {
         Coord c = new Coord();
         if(roundToEven)
         {
            c.x = MathTools.roundToEven(((double)i) * xStep);
            c.y = MathTools.roundToEven(((double)i) * yStep);
         }
         else
         {
            c.x = MathTools.roundToInt(((double)i) * xStep);
            c.y = MathTools.roundToInt(((double)i) * yStep);
         }
         c.x += origin.x;
         c.y += origin.y;
         list.add(c);
      }
      
      // take out the target if requested
      if(arguments == REMOVE_TARGET || arguments == REMOVE_ORIGIN_AND_TARGET)
      for(int i = 0; i < list.size(); i++)
      {
         if(list.elementAt(i).equals(target))
         {
            list.removeElementAt(i);
            i--;
         }
      }
      
      // take out the origin if requested
      if(arguments == REMOVE_ORIGIN || arguments == REMOVE_ORIGIN_AND_TARGET)
      for(int i = 0; i < list.size(); i++)
      {
         if(list.elementAt(i).equals(origin))
         {
            list.removeElementAt(i);
            i--;
         }
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
   
   
   public static void main(String[] args)
   {
      boolean[][] cell = new boolean[11][11];
      Coord start = new Coord(0, 6);
      Coord end = new Coord(10, 0);
      
      Vector<Coord> line = findLine(start, end);
      for(int i = 0; i < line.size(); i++)
         cell[line.elementAt(i).x][line.elementAt(i).y] = true;
      /*
      end = new Coord(10, 6);
      line = findLine(start, end);
      for(int i = 0; i < line.size(); i++)
         cell[line.elementAt(i).x][line.elementAt(i).y] = true;
      */
      end = new Coord(10, 8);
      line = findLine(start, end);
      for(int i = 0; i < line.size(); i++)
         cell[line.elementAt(i).x][line.elementAt(i).y] = true;
      
      for(int y = 0; y < 11; y++)
      {
         System.out.println();
         for(int x = 0; x < 11; x++)
         {
            if(cell[x][y])
               System.out.print("x");
            else
               System.out.print(".");
         }
      }
      
   }
}