/*******************************************************************************************
  
    Used for calculating a list of tiles which lie on a line, this class calls down to
    the appropropriate child class (rect or hex), and holds the shared functions those two
    children need.
    
    HexLine and RectLine can also be called directly, if preferred.
  
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.
  
*******************************************************************************************/

package WidlerSuite;

import java.util.*;

public class StraightLine implements WSConstants
{
    public static final int REMOVE_ORIGIN = 1;
    public static final int REMOVE_TARGET = 2;
    public static final int REMOVE_ORIGIN_AND_TARGET = 3;
    protected static boolean roundToEven = true;                // either round to nearest even number or nearest int
    protected static int mode = RECT_MODE;                      // either RECT_MODE or HEX_MODE
    
    public static void setRoundToEven(boolean r){roundToEven = r;} 
    public static void setMode(int m){mode = m;}
    
    // returns the line between two points, subject to arguments
    public static Vector<Coord> findLine(Coord origin, Coord target){return findLine(origin, target, 0);}
    public static Vector<Coord> findLine(Coord origin, Coord target, int arguments)
    {
        if(mode == HEX_MODE)
            return HexLine.findLine(origin, target, arguments);   
        else
            return RectLine.findLine(origin, target, arguments);    
    }
    
    // removes the origin and/or target, as determined by the arguments
    protected static void trim(Vector<Coord> line, int arguments)
    {
        // take out the target if requested
        if(arguments == REMOVE_TARGET || arguments == REMOVE_ORIGIN_AND_TARGET &&
           line.size() > 1)
            line.removeElementAt(line.size() - 1);
        
        // take out the origin if requested
        if(arguments == REMOVE_ORIGIN || arguments == REMOVE_ORIGIN_AND_TARGET &&
           line.size() > 0)
            line.removeElementAt(0);
    }
    
    // returns a new Vector which is the sum of the two, in sequence
    protected static Vector<Coord> combine(Vector<Coord> to, Vector<Coord> from)
    {
        Vector<Coord> list = new Vector<Coord>();
        for(Coord c : to)
            list.add(c);
        for(int i = from.size() - 1; i >= 0; i--)
            list.add(from.elementAt(i));
        return list;
    }
}