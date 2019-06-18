/*******************************************************************************************
  
A binary space partitioning algorithm, which recursively splits an area into smaller
rooms. As there is little data to keep track of other than the list of rooms,
basically you can just call BinarySpacePartitioning.partition(x, y, minRoomDiameter, 
maxRoomDiameter).
 
The first room on the returned list (index 0) is the entire area; beyond that, every pair
((n*2)-1) and (n*2) are sibilings and either horizontally or vertically adjacent. The 
first sibiling is always the left or top of the pair.
  
If you just want the lowest level of the tree, remove every room where isParent == true.
 
Modeled after the algorithm at:
http:  www.roguebasin.com/index.php?title=Basic_BSP_Dungeon_generation
  
Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

import java.util.*;

public class BinarySpacePartitioning
{
   protected static double partitionChance = .5;     // how likely a room is to split if it's below
                                                   // max size but could still split into legal rooms
    
   public static void setPartitionChance(double pc){partitionChance = pc;}
    
   // the main function
   public static Vector<Room> partition(int x, int y, int minRoomDiameter, int maxRoomDiameter)
   {
      Vector<Room> roomList = new Vector<Room>();
      Room[] addRooms;
   
      // The max has to be at least twice the min
      if(maxRoomDiameter < 2 * minRoomDiameter)
         maxRoomDiameter = 2 * minRoomDiameter;
   
      Room startRoom = new Room();
      startRoom.origin = new Coord(0, 0);
      startRoom.size = new Coord(x, y);
      startRoom.iteration = 0;
      roomList.add(startRoom);
   
      for(int i = 0; i < roomList.size(); i++)
      {
         // rooms that are larger than the max are always split
         if(roomList.elementAt(i).size.x > maxRoomDiameter ||
            roomList.elementAt(i).size.y > maxRoomDiameter)
         {
            addRooms = divide(roomList.elementAt(i), minRoomDiameter);
            roomList.add(addRooms[0]);
            roomList.add(addRooms[1]);
         }
         else // rooms which can be, but don't need to be split, are handled here
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
   public static Vector<Room> partition(Coord size, int minRoomDiameter, int maxRoomDiameter)
   {
      return partition(size.x, size.y, minRoomDiameter, maxRoomDiameter);
   }
   
   // the main work method. Splits a room and returns its children
   protected static Room[] divide(Room r, int minRoomDiameter)
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