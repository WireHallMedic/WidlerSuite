/*****************************************************************
//
//	Vector system.  Used for tracking angle and magnitude
//	Michael Widler
//
//
*****************************************************************/

package WidlerSuite;


public class Vect
{
	public double angle; //	in radians
	public double magnitude;
   
   public static final double FULL_CIRCLE = Math.PI * 2;
   public static final double HALF_CIRCLE = FULL_CIRCLE / 2;
   public static final double QUARTER_CIRCLE = FULL_CIRCLE / 4;
   public static final double EIGHTH_CIRCLE = FULL_CIRCLE / 8;
	
	
	public Vect()
	{
		angle = 0.0;
		magnitude = 0;
	}
	
	
	public Vect(double theta, double length)
	{
		this.set(theta, length);
	}
	
	
	public Vect(double theta, int length)
	{
		this.set(theta, (double)length);
	}
	
	
	public Vect(Coord cart)
	{
		this.set(cart);
	}
	
	
	public Vect(Vect that)
	{
		this.angle = that.angle;
		this.magnitude = that.magnitude;
	}
	
	
	public Vect(Coord origin, Coord terminus)
	{
		this.set(origin, terminus);
	}
	
	public Vect copy()
	{
		return new Vect(this);
	}
	
///////////////////////////////////////////////////
	//	setters
	
	
	public void set(double a, double m)
		{angle = a; magnitude = m;}
	
	public void set(double a, int m)
		{angle = a; magnitude = (double)m;}
	
	public void set(Vect that)
		{this.angle = that.angle; this.magnitude = that.magnitude;}
	
	public void set(Coord origin, Coord terminus)
	//	sets the values of this Vect equal to the difference of the passed coords
		{this.set(new Coord(terminus.x - origin.x, terminus.y - origin.y));}
	
	public void set(Coord cart)
	//	converts the values of the passed Coord to a Vect, then sets this Vect to those
	{
		double x = Math.abs(cart.x);
		double y = Math.abs(cart.y);
		this.magnitude = Math.sqrt( (x * x) + (y * y) );
		
		try
		{
			this.angle = Math.atan( (double)(-1.0 * cart.y) / (double)cart.x);
			if(cart.x < 0)
				angle += WSTools.HALF_CIRCLE;
			angle = WSTools.simplifyAngle(angle);
		}
		catch(ArithmeticException aEx)
		{
			if(cart.y > 0)
				angle = WSTools.THREE_QUARTER_CIRCLE;
			else
				angle = WSTools.QUARTER_CIRCLE;
		}	
	}
	
/////////////////////////////////////////////////
	//	Getters.  Most common getters are unnecessary, as
	//		both data members are public
	

	public int getX()
	{
		return WSTools.roundToInt(Math.cos(this.angle) * this.magnitude);
	}
	
	public int getY()
	{
		return WSTools.roundToInt((Math.sin(this.angle) * this.magnitude * -1));
	}
	
	public double getXAsDouble()
	{
		return Math.cos(this.angle) * this.magnitude;
	}
	
	public double getYAsDouble()
	{
		return (Math.sin(this.angle) * this.magnitude * -1);
	}
	

/////////////////////////////////////////////
	//	Other methods
	
	
	public void add(Vect that)
	// loss of precision may result if the magnitudes are small, as Coord
	//	members are all int.
	{
		Coord tempCoord = new Coord(this);
		tempCoord.add(new Coord(that));
		this.set(tempCoord);
	}
	
	public void add(Coord that)
	{
		this.add(new Vect(that));
	}
	
	public boolean equals(Vect that)
	{
		if(this.angle == that.angle &&
		   this.magnitude == that.magnitude)
			return true;
		else
			return false;
	}
    
    public Coord getAsCoord()
    {
        return new Coord(this);
    }
	
	
}