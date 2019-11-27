/***************************************************************************

Calculates concentric rings using a Dijkstra map. Call once to generate,
then use the static call.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

***************************************************************************/

package WidlerSuite;

import java.util.*;

public class DijkstraRing
{
   private static ListWrapper[] ringList;
   private static final int MAX_RADIUS = 25;
   
   public static void calculate()
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
   
   public static Vector<Coord> getRing(int radius)
   {
      return ringList[radius].list;
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
   
   public static void main(String[] args)
   {
      DijkstraRing.calculate();
      char[][] charArr = new char[15][15];
      for(int i = 0; i < 6; i++)
      {
         charArr = setTestArr(charArr, DijkstraRing.getRing(i));
         printTestArr(charArr);
      }
   }
   
   private static char[][] clearTestArr(char[][] charArr)
   {
      for(int x = 0; x < charArr.length; x++)
      for(int y = 0; y < charArr[0].length; y++)
      {
         charArr[x][y] = ' ';
      }
      return charArr;
   }
   
   private static char[][] setTestArr(char[][] charArr, Vector<Coord> list)
   {
      charArr = clearTestArr(charArr);
      for(Coord element : list)
      {
         charArr[element.x + 7][element.y + 7] = 'X';
      }
      return charArr;
   }
   
   private static void printTestArr(char[][] charArr)
   {
      System.out.println("///////////");
      for(int y = 0; y < 15; y++)
      {
         for(int x = 0; x < 15; x++)
         {
            System.out.print("" + charArr[x][y]);
         }
         System.out.println();
      }
   }
}