package WidlerSuite;

import java.util.*;

public class MathTools
{
	//	Enumerators
	public final static double FULL_CIRCLE = 2 * Math.PI;                // 360 degrees
	public final static double THREE_QUARTER_CIRCLE = 3 * (Math.PI / 2); // 270 degrees
	public final static double HALF_CIRCLE = Math.PI;                    // 180 degrees
	public final static double QUARTER_CIRCLE = Math.PI / 2;             // 90 degrees
	public final static double EIGHTH_CIRCLE = Math.PI / 4;              // 45 degrees
	public final static double SIXTH_CIRCLE = Math.PI / 3;               // 30 degrees
	public final static double TWELFTH_CIRCLE = Math.PI / 6;            // 15 degrees


	// Rounds a double to an int.
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
	} public double pathDistance(Coord origin, Vector<Coord> path){return pathDistance(origin, path, 1.41);}


	public static String doubleToPercent(double d)
	{
		d *= 100;
		return (int)d + "%";
	}
   
   
   // all the long axis plus half the short axis
   public static int getAngbandMetric(Coord start, Coord end){return getAngbandMetric(start.x, start.y, end.x, end.y);}
   public static int getAngbandMetric(int startX, int startY, int endX, int endY)
   {
      int totalX = endX - startX;
      int totalY = endY - startY;
      
      if(Math.abs(totalX) > Math.abs(totalY))
         return Math.abs(totalX) + Math.abs(totalY / 2);
      
      return Math.abs(totalY) + Math.abs(totalX / 2);
   }
   
   /*
   public static int getNumberOfSteps(Coord start, Coord end)
   {
      return StraightLine.findLine(start, end, StraightLine.REMOVE_ORIGIN).size();
   }
	
	*/
   // returns the square of the hypotenuse; used for quickly calculating which of several distances is longer
   public static int getDistanceMetric(Coord start, Coord end){return getDistanceMetric(start.x, start.y, end.x, end.y);}
   public static int getDistanceMetric(int startX, int startY, int endX, int endY)
   {
		int a = Math.abs(endX - startX);
		int b = Math.abs(endY - startY);
		return (a * a) + (b * b);
   }
   
   public static double getDistance(Coord start, Coord end){return getDistance(start.x, start.y, end.x, end.y);}
   public static double getDistance(int startX, int startY, int endX, int endY)
   {
      return Math.sqrt(getDistanceMetric(startX, startY, endX, endY));
   }
   
   
   // returns the value of a point between two values.  For example, if the passed values are 2 and 4, and the xOffset
   // is .5 (halfway between the two), this will return 3.
   public static double interpolateLinear(float p1, float p2, float xOff)
   {
      return (double)(p1 + ((p2 - p1) * xOff));
   }
   
   
   // returns a value similar to interpolateLinear(), but on an s-curve so that results are more heavily weighted towards
   // the closer of the two points.
   public static double interpolateCosine(float p1, float p2, float xOff)
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
}