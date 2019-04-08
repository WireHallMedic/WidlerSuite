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

public class HexLine extends StraightLine
{
   public static final double THIRTY_DEGREE_SLOPE = Math.atan(Math.PI / 6.0);
   
   public static Vector<Coord> findLine(Coord tileOrigin, Coord tileTarget){return findLine(tileOrigin, tileTarget, 0);}
   public static Vector<Coord> findLine(Coord tileOrigin, Coord tileTarget, int arguments)
   {
      double xOrigin = getHexX(tileOrigin.x, tileOrigin.y);
      double yOrigin = tileOrigin.y;
      double xTarget = getHexX(tileTarget.x, tileTarget.y);
      double yTarget = tileTarget.y;
      double totalX = xTarget - xOrigin;
      double totalY = yTarget - yOrigin;
      int steps = countSteps(tileOrigin, tileTarget);
      double xStep = totalX / steps;
      double yStep = totalY / steps;
      int x;
      int y;
      
      Vector<Coord> list = new Vector<Coord>();
      for(int i = 0; i <= steps; i++)
      {
          y = getRow(yOrigin + (i * yStep));
          x = getColumn(xOrigin + (i * xStep), y);
          list.add(new Coord(x, y));
      }
      // fill in gaps which occur in x-dominant lines
      smooth(list);
      
      trim(list, arguments);
      
      return list;
   }

    private static double getHexX(int rectX, int rectY)
    {
        double hexX = rectX;
        if(rectY % 2 == 1)
            hexX += .5;
        return hexX;
    }
    
    private static int getRow(double y)
    {
        if(roundToEven)
            return WSTools.roundToEven(y);
        return WSTools.roundToInt(y);
    }
    
    private static int getColumn(double x, int y)
    {
        if(y % 2 == 1)
            x -= .5;
        if(roundToEven)
            return WSTools.roundToEven(x);
        return WSTools.roundToInt(x);
    }
    
    private static int countSteps(Coord origin, Coord target)
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
    private static void smooth(Vector<Coord> list)
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
    
    private static Vector<Coord> walkTo(Coord origin, Coord target)
    {
        Vector<Coord> list = new Vector<Coord>();
        list.add(new Coord(origin));
        while(list.lastElement().equals(target) == false)
            list.add(getStepTowards(list.lastElement(), target));
        return list;
    }
    

    private static Coord getStepTowards(Coord origin, Coord target)
    {
        int hextant = getHextant(origin, target);
        Coord step = new Coord(0, 0);
        
        // select correct array
        int[][] intArr = HEX_EVEN_ROW;
        if(origin.y % 2 == 1)
            intArr = HEX_ODD_ROW;
        
        step.x = intArr[hextant][0] + origin.x;
        step.y = intArr[hextant][1] + origin.y;

        return step;
    }
    

    private static double getAngle(Coord origin, Coord target)
    {
        double x = getHexX(target.x, target.y) - getHexX(origin.x, origin.y);
        double y = (double)(target.y - origin.y);
        double angle = 0.0;

		if(x == 0.0)
        {
			if(y < 0.0)
				angle = WSTools.THREE_QUARTER_CIRCLE;
			else
				angle = WSTools.QUARTER_CIRCLE;
        }
		else
		{
			angle = Math.atan(y / x);
			if(x < 0.0)
				angle += WSTools.HALF_CIRCLE;
			angle = WSTools.simplifyAngle(angle);
		}
		return angle;
    }
    
    private static int getHextant(Coord origin, Coord target)
    {
        double angle = getAngle(origin, target);
        angle += WSTools.TWELFTH_CIRCLE;
        int hextant = (int)(angle / WSTools.SIXTH_CIRCLE) + E;
        return hextant % 6;
    }
    
    private static boolean areAdjacent(Coord a, Coord b)
    {
        for(Coord c : getAdjArray(a))
        {
            if(c.equals(b))
                return true;
        }
        return false;
    }
    
    private static Coord[] getAdjArray(Coord c)
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
    
    private static Vector<Coord> combine(Vector<Coord> to, Vector<Coord> from)
    {
        Vector<Coord> list = new Vector<Coord>();
        for(Coord c : to)
            list.add(c);
        for(int i = from.size() - 1; i >= 0; i--)
            list.add(from.elementAt(i));
        return list;
    }
}