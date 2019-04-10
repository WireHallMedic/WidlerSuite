/*******************************************************************************************
  
    A simple cartesian coordinate system for WidlerSuite. Plays nicely with Vect. Treated
    as more of a struct than a class.
  
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.
  
*******************************************************************************************/

package WidlerSuite;

import java.util.Vector;


public class Coord
{
	public int x;
	public int y;
	
    // empty constructor
	public Coord()
	{
		x = -1;
		y = -1;
	}
	
    // int constructor
	public Coord(int newX, int newY)
	{
		x = newX;
		y = newY;
	}
	
    // copy constructor
	public Coord(Coord that)
	{
		this.x = that.x;
		this.y = that.y;
	}
	
    // int array constructor
	public Coord(int[] that)
	{	
		try
		{
			this.x = that[0];
			this.y = that[1];
		}
		catch(ArrayIndexOutOfBoundsException arrEx){}
	}
	
    // Vect constructor
	public Coord(Vect that)
	{
		this.set(that);
	}
	
    // deep copies an existing Coord into this
	public void copy(Coord that)
	{
		this.x = that.x;
		this.y = that.y;
	}
	
    // returns a deep copy of this Coord
	public Coord copy()
	{
		return new Coord(x, y);
	}
	
    // sums another Coord into this one
	public void add(Coord that)
	{
		this.x += that.x;
		this.y += that.y;
	}
	
    // subtracts another Coord from this one
	public void subtract(Coord that)
	{
		this.x -= that.x;
		this.y -= that.y;
	}
	
    // set this Coord by a Vect (converts mathematical vector to cartesian coordinates)
	public void set(Vect that)
	{
		this.x = that.getX();
		this.y = that.getY();
	}
	
    // return the distance between this and that
	public double distanceTo(Coord that)
	{
		int a = Math.abs(this.x - that.x);
		int b = Math.abs(this.y - that.y);
		return Math.sqrt((double)((a * a) + (b * b)));
	}
	
    // checks if this and that have identical data
	public boolean equals(Coord that)
	{
      if(that == null)
         return false;
		if(this.x == that.x)
		if(this.y == that.y)
			return true;
		return false;
	}
	
    // checks if this has identical data to the passed data
	public boolean equals(int w, int h)
	{
		return equals(new Coord(w, h));
	}
	
    // checks if this and that are adjacent (orthogonally or diagonally)
	public boolean isAdjacent(Coord that)
	{
		if(this.distanceTo(that) < 1.5)
			return true;
		return false;
	}
	
    // checks if this and that are orthogonally adjacent
	public boolean isOrthogonallyAdjacent(Coord that)
	{
		if(this.distanceTo(that) < 1.1)
			return true;
		return false;
	}
    
    // converts the xy coordinates to a mathematical vector, returned as a Vect
    public Vect getAsVect()
    {
        return new Vect(this);
    }
	
    // converts the xy coordinates to a mathematical vector, and returns just the magnitude
	public double getMagnitude()
	{
		Vect v = new Vect(this);
		return v.magnitude;
	}
	
    // converts the xy coordinates to a mathematical vector, and returns just the angle
	public double getAngle()
	{
		Vect v = new Vect(this);
		return v.angle;
	}
	
    // represents the data as a string formatted as [x][y]
	public String toString()
	{
		return String.format("[%d][%d]", x, y);
	}
	
    // skims a Vector (java.util.Vector, not WidlerSuite.Vect) of Coords and removes duplicates
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
	
}