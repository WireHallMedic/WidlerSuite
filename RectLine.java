/*******************************************************************************************
  
    Creates a straight line between two points on a rectangular grid.
    This is an implementation of Bresenham's Line. Can be called directly or through
    StraightLine when StraightLine is in Rect Mode. Static.
  
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.
  
*******************************************************************************************/

package WidlerSuite;
import java.util.*;

public class RectLine extends StraightLine
{
    // returns the line between two points, subject to arguments
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
        trim(list, arguments);
        
        return list;
    }
    
    // the work function
    private static Vector<Coord> rectLoop(Coord origin, int steps, double xStep, double yStep)
    {
        Vector<Coord> list = new Vector<Coord>();
        for(int i = 0; i <= steps; i++)
        {
            Coord c = new Coord();
            if(roundToEven)
            {
                c.y = WSTools.roundToEven(i * yStep);
                c.x = WSTools.roundToEven(i * xStep);
            }
            else
            {
                c.y = WSTools.roundToInt(i * yStep);
                c.x = WSTools.roundToInt(i * xStep);
            }
            c.x += origin.x;
            c.y += origin.y;
            list.add(c);
        }
        return list;
    }
}