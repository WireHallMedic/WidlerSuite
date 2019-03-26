package WidlerSuite;

import java.util.*;

public class StraightLine implements WSConstants
{
    public static final int REMOVE_ORIGIN = 1;
    public static final int REMOVE_TARGET = 2;
    public static final int REMOVE_ORIGIN_AND_TARGET = 3;
    protected static boolean roundToEven = true;
    protected static int mode = RECT_MODE;
    
    public static void setRoundToEven(boolean r){roundToEven = r;}
    public static void setMode(int m){mode = m;}
    
    public static Vector<Coord> findLine(Coord origin, Coord target){return findLine(origin, target, 0);}
    public static Vector<Coord> findLine(Coord origin, Coord target, int arguments)
    {
        if(mode == HEX_MODE)
            return HexLine.findLine(origin, target, arguments);   
        else
            return RectLine.findLine(origin, target, arguments);    
    }
    
    // removes the origin and/or target, as determined by the arguments
    public static void trim(Vector<Coord> line, int arguments)
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
}