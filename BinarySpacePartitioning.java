package WidlerSuite;

import java.util.*;


public class BinarySpacePartitioning
{
    private static double partitionChance = .5;
    
    public void setPartitionChance(double pc){partitionChance = pc;}
    
	public static Vector<Room> partition(Coord size, int minRoomDiameter, int maxRoomDiameter)
	{
		Vector<Room> roomList = new Vector<Room>();
		Room[] addRooms;
		
		if(maxRoomDiameter < 2 * minRoomDiameter)
			maxRoomDiameter = 2 * minRoomDiameter;
		
		Room startRoom = new Room();
		startRoom.origin = new Coord(0, 0);
		startRoom.size = new Coord(size);
		startRoom.iteration = 0;
		roomList.add(startRoom);
		
		for(int i = 0; i < roomList.size(); i++)
		{
			if(roomList.elementAt(i).size.x > maxRoomDiameter ||
			   roomList.elementAt(i).size.y > maxRoomDiameter)
			{
				addRooms = divide(roomList.elementAt(i), minRoomDiameter);
				roomList.add(addRooms[0]);
				roomList.add(addRooms[1]);
			}
			else // if less than max room size
			{
				if(roomList.elementAt(i).size.x >= minRoomDiameter * 2 ||
			      roomList.elementAt(i).size.y >= minRoomDiameter * 2)
				if(WSTools.random() < partitionChance)
				{
					addRooms = divide(roomList.elementAt(i), minRoomDiameter);
					roomList.add(addRooms[0]);
					roomList.add(addRooms[1]);
				}
			}
		}
		
		return roomList;
	}

	
	private static Room[] divide(Room r, int minRoomDiameter)
	{
		Room a = new Room();
		Room b = new Room();
		a.iteration = r.iteration + 1;
		b.iteration = r.iteration + 1;
		r.isParent = true;
		int diameterVarianceX = r.size.x - (2 * minRoomDiameter);
		int diameterVarianceY = r.size.y - (2 * minRoomDiameter);
		
		if(r.size.x > r.size.y) // wider; divide vertically
		{
			a.origin = new Coord(r.origin);
			a.size.x = minRoomDiameter + (int)(diameterVarianceX * WSTools.random());
			a.size.y = r.size.y;
			
			b.origin.x = a.origin.x + a.size.x;
			b.origin.y = a.origin.y;
			b.size.x = r.size.x - a.size.x;
			b.size.y = a.size.y;
		}
		else // taller; divide horizontally
		{
			a.origin = new Coord(r.origin);
			a.size.x = r.size.x;
			a.size.y = minRoomDiameter + (int)(diameterVarianceY * WSTools.random());
			
			b.origin.x = a.origin.x;
			b.origin.y = a.origin.y + a.size.y;
			b.size.x = a.size.x;
			b.size.y = r.size.y - a.size.y;
		}
		
		return new Room[] {a, b};
	}
	
}