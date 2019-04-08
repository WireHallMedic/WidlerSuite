/*******************************************************************************************
//
//  A class used by the BSP generator. Keeps track of abstract rooms. Child rooms are made 
//  by splitting a single parent room.
//  Modeled after the algorithm at:
//    http://www.roguebasin.com/index.php?title=Basic_BSP_Dungeon_generation
//
*******************************************************************************************/

package WidlerSuite;

public class Room
{
	public Coord origin;        // xy coordinate of the upper left corner of the room
	public Coord size;          // width (x) and height (y) of the room
	public int iteration;       // how many split iterations it took to create this room
	public boolean isParent;    // have any child rooms been made out of this one
	
    // empty constructor
	public Room()
	{
		origin = new Coord();
		size = new Coord();
		iteration = -1;
		isParent = false;
	}
	
    // checks if the room contains the passed location
	public boolean contains(Coord loc){return contains(loc.x, loc.y);}
    public boolean contains(int x, int y)
	{
		if(x >= origin.x &&
		   x <= origin.x + size.x &&
		   y >= origin.y &&
		   y <= origin.y + size.y)
			return true;
		return false;
	}
	
    // checks if this room is the parent of the passed room
	public boolean isParentOf(Room that)
	{
		if(this.contains(that.origin))
		if(this.iteration == that.iteration - 1)
			return true;
		return false;
	}
	
    // returns a Coord corresponding to the center of this room
	public Coord getCenter()
	{
		Coord center = new Coord(origin);
		center.x += size.x / 2;
		center.y += size.y / 2;
		
		return center;
	}
	
	// checks if two rooms are equivalent
	public boolean equals(Room that)
	{
		if(this.origin.x == that.origin.x)
		if(this.origin.y == that.origin.y)
		if(this.size.x == that.size.x)
		if(this.size.y == that.size.y)
			return true;
		return false;
	}
	
	// returns true if the right boundary of this room and the left boundary of that room are in adjacent columns.
    // find sibiling then checks the opposite (right of that adjacent to left of this)
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
    
    // returns true if the bottom boundary of this room and the top boundary of that room are in adjacent columns.
    // find sibiling then checks the opposite (bottom of that adjacent to top of this)
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
    
    // returns a random cell from the room
	public Coord getRandomCell()
	{
		Coord cell = new Coord(origin);
		
		cell.x += WSTools.random(size.x);
		cell.y += WSTools.random(size.y);
		
		return cell;
	}
}