/***************************************************************************

Calculates concentric rings using a Dijkstra map. Generated on first call,
and always considers diagonals valid.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

***************************************************************************/

package WidlerSuite;

import java.util.*;

public class DijkstraRing
{
   private static ListWrapper[] ringList;
   private static final int MAX_RADIUS = 25;
   private static boolean hasBeenCalculated = false;
   
   // the only public method. Returns a list of Coords in a set radius around [0,0]
   public static Vector<Coord> getRing(int radius)
   {
      if(!hasBeenCalculated)
         calculate();
      return ringList[radius].list;
   }
   
   private static void calculate()
   {
      ringList = new ListWrapper[MAX_RADIUS + 1];
      for(int i = 0; i < ringList.length; i++)
         ringList[i] = new ListWrapper();
      DijkstraMap map = new DijkstraMap(openArray());
      map.addGoal(MAX_RADIUS, MAX_RADIUS);
      boolean previousValue = map.getSearchDiagonal();
      map.setSearchDiagonal(true);
      map.process();
      int size = MAX_RADIUS + MAX_RADIUS + 1;
      for(int x = 0; x < size; x++)
      for(int y = 0; y < size; y++)
      {
         // normal rounding
         int dist = (map.getValue(x, y) + 5) / 10;
         if(dist <= MAX_RADIUS)
            ringList[dist].addItem(x, y);
      }
      map.setSearchDiagonal(previousValue);
   }
   
   private static boolean[][] openArray()
   {
      int size = MAX_RADIUS + MAX_RADIUS + 1;
      boolean[][] trueArr = new boolean[size][size];
      for(int x = 0; x < size; x++)
      for(int y = 0; y < size; y++)
         trueArr[x][y] = true;
      return trueArr;
   }
   
   private static class ListWrapper
   {
      public Vector<Coord> list;
      
      public ListWrapper()
      {
         list = new Vector<Coord>();
      }
      
      public void addItem(int rawX, int rawY)
      {
         list.add(new Coord(rawX - MAX_RADIUS, rawY - MAX_RADIUS));
      }
   }
}