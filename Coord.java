/*****************************************************************
//
//	Coordinate system.  Used for Cartesian Coordinates
//	Michael Widler
//
//
*****************************************************************/

package WidlerSuite;

import java.util.Vector;


public class Coord
{
	public int x;
	public int y;
	
	public Coord()
	{
		x = -1;
		y = -1;
	}
	
	public Coord(int newX, int newY)
	{
		x = newX;
		y = newY;
	}
	
	public Coord(Coord that)
	{
		this.x = that.x;
		this.y = that.y;
	}
	
	public Coord(int[] that)
	{	
		try
		{
			this.x = that[0];
			this.y = that[1];
		}
		catch(ArrayIndexOutOfBoundsException arrEx){}
	}
	
	public Coord(Vect that)
	{
		this.set(that);
	}
	
	public void copy(Coord that)
	{
		this.x = that.x;
		this.y = that.y;
	}
	
	public Coord copy()
	{
		return new Coord(x, y);
	}
	
	public void add(Coord that)
	{
		this.x += that.x;
		this.y += that.y;
	}
	
	public void subtract(Coord that)
	{
		this.x -= that.x;
		this.y -= that.y;
	}
	
	public void set(Vect that)
	{
		this.x = that.getX();
		this.y = that.getY();
	}
	
	public double distanceTo(Coord that)
	{
		int a = Math.abs(this.x - that.x);
		int b = Math.abs(this.y - that.y);
		return Math.sqrt((double)((a * a) + (b * b)));
	}
	
	public boolean equals(Coord that)
	{
      if(that == null)
         return false;
		if(this.x == that.x)
		if(this.y == that.y)
			return true;
		return false;
	}
	
	public boolean equals(int w, int h)
	{
		return equals(new Coord(w, h));
	}
	
	public boolean isAdjacent(Coord that)
	{
		if(this.distanceTo(that) < 1.5)
			return true;
		return false;
	}
	
	public boolean isOrthogonallyAdjacent(Coord that)
	{
		if(this.distanceTo(that) < 1.1)
			return true;
		return false;
	}
    
    public Vect getAsVect()
    {
        return new Vect(this);
    }
	
	public double getMagnitude()
	{
		Vect v = new Vect(this);
		return v.magnitude;
	}
	
	public double getAngle()
	{
		Vect v = new Vect(this);
		return v.angle;
	}
	
	public String toString()
	{
		return String.format("[%d][%d]", x, y);
	}
	
	public static void removeDuplicates(Vector<Coord> list)
	{
		for(int i = 0; i < list.size() - 1; i++)
		{
			for(int j = i + 1; j < list.size(); j++)
			{
				if(list.elementAt(i).equals( list.elementAt(j) ))
				{
					list.removeElementAt(j);
					j--;
				}
			}
		}
	}
	
	
	
	public static void main(String args[])
	{
		Coord origin = new Coord(5, 5);
		Coord target = new Coord(5, 10);
		Coord angle = new Coord(origin.x - target.x, origin.y - target.y);
		System.out.println("" + angle.getAngle());
	}
	
}