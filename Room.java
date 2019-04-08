package WidlerSuite;

public class Room
{
	public Coord origin;
	public Coord size;
	public int iteration;
	public boolean isParent;
	
	public Room()
	{
		origin = new Coord();
		size = new Coord();
		iteration = -1;
		isParent = false;
	}
	
	public boolean contains(Coord loc)
	{
		if(loc.x >= origin.x &&
		   loc.x <= origin.x + size.x &&
		   loc.y >= origin.y &&
		   loc.y <= origin.y + size.y)
			return true;
		return false;
	}
	
	public boolean isParentOf(Room that)
	{
		if(this.contains(that.origin))
		if(this.iteration == that.iteration - 1)
			return true;
		return false;
	}
	
	public Coord getCenter()
	{
		Coord center = new Coord(origin);
		center.x += size.x / 2;
		center.y += size.y / 2;
		
		return center;
	}
	
	
	public boolean equals(Room that)
	{
		if(this.origin.x == that.origin.x)
		if(this.origin.y == that.origin.y)
		if(this.size.x == that.size.x)
		if(this.size.y == that.size.y)
			return true;
		return false;
	}
	
	
	public boolean isHorizontallyAdjacent(Room that, boolean findSibiling)
	{
		if(this.origin.x + this.size.x == that.origin.x)
			return true;
			
		if(findSibiling == false)
		if(that.origin.x + that.size.x == this.origin.x)
			return true;
			
		return false;
	}
	public boolean isHorizontallyAdjacent(Room that){return isHorizontallyAdjacent(that, false);}
	
	
	public boolean isVerticallyAdjacent(Room that, boolean findSibiling)
	{
		if(this.origin.y + this.size.y == that.origin.y)
			return true;
		
		if(findSibiling == false)
		if(that.origin.y + that.size.y == this.origin.y)
			return true;
			
		return false;
	}
	public boolean isVerticallyAdjacent(Room that){return isVerticallyAdjacent(that, false);}

	public Coord getRandomCell()
	{
		Coord cell = new Coord(origin);
		
		cell.x += WSTools.random(size.x);
		cell.y += WSTools.random(size.y);
		
		return cell;
	}
}